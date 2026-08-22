import { useMutation, useQuery } from "@tanstack/react-query"
import { orderApi } from "../api/OrderApis"
import type { OrderFilterRequest } from "../types/order.type"

export const useCreateOrder = () => {
    return useMutation({
        mutationFn: orderApi.createOrder,
        onSuccess: (response) => response.body
    })
}

export const useRetryPayment = () => {
    return useMutation({
        mutationFn: orderApi.retryPayment
    })
}

export const useShippingFee = (provinceCode: string, subtotalAmount: number) => {
    return useQuery({
        queryKey: [provinceCode, subtotalAmount, 'shipping-fee'],
        queryFn: () =>  orderApi.calcShippingFee(provinceCode, subtotalAmount),
        enabled: !!provinceCode && subtotalAmount > 0,
        select: (response) => response.body
    })
}

export const useGetMyOrders = (filter: OrderFilterRequest) => {
    return useQuery({
        queryKey: ['my-orders', filter],
        queryFn: () => orderApi.getMyOrders(filter),
        select: (response) => response.body
    })
}

export const useGetOrderDetails = (id: number) => {
    return useQuery({
        queryKey: ['order-details', id],
        queryFn: () => orderApi.getOrderDetails(id),
        select: (response) => response.body
    })
}