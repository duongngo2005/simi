package com.ndd.simi_be.category.service;

import com.ndd.simi_be.category.dto.CategoryResponse;
import com.ndd.simi_be.category.dto.CreateCategoryRequest;
import com.ndd.simi_be.category.dto.UpdateCategoryRequest;
import com.ndd.simi_be.category.entity.Category;
import com.ndd.simi_be.category.mapper.CategoryMapper;
import com.ndd.simi_be.category.repository.CategoryRepository;
import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ConflictException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.common.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request){
        if (categoryRepository.existsByName(request.getName())){
            throw new ConflictException("Tên danh mục đã tồn tại");
        }

        Category parent = null;
        if (request.getParentId() != null){
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Danh mục cha không tồn tại"));
            if (parent.getParent() != null){
                throw new BadRequestException("Danh mục tối đa chỉ 2 cấp");
            }
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(SlugUtils.toSlug(request.getName()))
                .parent(parent)
                .build();
        category = categoryRepository.save(category);

        return CategoryMapper.toCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(UpdateCategoryRequest request, Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));

        if (request.getName() != null){
            if (categoryRepository.existsByName(request.getName())){
                throw new BadRequestException("Tên danh mục bị trùng");
            }
            category.setName(request.getName());
        }


        if (request.getSlug() != null ){
            if (categoryRepository.existsBySlug(request.getSlug())){
                throw new BadRequestException("Slug bị trùng");
            }
            category.setSlug(request.getSlug());
        }

        if (request.getParentId() != null){
            if (!category.getChildren().isEmpty()){
                throw new BadRequestException("Danh mục tối đa 2 cấp");
            }
            if (request.getParentId().equals(id)){
                throw new BadRequestException("Một danh mục không thể tự nhận mình làm cha");
            }

            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Danh mục cha không tồn tại"));
            if (parent.getParent() != null){
                throw new BadRequestException("Danh mục tối đa 2 cấp");
            }
            category.setParent(parent);
        }

        if (request.getActive() != null){
            category.setActive(request.getActive());
        }

        category = categoryRepository.save(category);

        return CategoryMapper.toCategoryResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree(){
        return categoryRepository.findByParentIsNull().stream().map(CategoryMapper::toCategoryResponse).toList();
    }

    @Transactional
    public void softDeleteCategory(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
        category.setActive(false);
    }
}
