import { Link } from "react-router";
import styles from "./ProductCard.module.css";
import type { ProductSummaryResponse } from "../types/product.type";

export const ProductCard = ({
  id,
  name,
  brandName,
  currentPrice,
  size,
  productCondition,
  thumbnail,
}: ProductSummaryResponse) => {
  const thumbnailImage =
    thumbnail ||
    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500";

  const isNewWithTag = productCondition === "NEW_TAG";

  const formatPrice = (price: number) => {
    return price.toLocaleString("vi-VN") + "đ";
  };

  return (
    <Link to={`/products/${id}`} className={styles.card}>
      <div className={styles.cardImageContainer}>
        <img
          src={thumbnailImage}
          alt={name}
          className={styles.cardImage}
          loading="lazy"
        />

        {productCondition && (
          <span
            className={`${styles.badge} ${
              isNewWithTag ? styles.badgeTag : ""
            }`}
          >
            {productCondition}
          </span>
        )}
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardMetaRow}>
          {size && <span className={styles.cardSize}>Size {size}</span>}
          {brandName && <span className={styles.cardBrand}>{brandName}</span>}
        </div>

        <h3 className={styles.cardName}>{name}</h3>

        <p className={styles.cardPrice}>{formatPrice(currentPrice)}</p>
      </div>
    </Link>
  );
};
