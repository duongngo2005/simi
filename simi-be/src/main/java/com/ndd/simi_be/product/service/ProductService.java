package com.ndd.simi_be.product.service;

import com.ndd.simi_be.brand.entity.Brand;
import com.ndd.simi_be.brand.repository.BrandRepository;
import com.ndd.simi_be.category.entity.Category;
import com.ndd.simi_be.category.repository.CategoryRepository;
import com.ndd.simi_be.cloudinary.CloudinaryResponse;
import com.ndd.simi_be.cloudinary.CloudinaryService;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.repository.ProductImageRepository;
import com.ndd.simi_be.product.dto.ProductRequest;
import com.ndd.simi_be.product.dto.ProductResponse;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.entity.ProductImage;
import com.ndd.simi_be.product.mapper.ProductMapper;
import com.ndd.simi_be.product.repository.ProductRepository;
import com.ndd.simi_be.tag.entity.Tag;
import com.ndd.simi_be.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TagService tagService;
    private final CloudinaryService cloudinaryService;
    private final ProductImageRepository productImageRepository;

    @Transactional
    public Product createProduct(ProductRequest request){
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        Brand brand = null;
        if (request.getBrandId() != null){
            brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu"));
        }
        List<Tag> tags = tagService.createTagOrFind(request.getTagNames());

        Product product = Product.builder()
                .color(request.getColor())
                .description(request.getDescription())
                .name(request.getName())
                .size(request.getSize())
                .category(category)
                .brand(brand)
                .build();
        return productRepository.save(product);
    }

    @Transactional
    public Product createProduct(
            ProductRequest request,
            MultipartFile thumbnailFile,
            List<MultipartFile> imageFiles){
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        Brand brand = null;
        if (request.getBrandId() != null){
            brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu"));
        }
        List<Tag> tags = tagService.createTagOrFind(request.getTagNames());

        Product product = Product.builder()
                .color(request.getColor())
                .description(request.getDescription())
                .name(request.getName())
                .size(request.getSize())
                .category(category)
                .brand(brand)
                .productCondition(request.getProductCondition())
                .tags(tags)
                .build();

        product = productRepository.save(product);

        CloudinaryResponse thumb = cloudinaryService.uploadImage(thumbnailFile);
        ProductImage thumbnail = ProductImage.builder()
                .thumbnail(true)
                .product(product)
                .imagePublicId(thumb.getPublicId())
                .imageUrl(thumb.getUrl())
                .build();
        product.getProductImages().add(thumbnail);
        productImageRepository.save(thumbnail);

        for (MultipartFile file : imageFiles){
            CloudinaryResponse response = cloudinaryService.uploadImage(file);
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(response.getUrl())
                    .imagePublicId(response.getPublicId())
                    .thumbnail(false)
                    .build();
            productImageRepository.save(image);
            product.getProductImages().add(image);
        }

        return product;
    }

    @Transactional
    public void hardDeleteProduct(Product product){
        List<ProductImage> productImages = product.getProductImages();
        for (ProductImage productImage : productImages){
            cloudinaryService.deleteImage(productImage.getImagePublicId());
            productImageRepository.delete(productImage);
        }
        productRepository.delete(product);
    }
}
