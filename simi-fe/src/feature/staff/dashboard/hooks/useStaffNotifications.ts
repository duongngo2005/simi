import { useMutation, useQuery } from "@tanstack/react-query";
import { queryClient } from "../../../../app/queryClient";
import { dashboardApi } from "../api/dashboardApi";

export function useStaffNotifications() {
  return useQuery({
    queryKey: ["staff-notifications"],
    queryFn: dashboardApi.getNotifications,
    staleTime: 10_000,
    refetchInterval: 30_000,
  });
}

export function useMarkStaffNotificationsAsRead() {
  return useMutation({
    mutationFn: dashboardApi.markNotificationsAsRead,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["staff-notifications"],
      });
    },
  });
}