package com.lhh.techjobs.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponse {
    private Integer id;
    private Integer amount;
    private Integer jobId;
}
