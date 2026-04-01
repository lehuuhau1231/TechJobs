package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.response.MessageResponse;
import com.lhh.techjobs.entity.ChatSession;
import com.lhh.techjobs.entity.Message;
import com.lhh.techjobs.enums.Sender;
import com.lhh.techjobs.repository.ChatSessionRepository;
import com.lhh.techjobs.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;
    public void createAndSaveMessage(ChatSession chatSession, String content) {

        if (chatSession == null) {
            log.error("ChatSession not found so cannot save message");
            return;
        }

        Message message = Message.builder()
                .content(content)
                .createdAt(LocalDateTime.now())
                .chatSession(chatSession)
                .sender(Sender.ASSISTANT)
                .build();

        messageRepository.save(message);
    }

    public List<MessageResponse> getTop10RecentMessages(Integer chatSessionId) {
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found with id: " + chatSessionId));

        return messageRepository.findTop10RecentMessage(chatSession, PageRequest.of(0, 10));
    }
}
