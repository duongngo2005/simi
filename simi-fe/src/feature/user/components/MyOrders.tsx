import { useState } from "react";
import styles from "./MyOrders.module.css";
import { formatPrice } from "../../../utils/formatPrice";
import { useGetMyOrders } from "../../order/hooks/useOrder";
import type { OrderFilterRequest } from "../../order/types/order.type";
import { OrderDetailModal } from "./OrderDetailModal";

const STATUS_LABEL: Record<string, string> = {
  PENDING: "Chờ xác nhận",
  PACKING: "Đang đóng gói",
  SHIPPING: "Đang giao hàng",
  COMPLETED: "Đã hoàn thành",
  CANCELLED: "Đã hủy",
};

const TABS = [
  { key: "", label: "Tất cả" },
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

  const handleStatusChange = (status: string) => {
    setFilters((prev) => ({
      ...prev,
      orderStatus: status,
      page: 0,
    }));
  };

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>Đơn hàng của tôi ({pageData?.totalElements || 0})</h2>

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
                  <span className={styles.orderId}>Đơn hàng #{order.id}</span>
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
                  <div className={styles.noThumbnail}>No Img</div>
                )}
                <div className={styles.itemInfo}>
                  <span className={styles.itemName}>{order.firstItemName || "Sản phẩm"}</span>
                  {order.totalItem > 1 && (
                    <span className={styles.itemCount}>Và {order.totalItem - 1} sản phẩm khác</span>
                  )}
                </div>
              </div>

              <div className={styles.cardFooter}>
                <div className={styles.totalWrapper}>
                  <span className={styles.totalLabel}>Tổng tiền:</span>
                  <strong className={styles.totalAmount}>{formatPrice(order.finalAmount)}</strong>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal xem chi tiết đơn hàng */}
      <OrderDetailModal
        orderId={selectedOrderId}
        onClose={() => setSelectedOrderId(null)}
      />
    </div>
  );
};
