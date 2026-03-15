package com.lhh.techjobs.service;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmbeddingService {
    @Value("${embedding.model}")
    private String embeddingModel;

    private final Client client;

    public EmbeddingService(@Value("${gemini.api.key}") String apiKey) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public float[] getEmbedding(String text) {
        try {
            EmbedContentConfig config = EmbedContentConfig.builder().build();

            EmbedContentResponse response = client.models.embedContent(embeddingModel, text, config);
            System.out.println("response: " + response);
            if (response != null && response.embeddings() != null && response.embeddings().isPresent()) {
                List<ContentEmbedding> embeddings = response.embeddings().get();

                if (!embeddings.isEmpty()) {
                    List<Float> values = embeddings.getFirst().values().orElse(null);

                    float[] result = new float[values.size()];
                    for (int i = 0; i < values.size(); i++) {
                        result[i] = values.get(i).floatValue();
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("Error creating embedding", e);
            throw new RuntimeException("Error creating embedding", e);
        }
        return new float[0];
    }
}
