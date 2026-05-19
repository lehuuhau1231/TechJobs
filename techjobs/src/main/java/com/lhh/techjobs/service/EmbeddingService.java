package com.lhh.techjobs.service;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            EmbedContentConfig config = EmbedContentConfig.builder()
                    .outputDimensionality(768)
                    .build();

            EmbedContentResponse response = client.models.embedContent(embeddingModel, text, config);
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

    public List<float[]> getEmbeddingsBatch(List<String> texts) {
        try {
            EmbedContentConfig config = EmbedContentConfig.builder()
                    .outputDimensionality(768)
                    .build();

            EmbedContentResponse response = client.models.embedContent(embeddingModel, texts, config);

            if (response != null && response.embeddings().isPresent()) {
                List<ContentEmbedding> embeddings = response.embeddings().get();

                return embeddings.stream().map(emb -> {
                    List<Float> values = emb.values().orElse(new ArrayList<>());
                    float[] result = new float[values.size()];
                    for (int i = 0; i < values.size(); i++) {
                        result[i] = values.get(i);
                    }
                    return result;
                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Lỗi khi gọi Batch Embedding", e);
            throw new RuntimeException("Batch embedding failed", e);
        }
        return new ArrayList<>();
    }
}
