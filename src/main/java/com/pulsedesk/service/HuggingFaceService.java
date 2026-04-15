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

    public JsonNode analyzeComment(String commentText) {
        Map<String, Object> requestBody = Map.of(
            "model", modelName,
            "messages", List.of(
                Map.of("role", "user", "content", buildPrompt(commentText))
            ),
            "max_tokens", 300,
            "temperature", 0.2
        );

        try {
            String rawResponse = webClient.post()
                .uri(modelUrl)
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .header("x-wait-for-model", "true")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            System.out.println("HF RAW RESPONSE: " + rawResponse);
            return extractJson(rawResponse);

        } catch (Exception e) {
    System.err.println("Error calling Hugging Face API: " + e.getMessage());
    com.fasterxml.jackson.databind.node.ObjectNode fallbackJson = objectMapper.createObjectNode();
    fallbackJson.put("isTicket", true);
    fallbackJson.put("title", "AI Fallback: " + commentText);
    fallbackJson.put("category", "BUG");
    fallbackJson.put("priority", "HIGH");
    fallbackJson.put("summary", "Fallback activated due to: " + e.getMessage());
    
    return fallbackJson;
    }

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

    private JsonNode extractJson(String rawResponse) throws Exception {
        JsonNode root = objectMapper.readTree(rawResponse);
        String content = root.path("choices").get(0).path("message").path("content").asText();

        Pattern pattern = Pattern.compile("\\{[\\s\\S]*\\}");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return objectMapper.readTree(matcher.group());
        }

        throw new RuntimeException("No JSON found in response: " + content);
    }
}