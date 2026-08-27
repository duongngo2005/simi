package com.ndd.simi_be.dashboard.service;

import com.ndd.simi_be.dashboard.dto.StaffNotificationResponse;
import com.ndd.simi_be.dashboard.dto.StaffNotificationsResponse;
import com.ndd.simi_be.dashboard.entity.StaffNotification;
import com.ndd.simi_be.dashboard.repository.StaffNotificationRepository;
import com.ndd.simi_be.order.entity.Order;
import com.ndd.simi_be.order.repository.OrderRepository;
import com.ndd.simi_be.payment.entity.Payment;
import com.ndd.simi_be.user.entity.User;
import com.ndd.simi_be.user.enums.Role;
import com.ndd.simi_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffNotificationService {
    private static final String NEW_ORDER = "NEW_ORDER";

    private final StaffNotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createOrderNotifications(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        List<User> recipients = userRepository.findByRoleIn(
                List.of(Role.STAFF, Role.ADMIN)
        );

        for (User recipient : recipients) {
            try {
                notificationRepository.save(
                        StaffNotification.builder()
                                .recipient(recipient)
                                .order(order)
                                .notificationType(NEW_ORDER)
                                .build()
                );
            } catch (DataIntegrityViolationException ignored) {
                // Event được gửi lại không tạo notification trùng.
            }
        }
    }

    @Transactional(readOnly = true)
    public StaffNotificationsResponse getNotifications(User staff) {
        List<StaffNotificationResponse> notifications =
                notificationRepository.findLatestByRecipientId(
                                staff.getId(),
                                PageRequest.of(0, 10)
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return StaffNotificationsResponse.builder()
                .unreadCount(
                        notificationRepository.countByRecipient_IdAndReadAtIsNull(staff.getId())
                )
                .notifications(notifications)
                .build();
    }

    @Transactional
    public void markAllAsRead(User staff) {
        List<StaffNotification> notifications =
                notificationRepository.findByRecipient_IdAndReadAtIsNull(staff.getId());

        LocalDateTime now = LocalDateTime.now();

        notifications.forEach(notification -> notification.setReadAt(now));
    }

    private StaffNotificationResponse toResponse(StaffNotification notification) {
        Order order = notification.getOrder();

        String paymentMethod = order.getPayments().stream()
                .max(Comparator.comparing(Payment::getCreatedDate))
                .map(payment -> payment.getPaymentMethod().name())
                .orElse("-");

        return StaffNotificationResponse.builder()
                .id(notification.getId())
                .read(notification.getReadAt() != null)
                .createdDate(notification.getCreatedDate())
                .orderId(order.getId())
                .recipientName(order.getRecipientName())
                .orderStatus(order.getOrderStatus().name())
                .paymentMethod(paymentMethod)
                .finalAmount(order.getFinalAmount())
                .build();
    }
}