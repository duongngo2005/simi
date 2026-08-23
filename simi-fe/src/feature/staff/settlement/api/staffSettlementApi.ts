import api from "../../../../lib/http/apiClient"
import type { ApiResponse } from "../../../../types/common"
import type { SettlementPreviewResponse, SettlementResponse } from "../types/staffSettlement.type"

export const staffSettlementApi = {
    getPreviewSettlement: async (consignmentId: number) => {
        const response = await api.get<ApiResponse<SettlementPreviewResponse>>(`/settlements/preview/${consignmentId}`)
        return response.data
    },
    createSettlement: async (consignmentId: number, file: File) => {
        const formData = new FormData();
        formData.append("file", file);
        const response = await api.post<ApiResponse<SettlementResponse>>(
        `/settlements/${consignmentId}`,
        formData,
        { headers: { "Content-Type": "multipart/form-data" } }
        );
        return response.data;
  },
  getSettlement: async (id: number) => {
    const response = await api.get<ApiResponse<SettlementResponse>>(`/settlements/consignment/${id}`);
    return response.data
  }
}