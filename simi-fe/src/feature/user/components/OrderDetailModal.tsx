import styles from "./OrderDetailModal.module.css";
import { formatPrice } from "../../../utils/formatPrice";
import { useGetOrderDetails } from "../../order/hooks/useOrder";

const STATUS_LABEL: Record<string, string> = {
  PENDING: "Chờ xác nhận",
  PACKING: "Đang đóng gói",
  SHIPPING: "Đang giao hàng",
  COMPLETED: "Đã hoàn thành",
  CANCELLED: "Đã hủy",
};

interface OrderDetailModalProps {
  orderId: number | null;
  onClose: () => void;
}

export const OrderDetailModal = ({ orderId, onClose }: OrderDetailModalProps) => {
  const { data: order, isLoading, isError } = useGetOrderDetails(orderId || 0);

  if (!orderId) return null;

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.header}>
          <div className={styles.titleWrapper}>
            <h3 className={styles.title}>Chi tiết đơn hàng #{orderId}</h3>
            {order && (
              <span className={`${styles.statusBadge} ${styles[`status${order.orderStatus}`]}`}>
                {STATUS_LABEL[order.orderStatus] || order.orderStatus}
              </span>
            )}
          </div>
          <button className={styles.closeBtn} onClick={onClose}>
            Đóng
          </button>
        </div>

        {isLoading ? (
          <div className={styles.stateBox}>Đang tải chi tiết đơn hàng...</div>
        ) : isError || !order ? (
          <div className={styles.stateBox}>Không thể tải chi tiết đơn hàng</div>
        ) : (
          <div className={styles.body}>
            <div className={styles.section}>
              <h4 className={styles.sectionTitle}>Thông tin nhận hàng</h4>
              <div className={styles.infoGrid}>
                <div className={styles.infoRow}>
                  <span className={styles.label}>Người nhận:</span>
                  <span className={styles.value}>{order.recipientName}</span>
                </div>
                <div className={styles.infoRow}>
                  <span className={styles.label}>Số điện thoại:</span>
                  <span className={styles.value}>{order.recipientPhone}</span>
                </div>
                <div className={styles.infoRow}>
                  <span className={styles.label}>Địa chỉ:</span>
                  <span className={styles.value}>
                    {order.addressDetail}, {order.ward}, {order.province}
                  </span>
                </div>
              </div>
            </div>

            <div className={styles.section}>
              <h4 className={styles.sectionTitle}>
                Sản phẩm trong đơn ({order.orderItems?.length || 0})
              </h4>
              <div className={styles.itemList}>
                {order.orderItems?.map((item) => (
                  <div key={item.id} className={styles.itemRow}>
                    {item.thumbnail ? (
                      <img src={item.thumbnail} alt={item.name} className={styles.thumbnail} />
                    ) : (
                      <div className={styles.noThumbnail}>No Img</div>
                    )}
                    <div className={styles.itemMeta}>
                      <span className={styles.itemName}>{item.name}</span>
                      <div className={styles.itemSubMeta}>
                        {item.brand && <span>{item.brand}</span>}
                        {item.size && <span>Size: {item.size}</span>}
                      </div>
                    </div>
                    <span className={styles.itemPrice}>{formatPrice(item.unitPrice)}</span>
                  </div>
                ))}
              </div>
            </div>

            <div className={styles.summarySection}>
              <div className={styles.summaryRow}>
                <span>Tạm tính:</span>
                <span>{formatPrice(order.subtotalAmount)}</span>
              </div>
              <div className={styles.summaryRow}>
                <span>Phí vận chuyển:</span>
                <span>{formatPrice(order.shippingFee)}</span>
              </div>
              {order.discount > 0 && (
                <div className={styles.summaryRow}>
                  <span>Giảm giá:</span>
                  <span>-{formatPrice(order.discount)}</span>
                </div>
              )}
              <div className={`${styles.summaryRow} ${styles.totalRow}`}>
                <span>Tổng thanh toán:</span>
                <strong className={styles.totalPrice}>{formatPrice(order.finalAmount)}</strong>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
