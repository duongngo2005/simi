import { useLocation, Link, useNavigate } from "react-router";
import { useEffect } from "react";
import styles from "./OrderSuccessPage.module.css";

export const OrderSuccessPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const orderId = location.state?.orderId as number | undefined;

  // Nếu truy cập thẳng URL không có orderId thì đá về trang chủ
  useEffect(() => {
    if (!orderId) navigate("/");
  }, [orderId, navigate]);

  if (!orderId) return null;

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        {/* Icon thành công */}
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

        {/* Nội dung */}
        <h1 className={styles.title}>Đặt hàng thành công!</h1>
        <p className={styles.subtitle}>
          Cảm ơn bạn đã mua sắm tại <strong>Simi</strong>. 
          Đơn hàng của bạn đang được xử lý và sẽ sớm được giao đến tay bạn.
        </p>

        {/* Mã đơn hàng */}
        <div className={styles.orderIdBox}>
          <span className={styles.orderIdLabel}>Mã đơn hàng</span>
          <span className={styles.orderIdValue}>#{String(orderId).padStart(6, "0")}</span>
        </div>

        {/* Thông tin bước tiếp theo */}
        <div className={styles.steps}>
          <div className={styles.step}>
            <span className={styles.stepIcon}>📧</span>
            <div className={styles.stepText}>
              <strong>Xác nhận qua email</strong>
              <span>Chúng tôi sẽ gửi hóa đơn và thông tin đơn hàng vào email của bạn</span>
            </div>
          </div>
          <div className={styles.step}>
            <span className={styles.stepIcon}>📦</span>
            <div className={styles.stepText}>
              <strong>Đang đóng gói</strong>
              <span>Đơn hàng sẽ được chuẩn bị và giao trong 2-5 ngày làm việc</span>
            </div>
          </div>
          <div className={styles.step}>
            <span className={styles.stepIcon}>🚚</span>
            <div className={styles.stepText}>
              <strong>Theo dõi vận chuyển</strong>
              <span>Bạn sẽ nhận được thông báo khi đơn hàng được giao cho đơn vị vận chuyển</span>
            </div>
          </div>
        </div>

        {/* Nút điều hướng */}
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
