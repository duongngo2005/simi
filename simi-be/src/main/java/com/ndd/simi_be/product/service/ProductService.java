package com.ndd.simi_be.product.service;

import com.ndd.simi_be.brand.entity.Brand;
import com.ndd.simi_be.brand.repository.BrandRepository;
import com.ndd.simi_be.category.entity.Category;
import com.ndd.simi_be.category.repository.CategoryRepository;
import com.ndd.simi_be.cloudinary.CloudinaryResponse;
import com.ndd.simi_be.cloudinary.CloudinaryService;
import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.product.enums.Gender;
import com.ndd.simi_be.product.repository.ProductImageRepository;
import com.ndd.simi_be.product.dto.request.ProductFilterRequest;
import com.ndd.simi_be.product.dto.request.ProductRequest;
import com.ndd.simi_be.product.dto.response.ProductDetailResponse;
import com.ndd.simi_be.product.dto.response.ProductImageResponse;
import com.ndd.simi_be.product.dto.response.ProductSummaryResponse;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.entity.ProductImage;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.product.mapper.ProductImageMapper;
import com.ndd.simi_be.product.mapper.ProductMapper;
import com.ndd.simi_be.product.repository.ProductRepository;
import com.ndd.simi_be.product.repository.ProductSpecification;
import com.ndd.simi_be.tag.entity.Tag;
import com.ndd.simi_be.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> searchProducts(
            ProductFilterRequest filterRequest
    ){
        List<Long> categoryIds = null;
        if (filterRequest.getCategorySlug() != null){
            Category parent = categoryRepository.findBySlug(filterRequest.getCategorySlug())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha"));

            categoryIds = new ArrayList<>();
            categoryIds.add(parent.getId());

            for (Category child : parent.getChildren()){
                categoryIds.add(child.getId());
            }
        }


        Specification<Product> specification = Specification.allOf(
                ProductSpecification.hasBrandId(filterRequest.getBrandId()),
                ProductSpecification.isAvailable(),
                categoryIds == null
                ? ProductSpecification.hasCategoryId(filterRequest.getCategoryId())
                : ProductSpecification.hasCategoryIdIn(categoryIds),
                ProductSpecification.hasProductCondition(filterRequest.getProductCondition()),
                ProductSpecification.hasColor(filterRequest.getColor()),
                ProductSpecification.hasSize(filterRequest.getSizeProduct()),
                ProductSpecification.hasKeyword(filterRequest.getKeyword()),
                ProductSpecification.hasMaxPrice(filterRequest.getMaxPrice()),
                ProductSpecification.hasMinPrice(filterRequest.getMinPrice()),
                ProductSpecification.hasGender(filterRequest.getGender()),
                ProductSpecification.hasMaterials(filterRequest.getMaterials())
        );

        Sort sort = filterRequest.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(filterRequest.getSortBy()).ascending()
                : Sort.by(filterRequest.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        return productRepository.findAll(specification, pageable).map(ProductMapper::toProductSummaryResponse);
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
                .gender(request.getGender() != null ? request.getGender() : Gender.UNISEX)
                .material(request.getMaterial())
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

        if (imageFiles != null){
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

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        return ProductMapper.toProductDetailResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductImageResponse getProductThumbnail(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        ProductImage thumbnail = product.getProductImages().stream()
                .filter(ProductImage::isThumbnail)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không có thumbnail"));

        return ProductImageMapper.toProductImageResponse(thumbnail);
    }

    @Transactional(readOnly = true)
    public ProductSummaryResponse getProductForPos(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        if (product.getProductStatus() != ProductStatus.AVAILABLE){
            throw new BadRequestException("Sản phẩm hiện không khả dụng để bán tại POS");
        }
        return ProductMapper.toProductSummaryResponse(product);
    }

    @Transactional
    public Product updateProduct(
            Product existingProduct,
            ProductRequest request,
            MultipartFile newThumb,
            List<MultipartFile> newImages,
            List<Long> deleteImageIds
    ){
        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setSize(request.getSize());
        existingProduct.setColor(request.getColor());
        existingProduct.setProductCondition(request.getProductCondition());
        existingProduct.setGender(request.getGender() != null ? request.getGender() : Gender.UNISEX);
        existingProduct.setMaterial(request.getMaterial());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại sản phẩm"));
        existingProduct.setCategory(category);

        if (request.getBrandId() != null){
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu"));
            existingProduct.setBrand(brand);
        }else {
            existingProduct.setBrand(null);
        }

        List<Tag> tags = tagService.createTagOrFind(request.getTagNames());
        existingProduct.setTags(tags);

        if (deleteImageIds != null && !deleteImageIds.isEmpty()){
            List<ProductImage> imagesToDelete = existingProduct.getProductImages().stream()
                    .filter((img) -> deleteImageIds.contains(img.getId()))
                    .toList();

            for (ProductImage img : imagesToDelete){
                cloudinaryService.deleteImage(img.getImagePublicId());
                productImageRepository.delete(img);
                existingProduct.getProductImages().remove(img);
            }
        }

        if (newThumb != null && !newThumb.isEmpty()){
            existingProduct.getProductImages().stream()
                    .filter(ProductImage::isThumbnail)
                    .findFirst()
                    .ifPresent((oldThumb) -> {
                        cloudinaryService.deleteImage(oldThumb.getImagePublicId());
                        productImageRepository.delete(oldThumb);
                        existingProduct.getProductImages().remove(oldThumb);
                        CloudinaryResponse response = cloudinaryService.uploadImage(newThumb);
                        ProductImage newThumbnail = ProductImage.builder()
                                .product(existingProduct)
                                .thumbnail(true)
                                .imagePublicId(response.getPublicId())
                                .imageUrl(response.getUrl())
                                .build();
                        productImageRepository.save(newThumbnail);
                        existingProduct.getProductImages().add(newThumbnail);
                    });
        }

        if (newImages != null){
            for (MultipartFile img : newImages){
                CloudinaryResponse response = cloudinaryService.uploadImage(img);
                ProductImage newImg = ProductImage.builder()
                        .product(existingProduct)
                        .thumbnail(false)
                        .imagePublicId(response.getPublicId())
                        .imageUrl(response.getUrl())
                        .build();
                existingProduct.getProductImages().add(newImg);
                productImageRepository.save(newImg);
            }
        }

        return productRepository.save(existingProduct);
    }
}
