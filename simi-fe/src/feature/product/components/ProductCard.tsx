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
  productImageResponses,
  oldPrice,
  discount,
  isSale = false
}: ProductSummaryResponse) => {
  // 1. Tìm ảnh thumbnail, nếu không có thì lấy ảnh đầu tiên, cuối cùng là ảnh mặc định
  const thumbnailImage = 
    productImageResponses?.find((img) => img.thumbnail)?.imageUrl ||
    productImageResponses?.[0]?.imageUrl ||
    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500";

  const isNewWithTag = productCondition === "NEW WITH TAG";

  // 2. Hàm format tiền tệ Việt Nam (150000 -> 150.000đ)
  const formatPrice = (price: number) => {
    return price.toLocaleString("vi-VN") + "đ";
  };

  return (
    <Link to={`/products/${id}`} className={styles.card}>
      <div className={styles.cardImageContainer}>
        <img src={thumbnailImage} alt={name} className={styles.cardImage} loading="lazy" />
        
        {/* Render nhãn trạng thái */}
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
