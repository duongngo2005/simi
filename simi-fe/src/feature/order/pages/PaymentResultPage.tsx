import { useSearchParams, Link } from "react-router";
import styles from "./PaymentResultPage.module.css";

export const PaymentResultPage = () => {
  const [searchParams] = useSearchParams();

  const responseCode = searchParams.get("vnp_ResponseCode");
  const txnRef = searchParams.get("vnp_TxnRef");
  const amount = searchParams.get("vnp_Amount");
  const orderInfo = searchParams.get("vnp_OrderInfo");

  const isSuccess = responseCode === "00";

  // Trích xuất mã đơn hàng thực tế từ vnp_OrderInfo (ví dụ: "Thanh toan don hang #45")
  const orderIdMatch = orderInfo?.match(/#(\d+)/);
  const orderId = orderIdMatch ? orderIdMatch[1] : null;

  const formattedAmount = amount
    ? new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
        Number(amount) / 100
      )
    : "";

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <div className={styles.iconWrapper}>
          <div className={`${styles.iconCircle} ${isSuccess ? styles.success : styles.failed}`}>
            {isSuccess ? (
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                strokeLinecap="round"
                strokeLinejoin="round"
                className={styles.checkIcon}
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
            ) : (
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                strokeLinecap="round"
                strokeLinejoin="round"
                className={styles.checkIcon}
              >
                <line x1="18" y1="6" x2="6" y2="18" />
                <line x1="6" y1="6" x2="18" y2="18" />
              </svg>
            )}
          </div>
        </div>

        <h1 className={styles.title}>
          {isSuccess ? "Thanh toán thành công!" : "Thanh toán chưa hoàn tất"}
        </h1>

        <p className={styles.subtitle}>
          {isSuccess
            ? "Cảm ơn bạn đã mua sắm tại Simi. Đơn hàng của bạn đã được thanh toán thành công và đang được chuẩn bị giao."
            : "Giao dịch thanh toán qua VNPay chưa hoàn tất hoặc đã bị hủy. Đơn hàng vẫn đang ở trạng thái chờ thanh toán."}
        </p>

        <div className={styles.orderIdBox}>
          <span className={styles.orderIdLabel}>
            {orderId ? "Mã đơn hàng" : "Mã giao dịch"}
          </span>
          <span className={styles.orderIdValue}>
            {orderId ? `#${String(orderId).padStart(6, "0")}` : txnRef}
          </span>
          <span className={styles.paymentMethodText}>
            Thanh toán online (VNPay){formattedAmount ? ` · ${formattedAmount}` : ""}
          </span>
        </div>

        <div className={styles.actions}>
          {isSuccess ? (
            <>
              <Link to="/" className={styles.btnPrimary}>
                Tiếp tục mua sắm
              </Link>
              <Link to="/profile" className={styles.btnSecondary}>
                Xem đơn hàng của tôi
              </Link>
            </>
          ) : (
            <>
              <Link to="/profile" className={styles.btnPrimary}>
                Vào đơn hàng để thanh toán lại
              </Link>
              <Link to="/" className={styles.btnSecondary}>
                Quay lại trang chủ
              </Link>
            </>
          )}
        </div>
      </div>
    </div>
  );
};