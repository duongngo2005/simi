import api from "../../../../lib/http/apiClient"
import type { ApiResponse, PageResponse } from "../../../../types/common"
import type { OrderFilterRequest, OrderSummaryResponse } from "../../../order/types/order.type"

export const staffOrderApi = {
    getOrders: async (filter: OrderFilterRequest) => {
        const response = await api.get<ApiResponse<PageResponse<OrderSummaryResponse>>>("/orders",
            {
                params: filter
            }
        )
        return response.data
    },
    changeOrderStatus: async (orderId: number, status: string) => {
        await api.patch(`/orders/${orderId}/status`, null, {
            params: {status}
        });
    }
}