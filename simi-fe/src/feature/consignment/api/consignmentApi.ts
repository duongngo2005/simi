import api from "../../../lib/http/apiClient";
import type { ApiResponse } from "../../../types/common";
import type {
  ConsignmentFullDetailResponse,
  ConsignmentResponse,
} from "../../staff/consignment/types/staffConsignment.type";
import type { MyDispositionResponse, MySettlementResponse } from "../types/consignment.type";

export const consignmentApi = {
  getMyConsignments: async () => {
    const response = await api.get<ApiResponse<ConsignmentResponse[]>>(
      "/consignments/my-consignments",
    );
    return response.data;
  },

  getConsignmentFullDetail: async (id: number) => {
    const response = await api.get<ApiResponse<ConsignmentFullDetailResponse>>(
      `/consignments/details/${id}`,
    );
    return response.data;
  },

  getMySettlements: async () => {
    const response = await api.get<ApiResponse<MySettlementResponse[]>>("/settlements/my");
    return response.data;
  },

  getMyDispositions: async (consignmentId: number) => {
    const response = await api.get<ApiResponse<MyDispositionResponse[]>>(
      `/item-dispositions/my/${consignmentId}`,
    );
    return response.data;
  },
};