package com.lhh.techjobs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.lhh.techjobs.dto.response.GenerateQuestionResponse;
import com.lhh.techjobs.dto.response.JobModerationResponse;
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
        log.info("GeminiService apiKey: {}", apiKey);
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
                  "experience": "Tóm tắt ngắn gọn các dự án, vị trí và công nghệ chủ chốt đã làm",
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
                                                      String currentSoftSkill, int questionCount, int maxQuestions) {
        try {
            String accumulatedState = String.format("""
                    Thông tin đã trích xuất từ các lượt trước:
                    - softSkill: %s
                    - Số câu hỏi đã hỏi: %d / %d (tối đa)
                    """,
                    currentSoftSkill != null ? currentSoftSkill : "chưa xác định",
                    questionCount, maxQuestions);

            String prompt = """
                    Bạn là một chatbot tư vấn hướng nghiệp chuyên nghiệp.
                    Mục tiêu của bạn là hiểu rõ kỹ năng mềm (soft_skill) của người dùng để gợi ý công việc phù hợp.
                    
                    THÔNG TIN ĐÃ TÍCH LŨY:
                    %s
                    
                    QUY TẮC QUAN TRỌNG:
                    1. KHÔNG BAO GIỜ xóa hoặc ghi đè thông tin đã trích xuất trước đó. Nếu softSkill đã có giá trị, BẮT BUỘC phải giữ lại và chỉ BỔ SUNG thêm nếu có thông tin mới.
                    2. CHỈ hỏi 1 câu hỏi mỗi lần. Câu hỏi phải giúp làm rõ về các kỹ năng mềm mà người dùng sở hữu (ví dụ: giao tiếp, làm việc nhóm, giải quyết vấn đề, quản lý thời gian, tư duy logic...).
                    3. Câu hỏi phải ngắn gọn, rõ ràng, dễ trả lời, có thể gợi ý cho người dùng một vài ý để trả lời dễ dàng hơn.
                    4. KHÔNG hỏi lại những gì đã hỏi trước đó trong lịch sử chat.
                    5. Nếu người dùng trả lời mơ hồ, hãy suy luận hợp lý từ câu trả lời thay vì hỏi lại.
                    
                    QUY TẮC KẾT THÚC (isReady = true):
                    - Nếu trong câu trả lời hiện tại hoặc trong lịch sử chat, người dùng đã nói về kỹ năng mềm của mình (ví dụ: tự mô tả bản thân có kỹ năng gì, hoặc trả lời câu hỏi về kỹ năng mềm) → LẬP TỨC isReady = true, không cần đặt thêm câu hỏi.
                    - Nếu đã đạt %d câu hỏi (tối đa) → BẮT BUỘC isReady = true, dù thông tin chưa đầy đủ. Hãy suy luận từ thông tin có sẵn.
                    
                    Khi isReady = true, trả về:
                    {
                      "question": null,
                      "summary": "kỹ năng mềm: <tóm tắt kỹ năng mềm>",
                      "isReady": true,
                      "softSkill": "Phải trả về String <giá trị cuối cùng>"
                    }
                    
                    Khi isReady = false, trả về:
                    {
                      "question": "<câu hỏi tiếp theo>",
                      "summary": null,
                      "isReady": false,
                      "softSkill": "Phải trả về String <giá trị hiện tại hoặc cập nhật, KHÔNG ĐƯỢC null nếu trước đó đã có giá trị>"
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
            log.info("Raw response from Gemini API: {}", generateQuestion);
            return objectMapper.readValue(generateQuestion, GenerateQuestionResponse.class);
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException(e);
        }
    }

    public String generateAnswer(List<String> careerDescription, String userSummary) {
        String prompt = """
            Bạn là một chatbot tư vấn hướng nghiệp chuyên nghiệp, thấu cảm và nhạy bén.

            ### HỒ SƠ NGƯỜI DÙNG:
            %s

            ### DANH SÁCH CÔNG VIỆC GỢI Ý (Dữ liệu gốc):
            %s

            ### NHIỆM VỤ CỦA BẠN:
            1. **Nhận xét cá nhân hóa**: Bắt đầu bằng một nhận xét ngắn gọn, ấm áp về thế mạnh hoặc đặc điểm nổi bật nhất trong hồ sơ người dùng.
            2. **Tư vấn chuyên sâu**: Với mỗi công việc gợi ý, hãy phân tích sự tương thích dựa trên kỹ năng/tính cách cụ thể của họ.
            3. **Chèn liên kết thông minh**: Tên công việc PHẢI được gắn link theo định dạng Markdown: [title](URL).
            4. **Độ chính xác của Title**: Bạn KHÔNG ĐƯỢC thay đổi bất kỳ ký tự nào trong field 'Title' mà tôi đã cung cấp. Không được tóm tắt hay dịch lại tên công việc.

            ### YÊU CẦU VỀ GIỌNG VĂN & ĐỊNH DẠNG:
            - **Phong cách**: Nhắn tin thân thiện. Tuyệt đối không dùng danh sách gạch đầu dòng lặp đi lặp lại.
            - **Định dạng**: Sử dụng hoàn toàn văn bản thuần kết hợp ký hiệu Markdown (in đậm **, xuống dòng, và link [text](url)).
            - **Độ dài**: Mỗi gợi ý chỉ từ 2-3 câu tập trung vào "điểm chạm" giữa người và nghề.
            - **An toàn**: Không bịa đặt thông tin. Nếu trong danh sách nghề không có URL, hãy chỉ để tên nghề in đậm.

            ### QUY TẮC NGHIÊM NGẶT:
            - KHÔNG trả về các thẻ HTML như <p>, <a>, <div>.
            - KHÔNG bọc toàn bộ câu trả lời trong block code (như ```markdown hoặc ```html).
            - Trả về nội dung sạch để hiển thị trực tiếp.

            Hãy trả lời bằng tiếng Việt.
            """.formatted(userSummary, String.join("\n- ", careerDescription));

        return callGeminiAPI(prompt);
    }

    public JobModerationResponse moderateJobPosting(String title, String description, String jobRequire, String benefits) {
        try {
            String prompt = """
                Bạn là một chuyên gia kiểm duyệt nội dung tuyển dụng.
                Nhiệm vụ của bạn là kiểm tra xem bài đăng tuyển dụng dưới đây có hợp lệ hay không.
                Các tiêu chí TỪ CHỐI bao gồm:
                - Chứa nội dung lừa đảo, đa cấp, hoặc không rõ ràng về bản chất công việc.
                - Chứa ngôn từ kích động, phân biệt đối xử, hoặc không phù hợp với văn hóa.
                - Bài đăng quá ngắn, spam, hoặc thiếu các thông tin cơ bản về công việc (ví dụ: mô tả công việc vô nghĩa).
                - Mức lương, yêu cầu hoặc quyền lợi quá phi lý.
                
                NỘI DUNG BÀI ĐĂNG TUYỂN DỤNG CẦN KIỂM DUYỆT:
                - Tiêu đề: %s
                - Mô tả công việc: %s
                - Yêu cầu công việc: %s
                - Quyền lợi được hưởng: %s
                
                Hãy trả về kết quả dưới dạng JSON thuần (KHÔNG dùng markdown code block, KHÔNG có text thừa), theo đúng cấu trúc sau:
                {
                  "isApproved": true hoặc false,
                  "rejectReason": "Lý do từ chối chung, ngắn gọn bằng tiếng Việt để hiển thị banner chính (nếu isApproved là false, ngược lại để null).",
                  "fieldErrors": {
                    "title": "Lý do từ chối cụ thể cho trường Tiêu đề bằng tiếng Việt nếu phát hiện vi phạm ở trường này, ngược lại để null.",
                    "description": "Lý do từ chối cụ thể cho trường Mô tả công việc bằng tiếng Việt nếu phát hiện vi phạm ở trường này, ngược lại để null.",
                    "jobRequire": "Lý do từ chối cụ thể cho trường Yêu cầu bằng tiếng Việt nếu phát hiện vi phạm ở trường này, ngược lại để null.",
                    "benefits": "Lý do từ chối cụ thể cho trường Quyền lợi bằng tiếng Việt nếu phát hiện vi phạm ở trường này, ngược lại để null."
                  }
                }
                """.formatted(title, description, jobRequire, benefits);

            String responseText = callGeminiAPI(prompt);
            log.info("Job moderation response: {}", responseText);

            responseText = responseText.trim();
            if (responseText.startsWith("```")) {
                int firstNewLine = responseText.indexOf("\n");
                int lastBackticks = responseText.lastIndexOf("```");
                if (firstNewLine != -1 && lastBackticks != -1 && lastBackticks > firstNewLine) {
                    responseText = responseText.substring(firstNewLine + 1, lastBackticks).trim();
                }
            }
            
            return objectMapper.readValue(responseText, JobModerationResponse.class);
        } catch (Exception e) {
            log.error("Error calling Gemini API for job moderation", e);
            throw new RuntimeException("Lỗi khi kiểm duyệt bài đăng: " + e.getMessage(), e);
        }
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
