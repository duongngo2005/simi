import { useMutation, useQuery } from "@tanstack/react-query"
import { staffSettlementApi } from "../api/staffSettlementApi"
import { queryClient } from "../../../../app/queryClient"
import { id } from "zod/locales"

export const usePreviewSettlement = (consignmentId: number) => {
    return useQuery({
        queryKey: ['preview', consignmentId],
        queryFn: () => staffSettlementApi.getPreviewSettlement(consignmentId),
        select: (res) => res.body
    })
}

export const useCreateSettlement = () => {
    return useMutation({
        mutationFn: (data: {consignmentId: number, file: File}) => staffSettlementApi.createSettlement(data.consignmentId, data.file),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({
                queryKey: ["consignments", variables.consignmentId, "details"],
            });
            queryClient.invalidateQueries({
                queryKey: ["consignment", "staff"],
            });
        }
    })
}

export const useGetSettlement = (id: number) => {
    return useQuery({
        queryKey: ['settlement', id],
        queryFn: () => staffSettlementApi.getSettlement(id),
        select: (res) => res.body
    })
}