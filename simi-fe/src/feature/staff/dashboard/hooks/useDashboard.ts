import { useQuery } from "@tanstack/react-query";
import { dashboardApi } from "../api/dashboardApi";
import type { DashboardRange } from "../types/dashboard.type";

export function useStaffDashboard(range: DashboardRange) {
  return useQuery({
    queryKey: ["staff-dashboard", range],
    queryFn: () => dashboardApi.getDashboard(range),
    staleTime: 15_000,
    refetchInterval: 30_000,
  });
}