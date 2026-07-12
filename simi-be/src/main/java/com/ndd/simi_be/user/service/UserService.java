package com.ndd.simi_be.user.service;

import com.ndd.simi_be.user.dto.response.UserResponse;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.mapper.UserMapper;
import com.ndd.simi_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getMe(User user){
        return UserMapper.toUserResponse(user);
    }
}
