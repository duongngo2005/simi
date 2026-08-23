import type { ProductDetailResponse } from "../../../product/types/product.type";

export interface ConsignmentItemRequest {
    commissionRate: number;
    priceScheduleRequests: PriceScheduleRequest[];
    productRequest: ProductRequest;
}

export interface CreateConsignmentItemRequest {
    consignmentItemRequest: ConsignmentItemRequest;
    thumbnail: File;
    images?: File[];
    consignmentId: number;
}

export interface UpdateConsignmentItemRequest {
    consignmentItemRequest: ConsignmentItemRequest;
    thumbnail?: File;
    images?: File[];
    consignmentId: number;
    consignmentItemId: number;
    deleteImageIds: number[]
}

export interface ConsignmentFilterRequest {
    keyword: string;
    startDateFrom: string;
    startDateTo: string;
    expiryDateFrom: string;
    expiryDateTo: string;
    isExpiringSoon: boolean;
    consignmentStatus: string;
    page: number;
    size: number;
    sortBy: string;
    sortDir: string;
}

export interface ProductRequest {
    name: string;
    categoryId: number;
    brandId?: number;
    size?: string;
    description?: string;
    color?: string;
    productCondition?: string;
    tagNames: string[];
    gender?: "MEN" | "WOMEN" | "UNISEX";
    material?: string;
}

export interface PriceScheduleRequest {
    effectiveAfterDays: number;
    price: number;
}

export interface CreateConsignmentRequest{
    consignorPhone: string;
    note: string;
}

export interface UpdateConsignmentRequest{
    note: string;
}

export interface ConsignmentFullDetailResponse{
    consignmentResponse: ConsignmentResponse;
    consignmentItemResponses: ConsignmentItemResponse[]
}

export interface ConsignmentResponse{
    id: number;
    consignorid: number;
    consignorName: string;
    receivedBy: number;
    receivedName: string;
    startDate: string;
    expiryDate: string;
    totalItem: number;
    soldItem: number;
    note: string;
    status: string;
}

export interface ConsignmentItemResponse{
    id: number;
    commissionRate: number;
    status: string;
    priceScheduleResponses: PriceScheduleResponse[];
    productDetailResponse: ProductDetailResponse;
    itemDispositionResponse: ItemDispositionResponse;
}

export interface PriceScheduleResponse{
    id: number;
    effectiveAfterDays: number;
    price: number;
    appliedAt: string;
    status: string;
}

export interface ItemDispositionResponse{
    id: number;
    type: string;
    status: string;
    pickupDeadline: string;
    consentedAt: string;
    processedAt: string;
    processedBy: number;
    note: string;
}