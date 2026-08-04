import api from "../../../../lib/http/apiClient"
import type { ApiResponse } from "../../../../types/common"
import type { OrderDetailResponse } from "../../../order/types/order.type";
import type { ProductSummaryResponse } from "../../../product/types/product.type"
import type { CreatePosOrderRequest } from "../types/staffOrder.type";

export const posApi =  {
    getProductForPos: async (id: number) => {
        const response = await api.get<ApiResponse<ProductSummaryResponse>>(`products/${id}/pos`);
        return response.data
    },
    createPosOrder: async (request: CreatePosOrderRequest) => {
        const response = await api.post<ApiResponse<OrderDetailResponse>>("/orders/pos", request);
        return response.data
    }
}