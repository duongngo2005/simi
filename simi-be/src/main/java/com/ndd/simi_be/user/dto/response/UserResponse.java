package com.ndd.simi_be.user.dto.response;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String role;
    private String status;
    private String avatarUrl;
    private String address;
    private String phoneNumber;
}
