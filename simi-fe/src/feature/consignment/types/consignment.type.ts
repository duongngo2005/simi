export interface MySettlementResponse {
  id: number;
  totalSoldAmount: number;
  totalCommissionAmount: number;
  netAmount: number;
  settledAt: string;
  proofImageUrl: string;
  paymentMethod: string;
  accountHolder: string;
  accountNumber: string;
  bankName: string;
  consignmentId: number;
}

export interface MyDispositionResponse {
  id: number;
  type: string;
  status: string;
  productName: string;
  consignmentId: number;
  consignorName: string;
  consignorPhone: string;
  pickupDeadline: string | null;
}