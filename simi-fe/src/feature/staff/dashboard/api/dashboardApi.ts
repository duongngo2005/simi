import api from "../../../../lib/http/apiClient";
import type { ApiResponse } from "../../../../types/common";
import type {
  DashboardRange,
  StaffDashboardData,
  StaffNotificationsData,
} from "../types/dashboard.type";

export const dashboardApi = {
  getDashboard: async (range: DashboardRange) => {
    const response = await api.get<ApiResponse<StaffDashboardData>>("/dashboard", {
      params: { range },
    });

    return response.data.body;
  },

  getNotifications: async () => {
    const response = await api.get<ApiResponse<StaffNotificationsData>>(
      "/dashboard/notifications",
    );

    return response.data.body;
  },

  markNotificationsAsRead: async () => {
    await api.patch("/dashboard/notifications/read");
  },
};