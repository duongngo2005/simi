import { useAuthStore } from "../../../store/useAuthStore";
import styles from "./MyInfo.module.css";

const ROLE_LABEL: Record<string, string> = {
  ADMIN: "Quản trị viên",
  STAFF: "Nhân viên",
  CUSTOMER: "Khách hàng",
};

const STATUS_LABEL: Record<string, string> = {
  ACTIVE: "Đang hoạt động",
  LOCKED: "Đã bị khóa",
};

export const MyInfo = () => {
  const { user } = useAuthStore();

  if (!user) return null;

  return (
    <div className={styles.wrapper}>
      <div className={styles.card}>
        <div className={styles.cardHeader}>
          <h2 className={styles.cardTitle}>Thông tin cá nhân</h2>
        </div>

        <div className={styles.infoGrid}>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Họ và tên</span>
            <span className={styles.infoValue}>{user.fullName}</span>
          </div>

          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Email</span>
            <span className={styles.infoValue}>{user.email}</span>
          </div>

          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Số điện thoại</span>
            <span className={styles.infoValue}>
              {user.phoneNumber ?? <span className={styles.empty}>Chưa cập nhật</span>}
            </span>
          </div>

          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Địa chỉ</span>
            <span className={styles.infoValue}>
              {user.address ?? <span className={styles.empty}>Chưa cập nhật</span>}
            </span>
          </div>

          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Vai trò</span>
            <span className={styles.infoValue}>{ROLE_LABEL[user.role] ?? user.role}</span>
          </div>

          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Trạng thái tài khoản</span>
            <span
              className={`${styles.statusBadge} ${
                user.status === "ACTIVE" ? styles.statusActive : styles.statusLocked
              }`}
            >
              {STATUS_LABEL[user.status] ?? user.status}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
