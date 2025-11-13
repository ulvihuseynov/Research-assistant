package org.ideaprojects.researchassistant;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ResearchService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    public ResearchService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.build();
    }

    public String processContent(ResearchRequest request) {
        String prompt = buildPrompt(request);

        Map<String,Object> requestBody=Map.of(
                "contents",new Object []{
                        Map.of(
                                "parts",new Object[]{
                                        Map.of("text",prompt)
                                }
                        )
                }
        );

        String response=webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return extractTextFromResponse(response);
    }

    private String extractTextFromResponse(String response) {
        try {
                GeminiResponse geminiResponse=objectMapper.readValue(response,GeminiResponse.class);
                if (geminiResponse.getCandidates() !=null && !geminiResponse.getCandidates().isEmpty()){
                    GeminiResponse.Candidate firstCandidate=geminiResponse.getCandidates().getFirst();
                    if (firstCandidate.getContent()!=null &&
                        firstCandidate.getContent().getParts() != null &&
                        !firstCandidate.getContent().getParts().isEmpty()){

                        return firstCandidate.getContent().getParts().getFirst().getText();
                    }
                }
                return "No content found in response";
        } catch (Exception e) {
           return "Error parsing " + e.getMessage();

        }
    }

    private  String buildPrompt(ResearchRequest request){
        StringBuilder prompt=new StringBuilder();
        switch (request.getOperation()){
            case "summarize":
                prompt.append("Summarize this text: ");

                break;
            case "suggest":
                prompt.append("Give me research suggestions about: ");

                break;
            case "translate":
                prompt.append("Translate this text into English: ");
                break;
            default:
                throw new IllegalArgumentException("UnKnown Operation " + request.getOperation());
        }
        prompt.append(request.getContent());
        return prompt.toString();
    }
}
