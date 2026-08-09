import api from "../../../lib/http/apiClient"
import type { ApiResponse } from "../../../types/common"
import { type ConsignmentFullDetailResponse, type ConsignmentResponse } from "../../staff/consignment/types/staffConsignment.type"

export const consignmentApi = {
    getMyConsignments: async () => {
        const response = await api.get<ApiResponse<ConsignmentResponse[]>>("/consignments/my-consignments");
        return response.data
    },
    getConsignmentFullDetail: async (id: number) => {
        const response = await api.get<ApiResponse<ConsignmentFullDetailResponse>>(`consignments/details/${id}`);
        return response.data
    }
}