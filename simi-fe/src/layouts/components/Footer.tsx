import { Link } from "react-router";
import styles from "./Footer.module.css";
import logo from "../../assets/logo-mini.png";

const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className={styles.footer}>
      <div className={styles.container}>
        <div className={styles.topSection}>
          <div className={styles.brandSection}>
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

            <p className={styles.description}>
              Thời trang ký gửi được chọn lọc, giúp những món đồ đẹp có thêm
              một hành trình mới.
            </p>
          </div>

          <div className={styles.linkGroup}>
            <h3>Khám phá</h3>

            <Link to="/">Trang chủ</Link>
            <Link to="/products">Sản phẩm</Link>
            <Link to="/about">Về Simi</Link>
          </div>

          <div className={styles.linkGroup}>
            <h3>Hỗ trợ</h3>

            <Link to="/consignment-policy">Chính sách ký gửi</Link>
            <Link to="/shipping-policy">Giao hàng và thanh toán</Link>
            <Link to="/contact">Liên hệ</Link>
          </div>

          <div className={styles.contactSection}>
            <h3>Liên hệ</h3>

            <p>Email: support@simi.vn</p>
            <p>Điện thoại: 0900 000 000</p>
            <p>TP. Hồ Chí Minh, Việt Nam</p>
          </div>
        </div>

        <div className={styles.bottomSection}>
          <p>© {currentYear} Simi. All rights reserved.</p>

          <div className={styles.bottomLinks}>
            <Link to="/privacy-policy">Chính sách bảo mật</Link>
            <Link to="/terms">Điều khoản sử dụng</Link>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;