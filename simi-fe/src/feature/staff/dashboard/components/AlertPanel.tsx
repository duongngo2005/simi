import { Link } from "react-router";
import type { ExpiringConsignment } from "../types/dashboard.type";
import styles from "./AlertPanel.module.css";

interface AlertPanelProps {
  expiringConsignments: ExpiringConsignment[];
  pendingSettlementCount: number;
  pendingDispositionCount: number;
}

export const AlertPanel = ({
  expiringConsignments = [],
  pendingSettlementCount = 0,
  pendingDispositionCount = 0,
}: AlertPanelProps) => {
  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div>
          <h3 className={styles.title}>Cảnh báo vận hành</h3>
          <p className={styles.subtitle}>Theo dõi hạn ký gửi và xử lý tồn</p>
        </div>
      </div>

      {/* 2 khối số liệu tóm tắt */}
      <div className={styles.quickBoxes}>
        <Link to="/staff/settlements" className={styles.box}>
          <div className={styles.boxNumber}>{pendingSettlementCount}</div>
          <div className={styles.boxLabel}>Lô chờ quyết toán tiền</div>
        </Link>

        <Link to="/staff/consignments" className={styles.box}>
          <div className={styles.boxNumber}>{pendingDispositionCount}</div>
          <div className={styles.boxLabel}>Đồ cần trả hoặc quyên góp</div>
        </Link>
      </div>

      {/* Danh sách sắp hết hạn */}
      <div className={styles.listSection}>
        <span className={styles.sectionTitle}>Lô hàng sắp hết hạn (7 ngày tới)</span>

        {expiringConsignments.length === 0 ? (
          <div className={styles.empty}>Không có lô hàng nào sắp hết hạn.</div>
        ) : (
          <div className={styles.list}>
            {expiringConsignments.map((c) => (
              <Link
                key={c.consignmentId}
                to={`/staff/consignments/${c.consignmentId}`}
                className={styles.rowItem}
              >
                <div className={styles.meta}>
                  <span className={styles.code}>#{c.consignmentId}</span>
                  <span className={styles.name}>{c.consignorName}</span>
                  <span className={styles.count}>({c.itemCount} món)</span>
                </div>
                <span className={styles.daysBadge}>Còn {c.daysRemaining} ngày</span>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};