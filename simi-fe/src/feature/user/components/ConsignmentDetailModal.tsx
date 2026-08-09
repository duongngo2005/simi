import styles from "./ConsignmentDetailModal.module.css";
import { formatPrice } from "../../../utils/formatPrice";
import { useGetConsignmentDetail } from "../../consignment/hooks/useConsignment";

const STATUS_LABEL: Record<string, string> = {
  DRAFT: "Nháp",
  ACTIVE: "Đang ký gửi",
  PENDING_SETTLEMENT: "Chờ thanh toán",
  SETTLED: "Đã thanh toán",
  CLOSED: "Đã hoàn tất",
};

const ITEM_STATUS_LABEL: Record<string, string> = {
  DRAFT: "Nháp",
  ACTIVE: "Đang bán",
  RESERVED: "Giữ chỗ",
  EXPIRED: "Quá hạn",
  SOLD: "Đã bán",
  CANCELLED: "Đã hủy",
  RETURNED: "Đã trả lại",
  DONATED: "Từ thiện",
};

interface ConsignmentDetailModalProps {
  id: number | null;
  onClose: () => void;
}

export const ConsignmentDetailModal = ({ id, onClose }: ConsignmentDetailModalProps) => {
  const { data: detail, isLoading, isError } = useGetConsignmentDetail(id || 0);

  if (!id) return null;

  const consignment = detail?.consignmentResponse;
  const items = detail?.consignmentItemResponses || [];

  const soldCount = items.filter((i) => i.status === "SOLD").length;
  const activeCount = items.filter((i) => i.status === "ACTIVE").length;
  const expiredCount = items.filter((i) => i.status === "EXPIRED").length;

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.header}>
          <div className={styles.titleWrapper}>
            <h3 className={styles.title}>Chi tiết lô ký gửi #{id}</h3>
            {consignment && (
              <span className={`${styles.statusBadge} ${styles[`status${consignment.status}`]}`}>
                {STATUS_LABEL[consignment.status] || consignment.status}
              </span>
            )}
          </div>
          <button className={styles.closeBtn} onClick={onClose}>
            Đóng
          </button>
        </div>

        {isLoading ? (
          <div className={styles.stateBox}>Đang tải chi tiết lô ký gửi...</div>
        ) : isError || !detail ? (
          <div className={styles.stateBox}>Không thể tải chi tiết lô ký gửi</div>
        ) : (
          <div className={styles.body}>
            <div className={styles.summaryBar}>
              <span>Tổng số: <strong>{items.length} món</strong></span>
              <span>Đang bán: <strong>{activeCount}</strong></span>
              <span>Đã bán: <strong>{soldCount}</strong></span>
              <span>Đã hết hạn: <strong>{expiredCount}</strong></span>
            </div>

            <div className={styles.itemList}>
              {items.map((item) => {
                const product = item.productDetailResponse;
                const price = product?.currentPrice || 0;
                const thumb = product?.thumbnail || product?.productImageResponses?.[0]?.imageUrl;

                return (
                  <div key={item.id} className={styles.itemCard}>
                    <div className={styles.itemHeader}>
                      {thumb ? (
                        <img src={thumb} alt={product?.name} className={styles.thumbnail} />
                      ) : (
                        <div className={styles.noThumbnail}>No Img</div>
                      )}

                      <div className={styles.itemInfo}>
                        <span className={styles.itemName}>{product?.name || "Sản phẩm"}</span>
                        <div className={styles.itemMeta}>
                          {product?.size && <span>Size: {product.size}</span>}
                          {product?.color && <span>Màu: {product.color}</span>}
                        </div>
                      </div>

                      <div className={styles.itemRight}>
                        <span className={`${styles.itemStatus} ${styles[`itemStatus${item.status}`]}`}>
                          {ITEM_STATUS_LABEL[item.status] || item.status}
                        </span>
                        <span className={styles.itemPrice}>{formatPrice(price)}</span>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
