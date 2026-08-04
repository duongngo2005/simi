import type { OrderItemRequest } from "../../../order/types/order.type";

export interface CreatePosOrderRequest {
    recipientPhone: string;
    recipientName: string;
    discount?: number;
    paymentMethod: string;
    orderItemRequests: OrderItemRequest[]
}