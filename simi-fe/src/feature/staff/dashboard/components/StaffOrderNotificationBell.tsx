import { useState } from "react";
import { Link } from "react-router";
import { formatPrice } from "../../../../utils/formatPrice";
import {
  useMarkStaffNotificationsAsRead,
  useStaffNotifications,
} from "../hooks/useStaffNotifications";
import styles from "./StaffOrderNotificationBell.module.css";

const ORDER_STATUS_LABEL: Record<string, string> = {
  PENDING: "Chờ xác nhận",
  PENDING_PAYMENT: "Chờ thanh toán",
  PACKING: "Đang đóng gói",
  SHIPPING: "Đang giao",
  COMPLETED: "Hoàn thành",
  CANCELLED: "Đã hủy",
  EXPIRED: "Hết hạn",
};

export function StaffOrderNotificationBell() {
  const [isOpen, setIsOpen] = useState(false);
  const { data, isLoading } = useStaffNotifications();
  const markAsRead = useMarkStaffNotificationsAsRead();

  const unreadCount = data?.unreadCount ?? 0;

  const handleOpen = () => {
    setIsOpen(true);
    if (unreadCount > 0) {
      markAsRead.mutate();
    }
  };

  return (
    <>
      <button
        type="button"
        className={styles.bellButton}
        onClick={handleOpen}
        aria-label="Thông báo đơn hàng"
      >
        <span className={styles.bellText}>Thông báo</span>
        {unreadCount > 0 && (
          <span className={styles.badge}>
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div
          className={styles.backdrop}
          onMouseDown={() => setIsOpen(false)}
          role="presentation"
        >
          <section
            className={styles.modal}
            role="dialog"
            aria-modal="true"
            aria-labelledby="staff-notification-title"
            onMouseDown={(e) => e.stopPropagation()}
          >
            <header className={styles.modalHeader}>
              <div>
                <h2 id="staff-notification-title" className={styles.modalTitle}>
                  Thông báo đơn hàng mới
                </h2>
                <p className={styles.modalSubtitle}>Đơn hàng phát sinh gần nhất trong hệ thống.</p>
              </div>

              <button
                type="button"
                className={styles.closeButton}
                onClick={() => setIsOpen(false)}
                aria-label="Đóng"
              >
                Đóng
              </button>
            </header>

            <div className={styles.notificationList}>
              {isLoading && (
                <div className={styles.emptyState}>Đang tải thông báo…</div>
              )}

              {!isLoading && data?.notifications.length === 0 && (
                <div className={styles.emptyState}>Chưa có thông báo mới.</div>
              )}

              {data?.notifications.map((notification) => (
                <Link
                  key={notification.id}
                  to={`/staff/orders?orderId=${notification.orderId}`}
                  className={`${styles.notificationItem} ${
                    notification.read ? "" : styles.unread
                  }`}
                  onClick={() => setIsOpen(false)}
                >
                  <div className={styles.orderBadge}>
                    <span>#{notification.orderId}</span>
                  </div>

                  <div className={styles.notificationContent}>
                    <div className={styles.customerName}>
                      {notification.recipientName}
                    </div>

                    <div className={styles.orderMeta}>
                      <span>{notification.paymentMethod}</span>
                      <span>·</span>
                      <span>
                        {ORDER_STATUS_LABEL[notification.orderStatus] ?? notification.orderStatus}
                      </span>
                    </div>

                    <div className={styles.timestamp}>
                      {new Date(notification.createdDate).toLocaleTimeString("vi-VN", {
                        hour: "2-digit",
                        minute: "2-digit",
                      })}{" "}
                      · {new Date(notification.createdDate).toLocaleDateString("vi-VN")}
                    </div>
                  </div>

                  <div className={styles.orderAmount}>
                    {formatPrice(notification.finalAmount)}
                  </div>
                </Link>
              ))}
            </div>
          </section>
        </div>
      )}
    </>
  );
}