import { Link, NavLink } from "react-router";
import styles from "./Header.module.css";
import logo from "../../assets/logo-mini.png";
import { useAuthStore } from "../../store/useAuthStore";

const Header = () => {
  const authenticated = useAuthStore((state) => state.isAuthenticated());
  const user = useAuthStore((state) => state.user);

  const getNavLinkClass = ({
    isActive,
  }: {
    isActive: boolean;
  }) => `${styles.navLink} ${isActive ? styles.active : ""}`;

  const userInitial = user?.fullName
    ? user.fullName.trim().charAt(0).toUpperCase()
    : "U";

  return (
    <header className={styles.header}>
      <div className={styles.container}>
        <Link
          to="/"
          className={styles.brand}
          aria-label="Trang chủ Simi"
        >
          <img
            src={logo}
            alt="Logo Simi"
            className={styles.logoImage}
          />
        </Link>

        <nav
          className={styles.navigation}
          aria-label="Điều hướng chính"
        >
          <NavLink to="/" end className={getNavLinkClass}>
            Trang chủ
          </NavLink>

          <NavLink to="/products" className={getNavLinkClass}>
            Sản phẩm
          </NavLink>

          <NavLink to="/about" className={getNavLinkClass}>
            Về Simi
          </NavLink>
        </nav>

        {!authenticated ? (
          <div className={styles.actions}>
            <Link to="/login" className={styles.loginLink}>
              Đăng nhập
            </Link>

            <Link to="/register" className={styles.registerLink}>
              Đăng ký
            </Link>
          </div>
        ) : (
          <Link to="/profile" className={styles.userSection}>
            {user?.avatarUrl ? (
              <img
                src={user.avatarUrl}
                alt={`Ảnh đại diện của ${user.fullName}`}
                className={styles.avatar}
              />
            ) : (
              <span className={styles.avatarFallback}>
                {userInitial}
              </span>
            )}

            <div className={styles.userInfo}>
              <span className={styles.userName}>
                {user?.fullName ?? "Người dùng"}
              </span>

              <span className={styles.userRole}>
                {user?.role === "ADMIN"
                  ? "Quản trị viên"
                  : user?.role === "STAFF"
                    ? "Nhân viên"
                    : "Khách hàng"}
              </span>
            </div>
          </Link>
        )}
      </div>
    </header>
  );
};

export default Header;