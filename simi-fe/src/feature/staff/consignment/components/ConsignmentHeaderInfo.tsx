import type { ConsignmentResponse } from "../types/staffConsignment.type";
import styles from "./ConsignmentHeaderInfo.module.css";

interface ConsignmentHeaderInfoProps {
  consignment: ConsignmentResponse;
}

export const ConsignmentHeaderInfo = ({ consignment }: ConsignmentHeaderInfoProps) => {
  const activeItem = consignment.totalItem - consignment.soldItem;

  const formatDate = (dateString?: string) => {
    if (!dateString) return "—";
    return new Date(dateString).toLocaleDateString("vi-VN");
  };

  return (
    <div className={styles.container}>
      <div className={styles.infoGrid}>
        <div className={styles.infoItem}>
          <span className={styles.label}>Khách gửi:</span>
          <span className={styles.value}>{consignment.consignorName}</span>
        </div>
        <div className={styles.infoItem}>
          <span className={styles.label}>Nhân viên nhận:</span>
          <span className={styles.value}>{consignment.receivedName}</span>
        </div>
        <div className={styles.infoItem}>
          <span className={styles.label}>Ngày bắt đầu:</span>
          <span className={styles.value}>{formatDate(consignment.startDate)}</span>
        </div>
        <div className={styles.infoItem}>
          <span className={styles.label}>Ngày hết hạn:</span>
          <span className={styles.value}>{formatDate(consignment.expiryDate)}</span>
        </div>
        <div className={styles.infoItemFull}>
          <span className={styles.label}>Ghi chú:</span>
          <span className={styles.value}>{consignment.note || "Không có"}</span>
        </div>
      </div>

      <div className={styles.statsRow}>
        <div className={styles.statItem}>
          <span className={styles.statLabel}>Tổng sản phẩm:</span>
          <span className={styles.statValue}>{consignment.totalItem}</span>
        </div>
        <div className={styles.statItem}>
          <span className={styles.statLabel}>Đang bán:</span>
          <span className={styles.statValue}>{activeItem}</span>
        </div>
        <div className={styles.statItem}>
          <span className={styles.statLabel}>Đã bán:</span>
          <span className={styles.statValue}>{consignment.soldItem}</span>
        </div>
      </div>
    </div>
  );
};
