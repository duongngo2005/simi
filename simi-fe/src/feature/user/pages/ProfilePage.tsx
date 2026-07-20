import { useNavigate, Link } from "react-router";
import { useAuthStore } from "../../../store/useAuthStore";
import api from "../../../lib/http/apiClient";
import styles from "./ProfilePage.module.css";

const ROLE_LABEL: Record<string, string> = {
  ADMIN: "Quản trị viên",
  STAFF: "Nhân viên",
  CUSTOMER: "Khách hàng",
};

const STATUS_LABEL: Record<string, string> = {
  ACTIVE: "Đang hoạt động",
  LOCKED: "Đã bị khóa",
};

export const ProfilePage = () => {
  const { user, clearAuth } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      // Gọi BE để thu hồi refresh token
      await api.post("/auth/logout");
    } catch {
      // Vẫn logout dù API lỗi
    } finally {
      clearAuth();
      navigate("/login");
    }
  };

  if (!user) {
    return (
      <div className={styles.page}>
        <div className={styles.stateWrapper}>
          <p>Bạn chưa đăng nhập.</p>
          <Link to="/login" className={styles.btnPrimary}>Đăng nhập ngay</Link>
        </div>
      </div>
    );
  }

  const avatarFallback = user.fullName?.charAt(0).toUpperCase() ?? "U";

  return (
    <div className={styles.page}>
      <div className={styles.container}>
        {/* ── Panel trái: Avatar & Role ── */}
        <div className={styles.sidebar}>
          <div className={styles.avatarWrapper}>
            {user.avatarUrl ? (
              <img src={user.avatarUrl} alt={user.fullName} className={styles.avatar} />
            ) : (
              <div className={styles.avatarFallback}>{avatarFallback}</div>
            )}
          </div>

          <div className={styles.sidebarInfo}>
            <h2 className={styles.displayName}>{user.fullName}</h2>
            <span
              className={`${styles.roleBadge} ${styles[`role${user.role}`]}`}
            >
              {ROLE_LABEL[user.role] ?? user.role}
            </span>
          </div>

          <nav className={styles.sidebarNav}>
            <Link to="/profile" className={`${styles.navItem} ${styles.navActive}`}>
              <span>👤</span> Thông tin cá nhân
            </Link>
            <Link to="/orders" className={styles.navItem}>
              <span>📦</span> Đơn hàng của tôi
            </Link>
            <Link to="/consignments" className={styles.navItem}>
              <span>🏷️</span> Ký gửi của tôi
            </Link>
          </nav>

          <button onClick={handleLogout} className={styles.btnLogout}>
            Đăng xuất
          </button>
        </div>

        {/* ── Panel phải: Thông tin chi tiết ── */}
        <div className={styles.mainContent}>
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
                <span className={styles.infoValue}>{ROLE_LABEL[user.role]}</span>
              </div>

              <div className={styles.infoItem}>
                <span className={styles.infoLabel}>Trạng thái tài khoản</span>
                <span
                  className={`${styles.statusBadge} ${
                    user.status === "ACTIVE" ? styles.statusActive : styles.statusLocked
                  }`}
                >
                  {STATUS_LABEL[user.status]}
                </span>
              </div>
            </div>
          </div>

          {/* Cam kết bảo mật */}
          <div className={styles.securityNote}>
            <span className={styles.securityIcon}>🔒</span>
            <p>
              Thông tin cá nhân của bạn được bảo mật và chỉ được dùng cho mục đích xử lý đơn hàng.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
