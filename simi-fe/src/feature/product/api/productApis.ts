import api from "../../../lib/http/apiClient";
import type { ApiResponse, PageResponse } from "../../../types/common";
import type { ProductSummaryResponse } from "../types/product.type";

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