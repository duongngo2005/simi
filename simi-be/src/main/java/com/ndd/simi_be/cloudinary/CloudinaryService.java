package com.ndd.simi_be.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ndd.simi_be.common.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryResponse uploadImage(MultipartFile image){
        if (image == null || image.isEmpty()){
            throw new AppException(HttpStatus.BAD_REQUEST, "Ảnh không được để trống");
        }
        try{
            Map<?, ?> result = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "simi/images",
                            "resource_type", "image",
                            "allowed_formats", List.of("jpg", "jpeg", "png", "webp")
                    )
            );

            return CloudinaryResponse.builder()
                    .url(result.get("secure_url").toString())
                    .publicId(result.get("public_id").toString())
                    .build();
        } catch (IOException ex){
            throw new AppException(HttpStatus.BAD_GATEWAY, "Upload ảnh thất bại");
        }
    }

    public void deleteImage(String publicId){
        if (publicId == null || publicId.isBlank()){
            return;
        }

        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    )
            );
        } catch (IOException e) {
            throw new AppException(HttpStatus.BAD_GATEWAY, "Xóa ảnh không thành công");
        }
    }
}
