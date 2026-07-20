import { useMutation, useQuery } from "@tanstack/react-query"
import { orderApi } from "../api/OrderApis"

export const useCreateOrder = () => {
    return useMutation({
        mutationFn: orderApi.createOrder,
        onSuccess: (response) => response.body
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