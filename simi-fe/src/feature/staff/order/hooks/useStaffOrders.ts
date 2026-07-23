import { useMutation, useQuery } from "@tanstack/react-query"
import type { OrderFilterRequest } from "../../../order/types/order.type"
import { staffOrderApi } from "../api/staffOrderApi"

export const useStaffOrder = (filter: OrderFilterRequest) => {
    return useQuery({
        queryKey: ['orders', 'staff', filter],
        queryFn: () => staffOrderApi.getOrders(filter),
        select: (response) => response.body || []
    })
}

export const useChangeOrderStatus = () => {
    return useMutation({
        mutationFn: (data: {orderId: number, status: string}) => 
            staffOrderApi.changeOrderStatus(data.orderId, data.status)
    });
}