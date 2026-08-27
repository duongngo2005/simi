package com.ndd.simi_be.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StaffDashboardResponse {
    private Summary summary;
    private List<RevenuePoint> revenueSeries;
    private List<ProductOverviewItem> productOverview;
    private List<WorkQueueItem> workQueue;
    private List<RecentOrder> recentOrders;
    private LocalDateTime generatedAt;

    @Data
    @Builder
    public static class Summary {
        private BigDecimal unofficialRevenue;
        private BigDecimal unofficialPaidRevenue;
        private BigDecimal unofficialUnpaidRevenue;

        private BigDecimal completedRevenueThisMonth;
        private long activeProductCount;
        private long unconfirmedCodOrderCount;
        private long todayOrderCount;

        private long activeConsignmentCount;
        private long expiringConsignmentCount;
        private long pendingSettlementConsignmentCount;

        private long pendingReturnItemCount;
        private long pendingDonationConfirmationCount;
    }

    @Data
    @Builder
    public static class RevenuePoint {
        private LocalDate date;
        private BigDecimal revenue;
        private long completedOrderCount;
    }

    @Data
    @Builder
    public static class ProductOverviewItem {
        private String status;
        private String label;
        private long count;
    }

    @Data
    @Builder
    public static class WorkQueueItem {
        private String key;
        private long count;
        private String severity;
    }

    @Data
    @Builder
    public static class RecentOrder {
        private Long id;
        private String recipientName;
        private String paymentMethod;
        private String orderChannel;
        private String orderStatus;
        private BigDecimal finalAmount;
        private LocalDateTime createdDate;
    }
}