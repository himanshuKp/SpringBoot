package com.retailstore.feedbackservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Service
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private volatile boolean quotaExceeded;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent";

    /**
     * Sends a prompt to Gemini and returns the response
     *
     * @param prompt The prompt to send to Gemini
     * @return The response from Gemini
     */
    public String generateContent(String prompt) {
        if (quotaExceeded || apiKey == null || apiKey.isBlank()) {
            return null;
        }

        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode contents = objectMapper.createArrayNode();
            ObjectNode contentNode = objectMapper.createObjectNode();
            ArrayNode parts = objectMapper.createArrayNode();
            ObjectNode textPart = objectMapper.createObjectNode();

            textPart.put("text", prompt);
            parts.add(textPart);
            contentNode.set("parts", parts);
            contents.add(contentNode);
            requestBody.set("contents", contents);

            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 1024);
            requestBody.set("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = GEMINI_API_URL + "?key=" + apiKey;

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class);
            System.out.println(response.getStatusCode());

            // parse the response
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ObjectNode responseJson = (ObjectNode) objectMapper.readTree(response.getBody());
                ArrayNode candidates = (ArrayNode) responseJson.get("candidates");

                if (candidates != null && !candidates.isEmpty()) {
                    ObjectNode candidate = (ObjectNode) candidates.get(0);
                    ObjectNode candidateContent = (ObjectNode) candidate.get("content");
                    ArrayNode candidateParts = (ArrayNode) candidateContent.get("parts");

                    if (candidateParts != null && !candidateParts.isEmpty()) {
                        return candidateParts.get(0).get("text").asString();
                    }
                }
            }

            return "No response from Gemini";
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                quotaExceeded = true;
                System.err.println("Gemini quota exceeded; using local feedback fallback until restart.");
                return null;
            }
            System.err.println("Gemini API request failed: " + e.getStatusText());
            return null;
        } catch (Exception e) {
            System.err.println("Gemini API request failed: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    public void testConnection() {
        String testPrompt = "Say 'Hello, World!' if you can hear me.";
        String response = generateContent(testPrompt);
        System.out.println("Gemini API Test Response: " + response);
    }
}
