package com.ndd.simi_be.user.service;

import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.user.dto.request.UpdateBankInformationRequest;
import com.ndd.simi_be.user.dto.response.UserResponse;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.mapper.UserMapper;
import com.ndd.simi_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getMe(User user){
        return UserMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateBankInformation(UpdateBankInformationRequest request, User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        managedUser.setBankName(request.getBankName().trim());
        managedUser.setAccountNumber(request.getAccountNumber().trim());
        managedUser.setAccountHolder(request.getAccountHolder().trim());
        return UserMapper.toUserResponse(managedUser);
    }
}
