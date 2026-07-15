package com.ndd.simi_be.product.repository;

import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductCondition;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.tag.entity.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {
    private ProductSpecification(){}

    public static Specification<Product> hasKeyword(String keyword){
        return ((root, query, cb) -> {
            if (keyword == null || keyword.isBlank()){
                return cb.conjunction();
            }

            var brandJoin = root.join("brand", JoinType.LEFT);

            String pattern = "%" + keyword.toLowerCase().trim() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("category").get("name")), pattern),
                    cb.like(cb.lower(brandJoin.get("name")), pattern)
            );
        });
    }

    public static Specification<Product> hasColor(String color){
        return ((root, query, cb) -> {
            if (color == null || color.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + color.toLowerCase().trim() + "%";
            return cb.like(cb.lower(root.get("color")), pattern);
        });
    }

    public static Specification<Product> hasSize(String size){
        return ((root, query, cb) -> {
            if (size == null || size.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + size.toLowerCase().trim() + "%";
            return cb.like(cb.lower(root.get("size")), pattern);
        });
    }


    public static Specification<Product> hasCategoryId(Long categoryId){
        return (root, query, cb) -> {
            if (categoryId == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> hasBrandId(Long brandId) {
        return (root, query, cb) -> {
            if (brandId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("brand").get("id"), brandId);
        };
    }

    public static Specification<Product> hasTag(Long tagId){
        return ((root, query, cb) -> {
            if (tagId == null){
                return cb.conjunction();
            }

            assert query != null;
            query.distinct(true);

            Join<Product, Tag> tagJoin = root.join("tags");
            return cb.equal(tagJoin.get("id"), tagId);
        });
    }

    public static Specification<Product> hasProductCondition(ProductCondition productCondition){
        return (root, query, cb) -> {
            if (productCondition == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("productCondition"), productCondition);
        };
    }

    public static Specification<Product> hasStatus(ProductStatus productStatus){
        return (root, query, cb) -> {
            if (productStatus == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("productStatus"), productStatus);
        };
    }

    public static Specification<Product> hasMinPrice(BigDecimal minPrice){
        return (root, query, cb) -> {
            if (minPrice == null || minPrice.compareTo(BigDecimal.ZERO) == 0){
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(root.get("currentPrice"), minPrice);
        };
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice){
        return (root, query, cb) -> {
            if (maxPrice == null){
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(root.get("currentPrice"), maxPrice);
        };
    }

    public static Specification<Product> isAvailable(){
        return (root, query, cb) -> cb.equal(root.get("productStatus"), ProductStatus.AVAILABLE);
    }
}
