import { Link } from "react-router";
import type { RecentOrder } from "../types/dashboard.type";
import { formatPrice } from "../../../../utils/formatPrice";
import styles from "./RecentOrdersTable.module.css";

interface RecentOrdersTableProps {
  orders?: RecentOrder[];
}

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

export const RecentOrdersTable = ({ orders = [] }: RecentOrdersTableProps) => {
  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div>
          <h3 className={styles.title}>Đơn hàng gần đây</h3>
          <p className={styles.subtitle}>
            {orders.length > 0
              ? `${orders.length} đơn phát sinh gần nhất`
              : "Chưa có phát sinh giao dịch mới"}
          </p>
        </div>
        <Link to="/staff/orders" className={styles.viewLink}>
          Tất cả đơn hàng
        </Link>
      </div>

      {orders.length === 0 ? (
        <div className={styles.empty}>Chưa có đơn hàng nào phát sinh trong hệ thống.</div>
      ) : (
        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th style={{ width: "80px" }}>Mã đơn</th>
                <th>Khách hàng</th>
                <th style={{ width: "100px", textAlign: "center" }}>Kênh</th>
                <th style={{ width: "110px", textAlign: "center" }}>Thanh toán</th>
                <th style={{ width: "130px", textAlign: "right" }}>Tổng tiền</th>
                <th style={{ width: "130px", textAlign: "center" }}>Trạng thái</th>
                <th style={{ width: "110px", textAlign: "center" }}>Ngày tạo</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => {
                const statusCfg = ORDER_STATUS_CONFIG[order.orderStatus] || {
                  label: order.orderStatus,
                  className: styles.statusDefault,
                };

                return (
                  <tr key={order.id}>
                    <td>
                      <Link to={`/staff/orders?orderId=${order.id}`} className={styles.orderIdLink}>
                        #{order.id}
                      </Link>
                    </td>
                    <td>
                      <span className={styles.customerName}>{order.recipientName}</span>
                    </td>
                    <td style={{ textAlign: "center" }}>
                      <span className={styles.channelText}>
                        {order.orderChannel === "ONLINE" ? "Online" : "Tại quầy"}
                      </span>
                    </td>
                    <td style={{ textAlign: "center" }}>
                      <span className={styles.paymentText}>{order.paymentMethod}</span>
                    </td>
                    <td className={styles.amount}>
                      {formatPrice(order.finalAmount || 0)}
                    </td>
                    <td style={{ textAlign: "center" }}>
                      <span className={`${styles.statusPill} ${statusCfg.className}`}>
                        {statusCfg.label}
                      </span>
                    </td>
                    <td className={styles.dateText}>
                      {order.createdDate
                        ? new Date(order.createdDate).toLocaleDateString("vi-VN")
                        : "-"}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};