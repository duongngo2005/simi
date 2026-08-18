package com.ndd.simi_be.consignment.specification;

import com.ndd.simi_be.consignment.entity.ItemDisposition;
import com.ndd.simi_be.consignment.enums.ItemDispositionStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionType;
import org.springframework.data.jpa.domain.Specification;

public class ItemDispositionSpecification {
    public static Specification<ItemDisposition> hasKeyword(String keyword){
        return ((root, query, cb) -> {
            if (keyword == null || keyword.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("consignmentItem").get("consignment").get("consignor").get("fullName")), pattern),
                    cb.like(cb.lower(root.get("consignmentItem").get("consignment").get("consignor").get("phoneNumber")), pattern)
            );
        });
    }

    public static Specification<ItemDisposition> hasConsignmentId(Long consignmentId){
        return ((root, query, cb) -> {
            if (consignmentId == null){
                return cb.conjunction();
            }
            return cb.equal(root.get("consignmentItem").get("consignment").get("id"), consignmentId);
        });
    }

    public static Specification<ItemDisposition> hasStatus(ItemDispositionStatus status){
        return ((root, query, cb) -> {
            if (status == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("itemDispositionStatus"), status);
        });
    }

    public static Specification<ItemDisposition> hasType(ItemDispositionType type){
        return ((root, query, cb) -> {
            if (type == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("itemDispositionType"), type);
        });
    }


}
