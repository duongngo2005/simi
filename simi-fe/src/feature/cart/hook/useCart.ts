import { useMutation, useQuery } from "@tanstack/react-query"
import { cartApi } from "../api/cartApi"
import { queryClient } from "../../../app/queryClient"

export const useGetMyCart = () => {
    return useQuery({
        queryKey: ['my-cart'],
        queryFn: () => cartApi.getMyCart(),
        select: (res) => res.body 
    })
}

export const useRemoveItem = () => {
    return useMutation({
        mutationFn: (id: number) => cartApi.removeItem(id),
        onSuccess: () => queryClient.invalidateQueries({
            queryKey: ["my-cart"]
        })
    })
}

export const useAddToCart = () => {
    return useMutation({
        mutationFn: (id: number) => cartApi.addToCart(id),
        onSuccess: () => queryClient.invalidateQueries({
            queryKey: ["my-cart"]
        })
    })
}
    
