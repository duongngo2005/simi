import { Outlet } from "react-router";
import imageLogin from "../assets/images/image-login.png";
import styles from "./AuthLayout.module.css";

const AuthLayout = () => {
  return (
    <div className={styles.page}>
      <div className={styles.container}>
        <div className={styles.imageSection}>
          <img
            className={styles.imageLogin}
            src={imageLogin}
            alt="Simi login"
          />
        </div>

        <main className={styles.content}>
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default AuthLayout;