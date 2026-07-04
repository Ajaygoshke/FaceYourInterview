package com.FaceYourInterview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    // Direct entry point used across InterviewService
    public String generate(String prompt) {
        return askGemini(prompt);
    }

    public String analyzeResume(String resumeText) {
        try {
            String prompt = """
                    Analyze the following resume and provide:

                    1. Candidate Summary
                    2. Skills
                    3. Strengths
                    4. Weaknesses
                    5. ATS Score out of 100
                    6. Improvement Suggestions

                    Resume:
                    """ + resumeText;

            return askGemini(prompt);
        } catch (Exception e) {
            throw new RuntimeException("Gemini API Error during analysis: " + e.getMessage(), e);
        }
    }

    public String askGemini(String prompt) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            ObjectNode content = root.putArray("contents").addObject();
            ObjectNode parts = content.putArray("parts").addObject();

            parts.put("text", prompt);

            String requestBody = objectMapper.writeValueAsString(root);
            String url = apiUrl + "?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = executeWithRetry(() ->
                    restTemplate.exchange(url, HttpMethod.POST, entity, String.class)
            );

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode candidates = rootNode.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new RuntimeException("No response choices found from Gemini payload array mapping.");
            }

            return candidates
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Gemini Network Gateway Execution Error: " + e.getMessage(), e);
        }
    }

    private ResponseEntity<String> executeWithRetry(java.util.function.Supplier<ResponseEntity<String>> apiCall) {
        int maxRetries = 4;
        long delay = 2000;

        for (int i = 0; i < maxRetries; i++) {
            try {
                return apiCall.get();
            } catch (HttpClientErrorException.TooManyRequests e) {
                if (i == maxRetries - 1) {
                    throw e;
                }

                System.out.println("[Gemini API] 429 Rate Limit Encountered. Retrying execution pointer in " + (delay / 1000) + "s...");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                delay *= 2;
            }
        }
        throw new RuntimeException("Failed to contact Gemini API due to rate limits after multiple attempts");
    }
}