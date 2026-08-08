import type { ProductImageResponse, ProductSummaryResponse } from "../../product/types/product.type";

export interface CartResponse {
    id: number;
    cartItemResponses: CartItemResponse[];
    totalItem: number;
}

export interface CartItemResponse {
    id: number;
    cartId: number;
    productSummaryResponse: ProductSummaryResponse;
    currentPrice: number;
}