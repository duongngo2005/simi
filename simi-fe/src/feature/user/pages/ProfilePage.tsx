import { useState } from "react";
import { useNavigate, Link } from "react-router";
import { useAuthStore } from "../../../store/useAuthStore";
import api from "../../../lib/http/apiClient";
import styles from "./ProfilePage.module.css";
import { MyInfo } from "../components/MyInfo";
import { MyOrders } from "../components/MyOrders"; // Import component Đơn hàng của bạn
import { MyConsignment } from "../components/MyConsignment";

const ROLE_LABEL: Record<string, string> = {
  ADMIN: "Quản trị viên",
  STAFF: "Nhân viên",
  CUSTOMER: "Khách hàng",
};

export const ProfilePage = () => {
  const { user, clearAuth } = useAuthStore();
  const navigate = useNavigate();

  // State quản lý tab đang được chọn
  const [activeTab, setActiveTab] = useState<"info" | "orders" | "consignments">("info");

  const handleLogout = async () => {
    try {
      await api.post("/auth/logout");
    } catch {
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
            <span className={`${styles.roleBadge} ${styles[`role${user.role}`]}`}>
              {ROLE_LABEL[user.role] ?? user.role}
            </span>
          </div>

          {/* Thay thẻ <Link> bằng <button> chuyển tab */}
          <nav className={styles.sidebarNav}>
            <button
              onClick={() => setActiveTab("info")}
              className={`${styles.navItem} ${activeTab === "info" ? styles.navActive : ""}`}
            >
              Thông tin cá nhân
            </button>
            <button
              onClick={() => setActiveTab("orders")}
              className={`${styles.navItem} ${activeTab === "orders" ? styles.navActive : ""}`}
            >
              Đơn hàng của tôi
            </button>
            <button
              onClick={() => setActiveTab("consignments")}
              className={`${styles.navItem} ${activeTab === "consignments" ? styles.navActive : ""}`}
            >
              Ký gửi của tôi
            </button>
          </nav>

          <button onClick={handleLogout} className={styles.btnLogout}>
            Đăng xuất
          </button>
        </div>

        <div className={styles.mainContent}>
          {activeTab === "info" && <MyInfo />}
          {activeTab === "orders" && <MyOrders />}
          {activeTab === "consignments" && <MyConsignment/>}
        </div>
      </div>
    </div>
  );
};
