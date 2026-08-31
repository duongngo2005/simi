import { useRef } from "react";
import { Link } from "react-router";
import { ProductCard } from "./ProductCard";
import type { ProductSummaryResponse } from "../types/product.type";
import styles from "./ProductSection.module.css";

interface ProductSectionProps {
  title: string;
  products: ProductSummaryResponse[];
  viewAllPath: string;
}

export const ProductSection = ({
  title,
  products,
  viewAllPath,
}: ProductSectionProps) => {
  const trackRef = useRef<HTMLDivElement>(null);

  const handleScroll = (direction: "left" | "right") => {
    if (trackRef.current) {
      const scrollAmount = 560;
      trackRef.current.scrollBy({
        left: direction === "left" ? -scrollAmount : scrollAmount,
        behavior: "smooth",
      });
    }
  };

  if (!products || products.length === 0){
    return(
      <></>
    )
  }

  return (
    <section className={styles.section}>
      <h2 className={styles.sectionTitle}>{title}</h2>

      <div className={styles.carouselWrapper}>
        <button
          onClick={() => handleScroll("left")}
          className={`${styles.navBtn} ${styles.navBtnLeft}`}
          aria-label="Xem sản phẩm trước"
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
              size={prod.size}
              productCondition={prod.productCondition}
              productStatus={prod.productStatus}
              thumbnail={prod.thumbnail}
            />
          ))}
        </div>

        <button
          onClick={() => handleScroll("right")}
          className={`${styles.navBtn} ${styles.navBtnRight}`}
          aria-label="Xem thêm sản phẩm"
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
