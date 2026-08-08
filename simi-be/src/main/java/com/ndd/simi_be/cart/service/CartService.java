package com.ndd.simi_be.cart.service;

import com.ndd.simi_be.cart.dto.CartItemResponse;
import com.ndd.simi_be.cart.dto.CartResponse;
import com.ndd.simi_be.cart.entity.Cart;
import com.ndd.simi_be.cart.entity.CartItem;
import com.ndd.simi_be.cart.mapper.CartMapper;
import com.ndd.simi_be.cart.repository.CartItemRepository;
import com.ndd.simi_be.cart.repository.CartRepository;
import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ConflictException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.product.repository.ProductRepository;
import com.ndd.simi_be.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartResponse getMyCart(User user){
        return CartMapper.toCartResponse(
                cartRepository.findByUser(user)
                        .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()))
        );
    }

    @Transactional
    public CartResponse addToCart(Long productId, User user){

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin sản phẩm"));

        if (product.getProductStatus() != ProductStatus.AVAILABLE){
            throw new BadRequestException("Sản phẩm hiện đã được giữ chỗ hoặc đã bán");
        }

        if (cartItemRepository.existsByCartAndProduct(cart, product)){
            throw new ConflictException("Sản phẩm này đã có trong giỏ hàng");
        }

        CartItem cartItem = CartItem.builder()
                .product(product)
                .cart(cart)
                .build();
        cartItemRepository.save(cartItem);

        cart.getCartItems().add(cartItem);
        return CartMapper.toCartResponse(cart);
    }

    @Transactional
    public void removeItemFromCart(Long cartItemId, User user){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết giỏ hàng!"));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())){
            throw new BadRequestException("Chi tiết giỏ hàng không thuộc về giỏ hàng của bạn");
        }

        cartItemRepository.delete(cartItem);
    }
}
