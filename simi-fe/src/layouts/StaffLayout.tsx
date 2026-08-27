import { Outlet, NavLink, useNavigate, Navigate } from "react-router";
import { useAuthStore } from "../store/useAuthStore";
import styles from "./StaffLayout.module.css";
import { StaffOrderNotificationBell } from "../feature/staff/dashboard/components/StaffOrderNotificationBell";

export const StaffLayout = () => {
  const { user, clearAuth, isInitialized } = useAuthStore();
  const navigate = useNavigate();

  if (!isInitialized) {
    return null;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role !== "STAFF" && user.role !== "ADMIN") {
    return <Navigate to="/" replace />;
  }

  const handleLogout = () => {
    clearAuth();
    navigate("/login");
  };

  return (
    <div className={styles.layout}>
      <aside className={styles.sidebar}>
        <div className={styles.brandSection}>
          <span className={styles.logo}>Simi.</span>
          <span className={styles.badge}>STAFF PANEL</span>
        </div>

        <nav className={styles.navMenu}>
          <NavLink
            to="/staff/dashboard"
            end
            className={({ isActive }) =>
              `${styles.navLink} ${isActive ? styles.navLinkActive : ""}`
            }
          >
            <span className={styles.navIcon}></span>
            Tổng quan
          </NavLink>

          <NavLink
            to="/staff/pos"
            className={({ isActive }) =>
              `${styles.navLink} ${isActive ? styles.navLinkActive : ""}`
            }
          >
            <span className={styles.navIcon}></span>
            Bán hàng
          </NavLink>

          <NavLink
            to="/staff/orders"
            className={({ isActive }) =>
              `${styles.navLink} ${isActive ? styles.navLinkActive : ""}`
            }
          >
            <span className={styles.navIcon}></span>
            Quản lý đơn hàng
          </NavLink>

          <NavLink
            to="/staff/consignments"
            className={({ isActive }) =>
              `${styles.navLink} ${isActive ? styles.navLinkActive : ""}`
            }
          >
            <span className={styles.navIcon}></span>
            Quản lý ký gửi
          </NavLink>

          <NavLink
            to="/staff/dispositions"
            className={({ isActive }) =>
              `${styles.navLink} ${isActive ? styles.navLinkActive : ""}`
            }
          >
            <span className={styles.navIcon}></span>
            Trả hàng
          </NavLink>

          <NavLink
            to="/staff/products"
            className={({ isActive }) =>
              `${styles.navLink} ${isActive ? styles.navLinkActive : ""}`
            }
          >
            <span className={styles.navIcon}></span>
            Quản lý sản phẩm
          </NavLink>
        </nav>

        <div className={styles.footerSection}>
          <button onClick={handleLogout} className={styles.btnLogout}>
            <span className={styles.navIcon}></span>
            Đăng xuất
          </button>
        </div>
      </aside>

      <div className={styles.mainContainer}>
        <header className={styles.header}>
          <div className={styles.headerLeft}>
            <h2 className={styles.pageTitle}>
              Xin chào, {user?.fullName || "Staff"}
            </h2>
          </div>

          <div className={styles.headerRight}>
            <StaffOrderNotificationBell />

            <div className={styles.userInfo}>
              <div className={styles.avatar}>
                {user?.fullName?.charAt(0).toUpperCase() || "S"}
              </div>

              <div className={styles.userMeta}>
                <span className={styles.userName}>{user?.fullName}</span>
                <span className={styles.userRole}>{user?.role}</span>
              </div>
            </div>
          </div>
        </header>

        <main className={styles.content}>
          <Outlet />
        </main>
      </div>
    </div>
  );
};