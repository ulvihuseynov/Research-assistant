package org.ideaprojects.researchassistant;


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

    public ResearchService(WebClient.Builder webClientBuilder) {
        this.webClient = WebClient.builder().build();
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

        } catch (Exception e) {
           return "Error parsing " + e.getMessage();

        }
        return null;
    }

    private  String buildPrompt(ResearchRequest request){
        StringBuilder prompt=new StringBuilder();
        switch (request.getOperation()){
            case "summarize":
                prompt.append("what;s your name");
                break;
            case "suggest":
                prompt.append("suggest");
                break;
            default:
                throw new IllegalArgumentException("UnKnown Operation " + request.getOperation());
        }
        prompt.append(request.getContent());
        return prompt.toString();
    }
}
