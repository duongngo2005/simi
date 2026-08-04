import { number } from "zod";

export interface Province {
  code: string;
  name: string;
  fullName: string;
}

export interface Ward {
  code: string;
  name: string;
  fullName: string;
}

export interface OrderItemRequest {
    productId: number
}

export interface OrderRequest {
    province: string;
    ward: string;
    addressDetail: string;
    recipientName: string;
    recipientPhone: string;
    discount: number;
    paymentMethod: "COD" | "VNPAY",
    orderItemRequests: OrderItemRequest[]
}

export interface OrderDetailResponse {
    id: number;
    acceptedByName: string;
    recipientName: string;
    recipientPhone: string;
    province: string;
    ward: string;
    addressDetail: string;
    subtotalAmount: number;
    shippingFee: number;
    discount: number;
    finalAmount: number;
    orderStatus: string;
    orderChannel: string;
    createDate: string;
    paymentStatus?: string;
    paymentMethod?: string;
    orderItems?: OrderItemResponse[]
    paymentResponses?: PaymentResponse[]
}

export interface OrderItemResponse {
    id: number;
    name: string;
    size: string;
    color: string;
    brand: string;
    thumbnail: string;
    unitPrice: number;
}

export interface PaymentResponse {
    id: number;
    orderId: number;
    paymentStatus: string;
    amount: number;
    paidAt?: string;
    paymentMethod: string;
    refundedAt?: string;
    transactionId?: string;
}

export interface OrderSummaryResponse{
    id: number
    orderStatus: string;
    finalAmount: number;
    createdDate: string;

    firstItemName: string;
    firstItemThumbnail: string;
    totalItem: number;
}

export interface OrderFilterRequest{
    orderStatus: string;
    orderChannel: string;
    keyword: string;

    page: number;
    size: number;

    fromDate: string;
    toDate: string;

    sortDir: string;
    sortBy: string;
}