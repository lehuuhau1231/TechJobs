package com.lhh.techjobs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.lhh.techjobs.dto.response.GenerateQuestionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GeminiService {
    private final Client client;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                  "title": "string",
                  "skills": ["string", "..."],
                  "education": "string",
                  "major": "string",
                  "experience": "string",
                  "preferred_location": "string"
                }
                Yêu cầu chuẩn hóa:
                    - preferred_location: viết thường toàn bộ (lowercase), loại bỏ khoảng trắng thừa, chỉ trả về chuẩn tên thành phố nghĩa là sẽ không có tên đường, quận,..., ví dụ "Hồ Chí Minh", "Hồ chí minh", "   hO CHi minh   " → "hồ chí minh".
                    - preferred_salary: chỉ lấy số hoặc khoảng số, loại bỏ chữ/thông tin dư thừa.
                Không giải thích gì thêm. Không dùng markdown. Không thêm text ngoài JSON.
                """.formatted(cvText);

            return callGeminiAPI(prompt);
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException(e);
        }
    }

    public GenerateQuestionResponse generateQuestion(String cvInfo, String userQuery, List<String> chatHistory,
                                                      String currentWorkStyles, String currentCharacterTraits,
                                                      String currentInterests, int questionCount, int maxQuestions) {
        try {
            String accumulatedState = String.format("""
                    Thông tin đã trích xuất từ các lượt trước:
                    - workStyles: %s
                    - characterTraits: %s
                    - interests: %s
                    - Số câu hỏi đã hỏi: %d / %d (tối đa)
                    """,
                    currentWorkStyles != null ? currentWorkStyles : "chưa xác định",
                    currentCharacterTraits != null ? currentCharacterTraits : "chưa xác định",
                    currentInterests != null ? currentInterests : "chưa xác định",
                    questionCount, maxQuestions);

            String prompt = """
                    Bạn là một chatbot tư vấn hướng nghiệp chuyên nghiệp.
                    Mục tiêu của bạn là hiểu rõ tính cách, sở thích và phong cách làm việc của người dùng.
                    
                    THÔNG TIN ĐÃ TÍCH LŨY:
                    %s
                    
                    QUY TẮC QUAN TRỌNG:
                    1. KHÔNG BAO GIỜ xóa hoặc ghi đè thông tin đã trích xuất trước đó. Nếu workStyles, characterTraits hoặc interests đã có giá trị, BẮT BUỘC phải giữ lại và chỉ BỔ SUNG thêm nếu có thông tin mới.
                    2. CHỈ hỏi 1 câu hỏi mỗi lần. Câu hỏi phải giúp làm rõ một trong các yếu tố CHƯA được xác định:
                        + Tính cách (characterTraits): phân tích, sáng tạo, tỉ mỉ, hướng nội/hướng ngoại
                        + Sở thích (interests): dữ liệu, con người, thiết kế, kinh doanh
                        + Phong cách làm việc (workStyles): làm việc nhóm, làm việc độc lập, môi trường nhanh, ổn định
                    3. Câu hỏi phải ngắn gọn, rõ ràng, dễ trả lời, có thể gợi ý cho người dùng một vài ý để trả lời dễ dàng hơn.
                    4. KHÔNG hỏi lại những gì đã hỏi trước đó trong lịch sử chat.
                    5. Nếu người dùng trả lời mơ hồ, hãy suy luận hợp lý từ câu trả lời thay vì hỏi lại.
                    
                    QUY TẮC KẾT THÚC (isReady = true):
                    - Nếu cả 3 giá trị workStyles, characterTraits, interests đều đã có thông tin rõ ràng → isReady = true
                    - Nếu đã đạt %d câu hỏi (tối đa) → BẮT BUỘC isReady = true, dù thông tin chưa đầy đủ. Hãy suy luận từ thông tin có sẵn.
                    
                    Khi isReady = true, trả về:
                    {
                      "question": null,
                      "summary": "tính cách: <tóm tắt> | sở thích: <tóm tắt> | phong cách làm việc: <tóm tắt>",
                      "isReady": true,
                      "characterTraits": "<giá trị cuối cùng>",
                      "interests": "<giá trị cuối cùng>",
                      "workStyles": "<giá trị cuối cùng>"
                    }
                    
                    Khi isReady = false, trả về:
                    {
                      "question": "<câu hỏi tiếp theo>",
                      "summary": null,
                      "isReady": false,
                      "characterTraits": "<giá trị hiện tại hoặc cập nhật, KHÔNG ĐƯỢC null nếu trước đó đã có giá trị>",
                      "interests": "<giá trị hiện tại hoặc cập nhật, KHÔNG ĐƯỢC null nếu trước đó đã có giá trị>",
                      "workStyles": "<giá trị hiện tại hoặc cập nhật, KHÔNG ĐƯỢC null nếu trước đó đã có giá trị>"
                    }
                    
                    Nếu người dùng có chào, thì bạn phải lịch sự chào lại cùng với dẫn dắt vào câu hỏi.
                    
                    Quy tắc về định dạng output:
                        - CHỈ trả về JSON hợp lệ
                        - KHÔNG thêm markdown, KHÔNG dùng ```json hoặc ```
                        - Output phải bắt đầu bằng ký tự { và kết thúc bằng }
                    
                    Dữ liệu:
                    - Hồ sơ: %s
                    - Lịch sử: %s
                    - Trả lời user: %s
                    """.formatted(accumulatedState, maxQuestions, cvInfo, chatHistory, userQuery);

            String generateQuestion = callGeminiAPI(prompt);
            return objectMapper.readValue(generateQuestion, GenerateQuestionResponse.class);
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException(e);
        }
    }

    public String generateAnswer(List<String> careerDescription, String userSummary) {
        String prompt = """
                Bạn là một người anh/chị đi trước trong ngành công nghệ, đang nhắn tin tư vấn cho một bạn trẻ đang tìm hướng đi nghề nghiệp.

                HỒ SƠ NGƯỜI DÙNG (tóm tắt từ cuộc trò chuyện):
                %s

                DANH SÁCH NGHỀ PHÙ HỢP (từ hệ thống gợi ý):
                %s

                CÁCH VIẾT:
                - Viết như đang nhắn tin tư vấn 1-1, thân thiện, có cảm xúc, không công thức.
                - Mở đầu bằng 1 câu nhận xét tổng quan về profile của người dùng (dựa trên hồ sơ ở trên), tạo cảm giác "mình hiểu bạn".
                - Với mỗi nghề gợi ý:
                  + Giải thích TẠI SAO nghề đó hợp với CHÍNH người này (liên hệ cụ thể tới tính cách / sở thích / kỹ năng của họ, KHÔNG nói chung chung).
                  + Đưa ra 1 gợi ý hành động cụ thể, thực tế (ví dụ: "Thử build một project nhỏ dùng X", "Tìm hiểu về Y trên Z").
                  + Mỗi nghề viết 2-4 câu, KHÔNG dài hơn.
                - Kết thúc bằng 1 câu động viên hoặc khích lệ ngắn gọn.

                TUYỆT ĐỐI KHÔNG:
                - Dùng format "Tên nghề: Lý do. Gợi ý: ..." lặp đi lặp lại — hãy viết đa dạng, mỗi nghề một kiểu diễn đạt khác nhau.
                - Bịa thêm kỹ năng hoặc thông tin mà dữ liệu không có.
                - Dùng giọng văn cứng nhắc, hàn lâm, hay quá formal.

                ĐỊNH DẠNG:
                - Trả về HTML (dùng <p>, <strong>, <em>, <br> — KHÔNG dùng <h1>-<h6>, KHÔNG dùng <ul>/<ol> trừ khi thực sự cần).
                - KHÔNG bọc trong ```html``` hay markdown.
                - Giữ cho gọn gàng, dễ đọc trên giao diện chat.

                Hãy trả lời bằng tiếng Việt.
                """.formatted(userSummary, careerDescription);

        return callGeminiAPI(prompt);
    }

    private String callGeminiAPI(String prompt) {
        try {
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );
            return response.text();
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException(e);
        }
    }
}
