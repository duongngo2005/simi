import { Outlet, useLocation } from "react-router";
import styles from "./MainLayout.module.css";
import Header from "./components/Header";
import Footer from "./components/Footer";
import { useEffect } from "react";

export const MainLayout = () => {
  const {pathname} = useLocation();

  useEffect(() => {
    window.scroll(0, 0);
  }, [pathname])
  
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
