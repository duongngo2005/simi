import { Outlet } from "react-router";
import styles from "./MainLayout.module.css";
import Header from "./components/Header";
import Footer from "./components/Footer";

export const MainLayout = () => {
  return (
    <div className={styles.layout}>
      <Header />

      <main className={styles.main}>
        <Outlet />
      </main>

      <Footer />
    </div>
  );
};
