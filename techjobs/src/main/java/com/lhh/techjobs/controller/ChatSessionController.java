package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.response.MessageResponse;
import com.lhh.techjobs.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat-session")
@RequiredArgsConstructor
@Slf4j
public class ChatSessionController {
    private final MessageService messageService;

    @GetMapping("/{chatSessionId}")
    public List<MessageResponse> getTop10RecentMessages(@PathVariable Integer chatSessionId) {
        return messageService.getTop10RecentMessages(chatSessionId);
    }


}
