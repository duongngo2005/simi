import api from "../../../lib/http/apiClient";
import type { ApiResponse, PageResponse } from "../../../types/common";
import type {
  ItemDisposition,
  ItemDispositionFilter,
  ProcessItemDispositionsRequest,
} from "../types/disposition.type";

export const dispositionApi = {
  searchDispositions: async (
    filter: ItemDispositionFilter,
  ): Promise<PageResponse<ItemDisposition>> => {
    const params = new URLSearchParams();
    if (filter.keyword) params.append("keyword", filter.keyword);
    if (filter.type) params.append("type", filter.type);
    if (filter.status) params.append("status", filter.status);
    if (filter.consignmentId) params.append("consignmentId", String(filter.consignmentId));
    params.append("page", String(filter.page ?? 0));
    params.append("size", String(filter.size ?? 15));
    params.append("sortBy", filter.sortBy ?? "createdDate");
    params.append("sortDir", filter.sortDir ?? "desc");

    const res = await api.get<ApiResponse<PageResponse<ItemDisposition>>>(
      `/item-dispositions?${params.toString()}`,
    );
    return res.data.body;
  },

  confirmReturn: async (request: ProcessItemDispositionsRequest): Promise<void> => {
    await api.patch("/item-dispositions/confirm-return", request);
  },

  confirmDonation: async (request: ProcessItemDispositionsRequest): Promise<void> => {
    await api.patch("/item-dispositions/confirm-donation", request);
  },
};