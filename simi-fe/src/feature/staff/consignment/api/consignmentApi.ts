import { number } from "zod";
import api from "../../../../lib/http/apiClient";
import type { ApiResponse, PageResponse } from "../../../../types/common";
import type {ConsignmentResponse, ConsignmentFilterRequest, CreateConsignmentRequest, 
    ConsignmentItemRequest, ConsignmentItemResponse, ConsignmentFullDetailResponse, 
    CreateConsignmentItemRequest,
    UpdateConsignmentItemRequest} 
    from "../types/staffConsignment.type";

export const consignmentApi = {
    getAllConsignment: async (filter: ConsignmentFilterRequest) => {
        const response = await api.get<ApiResponse<PageResponse<ConsignmentResponse>>>("/consignments", {
            params: filter
        });

        return response.data
    },
    createConsignment: async (request: CreateConsignmentRequest) => {
        const response = await api.post<ApiResponse<ConsignmentResponse>>("/consignments", request);
        return response.data
    },
    createConsignmentItem: async (request: CreateConsignmentItemRequest) => {
        const formData = new FormData();
        formData.append("data", new Blob([JSON.stringify(request.consignmentItemRequest)], {type: "application/json"}))
        formData.append("thumbnail", request.thumbnail);
        if(request.images){
            request.images.forEach((img) => formData.append("images", img))
        }
        const response = await api.post<ApiResponse<ConsignmentItemResponse>>(
            `/consignments/${request.consignmentId}/items`,
            formData,
            {headers: {"Content-Type": "multipart/form-data"}}
        );
        return response.data
    },
    getConsignmentFullDetail: async (consignmentId: number) => {
        const response = await api.get<ApiResponse<ConsignmentFullDetailResponse>>(`/consignments/${consignmentId}/details`);
        return response.data
    },
    deleteConsignmentItem: async (consignmentItemId: number, consignmentId: number) => {
        await api.delete(`/consignments/${consignmentId}/items/${consignmentItemId}`)
    },
    updateConsignmentItem: async (
        request: UpdateConsignmentItemRequest
    ) => {
        const formData = new FormData();

        const jsonData = {
            commissionRate: request.consignmentItemRequest.commissionRate,
            priceScheduleRequests: request.consignmentItemRequest.priceScheduleRequests,
            productRequest: request.consignmentItemRequest.productRequest,
            deleteImageIds: request.deleteImageIds
        }

        formData.append("data", new Blob([JSON.stringify(jsonData)], {type: "application/json"}))
        if(request.thumbnail){
            formData.append("thumbnail", request.thumbnail)
        }
        if(request.images){
            request.images.forEach((img) => formData.append("images", img)) 
        }

        const response = await api.patch<ApiResponse<ConsignmentItemResponse>>(
            `/consignments/${request.consignmentId}/items/${request.consignmentItemId}`,
            formData,
            {headers: {"Content-Type": "multipart/form-data"}}
        );
        return response.data
    },
    activeConsignment: async (consignmentId: number) => {
        const response = await api.post<ApiResponse<ConsignmentResponse>>(`/consignments/${consignmentId}/active`);
        return response.data
    }
}