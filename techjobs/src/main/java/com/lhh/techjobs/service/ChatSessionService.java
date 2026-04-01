package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.request.CareerRequest;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.ChatSession;
import com.lhh.techjobs.entity.Message;
import com.lhh.techjobs.enums.Sender;
import com.lhh.techjobs.repository.CandidateRepository;
import com.lhh.techjobs.repository.ChatSessionRepository;
import com.lhh.techjobs.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatSessionService {
    private final CandidateRepository candidateRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public ChatSession createChatSession(CareerRequest careerRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Candidate candidate = candidateRepository.findByUserEmail(email);

        ChatSession chatSession = chatSessionRepository.findById(careerRequest.getChatSessionId()).orElse(null);

        LocalDateTime now = LocalDateTime.now();

        if(chatSession == null) {
            chatSession = ChatSession.builder()
                    .candidate(candidate)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            chatSessionRepository.save(chatSession);
        }

        Message message = Message.builder()
                .chatSession(chatSession)
                .sender(Sender.USER)
                .content(careerRequest.getContent())
                .createdAt(now)
                .build();
        messageRepository.save(message);

        return chatSession;
    }

    public void save(ChatSession chatSession) {
        chatSessionRepository.save(chatSession);
    }

}
