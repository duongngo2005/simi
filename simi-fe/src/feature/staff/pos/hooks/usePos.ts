import { useMutation } from "@tanstack/react-query"
import { posApi } from "../api/posApi"
import type { CreatePosOrderRequest } from "../types/staffOrder.type"

export const useProductForPos = () => {
    return useMutation({
        mutationFn: (id: number) => posApi.getProductForPos(id)
    })
}

export const useCreatePosOrder = () => {
    return useMutation({
        mutationFn: (request: CreatePosOrderRequest) => posApi.createPosOrder(request)
    })
}