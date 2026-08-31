import { formatPrice } from "../../../../utils/formatPrice";
import styles from "./KpiCards.module.css";

interface KpiCardsProps {
  monthlyRevenue: number;
  monthlyCommission: number;
  pendingOrderCount: number;
  availableProductCount: number;
}

export const KpiCards = ({
  monthlyRevenue,
  monthlyCommission,
  pendingOrderCount,
  availableProductCount,
}: KpiCardsProps) => {
  return (
    <div className={styles.grid}>
      <div className={styles.card}>
        <span className={styles.label}>Doanh thu tháng</span>
        <div className={styles.value}>{formatPrice(monthlyRevenue || 0)}</div>
        <span className={styles.desc}>Tổng giá trị đơn hoàn thành</span>
      </div>

      <div className={styles.card}>
        <span className={styles.label}>Hoa hồng Simi</span>
        <div className={styles.value}>{formatPrice(monthlyCommission || 0)}</div>
        <span className={styles.desc}>Lợi nhuận từ các lô quyết toán</span>
      </div>

      <div className={styles.card}>
        <span className={styles.label}>Đơn chờ xử lý</span>
        <div className={styles.value}>{pendingOrderCount || 0}</div>
        <span className={styles.desc}>Đơn mới và đang đóng gói</span>
      </div>

      <div className={styles.card}>
        <span className={styles.label}>Sản phẩm trên kệ</span>
        <div className={styles.value}>{availableProductCount || 0}</div>
        <span className={styles.desc}>Đang bày bán trong kho</span>
      </div>
    </div>
  );
};
