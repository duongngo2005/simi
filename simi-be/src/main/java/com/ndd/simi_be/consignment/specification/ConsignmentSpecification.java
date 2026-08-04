package com.ndd.simi_be.consignment.specification;

import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ConsignmentSpecification {
    private ConsignmentSpecification(){}
    public static Specification<Consignment> hasKeyword(String keyword){
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase().trim() + "%";
            return cb.or(
                    cb.like(root.get("consignor").get("fullName"), pattern),
                    cb.like(root.get("consignor").get("phoneNumber"), pattern),
                    cb.like(root.get("id"), pattern)
            );
        };
    }

    public static Specification<Consignment> hasStatus(ConsignmentStatus status){
        return (root, query, cb) -> {
            if (status == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("consignmentStatus"), status);
        };
    }

    public static Specification<Consignment> hasStartDateFrom(LocalDateTime startDateFrom){
        return (root, query, cb) -> {
            if (startDateFrom == null){
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(root.get("startDate"), startDateFrom);
        };
    }

    public static Specification<Consignment> hasStartDateTo(LocalDateTime startDateTo){
        return (root, query, cb) -> {
            if (startDateTo == null){
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(root.get("startDate"), startDateTo);
        };
    }

    public static Specification<Consignment> hasExpiryDateFrom(LocalDateTime expiryDateFrom){
        return (root, query, cb) -> {
            if (expiryDateFrom == null){
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(root.get("expiryDate"), expiryDateFrom);
        };
    }

    public static Specification<Consignment> hasExpiryDateTo(LocalDateTime expiryDateTo){
        return (root, query, cb) -> {
            if (expiryDateTo == null){
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(root.get("expiryDate"), expiryDateTo);
        };
    }

    public static Specification<Consignment> hasIsExpiringSoon(Boolean isExpiringSoon){
        return (root, query, cb) -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threshold = now.plusDays(7);

            if (isExpiringSoon == null || !isExpiringSoon){
                return cb.conjunction();
            }

            return cb.and(
                    cb.equal(root.get("consignmentStatus"), ConsignmentStatus.ACTIVE),
                    cb.between(root.get("expiryDate"), now, threshold)
            );
        };
    }
}
