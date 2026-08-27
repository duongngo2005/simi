import styles from "./ProductStatusChart.module.css";

interface ProductStatusChartProps {
  distribution?: Record<string, number>;
}

// Bảng màu trạng thái trực quan, tách biệt rõ ràng và dịu mắt
const STATUS_CONFIG: Record<string, { label: string; color: string }> = {
  AVAILABLE: { label: "Đang bày bán", color: "#4F772D" }, // Xanh lá rêu
  SOLD: { label: "Đã bán", color: "#2E5B88" },           // Xanh dương trầm
  RESERVED: { label: "Giữ chỗ", color: "#D97706" },       // Vàng cam ấm
  EXPIRED: { label: "Hết hạn", color: "#BC3908" },        // Đỏ đất
  DRAFT: { label: "Nháp", color: "#8C7365" },             // Nâu nhạt
  CANCELLED: { label: "Đã hủy", color: "#9CA3AF" },       // Xám trung tính
};

export const ProductStatusChart = ({ distribution = {} }: ProductStatusChartProps) => {
  const total = Object.values(distribution).reduce((sum, count) => sum + count, 0);

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div>
          <h3 className={styles.title}>Trạng thái kho hàng</h3>
          <p className={styles.subtitle}>Phân bổ số lượng sản phẩm</p>
        </div>
        <span className={styles.totalBadge}>Tổng: {total}</span>
      </div>

      {/* Thanh chia tỷ lệ màu */}
      <div className={styles.segmentedBar}>
        {Object.entries(distribution).map(([status, count]) => {
          if (count === 0 || total === 0) return null;
          const pct = ((count / total) * 100).toFixed(1);
          const color = STATUS_CONFIG[status]?.color || "#8C7365";

          return (
            <div
              key={status}
              className={styles.segment}
              style={{ width: `${pct}%`, backgroundColor: color }}
              title={`${STATUS_CONFIG[status]?.label || status}: ${count} (${pct}%)`}
            />
          );
        })}
      </div>

      {/* Danh sách 2 cột như cũ */}
      <div className={styles.listGrid}>
        {Object.entries(STATUS_CONFIG).map(([status, conf]) => {
          const count = distribution[status] || 0;
          const pct = total > 0 ? ((count / total) * 100).toFixed(0) : "0";

          return (
            <div key={status} className={styles.listItem}>
              <div className={styles.itemLeft}>
                <span className={styles.dot} style={{ backgroundColor: conf.color }} />
                <span>{conf.label}</span>
              </div>
              <div className={styles.itemRight}>
                <span className={styles.count}>{count}</span>
                <span className={styles.pct}>({pct}%)</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};