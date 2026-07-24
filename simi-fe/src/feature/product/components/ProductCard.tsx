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
  thumbnail, // 💡 Hứng trường thumbnail mới từ BE
  oldPrice,
  discount,
  isSale = false
}: ProductSummaryResponse) => {
  // Lấy thumbnail từ BE, nếu không có mới dùng ảnh fallback
  const thumbnailImage = 
    thumbnail || 
    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500";

  const isNewWithTag = productCondition === "NEW WITH TAG";

  const formatPrice = (price: number) => {
    return price.toLocaleString("vi-VN") + "đ";
  };

  return (
    <Link to={`/products/${id}`} className={styles.card}>
      <div className={styles.cardImageContainer}>
        <img src={thumbnailImage} alt={name} className={styles.cardImage} loading="lazy" />
        
        {(productCondition || discount) && (
          <span className={`${styles.badge} ${isNewWithTag ? styles.badgeTag : (isSale ? styles.badgeDanger : "")}`}>
            {isSale && discount ? discount : productCondition}
          </span>
        )}
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardMetaRow}>
          {size && <span className={styles.cardSize}>Size {size}</span>}
          {brandName && <span className={styles.cardBrand}>{brandName}</span>}
        </div>
        
        <h3 className={styles.cardName}>{name}</h3>
        
        {isSale && oldPrice ? (
          <div className={styles.priceContainer}>
            <span className={styles.cardPriceNew}>{formatPrice(currentPrice)}</span>
            <span className={styles.cardPriceOld}>{formatPrice(oldPrice)}</span>
          </div>
        ) : (
          <p className={styles.cardPrice}>{formatPrice(currentPrice)}</p>
        )}
      </div>
    </Link>
  );
};
