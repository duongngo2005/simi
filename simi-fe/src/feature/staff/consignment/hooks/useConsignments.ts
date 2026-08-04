import { useMutation, useQuery } from "@tanstack/react-query";
import type { ConsignmentFilterRequest, ConsignmentItemRequest, CreateConsignmentItemRequest, CreateConsignmentRequest, UpdateConsignmentItemRequest } from "../types/staffConsignment.type";
import { consignmentApi } from "../api/consignmentApi";
import { queryClient } from "../../../../app/queryClient";

export const useStaffConsignments = (filter: ConsignmentFilterRequest) => {
    return useQuery({
        queryKey: ['consignment', 'staff', filter],
        queryFn: () => consignmentApi.getAllConsignment(filter),
        select: (response) => response.body
    })
}

export const useCreateConsignment = () => {
    return useMutation({
        mutationFn: (request: CreateConsignmentRequest) => consignmentApi.createConsignment(request)
    })
}

export const useConsignmentFullDetail = (consignmentId: number) => {
    return useQuery({
        queryKey: ['consignments', consignmentId, 'details'],
        queryFn: () => consignmentApi.getConsignmentFullDetail(consignmentId),
        select: (response) => response.body,
        enabled: !!consignmentId
    })
}

export const useCreateConsignmentItem = () => {
    return useMutation({
        mutationFn: (request: CreateConsignmentItemRequest) => consignmentApi.createConsignmentItem(request),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({
                queryKey: ['consignments', variables.consignmentId, 'details']
            })
        }
    })
}

export const useUpdateConsignmentItem = () => {
    return useMutation({
        mutationFn: (request: UpdateConsignmentItemRequest) => 
            consignmentApi.updateConsignmentItem(request),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({
                queryKey: ['consignments', variables.consignmentId, 'details']
            })
        }
    })
}

export const useDeleteConsignmentItem = () => {
    return useMutation({
        mutationFn: (data: {consignmentItemId: number, consignmentId: number}) => 
            consignmentApi.deleteConsignmentItem(data.consignmentItemId, data.consignmentId),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({
                queryKey: ['consignments', variables.consignmentId, 'details']
            })
        }
    })
}

export const useActiveConsignment = () => {
    return useMutation({
        mutationFn: (consignmentId: number) => consignmentApi.activeConsignment(consignmentId),
        onSuccess: (_, consignmentId) => {
            queryClient.invalidateQueries({
                queryKey: ['consignments', consignmentId, 'details']
            }),
            queryClient.invalidateQueries({
                queryKey: ['consignment', 'staff']
            })
        }
    })
}