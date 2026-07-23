package com.ndd.simi_be.order.specification;

import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.enums.OrderChannel;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.user.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {
    private OrderSpecification (){}

    public static Specification<Order> hasKeyword(String keyword){
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase().trim() + "%";

            Join<Order, User> customerJoin = root.join("customer", JoinType.LEFT);
            query.distinct(true);

            return cb.or(
                    cb.like(cb.lower(root.get("recipientPhone")), pattern),
                    cb.like(cb.lower(root.get("recipientName")), pattern),
                    cb.like(cb.lower(customerJoin.get("fullName")), pattern),
                    cb.like(cb.lower(customerJoin.get("phoneNumber")), pattern),
                    cb.like(cb.lower(root.get("id").as(String.class)), pattern)
            );
        };
    }

    public static Specification<Order> hasStatus(OrderStatus status){
        return (root, query, cb) -> {
            if (status == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("orderStatus"), status);
        };
    }

    public static Specification<Order> hasOrderChannel(OrderChannel orderChannel){
        return (root, query, cb) -> {
            if (orderChannel == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("orderChannel"), orderChannel);
        };
    }

    public static Specification<Order> hasFromDate(LocalDateTime fromDate){
        return (root, query, cb) ->
        {
            if (fromDate == null){
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("createdDate"), fromDate);
        };
    }

    public static Specification<Order> hasToDate(LocalDateTime toDate){
        return (root, query, cb) ->
        {
            if (toDate == null){
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("createdDate"), toDate);
        };
    }
}
