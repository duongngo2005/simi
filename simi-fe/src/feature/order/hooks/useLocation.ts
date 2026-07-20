import { useQuery } from "@tanstack/react-query";
import api from "../../../lib/http/apiClient";
import type { ApiResponse } from "../../../types/common";
import type { Province, Ward } from "../types/order.type";

export const useProvinces = () =>
  useQuery({
    queryKey: ["provinces"],
    queryFn: async () => {
      const res = await api.get<ApiResponse<Province[]>>("/provinces");
      return res.data.body ?? [];
    },
  });

export const useWards = (provinceCode: string) =>
  useQuery({
    queryKey: ["wards", provinceCode],
    queryFn: async () => {
      const res = await api.get<ApiResponse<Ward[]>>(`/provinces/${provinceCode}/wards`);
      return res.data.body ?? [];
    },
    enabled: !!provinceCode,
  });