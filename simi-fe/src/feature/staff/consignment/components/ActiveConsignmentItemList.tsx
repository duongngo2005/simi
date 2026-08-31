import type { ConsignmentItemResponse } from "../types/staffConsignment.type";
import styles from "./ActiveConsignmentItemList.module.css";

interface ActiveConsignmentItemListProps {
  items: ConsignmentItemResponse[];
}

const STATUS_LABEL: Record<string, string> = {
  ACTIVE: "Đang bán",
  RESERVED: "Đang giữ chỗ",
  SOLD: "Đã bán",
  EXPIRED: "Hết hạn",
};

export const ActiveConsignmentItemList = ({ items }: ActiveConsignmentItemListProps) => {
  const formatPrice = (price?: number) => {
    if (price === undefined || price === null) return "—";
    return price.toLocaleString("vi-VN") + "đ";
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return "—";
    return new Date(dateString).toLocaleDateString("vi-VN");
  };

  if (!items || items.length === 0) {
    return <div className={styles.emptyState}>Không có sản phẩm nào trong lô hàng này.</div>;
  }

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>Danh sách sản phẩm ({items.length})</h2>

      <div className={styles.itemList}>
        {items.map((item) => {
          const product = item.productDetailResponse;
          const currentPrice = product?.currentPrice || 0;
          const consignorPayout = currentPrice * (1 - (item.commissionRate || 0));
          const schedules = item.priceScheduleResponses || [];

          return (
            <div key={item.id} className={styles.itemCard}>
              <div className={styles.itemHeader}>
                <div className={styles.productMain}>
                  {product?.thumbnail ? (
                    <img src={product.thumbnail} alt={product.name} className={styles.thumbnail} />
                  ) : (
                    <div className={styles.noThumbnail}>Ảnh đang cập nhật</div>
                  )}

                  <div className={styles.productDetails}>
                    <h3 className={styles.productName}>{product?.name || "Chưa có tên"}</h3>
                    <div className={styles.metaRow}>
                      {product?.size && <span>Size: {product.size}</span>}
                      {product?.color && <span>Màu: {product.color}</span>}
                      {product?.productCondition && <span>Tình trạng: {product.productCondition}</span>}
                    </div>
                  </div>
                </div>

                <span className={`${styles.statusBadge} ${styles[`status_${item.status}`]}`}>
                  {STATUS_LABEL[item.status] ?? item.status}
                </span>
              </div>

              <div className={styles.financialRow}>
                <div className={styles.financialItem}>
                  <span className={styles.financialLabel}>Giá bán hiện tại:</span>
                  <span className={styles.currentPrice}>{formatPrice(currentPrice)}</span>
                </div>
                <div className={styles.financialItem}>
                  <span className={styles.financialLabel}>Tỷ lệ hoa hồng:</span>
                  <span className={styles.financialValue}>{((item.commissionRate || 0) * 100).toFixed(0)}%</span>
                </div>
                <div className={styles.financialItem}>
                  <span className={styles.financialLabel}>Khách nhận dự kiến:</span>
                  <span className={styles.payoutValue}>{formatPrice(consignorPayout)}</span>
                </div>
              </div>

              {schedules.length > 0 && (
                <div className={styles.scheduleSection}>
                  <span className={styles.scheduleTitle}>Lịch điều chỉnh giá:</span>
                  <div className={styles.scheduleList}>
                    {schedules.map((sched) => (
                      <div key={sched.id} className={styles.scheduleTag}>
                        <span>Sau {sched.effectiveAfterDays} ngày:</span>
                        <strong className={styles.schedulePrice}>{formatPrice(sched.price)}</strong>
                        <span className={`${styles.scheduleStatus} ${styles[`schedStatus_${sched.status}`]}`}>
                          {sched.status === "APPLIED" ? `Đã áp dụng (${formatDate(sched.appliedAt)})` : "Chờ áp dụng"}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
