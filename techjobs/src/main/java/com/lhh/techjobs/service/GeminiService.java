package com.lhh.techjobs.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GeminiService {
    private final Client client;

    public GeminiService(@Value("${gemini.api.key}") String apiKey) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String extractInfoFromCV(String cvText) {
        try {
            // Prompt cho AI
            String prompt = """
                Bạn là một hệ thống phân tích CV.
                CV content: %s
                Hãy trích xuất thông tin từ CV dưới đây và TRẢ RA JSON THUẦN theo schema:
                {
                  "name": "string",
                  "title": "string",
                  "email": "string",
                  "phone": "string",
                  "skills": ["string", "..."],
                  "education": "string",
                  "experience": "string",
                  "preferred_location": "string",
                  "preferred_salary": "string"
                }
                Yêu cầu chuẩn hóa:
                    - preferred_location: viết thường toàn bộ (lowercase), loại bỏ khoảng trắng thừa, chỉ trả về chuẩn tên thành phố nghĩa là sẽ không có tên đường, quận,..., ví dụ "Hồ Chí Minh", "Hồ chí minh", "   hO CHi minh   " → "hồ chí minh".
                    - preferred_salary: chỉ lấy số hoặc khoảng số, loại bỏ chữ/thông tin dư thừa.
                Không giải thích gì thêm. Không dùng markdown. Không thêm text ngoài JSON.
                """.formatted(cvText);
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash", // model mới & nhanh
                    prompt,
                    null
            );

            return response.text(); // JSON string
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException(e);
        }
    }
}
