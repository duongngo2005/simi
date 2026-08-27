export type DashboardRange = "7d" | "month";

export interface StaffDashboardData {
  summary: DashboardSummary;
  revenueSeries: RevenuePoint[];
  productOverview: ProductOverviewItem[];
  workQueue: WorkQueueItem[];
  recentOrders: RecentOrder[];
  generatedAt: string;
}

export interface DashboardSummary {
  unofficialRevenue: number;
  unofficialPaidRevenue: number;
  unofficialUnpaidRevenue: number;

  completedRevenueThisMonth: number;
  activeProductCount: number;
  unconfirmedCodOrderCount: number;
  todayOrderCount: number;

  activeConsignmentCount: number;
  expiringConsignmentCount: number;
  pendingSettlementConsignmentCount: number;

  pendingReturnItemCount: number;
  pendingDonationConfirmationCount: number;
}

export interface RevenuePoint {
  date: string;
  revenue: number;
  completedOrderCount: number;
}

export interface ProductOverviewItem {
  status: string;
  label: string;
  count: number;
}

export interface WorkQueueItem {
  key:
    | "COD_CONFIRMATION"
    | "PENDING_SETTLEMENT"
    | "RETURN_PENDING"
    | "DONATION_CONFIRMATION";
  count: number;
  severity: "danger" | "warning" | "info";
}

export interface RecentOrder {
  id: number;
  recipientName: string;
  paymentMethod: "COD" | "ONLINE" | "CASH" | "-";
  orderChannel: "ONLINE" | "IN_STORE";
  orderStatus: string;
  finalAmount: number;
  createdDate: string;
}

export interface StaffNotification {
  id: number;
  read: boolean;
  createdDate: string;
  orderId: number;
  recipientName: string;
  orderStatus: string;
  paymentMethod: string;
  finalAmount: number;
}

export interface StaffNotificationsData {
  unreadCount: number;
  notifications: StaffNotification[];
}