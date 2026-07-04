package com.ndd.simi_be.brand.service;

import com.ndd.simi_be.brand.dto.BrandResponse;
import com.ndd.simi_be.brand.dto.CreateBrandRequest;
import com.ndd.simi_be.brand.dto.UpdateBrandRequest;
import com.ndd.simi_be.brand.entity.Brand;
import com.ndd.simi_be.brand.mapper.BrandMapper;
import com.ndd.simi_be.brand.repository.BrandRepository;
import com.ndd.simi_be.common.exception.ConflictException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.common.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;

    @Transactional
    public BrandResponse createBrand(CreateBrandRequest request){
        if (brandRepository.existsByName(request.getName())){
            throw new ConflictException("Tên brand đã tồn tại");
        }

        Brand brand = Brand.builder()
                .slug(SlugUtils.toSlug(request.getName()))
                .name(request.getName())
                .description(request.getDescription())
                .build();

        brand = brandRepository.save(brand);
        return BrandMapper.toBrandResponse(brand);
    }

    @Transactional
    public BrandResponse updateBrand(UpdateBrandRequest request, Long id){
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy brand"));

        if (request.getName() != null && !request.getName().isBlank()){
            if (brandRepository.existsByName(request.getName())){
                throw new ConflictException("Tên brand bị trùng");
            }
            brand.setName(request.getName());
            brand.setSlug(SlugUtils.toSlug(request.getName()));
        }

        if (request.getDescription() != null){
            brand.setDescription(request.getDescription());
        }

        if (request.getActive() != null){
            brand.setActive(request.getActive());
        }

        brand = brandRepository.save(brand);
        return BrandMapper.toBrandResponse(brand);
    }

    public List<BrandResponse> getAllBrands(){
        return brandRepository.findAll().stream().map(BrandMapper::toBrandResponse).toList();
    }

    @Transactional
    public void softDeleteBrand(Long id){
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy brand"));
        brand.setActive(false);
    }
}
