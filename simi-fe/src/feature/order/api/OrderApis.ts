import api from "../../../lib/http/apiClient";
import type { ApiResponse } from "../../../types/common";
import type { OrderDetailResponse, OrderRequest } from "../types/order.type";

export const orderApi = {
    createOrder: async (data: OrderRequest) => {
        const response = await api.post<ApiResponse<OrderDetailResponse>>("/orders", data)
        return response.data
    },
    calcShippingFee: async (provinceCode: string, subtotalAmount: number) => {
        const response = await api.get<ApiResponse<number>>('/orders/shipping-fee', {
            params: {provinceCode, subtotalAmount}
        });
        return response.data
    }
}