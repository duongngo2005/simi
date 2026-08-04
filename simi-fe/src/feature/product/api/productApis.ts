import api from "../../../lib/http/apiClient";
import type { ApiResponse, PageResponse } from "../../../types/common";
import  { type ProductDetailResponse, type ProductImageResponse, type ProductSummaryResponse } from "../types/product.type";

export const getProducts = async (params: {
    page?: number;
    size?: number;
    keyword?: string;
    categoryId?: number;
    brandId?: number;
    sizeProduct?: string;
    color?: string;
    productCondition?: string;
    minPrice?: number;
    maxPrice?: number;
    sortBy?: string;
    sortDir?: "desc" | "asc";
    categorySlug?: string;
}) =>  {
    const response = await api.get<ApiResponse<PageResponse<ProductSummaryResponse>>>("/products", {params})
    return response.data
}

export const getThumbnail = async (id: number) => {
    const response = await api.get<ApiResponse<ProductImageResponse>>(`/products/${id}/thumbnail`);
    return response.data
}

export const getProductDetail = async (id: number) => {
    const res = await api.get<ApiResponse<ProductDetailResponse>>(`/products/${id}`);
      return res.data;
}