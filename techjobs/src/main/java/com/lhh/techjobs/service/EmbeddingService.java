package com.lhh.techjobs.service;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {
    private final Client client;

    public EmbeddingService(@Value("${gemini.api.key}") String apiKey) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public float[] getEmbedding(String text) {
        try {
            EmbedContentConfig config = EmbedContentConfig.builder().build();

            // Gọi API embedContent với cú pháp đúng
            EmbedContentResponse response = client.models.embedContent("models/embedding-001", text, config);

            // Gemini API trả về embedding trong response object
            if (response != null && response.embeddings() != null && response.embeddings().isPresent()) {
                List<ContentEmbedding> embeddings = response.embeddings().get();

                if (!embeddings.isEmpty()) {
                    // Lấy embedding đầu tiên
                    List<Float> values = embeddings.getFirst().values().orElse(null);

                    // Chuyển sang mảng float[]
                    float[] result = new float[values.size()];
                    for (int i = 0; i < values.size(); i++) {
                        result[i] = values.get(i).floatValue();
                    }
                    return result;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo embedding: " + e.getMessage(), e);
        }
        return new float[0];
    }
}
