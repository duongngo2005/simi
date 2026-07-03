package com.ndd.simi_be.user.mapper;

import com.ndd.simi_be.user.dto.response.UserResponse;
import com.ndd.simi_be.user.entity.User;

public class UserMapper {
    public static UserResponse toUserResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .avatarUrl(user.getAvatarUrl())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
