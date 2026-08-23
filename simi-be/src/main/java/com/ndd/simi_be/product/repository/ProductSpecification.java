package com.ndd.simi_be.product.repository;

import com.ndd.simi_be.brand.entity.Brand;
import com.ndd.simi_be.category.entity.Category;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.Gender;
import com.ndd.simi_be.product.enums.ProductCondition;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.tag.entity.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public static Specification<Product> hasGender(Gender gender){
        return ((root, query, cb) -> {
            if (gender == null){
                return cb.conjunction();
            }
            return cb.or(
                    cb.equal(root.get("gender"), gender),
                    cb.equal(root.get("gender"), Gender.UNISEX)
            );
        });
    }

    public static Specification<Product> hasMaterials(List<String> materials) {
        return (root, query, cb) -> {
            if (materials == null || materials.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = materials.stream()
                    .filter(m -> m != null && !m.isBlank())
                    .map(m -> cb.like(cb.lower(root.get("material")), "%" + m.toLowerCase().trim() + "%"))
                    .toList();
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
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

    public static Specification<Product> hasCategoryIdIn(List<Long> categoryIds){
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()){
                return cb.conjunction();
            }
            return root.get("category").get("id").in(categoryIds);
        };
    }

    public static Specification<Product> hasAiKeywords(List<String> keywords){
        return ((root, query, cb) -> {
            if (keywords == null || keywords.isEmpty()){
                return cb.conjunction();
            }

            if (query != null){
                query.distinct(true);
            }

            Join<Product, Tag> tagJoin = root.join("tags", JoinType.LEFT);
            List<Predicate> keywordPredicates = new ArrayList<>();

            for (String kw : keywords){
                if (kw == null || kw.isBlank()) continue;;

                String pattern = "%" + kw.toLowerCase().trim() + "%";

                Predicate matchInName = cb.like(cb.lower(root.get("name")), pattern);
                Predicate matchInDesc = cb.like(cb.lower(root.get("description")), pattern);
                Predicate matchInCate = cb.like(cb.lower(root.get("category").get("name")), pattern);
                Predicate matchInTag = cb.like(cb.lower(tagJoin.get("name")), pattern);

                keywordPredicates.add(cb.or(matchInCate, matchInDesc, matchInName, matchInTag));
            }

            if (keywordPredicates.isEmpty()){
                return cb.conjunction();
            }

            return cb.or(keywordPredicates.toArray(new Predicate[0]));
        });
    }

    public static Specification<Product> hasGenders(List<Gender> genders) {
        return (root, query, cb) -> {
            if (genders == null || genders.isEmpty()) return cb.conjunction();
            return root.get("gender").in(genders);
        };
    }

    public static Specification<Product> hasColors(List<String> colors) {
        return (root, query, cb) -> {
            if (colors == null || colors.isEmpty()) return cb.conjunction();
            List<Predicate> predicates = colors.stream()
                    .filter(c -> c != null && !c.isBlank())
                    .map(c -> cb.like(cb.lower(root.get("color")), "%" + c.toLowerCase().trim() + "%"))
                    .toList();
            return predicates.isEmpty() ? cb.conjunction() : cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> hasSizes(List<String> sizes) {
        return (root, query, cb) -> {
            if (sizes == null || sizes.isEmpty()) return cb.conjunction();
            List<Predicate> predicates = sizes.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(s -> cb.like(cb.lower(root.get("size")), "%" + s.toLowerCase().trim() + "%"))
                    .toList();
            return predicates.isEmpty() ? cb.conjunction() : cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> hasBrandNames(List<String> brandNames) {
        return (root, query, cb) -> {
            if (brandNames == null || brandNames.isEmpty()) return cb.conjunction();
            Join<Product, Brand> brandJoin = root.join("brand", JoinType.LEFT);
            List<Predicate> predicates = brandNames.stream()
                    .filter(b -> b != null && !b.isBlank())
                    .map(b -> cb.like(cb.lower(brandJoin.get("name")), "%" + b.toLowerCase().trim() + "%"))
                    .toList();
            return predicates.isEmpty() ? cb.conjunction() : cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> hasItemKeywords(List<String> itemKeywords) {
        return (root, query, cb) -> {
            if (itemKeywords == null || itemKeywords.isEmpty()) return cb.conjunction();
            if (query != null) query.distinct(true);
            Join<Product, Category> cateJoin = root.join("category", JoinType.LEFT);
            List<Predicate> predicates = itemKeywords.stream()
                    .filter(k -> k != null && !k.isBlank())
                    .map(k -> {
                        String pattern = "%" + k.toLowerCase().trim() + "%";
                        return cb.or(
                                cb.like(cb.lower(root.get("name")), pattern),
                                cb.like(cb.lower(cateJoin.get("name")), pattern)
                        );
                    }).toList();
            return predicates.isEmpty() ? cb.conjunction() : cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> hasExcludedKeywords(List<String> excludedKeywords) {
        return (root, query, cb) -> {
            if (excludedKeywords == null || excludedKeywords.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> allowPredicates = new ArrayList<>();

            for (String keyword : excludedKeywords) {
                if (keyword == null || keyword.isBlank()) {
                    continue;
                }

                String pattern = "%" + keyword.toLowerCase().trim() + "%";

                Expression<String> description = cb.coalesce(root.<String>get("description"), "");

                Predicate matchProductText = cb.or(
                        cb.like(cb.lower(root.<String>get("name")), pattern),
                        cb.like(cb.lower(description), pattern)
                );

                Subquery<Long> matchingTagQuery = query.subquery(Long.class);
                Root<Product> correlatedProduct = matchingTagQuery.correlate(root);
                Join<Product, Tag> tagJoin = correlatedProduct.join("tags", JoinType.INNER);

                matchingTagQuery
                        .select(cb.literal(1L))
                        .where(cb.like(cb.lower(tagJoin.<String>get("name")), pattern));

                allowPredicates.add(
                        cb.and(
                                cb.not(matchProductText),
                                cb.not(cb.exists(matchingTagQuery))
                        )
                );
            }

            return allowPredicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(allowPredicates.toArray(new Predicate[0]));
        };
    }
}
