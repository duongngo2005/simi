import { useState } from "react";
import { Link } from "react-router";
import { formatPrice } from "../../../../utils/formatPrice";
import type {
  DashboardRange,
  ProductOverviewItem,
  RevenuePoint,
  WorkQueueItem,
} from "../types/dashboard.type";
import styles from "./StaffDashboard.module.css";
import { useStaffDashboard } from "../hooks/useDashboard";

const ORDER_STATUS_CONFIG: Record<
  string,
  { label: string; className: string }
> = {
  PENDING: { label: "Chờ xác nhận", className: styles.statusWarning },
  PENDING_PAYMENT: { label: "Chờ thanh toán", className: styles.statusWarning },
  PACKING: { label: "Đang đóng gói", className: styles.statusInfo },
  SHIPPING: { label: "Đang giao", className: styles.statusInfo },
  COMPLETED: { label: "Hoàn thành", className: styles.statusSuccess },
  CANCELLED: { label: "Đã hủy", className: styles.statusDanger },
  EXPIRED: { label: "Hết hạn", className: styles.statusDanger },
};

const QUEUE_CONFIG: Record<
  WorkQueueItem["key"],
  { title: string; description: string; to: string; actionText: string }
> = {
  COD_CONFIRMATION: {
    title: "Đơn COD chờ xác nhận",
    description: "Đơn hàng COD mới cần gọi xác nhận với khách trước khi đóng gói.",
    to: "/staff/orders?status=PENDING&paymentMethod=COD",
    actionText: "Duyệt đơn",
  },
  PENDING_SETTLEMENT: {
    title: "Lô ký gửi đến hạn kết toán",
    description: "Lô hàng đã kết thúc thời hạn, đang chờ nhân viên đối soát tiền.",
    to: "/staff/consignments?status=PENDING_SETTLEMENT",
    actionText: "Kết toán",
  },
  RETURN_PENDING: {
    title: "Hàng chờ khách đến nhận lại",
    description: "Món hàng không bán được đang trong hạn chờ chủ hàng đến nhận.",
    to: "/staff/dispositions?type=RETURN&status=PENDING",
    actionText: "Trả hàng",
  },
  DONATION_CONFIRMATION: {
    title: "Món quá hạn chờ duyệt quyên góp",
    description: "Món hàng đã hết hạn nhận lại, cần xác nhận đem đi quyên góp.",
    to: "/staff/dispositions?type=DONATE&status=CONFIRM",
    actionText: "Quyên góp",
  },
};

const PRODUCT_STATUS_CLASS: Record<string, string> = {
  AVAILABLE: styles.productAvailable,
  RESERVED: styles.productReserved,
  SOLD: styles.productSold,
  EXPIRED: styles.productExpired,
  DRAFT: styles.productDraft,
  HIDDEN: styles.productHidden,
  CANCELLED: styles.productCancelled,
};

function RevenueChart({ points }: { points: RevenuePoint[] }) {
  const maxRevenue = Math.max(...points.map((p) => p.revenue), 1);

  return (
    <div className={styles.chartContainer}>
      <div className={styles.chartBars}>
        {points.map((point) => {
          const heightPercent = Math.max((point.revenue / maxRevenue) * 100, 4);

          return (
            <div className={styles.chartCol} key={point.date}>
              <div className={styles.chartTooltip}>
                <span className={styles.tooltipRevenue}>{formatPrice(point.revenue)}</span>
                <span className={styles.tooltipCount}>{point.completedOrderCount} đơn hoàn thành</span>
              </div>

              <div
                className={styles.barFill}
                style={{ height: `${heightPercent}%` }}
                aria-label={`${point.date}: ${formatPrice(point.revenue)}`}
              />

              <span className={styles.chartDateLabel}>
                {new Date(`${point.date}T00:00:00`).toLocaleDateString("vi-VN", {
                  day: "2-digit",
                  month: "2-digit",
                })}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function ProductOverview({ items }: { items: ProductOverviewItem[] }) {
  const total = items.reduce((acc, cur) => acc + cur.count, 0);

  return (
    <div className={styles.productOverview}>
      <div className={styles.productTotalRow}>
        <span>Tổng sản phẩm trong kho</span>
        <strong>{total.toLocaleString("vi-VN")}</strong>
      </div>

      <div className={styles.productList}>
        {items.map((item) => (
          <div className={styles.productRow} key={item.status}>
            <div className={styles.productLabelGroup}>
              <span className={`${styles.statusDot} ${PRODUCT_STATUS_CLASS[item.status] || ""}`} />
              <span className={styles.productLabelText}>{item.label}</span>
            </div>
            <span className={styles.productCount}>{item.count.toLocaleString("vi-VN")}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export function StaffDashboard() {
  const [range, setRange] = useState<DashboardRange>("7d");
  const { data, isLoading, isError, isFetching, refetch } = useStaffDashboard(range);

  if (isLoading) {
    return (
      <div className={styles.loadingCard}>
        <span>Đang tải dữ liệu tổng quan…</span>
      </div>
    );
  }

  if (isError || !data) {
    return (
      <div className={styles.errorCard}>
        <p>Không thể tải dữ liệu dashboard.</p>
        <button type="button" onClick={() => refetch()} className={styles.btnRetry}>
          Thử lại
        </button>
      </div>
    );
  }

  const { summary } = data;

  const primaryCards = [
    {
      title: "Doanh thu chưa ghi nhận",
      amount: formatPrice(summary.unofficialRevenue),
      subtext: `Đã TT: ${formatPrice(summary.unofficialPaidRevenue)} · Chưa TT: ${formatPrice(
        summary.unofficialUnpaidRevenue,
      )}`,
    },
    {
      title: "Doanh thu tháng này",
      amount: formatPrice(summary.completedRevenueThisMonth),
      subtext: null,
    },
    {
      title: "Đơn COD chờ xác nhận",
      amount: `${summary.unconfirmedCodOrderCount.toLocaleString("vi-VN")} đơn`,
      subtext: null,
      to: "/staff/orders?status=PENDING&paymentMethod=COD",
    },
  ];

  const secondaryStats = [
    {
      label: "Đơn hàng hôm nay",
      value: summary.todayOrderCount,
      to: "/staff/orders",
      isUrgent: false,
    },
    {
      label: "Lô ký gửi hoạt động",
      value: summary.activeConsignmentCount,
      to: "/staff/consignments?status=ACTIVE",
      isUrgent: false,
    },
    {
      label: "Lô sắp hết hạn (<7d)",
      value: summary.expiringConsignmentCount,
      to: "/staff/consignments?expiringSoon=true",
      isUrgent: summary.expiringConsignmentCount > 0,
    },
    {
      label: "Lô chờ kết toán",
      value: summary.pendingSettlementConsignmentCount,
      to: "/staff/consignments?status=PENDING_SETTLEMENT",
      isUrgent: summary.pendingSettlementConsignmentCount > 0,
    },
    {
      label: "Item chờ trả hàng",
      value: summary.pendingReturnItemCount,
      to: "/staff/dispositions?type=RETURN&status=PENDING",
      isUrgent: summary.pendingReturnItemCount > 0,
    },
    {
      label: "Chờ xác nhận quyên góp",
      value: summary.pendingDonationConfirmationCount,
      to: "/staff/dispositions?type=DONATE&status=CONFIRM",
      isUrgent: summary.pendingDonationConfirmationCount > 0,
    },
  ];

  return (
    <div className={styles.container}>
      <header className={styles.topBar}>
        <div>
          <h1 className={styles.pageTitle}>Tổng quan vận hành</h1>
          <p className={styles.subtitle}>
            Cập nhật lúc {new Date(data.generatedAt).toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" })} ·{" "}
            {new Date(data.generatedAt).toLocaleDateString("vi-VN")}
          </p>
        </div>

        <div className={styles.topActions}>
          <button
            type="button"
            className={styles.btnSecondary}
            onClick={() => refetch()}
            disabled={isFetching}
          >
            {isFetching ? "Đang đồng bộ…" : "Làm mới"}
          </button>

          <Link to="/staff/pos" className={styles.btnPrimary}>
            Tạo đơn tại quầy
          </Link>
        </div>
      </header>

      <section className={styles.primaryGrid}>
        {primaryCards.map((card) => {
          const innerContent = (
            <>
              <span className={styles.primaryCardTitle}>{card.title}</span>
              <div className={styles.primaryAmount}>{card.amount}</div>
              {card.subtext && (
                <div className={styles.primarySubtext}>{card.subtext}</div>
              )}
            </>
          );

          return card.to ? (
            <Link
              to={card.to}
              key={card.title}
              className={styles.primaryCard}
            >
              {innerContent}
            </Link>
          ) : (
            <div
              key={card.title}
              className={styles.primaryCard}
            >
              {innerContent}
            </div>
          );
        })}
      </section>

      <section className={styles.analyticsGrid}>
        <div className={styles.panel}>
          <div className={styles.panelHeader}>
            <div>
              <h2 className={styles.panelTitle}>Doanh thu hoàn thành</h2>
              <p className={styles.panelSubtitle}>Doanh thu từ các đơn hàng đã hoàn tất giao dịch.</p>
            </div>

            <div className={styles.rangeTabs}>
              <button
                type="button"
                className={`${styles.rangeBtn} ${range === "7d" ? styles.rangeBtnActive : ""}`}
                onClick={() => setRange("7d")}
              >
                7 ngày qua
              </button>
              <button
                type="button"
                className={`${styles.rangeBtn} ${range === "month" ? styles.rangeBtnActive : ""}`}
                onClick={() => setRange("month")}
              >
                Tháng này
              </button>
            </div>
          </div>

          <RevenueChart points={data.revenueSeries} />
        </div>

        <div className={styles.panel}>
          <div className={styles.panelHeader}>
            <div>
              <h2 className={styles.panelTitle}>Trạng thái kho hàng</h2>
              <p className={styles.panelSubtitle}>Phân bổ theo vòng đời sản phẩm.</p>
            </div>
          </div>

          <ProductOverview items={data.productOverview} />
        </div>
      </section>

      <section className={styles.secondaryGrid}>
        {secondaryStats.map((stat) => (
          <Link
            to={stat.to}
            key={stat.label}
            className={`${styles.secondaryCard} ${stat.isUrgent ? styles.secondaryUrgent : ""}`}
          >
            <span className={styles.secondaryLabel}>{stat.label}</span>
            <span className={styles.secondaryValue}>
              {stat.value.toLocaleString("vi-VN")}
            </span>
          </Link>
        ))}
      </section>

      <section className={styles.bottomGrid}>
        <div className={styles.panel}>
          <div className={styles.panelHeader}>
            <div>
              <h2 className={styles.panelTitle}>Việc cần xử lý ngay</h2>
              <p className={styles.panelSubtitle}>Hàng đợi nghiệp vụ cần nhân viên thao tác.</p>
            </div>
          </div>

          <div className={styles.queueList}>
            {data.workQueue
              .filter((item) => item.count > 0)
              .map((item) => {
                const config = QUEUE_CONFIG[item.key];
                return (
                  <div key={item.key} className={styles.queueItem}>
                    <div className={styles.queueCountBadge}>{item.count}</div>
                    <div className={styles.queueInfo}>
                      <span className={styles.queueTitle}>{config.title}</span>
                      <span className={styles.queueDesc}>{config.description}</span>
                    </div>
                    <Link to={config.to} className={styles.queueActionBtn}>
                      {config.actionText}
                    </Link>
                  </div>
                );
              })}

            {!data.workQueue.some((item) => item.count > 0) && (
              <div className={styles.emptyState}>
                <span>Không có việc tồn đọng cần xử lý.</span>
              </div>
            )}
          </div>
        </div>

        <div className={styles.panel}>
          <div className={styles.panelHeader}>
            <div>
              <h2 className={styles.panelTitle}>Đơn hàng gần đây</h2>
              <p className={styles.panelSubtitle}>
                {data.recentOrders.length > 0
                  ? `${data.recentOrders.length} đơn phát sinh gần nhất`
                  : "Chưa có phát sinh giao dịch mới"}
              </p>
            </div>
            <Link to="/staff/orders" className={styles.viewAllLink}>
              Xem tất cả đơn
            </Link>
          </div>

          <div className={styles.recentOrdersList}>
            {data.recentOrders.map((order) => {
              const statusCfg = ORDER_STATUS_CONFIG[order.orderStatus] || {
                label: order.orderStatus,
                className: styles.statusDefault,
              };

              return (
                <Link
                  to={`/staff/orders?orderId=${order.id}`}
                  key={order.id}
                  className={styles.orderRow}
                >
                  <div className={styles.orderMainInfo}>
                    <div className={styles.orderIdLine}>
                      <span className={styles.orderIdText}>#{order.id}</span>
                      <span className={styles.orderCustomer}>{order.recipientName}</span>
                    </div>
                    <div className={styles.orderMetaLine}>
                      <span>{order.orderChannel === "ONLINE" ? "Online" : "Tại quầy"}</span>
                      <span>·</span>
                      <span>{order.paymentMethod}</span>
                      <span>·</span>
                      <span>{new Date(order.createdDate).toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" })}</span>
                    </div>
                  </div>

                  <div className={styles.orderRightInfo}>
                    <span className={styles.orderAmount}>{formatPrice(order.finalAmount)}</span>
                    <span className={`${styles.statusBadge} ${statusCfg.className}`}>
                      {statusCfg.label}
                    </span>
                  </div>
                </Link>
              );
            })}

            {data.recentOrders.length === 0 && (
              <div className={styles.emptyState}>
                <span>Chưa có đơn hàng nào.</span>
              </div>
            )}
          </div>
        </div>
      </section>
    </div>
  );
}