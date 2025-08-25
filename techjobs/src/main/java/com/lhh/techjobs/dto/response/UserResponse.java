package com.lhh.techjobs.dto.response;

import com.lhh.techjobs.enums.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Role role;
    private String avatar;
}
