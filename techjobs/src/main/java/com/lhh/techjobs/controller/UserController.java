package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.response.ApiResponse;
import com.lhh.techjobs.dto.response.AuthenticationResponse;
import com.lhh.techjobs.dto.response.UserResponse;
import com.lhh.techjobs.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    @GetMapping("/avatar/{id}")
    public ResponseEntity<String> getAvatar(@PathVariable int id) {
        return ResponseEntity.ok(userService.getAvatar(id));
    }

    @GetMapping()
    public ResponseEntity<UserResponse> getUser() {
        return ResponseEntity.ok(userService.getUser());
    }
}
