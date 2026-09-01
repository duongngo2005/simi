import { useState } from "react";
import styles from "./MyOrders.module.css";
import { formatPrice } from "../../../utils/formatPrice";
import { useGetMyOrders, useRetryPayment } from "../../order/hooks/useOrder";
import type { OrderFilterRequest } from "../../order/types/order.type";
import { OrderDetailModal } from "./OrderDetailModal";
import { getServerError } from "../../../utils/getMessageError";

const STATUS_LABEL: Record<string, string> = {
  PENDING: "Chờ xác nhận",
  PENDING_PAYMENT: "Chờ thanh toán",
  PACKING: "Đang đóng gói",
  SHIPPING: "Đang giao hàng",
  COMPLETED: "Đã hoàn thành",
  CANCELLED: "Đã hủy",
  EXPIRED: "Hết hạn",               
};

const TABS = [
  { key: "", label: "Tất cả" },
  { key: "PENDING_PAYMENT", label: "Chờ thanh toán" }, 
  { key: "PENDING", label: "Chờ xác nhận" },
  { key: "PACKING", label: "Đang đóng gói" },
  { key: "SHIPPING", label: "Đang giao hàng" },
  { key: "COMPLETED", label: "Đã hoàn thành" },
  { key: "CANCELLED", label: "Đã hủy" },
];

export const MyOrders = () => {
  const [selectedOrderId, setSelectedOrderId] = useState<number | null>(null);

  const [filters, setFilters] = useState<OrderFilterRequest>({
    orderStatus: "",
    orderChannel: "",
    keyword: "",
    fromDate: "",
    toDate: "",
    page: 0,
    size: 10,
    sortBy: "createdDate",
    sortDir: "desc",
  });

  const { data: pageData, isLoading, isError } = useGetMyOrders(filters);
  const orders = pageData?.content || [];

  const { mutateAsync: retryPayment, isPending: isRetrying } = useRetryPayment();

  const handleStatusChange = (status: string) => {
    setFilters((prev) => ({
      ...prev,
      orderStatus: status,
      page: 0,
    }));
  };

  const handleRetryPayment = async (orderId: number, e: React.MouseEvent) => {
    e.stopPropagation(); 
    try {
      const res = await retryPayment(orderId);
      if (res.body?.paymentUrl) {
        window.location.assign(res.body.paymentUrl);
      } else {
        alert("Không tìm thấy đường dẫn thanh toán.");
      }
    } catch (error) {
      alert(getServerError(error, "Không thể thanh toán lại. Đơn hàng có thể đã hết thời gian giữ chỗ."));
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.sectionHeader}>
        <h2 className={styles.title}>Đơn hàng của tôi</h2>
        <p className={styles.count}>{pageData?.totalElements || 0} đơn hàng</p>
      </div>

      <div className={styles.filterTabs}>
        {TABS.map((tab) => (
          <button
            key={tab.key}
            className={`${styles.tabBtn} ${filters.orderStatus === tab.key ? styles.tabActive : ""}`}
            onClick={() => handleStatusChange(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className={styles.stateBox}>Đang tải danh sách đơn hàng...</div>
      ) : isError ? (
        <div className={styles.stateBox}>Không thể tải danh sách đơn hàng</div>
      ) : orders.length === 0 ? (
        <div className={styles.emptyBox}>Không có đơn hàng nào</div>
      ) : (
        <div className={styles.orderList}>
          {orders.map((order) => (
            <div
              key={order.id}
              className={styles.orderCard}
              onClick={() => setSelectedOrderId(order.id)}
            >
              <div className={styles.cardHeader}>
                <div className={styles.headerLeft}>
                  <span className={styles.orderId}>ĐƠN #{String(order.id).padStart(6, "0")}</span>
                  <span className={styles.orderDate}>
                    {new Date(order.createdDate).toLocaleDateString("vi-VN")}
                  </span>
                </div>
                <span className={`${styles.statusBadge} ${styles[`status${order.orderStatus}`]}`}>
                  {STATUS_LABEL[order.orderStatus] || order.orderStatus}
                </span>
              </div>

              <div className={styles.cardBody}>
                {order.firstItemThumbnail ? (
                  <img src={order.firstItemThumbnail} alt={order.firstItemName} className={styles.thumbnail} />
                ) : (
                  <div className={styles.noThumbnail}>Ảnh đang cập nhật</div>
                )}
                <div className={styles.itemInfo}>
                  <span className={styles.itemName}>{order.firstItemName || "Sản phẩm"}</span>
                  {order.totalItem > 1 && (
                    <span className={styles.itemCount}>Và {order.totalItem - 1} sản phẩm khác</span>
                  )}
                </div>
              </div>

              <div className={styles.cardFooter}>
                {order.orderStatus === "PENDING_PAYMENT" && (
                  <button
                    className={styles.btnRetry}
                    onClick={(e) => handleRetryPayment(order.id, e)}
                    disabled={isRetrying}
                  >
                    {isRetrying ? "Đang xử lý..." : "Thanh toán"}
                  </button>
                )}

                <div className={styles.totalWrapper}>
                  <span className={styles.totalLabel}>Tổng thanh toán</span>
                  <strong className={styles.totalAmount}>{formatPrice(order.finalAmount)}</strong>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <OrderDetailModal
        orderId={selectedOrderId}
        onClose={() => setSelectedOrderId(null)}
      />
    </div>
  );
};
