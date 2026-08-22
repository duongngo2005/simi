import api from "../../../lib/http/apiClient";
import { type PageResponse, type ApiResponse } from "../../../types/common";
import { type OrderSummaryResponse, type OrderDetailResponse, type OrderRequest, type OrderFilterRequest, type CreateOrderResponse } from "../types/order.type";

export const orderApi = {
    createOrder: async (data: OrderRequest) => {
        const response = await api.post<ApiResponse<CreateOrderResponse>>("/orders", data)
        return response.data
    },
    retryPayment: async(orderId: number) => {
        const response = await api.post<ApiResponse<CreateOrderResponse>>(
            `/orders/${orderId}/retry-payment`
        );
        return response.data
    },
    calcShippingFee: async (provinceCode: string, subtotalAmount: number) => {
        const response = await api.get<ApiResponse<number>>('/orders/shipping-fee', {
            params: {provinceCode, subtotalAmount}
        }); 
        return response.data
    },
    getMyOrders: async (filter: OrderFilterRequest) => {
        const response = await api.get<ApiResponse<PageResponse<OrderSummaryResponse>>>("/orders/my-orders", {
            params: filter
        });
        return response.data
    },
    getOrderDetails: async (id: number) => {
        const response = await api.get<ApiResponse<OrderDetailResponse>>(`/orders/details/${id}`);
        return response.data
    }
}