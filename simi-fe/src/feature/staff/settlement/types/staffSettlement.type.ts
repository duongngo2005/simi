import type { ConsignmentItemResponse } from "../../consignment/types/staffConsignment.type";

export interface SettlementPreviewResponse {
    consignorId: number;
    consignorName: string;
    bankName: string;
    accountHolder: string;
    accountNumber: string;
    totalSoldAmount: number;
    totalCommissionAmount: number;
    netAmount: number;
    soldItemCount: number;
    canSettle: boolean;
    soldItems: ConsignmentItemResponse[];
    returnItems: ConsignmentItemResponse[];
    reserveItems: ConsignmentItemResponse[];
} 

export interface SettlementResponse{
    id: number;
    consignmentId: number;
    consignorId: number;
    processedById: number;
    totalSoldAmount: number;
    totalCommissionAmount: number;
    netAmount: number;
    paymentMethod: string;
    bankName: string;
    accountHolder: string;
    accountNumber: string;
    proofImageUrl: string;
    settledAt: string;
    settlementItemResponses: ConsignmentItemResponse[]
}