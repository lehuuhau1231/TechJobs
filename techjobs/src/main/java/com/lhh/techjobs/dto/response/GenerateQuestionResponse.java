package com.lhh.techjobs.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateQuestionResponse {
    private Boolean isReady;
    private String question;
    private String summary;
    private String characterTraits;
    private String interests;
    private String workStyles;
}
