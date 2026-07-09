package com.ndd.simi_be.cloudinary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CloudinaryResponse {
    private String url;
    private String publicId;
}
