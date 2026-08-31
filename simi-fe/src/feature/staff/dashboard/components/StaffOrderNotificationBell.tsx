import { useEffect, useId, useRef, useState } from "react";
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
  const notificationPanelId = useId();
  const wrapperRef = useRef<HTMLDivElement>(null);
  const { data, isLoading } = useStaffNotifications();
  const markAsRead = useMarkStaffNotificationsAsRead();

  const unreadCount = data?.unreadCount ?? 0;

  const handleToggle = () => {
    const willOpen = !isOpen;
    setIsOpen(willOpen);
    if (willOpen && unreadCount > 0) {
      markAsRead.mutate();
    }
  };

  useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") setIsOpen(false);
    };

    const handlePointerDown = (event: PointerEvent) => {
      const target = event.target;
      if (target instanceof Node && !wrapperRef.current?.contains(target)) {
        setIsOpen(false);
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    window.addEventListener("pointerdown", handlePointerDown);
    return () => {
      window.removeEventListener("keydown", handleKeyDown);
      window.removeEventListener("pointerdown", handlePointerDown);
    };
  }, [isOpen]);

  return (
    <div className={styles.wrapper} ref={wrapperRef}>
      <button
        type="button"
        className={styles.bellButton}
        onClick={handleToggle}
        aria-label="Thông báo đơn hàng"
        aria-expanded={isOpen}
        aria-controls={notificationPanelId}
        aria-haspopup="dialog"
      >
        <span className={styles.bellText}>Thông báo</span>
        {unreadCount > 0 && (
          <span className={styles.badge}>
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <section
          className={styles.popover}
          id={notificationPanelId}
          role="dialog"
          aria-modal="false"
          aria-labelledby="staff-notification-title"
        >
          <header className={styles.popoverHeader}>
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
                aria-label="Đóng thông báo"
                title="Đóng thông báo"
              >
                <span aria-hidden="true">×</span>
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
      )}
    </div>
  );
}
