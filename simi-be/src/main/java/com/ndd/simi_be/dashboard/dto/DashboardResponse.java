package com.ndd.simi_be.dashboard.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {

    private BigDecimal monthlyRevenue;
    private BigDecimal monthlyCommission;
    private long pendingOrderCount;
    private long availableProductCount;

    private List<DailyRevenue> dailyRevenues;
    private Map<String, Long> productStatusDistribution;

    private List<ExpiringConsignment> expiringConsignments;
    private long pendingSettlementCount;
    private long pendingDispositionCount;

    private List<RecentOrder> recentOrders;


    @Data @Builder
    public static class DailyRevenue {
        private LocalDate date;
        private BigDecimal onlineRevenue;
        private BigDecimal inStoreRevenue;
    }

    @Data @Builder
    public static class ExpiringConsignment {
        private Long consignmentId;
        private String consignorName;
        private LocalDateTime expiryDate;
        private int itemCount;
        private long daysRemaining;
    }

    @Data @Builder
    public static class RecentOrder {
        private Long orderId;
        private String recipientName;
        private String orderChannel;
        private BigDecimal finalAmount;
        private String orderStatus;
        private LocalDateTime createdDate;
    }
}