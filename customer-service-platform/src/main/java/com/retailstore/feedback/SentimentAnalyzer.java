package com.retailstore.feedback;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Analyzes the sentiment of customer feedback using Stanford CoreNLP.
 * This class reads feedback from a file, performs sentiment analysis,
 * and writes the results to an output file.
 */
public class SentimentAnalyzer {

    private static final Pattern FEEDBACK_NUMBER_PATTERN = Pattern.compile("Feedback #(\\d+).*");
    private static final Pattern CUSTOMER_PATTERN = Pattern.compile("Customer:\\s*(.+)");
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("Department:\\s*(.+)");
    private static final Pattern DATE_PATTERN = Pattern.compile("Date:\\s*(.+)");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("Comment:\\s*(.+)");

    //    stanford core nlp pipeline
    private final StanfordCoreNLP pipeline;

    /*
     * Constructor initializes the stanford core nlp pipeline with necessary properties
     * */
    public SentimentAnalyzer() {
//        set up pipeline properties
        Properties props = new Properties();
//        set the list of annotators to run
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
//        build pipeline
        System.out.println("Initializing Stanford CoreNLP pipeline...");
        this.pipeline = new StanfordCoreNLP(props);
        System.out.println("Initialized Stanford CoreNLP pipeline...");
    }

    static void main() {
        SentimentAnalyzer analyzer = new SentimentAnalyzer();

        String inputFilePath = "store_feedback.txt";
        String outputFilePath = "store_feedback_sentiment.txt";

        try {
            System.out.println("Loading Stanford CoreNLP pipeline...");
            System.out.println("Reading from file: " + inputFilePath);

//            process all feedback entries
            List<FeedbackEntry> feedbackEntries = analyzer.processFeedbackFile(inputFilePath);

            System.out.println("Processed "+feedbackEntries.size()+" feedback entries.");

            if (!feedbackEntries.isEmpty()) {
                System.out.println("\nFirst entry processed:");
                FeedbackEntry first = feedbackEntries.getFirst();
                System.out.println("ID: " + first.getId());
                System.out.println("Customer: " + first.getCustomer());
                System.out.println("Comment: " + first.getComment());
                System.out.println("Sentiment: " + first.getSentiment());
            }

            analyzer.writeResults(feedbackEntries, outputFilePath);

            System.out.println("\nSentiment analysis completed successfully.");
        } catch (IOException e){
            System.out.println("Error processing feedback: " + inputFilePath);
            System.out.println("Stack trace: "+e.getLocalizedMessage());
        }
    }

    /**
     * Writes the results of sentiment analysis to an output file
     *
     * @param feedbackEntries List of feedback entries with sentiment analysis
     * @param outputFilePath Path to the output file
     * @throws IOException If an I/O error occurs
     */
    private void writeResults(List<FeedbackEntry> feedbackEntries, String outputFilePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
//            write header
            writer.write("# Sentiment Analysis Results\n\n");

            writer.write("## Summary Statistics\n\n");

//            count sentiments
            Map<String, Long> sentimentCounts = feedbackEntries.stream()
                    .collect(Collectors.groupingBy(FeedbackEntry::getSentiment, Collectors.counting()));

            writer.write("Total Feedback Entries: " + feedbackEntries.size() + "\n");
            writer.write("Sentiment Distribution:\n");

            for (Map.Entry<String, Long> entry : sentimentCounts.entrySet()) {
                double percentage = (double) entry.getValue() / feedbackEntries.size() * 100;
                writer.write(String.format("- %s: %d (%.1f%%)\n", entry.getKey(), entry.getValue(), percentage));
            }

            writer.write("\n## Department Analysis\n\n");

//            group by department
            Map<String, List<FeedbackEntry>> byDepartment = feedbackEntries.stream()
                    .collect(Collectors.groupingBy(FeedbackEntry::getDepartment));

            for (Map.Entry<String, List<FeedbackEntry>> entry : byDepartment.entrySet()) {
                writer.write("### " + entry.getKey() + "\n\n");

                // Count sentiments per department
                Map<String, Long> deptSentimentCounts = entry.getValue().stream()
                        .collect(Collectors.groupingBy(FeedbackEntry::getSentiment, Collectors.counting()));

                for (Map.Entry<String, Long> sentCount : deptSentimentCounts.entrySet()) {
                    double percentage = (double) sentCount.getValue() / entry.getValue().size() * 100;
                    writer.write(String.format("- %s: %d (%.1f%%)\n", sentCount.getKey(), sentCount.getValue(), percentage));
                }

                writer.write("\n");
            }

            // Write detailed entries
            writer.write("## Detailed Feedback Entries\n\n");

            for (FeedbackEntry entry : feedbackEntries) {
                writer.write("Feedback #" + entry.getId() + "\n");
                writer.write("Customer: " + entry.getCustomer() + "\n");
                writer.write("Department: " + entry.getDepartment() + "\n");
                writer.write("Date: " + entry.getDate() + "\n");
                writer.write("Comment: " + entry.getComment() + "\n");
                writer.write("Sentiment: " + entry.getSentiment() + "\n\n");
            }
        }
    }


    /**
     * Processes the feedback file and returns a list of feedback entries with sentiment analysis
     *
     * @param inputFilePath Path to the feedback file
     * @return List of feedback entries with sentiment analysis
     * @throws IOException If an I/O error occurs
     */
    private List<FeedbackEntry> processFeedbackFile(String inputFilePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(inputFilePath)));

        List<FeedbackEntry> feedbackEntries = new ArrayList<>();

//        Split the content by "Feedback #" pattern to get individual results
        String[] rawEntries = content.split("(?=Feedback #)");
        System.out.println("Processing " + (rawEntries.length-1) + " feedback entries...");

        for (int i = 0; i < rawEntries.length; i++) {
            String rawEntry = rawEntries[i].trim();

            if (!rawEntry.startsWith("Feedback #")) {
                continue;
            }

            FeedbackEntry feedbackEntry = parseEntry(rawEntry);

            if (feedbackEntry != null) {
//                analyze sentiment
                System.out.println("Analyzing feedback entry: " + feedbackEntry.getId());
                String sentiment = analyzeSentiment(feedbackEntry.comment);
                feedbackEntry.setSentiment(sentiment);

                feedbackEntries.add(feedbackEntry);
            } else {
                System.out.println("Failed to process feedback entry: " + rawEntry);
            }
        }

        return feedbackEntries;
    }

    /**
     * Analyzes the sentiment of a comment using Stanford CoreNLP
     *
     * @param comment The comment to analyze
     * @return Sentiment (VERY_POSITIVE, POSITIVE, NEUTRAL, NEGATIVE, VERY_NEGATIVE)
     */
    private String analyzeSentiment(String comment) {
//        create a document for the comment
        CoreDocument document = new CoreDocument(comment);

//        annotate the document
        pipeline.annotate(document);

        List<CoreSentence> sentences = document.sentences();

        if (sentences.isEmpty()) {
            return "NEUTRAL";
        }

//        calculate the average sentiment
        Map<String, Integer> sentimentCounts = new HashMap<>();
        for (CoreSentence sentence : sentences) {
            String sentiment = sentence.sentiment();
            sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
        }

//        determine the most common sentiment
        return sentimentCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("NEUTRAL");
    }

    /*
    * Parse a single feedback entry text into a FeedbackEntry object
    *
    * @param entryText The text of a single feedback entry
    * @return FeedbackEntry object
    * */
    private FeedbackEntry parseEntry(String entryText) {
        FeedbackEntry feedbackEntry = new FeedbackEntry();

        try {
//            split into lines for easier parsing
            String[] lines = entryText.split("\n");

//            parse each line looking for specific patterns
            for (String line : lines) {
                line = line.trim();

//                extract feedback number from the first line
                if (line.startsWith("Feedback #")) {
                    Matcher numberMatcher = FEEDBACK_NUMBER_PATTERN.matcher(line);
                    if (numberMatcher.find()) {
                        feedbackEntry.setId(Integer.parseInt(numberMatcher.group(1)));
                    }
                }

                // Extract customer info
                else if (line.startsWith("Customer:")) {
                    Matcher customerMatcher = CUSTOMER_PATTERN.matcher(line);
                    if (customerMatcher.find()) {
                        feedbackEntry.setCustomer(customerMatcher.group(1).trim());
                    }
                }

                // Extract department
                else if (line.startsWith("Department:")) {
                    Matcher departmentMatcher = DEPARTMENT_PATTERN.matcher(line);
                    if (departmentMatcher.find()) {
                        feedbackEntry.setDepartment(departmentMatcher.group(1).trim());
                    }
                }

                // Extract date
                else if (line.startsWith("Date:")) {
                    Matcher dateMatcher = DATE_PATTERN.matcher(line);
                    if (dateMatcher.find()) {
                        feedbackEntry.setDate(dateMatcher.group(1).trim());
                    }
                }

                // Extract comment
                else if (line.startsWith("Comment:")) {
                    Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
                    if (commentMatcher.find()) {
                        feedbackEntry.setComment(commentMatcher.group(1).trim());
                    }
                }
            }

//            validate that we have all the required fields
            if (feedbackEntry.getId() > 0 && feedbackEntry.getComment() != null) {
                return feedbackEntry;
            } else {
                System.err.println("Invalid entry - missing ID or comment");
                System.err.println("ID: " + feedbackEntry.getId());
                System.err.println("Comment: " + feedbackEntry.getComment());
            }
        } catch (Exception e) {
            System.err.println("Error parsing entry: " + e.getMessage());
        }

        return feedbackEntry;
    }

    public static class FeedbackEntry {
        private int id;
        private String customer;
        private String department;
        private String date;
        private String comment;
        private String sentiment;

        // Default values to avoid null
        public FeedbackEntry() {
            this.customer = "";
            this.department = "";
            this.date = "";
            this.comment = "";
            this.sentiment = "";
        }

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getCustomer() { return customer; }
        public void setCustomer(String customer) { this.customer = customer; }

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public String getSentiment() { return sentiment; }
        public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    }
}
