package com.himanshu.aitextgen.controller;

import com.himanshu.aitextgen.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateTemplate(@RequestBody Map<String, String> request) {
        try {
            String issueType = request.get("issueType");

            if (issueType == null || issueType.trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Invalid issue type");
                return ResponseEntity.badRequest().body(response);
            }

            String template = geminiService.generateTemplate(issueType);

            Map<String, String> response = new HashMap<>();
            response.put("issueType", issueType);
            response.put("template", template);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok().body("Service is running");
    }
}
