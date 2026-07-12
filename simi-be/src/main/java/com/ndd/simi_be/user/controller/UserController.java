package com.ndd.simi_be.user.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.user.dto.response.UserResponse;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(
            @AuthenticationPrincipal User user
    ){
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status(200)
                .body(userService.getMe(user))
                .build();

        return ResponseEntity.ok(response);
    }
}
