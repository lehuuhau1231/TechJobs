package com.lhh.techjobs.dto.request;

import com.lhh.techjobs.enums.StatsPeriod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RevenueStatsRequest {
    @NotNull(message = "Giai đoạn không được để trống")
    private StatsPeriod period;
    private Integer year;
    private Integer fromYear;
    private Integer toYear;
}
