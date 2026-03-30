package com.pulsedesk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.Pattern;

@Service

public class HuggingFaceService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.model.url}")
    private String modelUrl;

    public HuggingFaceService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public JsonNode analyzeComment(String commentText) {
        String prompt = buildPrompt(commentText);

        Map<String , Object> requestBody = Map.of(
            "inputs", prompt,
            "parameters", Map.of(
                "max_new_tokens", 300,
                "temperature", 0.2,
                "return_full_text", false
            )
        );
        try{
            String rawResponse = webClient.post()
                .uri(modelUrl)
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return extractJson(rawResponse);
        }
        catch (Exception e) {
            System.err.println("Error calling Hugging Face API: " + e.getMessage());
            return null;
        }
    }
    private String buildPrompt(String commentText) {
        return """
            <s>[INST] You are a support ticket classifier. Analyze the user comment below and respond ONLY with a valid JSON object. No explanation, no extra text.

            Comment: "%s"

            Rules:
            - If the comment is a compliment or general feedback with no issue, set isTicket to false and leave other fields empty.
            - If it describes a bug, problem, request, or complaint, set isTicket to true and fill all fields.

            Respond with exactly this JSON structure:
            {
              "isTicket": true or false,
              "title": "short title here",
              "category": "bug or feature or billing or account or other",
              "priority": "low or medium or high",
              "summary": "one sentence summary"
            }
            [/INST]
            """.formatted(commentText);
    }
    private JsonNode extractJson(String rawResponse) throws Exception {
        JsonNode responseArray = objectMapper.readTree(rawResponse);
        String generatedText = responseArray.get(0).get("generated_text").asText();

        Pattern pattern = Pattern.compile("\\{[\\s\\S]*\\}");
        Matcher matcher = pattern.matcher(generatedText);

        if (matcher.find()) {
            String jsonString = matcher.group();
            return objectMapper.readTree(jsonString);
        } else {
            throw new RuntimeException("No JSON found in model response: " + generatedText);
        }
    }
}
