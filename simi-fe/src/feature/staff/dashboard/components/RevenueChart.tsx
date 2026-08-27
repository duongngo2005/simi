import type { RevenuePoint } from "../types/dashboard.type";
import { formatPrice } from "../../../../utils/formatPrice";
import styles from "./RevenueChart.module.css";

interface RevenueChartProps {
  points?: RevenuePoint[];
}

export const RevenueChart = ({ points = [] }: RevenueChartProps) => {
  const maxRevenue = Math.max(...points.map((p) => p.revenue || 0), 500_000);

  return (
    <div className={styles.chartContainer}>
      <div className={styles.chartBars}>
        {points.map((point) => {
          const heightPercent = Math.max(((point.revenue || 0) / maxRevenue) * 100, 4);

          return (
            <div className={styles.chartCol} key={point.date}>
              <div className={styles.chartTooltip}>
                <span className={styles.tooltipRevenue}>{formatPrice(point.revenue)}</span>
                <span className={styles.tooltipCount}>
                  {point.completedOrderCount} đơn hoàn thành
                </span>
              </div>

              <div
                className={styles.barFill}
                style={{ height: `${heightPercent}%` }}
                aria-label={`${point.date}: ${formatPrice(point.revenue)}`}
              />

              <span className={styles.chartDateLabel}>
                {point.date
                  ? new Date(`${point.date}T00:00:00`).toLocaleDateString("vi-VN", {
                      day: "2-digit",
                      month: "2-digit",
                    })
                  : ""}
              </span>
            </div>
          );
        })}

        {points.length === 0 && (
          <div className={styles.emptyState}>
            <span>Chưa có dữ liệu doanh thu trong khoảng thời gian này.</span>
          </div>
        )}
      </div>
    </div>
  );
};