package com.ndd.simi_be.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ndd.simi_be.user.dto.response.UserResponse;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenResponse {
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private UserResponse userResponse;

    @JsonIgnore
    private String refreshToken;
}
