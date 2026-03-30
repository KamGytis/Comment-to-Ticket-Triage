package com.pulsedesk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HuggingFaceService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.model.url}")
    private String modelUrl;

    @Value("${huggingface.model.name}")
    private String modelName;

    public HuggingFaceService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Analyze a user comment using Hugging Face Inference API
     * Returns a JsonNode with keys: isTicket, title, category, priority, summary
     */
    public JsonNode analyzeComment(String commentText) {
        // Build request body in OpenAI chat format
        Map<String, Object> requestBody = Map.of(
                "model", modelName,
                "messages", List.of(
                        Map.of("role", "user", "content", buildPrompt(commentText))
                ),
                "max_tokens", 300,
                "temperature", 0.2
        );

        try {
            // Call Hugging Face API
            String rawResponse = webClient.post()
                    .uri(modelUrl)
                    .header("Authorization", "Bearer " + apiToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("HF RAW RESPONSE: " + rawResponse);

            // Extract JSON object from HF response
            return extractJson(rawResponse);

        } catch (Exception e) {
            System.err.println("Error calling Hugging Face API: " + e.getMessage());
            return null;
        }
    }

    /**
     * Build the prompt to send to the model
     */
    private String buildPrompt(String commentText) {
        return """
                You are a support ticket classifier. Analyze the user comment below and respond ONLY with a valid JSON object. No explanation, no extra text, no markdown.

                Comment: "%s"

                Rules:
                - If the comment is a compliment or general feedback with no issue, set isTicket to false and leave other fields empty strings.
                - If it describes a bug, problem, request, or complaint, set isTicket to true and fill all fields.

                Respond with exactly this JSON structure:
                {
                  "isTicket": true or false,
                  "title": "short title here",
                  "category": "bug or feature or billing or account or other",
                  "priority": "low or medium or high",
                  "summary": "one sentence summary"
                }
                """.formatted(commentText);
    }

    /**
     * Extract JSON object from the raw Hugging Face response
     */
    private JsonNode extractJson(String rawResponse) throws Exception {
        if (rawResponse == null || rawResponse.isBlank()) {
            throw new RuntimeException("Empty response from Hugging Face API");
        }

        // HF returns OpenAI-like format
        JsonNode root = objectMapper.readTree(rawResponse);
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            throw new RuntimeException("No choices found in HF response");
        }

        String content = choices.get(0).path("message").path("content").asText();

        // Extract JSON from string
        Pattern pattern = Pattern.compile("\\{[\\s\\S]*\\}");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return objectMapper.readTree(matcher.group());
        }

        throw new RuntimeException("No valid JSON found in response: " + content);
    }
}