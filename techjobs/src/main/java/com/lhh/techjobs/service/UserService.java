package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.response.UserResponse;
import com.lhh.techjobs.entity.User;
import com.lhh.techjobs.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    public String getAvatar(int id) {
        return userRepository.getAvatar(id);
    }

    public UserResponse getUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(email);
        return UserResponse.builder()
                .role(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }
}
