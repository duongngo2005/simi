package com.ndd.simi_be.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatTurn {
    @NotBlank
    @Pattern(regexp = "^(user|ai)$", message = "Sender chỉ được là user hoặc ai")
    private String sender;

    @NotBlank
    @Size(max = 500, message = "Nội dung tin nhắn lịch sử tối đa 500 ký tự")
    private String text;
}