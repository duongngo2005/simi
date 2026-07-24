import { useRef } from "react";
import { Link } from "react-router";
import { ProductCard } from "./ProductCard";
import type { ProductSummaryResponse } from "../types/product.type"; // Cập nhật đúng đường dẫn types của bạn
import styles from "./ProductSection.module.css";

interface ProductSectionProps {
  title: string;
  products: ProductSummaryResponse[];
  viewAllPath: string;
  isSale?: boolean;
}

export const ProductSection = ({ title, products, viewAllPath, isSale = false }: ProductSectionProps) => {
  const trackRef = useRef<HTMLDivElement>(null);

  const handleScroll = (direction: "left" | "right") => {
    if (trackRef.current) {
      const scrollAmount = 560; // Cuộn qua 2 card sản phẩm
      trackRef.current.scrollBy({
        left: direction === "left" ? -scrollAmount : scrollAmount,
        behavior: "smooth"
      });
    }
  };

  return (
    <section className={styles.section}>
      <h2 className={styles.sectionTitle}>{title}</h2>

      <div className={styles.carouselWrapper}>
        <button 
          onClick={() => handleScroll("left")} 
          className={`${styles.navBtn} ${styles.navBtnLeft}`}
          aria-label="Previous"
        >
          ‹
        </button>
        
        <div ref={trackRef} className={styles.carouselTrack}>
          {products?.map((prod) => (
            <ProductCard 
              key={prod.id}
              id={prod.id}
              name={prod.name}
              brandName={prod.brandName}
              currentPrice={prod.currentPrice}
              oldPrice={prod.oldPrice}
              discount={prod.discount}
              size={prod.size}
              productCondition={prod.productCondition}
              thumbnail={prod.thumbnail}
              isSale={isSale}
            />
          ))}
        </div>

        <button 
          onClick={() => handleScroll("right")} 
          className={`${styles.navBtn} ${styles.navBtnRight}`}
          aria-label="Next"
        >
          ›
        </button>
      </div>

      <div className={styles.sectionFooter}>
        <Link to={viewAllPath} className={styles.btnViewAll}>
          Xem tất cả
        </Link>
      </div>
    </section>
  );
};
