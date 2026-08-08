package com.ndd.simi_be.cart.repository;

import com.ndd.simi_be.cart.entity.Cart;
import com.ndd.simi_be.cart.entity.CartItem;
import com.ndd.simi_be.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsByCartAndProduct(Cart cart, Product product);
}
