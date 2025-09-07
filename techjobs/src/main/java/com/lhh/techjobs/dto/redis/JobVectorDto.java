package com.lhh.techjobs.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobVectorDto {
    private Integer id;
    private String title;
    private String description;
    private Integer salaryMin;
    private Integer salaryMax;
    private String jobLevelName;
    private String cityName;
    private String districtName;
    private String image;
    private List<String> skillNames;

    // Phương thức này tạo nội dung để vector hóa
    public String getVectorContent() {
        StringBuilder sb = new StringBuilder();
        if (title != null) sb.append(title).append(" ");
        if (description != null) sb.append(description).append(" ");
        if (jobLevelName != null) sb.append(jobLevelName).append(" ");
        if (cityName != null) sb.append(cityName).append(" ");
        if (districtName != null) sb.append(districtName).append(" ");
        if (skillNames != null && !skillNames.isEmpty()) {
            sb.append(String.join(" ", skillNames));
        }
        return sb.toString().trim();
    }
}
