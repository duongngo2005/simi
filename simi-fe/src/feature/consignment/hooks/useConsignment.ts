import { useQuery } from "@tanstack/react-query"
import { consignmentApi } from "../api/consignmentApi"

export const useGetMyConsignments = () => {
    return useQuery({
        queryKey: ['my-consignments'],
        queryFn: () => consignmentApi.getMyConsignments(),
        select: (response) => response.body
    })
}

export const useGetConsignmentDetail = (id: number) => {
    return useQuery({
        queryKey: ['consignment-detail', id],
        queryFn: () => consignmentApi.getConsignmentFullDetail(id),
        select: (res) => res.body
    })
}