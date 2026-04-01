package com.lhh.techjobs.dto.response;

import com.lhh.techjobs.enums.Sender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String content;
    private LocalDateTime createdAt;
    private Sender sender;
}
