package com.ndd.simi_be.dashboard.service;

import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionType;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.consignment.repository.ItemDispositionRepository;
import com.ndd.simi_be.dashboard.dto.StaffDashboardResponse;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.product.enums.ProductStatus;
import com.ndd.simi_be.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private static final List<OrderStatus> UNOFFICIAL_REVENUE_STATUSES = List.of(
            OrderStatus.PENDING,
            OrderStatus.PENDING_PAYMENT,
            OrderStatus.PACKING,
            OrderStatus.SHIPPING
    );

    private static final List<ProductStatus> PRODUCT_OVERVIEW_STATUSES = List.of(
            ProductStatus.AVAILABLE,
            ProductStatus.RESERVED,
            ProductStatus.SOLD,
            ProductStatus.EXPIRED,
            ProductStatus.DRAFT,
            ProductStatus.HIDDEN,
            ProductStatus.CANCELLED
    );

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ConsignmentRepository consignmentRepository;
    private final ItemDispositionRepository itemDispositionRepository;

    @Transactional(readOnly = true)
    public StaffDashboardResponse getDashboard(String range) {
        LocalDate today = LocalDate.now(VIETNAM_ZONE);
        LocalDateTime now = LocalDateTime.now(VIETNAM_ZONE);

        LocalDateTime startToday = today.atStartOfDay();
        LocalDateTime endToday = today.plusDays(1).atStartOfDay();
        LocalDateTime startMonth = today.withDayOfMonth(1).atStartOfDay();

        LocalDate chartStartDate = "month".equalsIgnoreCase(range)
                ? today.withDayOfMonth(1)
                : today.minusDays(6);

        LocalDateTime chartFrom = chartStartDate.atStartOfDay();

        StaffDashboardResponse.Summary summary =
                StaffDashboardResponse.Summary.builder()
                        .unofficialRevenue(zero(
                                orderRepository.sumFinalAmountByStatuses(
                                        UNOFFICIAL_REVENUE_STATUSES
                                )
                        ))
                        .unofficialPaidRevenue(zero(
                                orderRepository.sumPaidFinalAmountByStatuses(
                                        UNOFFICIAL_REVENUE_STATUSES
                                )
                        ))
                        .unofficialUnpaidRevenue(zero(
                                orderRepository.sumUnpaidFinalAmountByStatuses(
                                        UNOFFICIAL_REVENUE_STATUSES
                                )
                        ))
                        .completedRevenueThisMonth(zero(
                                orderRepository.sumCompletedRevenueBetween(
                                        startMonth,
                                        endToday
                                )
                        ))
                        .activeProductCount(
                                productRepository.countByProductStatus(ProductStatus.AVAILABLE)
                        )
                        .unconfirmedCodOrderCount(
                                orderRepository.countUnconfirmedCodOrders()
                        )
                        .todayOrderCount(
                                orderRepository.countByCreatedDateGreaterThanEqualAndCreatedDateLessThan(
                                        startToday,
                                        endToday
                                )
                        )
                        .activeConsignmentCount(
                                consignmentRepository.countByConsignmentStatus(
                                        ConsignmentStatus.ACTIVE
                                )
                        )
                        .expiringConsignmentCount(
                                consignmentRepository.countExpiringBefore(
                                        now,
                                        now.plusDays(7)
                                )
                        )
                        .pendingSettlementConsignmentCount(
                                consignmentRepository.countByConsignmentStatus(
                                        ConsignmentStatus.PENDING_SETTLEMENT
                                )
                        )
                        .pendingReturnItemCount(
                                itemDispositionRepository.countByTypeAndStatus(
                                        ItemDispositionType.RETURN,
                                        ItemDispositionStatus.PENDING
                                )
                        )
                        .pendingDonationConfirmationCount(
                                itemDispositionRepository.countByTypeAndStatus(
                                        ItemDispositionType.DONATE,
                                        ItemDispositionStatus.CONFIRM
                                )
                        )
                        .build();

        return StaffDashboardResponse.builder()
                .summary(summary)
                .revenueSeries(buildRevenueSeries(chartStartDate, today, chartFrom, endToday))
                .productOverview(buildProductOverview())
                .workQueue(buildWorkQueue(summary))
                .recentOrders(buildRecentOrders())
                .generatedAt(now)
                .build();
    }

    private List<StaffDashboardResponse.RevenuePoint> buildRevenueSeries(
            LocalDate startDate,
            LocalDate endDate,
            LocalDateTime from,
            LocalDateTime to
    ) {
        Map<LocalDate, Object[]> rowsByDate =
                orderRepository.findCompletedRevenueByDay(from, to).stream()
                        .collect(Collectors.toMap(
                                row -> toLocalDate(row[0]),
                                row -> row
                        ));

        List<StaffDashboardResponse.RevenuePoint> result = new ArrayList<>();

        for (LocalDate day = startDate; !day.isAfter(endDate); day = day.plusDays(1)) {
            Object[] row = rowsByDate.get(day);

            result.add(StaffDashboardResponse.RevenuePoint.builder()
                    .date(day)
                    .revenue(row == null ? BigDecimal.ZERO : toBigDecimal(row[1]))
                    .completedOrderCount(row == null ? 0L : ((Number) row[2]).longValue())
                    .build());
        }

        return result;
    }

    private List<StaffDashboardResponse.ProductOverviewItem> buildProductOverview() {
        Map<ProductStatus, Long> counts = productRepository.countByProductStatusGrouped()
                .stream()
                .collect(Collectors.toMap(
                        row -> ProductStatus.valueOf(row[0].toString()),
                        row -> ((Number) row[1]).longValue()
                ));

        return PRODUCT_OVERVIEW_STATUSES.stream()
                .map(status -> StaffDashboardResponse.ProductOverviewItem.builder()
                        .status(status.name())
                        .label(productLabel(status))
                        .count(counts.getOrDefault(status, 0L))
                        .build())
                .toList();
    }

    private List<StaffDashboardResponse.WorkQueueItem> buildWorkQueue(
            StaffDashboardResponse.Summary summary
    ) {
        return List.of(
                queue("COD_CONFIRMATION", summary.getUnconfirmedCodOrderCount(), "danger"),
                queue("PENDING_SETTLEMENT", summary.getPendingSettlementConsignmentCount(), "danger"),
                queue("RETURN_PENDING", summary.getPendingReturnItemCount(), "warning"),
                queue(
                        "DONATION_CONFIRMATION",
                        summary.getPendingDonationConfirmationCount(),
                        "warning"
                )
        );
    }

    private List<StaffDashboardResponse.RecentOrder> buildRecentOrders() {
        return orderRepository.findAll(
                        PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "createdDate"))
                )
                .getContent()
                .stream()
                .map(this::toRecentOrder)
                .toList();
    }

    private StaffDashboardResponse.RecentOrder toRecentOrder(Order order) {
        String paymentMethod = order.getPayments().stream()
                .max(Comparator.comparing(Payment::getCreatedDate))
                .map(payment -> payment.getPaymentMethod().name())
                .orElse("-");

        return StaffDashboardResponse.RecentOrder.builder()
                .id(order.getId())
                .recipientName(order.getRecipientName())
                .paymentMethod(paymentMethod)
                .orderChannel(order.getOrderChannel().name())
                .orderStatus(order.getOrderStatus().name())
                .finalAmount(order.getFinalAmount())
                .createdDate(order.getCreatedDate())
                .build();
    }

    private StaffDashboardResponse.WorkQueueItem queue(
            String key,
            long count,
            String severity
    ) {
        return StaffDashboardResponse.WorkQueueItem.builder()
                .key(key)
                .count(count)
                .severity(severity)
                .build();
    }

    private String productLabel(ProductStatus status) {
        return switch (status) {
            case AVAILABLE -> "Đang bán";
            case RESERVED -> "Đang giữ";
            case SOLD -> "Đã bán";
            case EXPIRED -> "Hết hạn";
            case DRAFT -> "Nháp";
            case HIDDEN -> "Đang ẩn";
            case CANCELLED -> "Đã hủy";
        };
    }

    private BigDecimal zero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal toBigDecimal(Object value) {
        return value instanceof BigDecimal decimal
                ? decimal
                : new BigDecimal(value.toString());
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate;
        }

        if (value instanceof Date sqlDate) {
            return sqlDate.toLocalDate();
        }

        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate();
        }

        return LocalDate.parse(value.toString());
    }
}