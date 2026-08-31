import { Link, NavLink } from "react-router";
import styles from "./Header.module.css";
import logo from "../../assets/logo-mini.png";
import { useAuthStore } from "../../store/useAuthStore";

const Header = () => {
  const authenticated = useAuthStore((state) => state.isAuthenticated());
  const user = useAuthStore((state) => state.user);

  const isStaffOrAdmin = user?.role === "STAFF" || user?.role === "ADMIN";

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
        <Link to="/" className={styles.brand} aria-label="Trang chủ Simi">
          <img src={logo} alt="Logo Simi" className={styles.logoImage} />
        </Link>

        <nav className={styles.navigation} aria-label="Điều hướng chính">
          <NavLink to="/" end className={getNavLinkClass}>
            Trang chủ
          </NavLink>
          <NavLink to="/products" className={getNavLinkClass}>
            Sản phẩm
          </NavLink>
        </nav>

        <div className={styles.rightSection}>
          {!isStaffOrAdmin && (
            <Link to="/cart" className={styles.cartIconLink} aria-label="Giỏ hàng">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="9" cy="21" r="1"/>
                <circle cx="20" cy="21" r="1"/>
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
              </svg>
            </Link>
          )}

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
            <Link to="/profile" className={styles.userSection} title={user?.fullName}>
              {user?.avatarUrl ? (
                <img
                  src={user.avatarUrl}
                  alt={`Avatar ${user.fullName}`}
                  className={styles.avatar}
                />
              ) : (
                <span className={styles.avatarFallback}>
                  {userInitial}
                </span>
              )}

              {isStaffOrAdmin && (
                <span className={styles.userRole}>
                  {user?.role === "ADMIN" ? "Quản trị viên" : "Nhân viên"}
                </span>
              )}
            </Link>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
