export type ItemDispositionType = "RETURN" | "DONATE";
export type ItemDispositionStatus = "PENDING" | "CONFIRM" | "COMPLETED" | "CANCELLED";

export interface ItemDisposition {
  id: number;
  type: ItemDispositionType;
  status: ItemDispositionStatus;
  productName: string;
  consignmentId: number;
  consignorName: string;
  consignorPhone: string;
  pickupDeadline: string | null;
}

export interface ItemDispositionFilter {
  keyword?: string;
  type?: string;
  status?: string;
  consignmentId?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
}

export interface ProcessItemDispositionsRequest {
  itemDispositionIds: number[];
}