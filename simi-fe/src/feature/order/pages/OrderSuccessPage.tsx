import { useLocation, Link, useNavigate } from "react-router";
import { useEffect } from "react";
import styles from "./OrderSuccessPage.module.css";

export const OrderSuccessPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const orderId = location.state?.orderId as number | undefined;

  useEffect(() => {
    if (!orderId) navigate("/");
  }, [orderId, navigate]);

  if (!orderId) return null;

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <div className={styles.iconWrapper}>
          <div className={styles.iconCircle}>
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
          </div>
        </div>

        <h1 className={styles.title}>Đặt hàng thành công!</h1>
        <p className={styles.subtitle}>
          Cảm ơn bạn đã mua sắm tại <strong>Simi</strong>. Đơn hàng của bạn đã được ghi nhận và đang chờ nhân viên xác nhận đóng gói.
        </p>

        <div className={styles.orderIdBox}>
          <span className={styles.orderIdLabel}>Mã đơn hàng</span>
          <span className={styles.orderIdValue}>#{String(orderId).padStart(6, "0")}</span>
          <span className={styles.paymentMethodText}>Thanh toán khi nhận hàng (COD)</span>
        </div>

        <div className={styles.steps}>
          <div className={styles.step}>
            <span className={styles.stepNumber}>1</span>
            <div className={styles.stepText}>
              <strong>Xác nhận qua email</strong>
              <span>Hóa đơn và thông tin chi tiết đơn hàng đã được gửi tới email của bạn.</span>
            </div>
          </div>

          <div className={styles.step}>
            <span className={styles.stepNumber}>2</span>
            <div className={styles.stepText}>
              <strong>Chuẩn bị & Đóng gói</strong>
              <span>Nhân viên Simi sẽ kiểm tra tình trạng đồ và đóng gói cẩn thận.</span>
            </div>
          </div>

          <div className={styles.step}>
            <span className={styles.stepNumber}>3</span>
            <div className={styles.stepText}>
              <strong>Giao hàng tận nơi</strong>
              <span>Đơn vị vận chuyển sẽ giao hàng trong vòng 2 - 4 ngày làm việc.</span>
            </div>
          </div>
        </div>

        <div className={styles.actions}>
          <Link to="/" className={styles.btnPrimary}>
            Tiếp tục mua sắm
          </Link>
          <Link to="/profile" className={styles.btnSecondary}>
            Xem đơn hàng của tôi
          </Link>
        </div>
      </div>
    </div>
  );
};