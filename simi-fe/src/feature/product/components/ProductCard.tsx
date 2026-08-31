import { Link } from "react-router";
import styles from "./ProductCard.module.css";
import type { ProductSummaryResponse } from "../types/product.type";
import { CONDITION_LABEL } from "../../../utils/condition";

export const ProductCard = ({
  id,
  name,
  brandName,
  currentPrice,
  size,
  productCondition,
  thumbnail,
}: ProductSummaryResponse) => {
  const isNewWithTag = productCondition === "NEW_TAG";

  const formatPrice = (price: number) => {
    return price.toLocaleString("vi-VN") + "đ";
  };

  return (
    <Link to={`/products/${id}`} className={styles.card}>
      <div className={styles.cardImageContainer}>
        {thumbnail ? (
          <img
            src={thumbnail}
            alt={name}
            className={styles.cardImage}
            loading="lazy"
          />
        ) : (
          <span className={styles.imagePlaceholder}>Ảnh đang cập nhật</span>
        )}

        {productCondition && (
          <span
            className={`${styles.badge} ${
              isNewWithTag ? styles.badgeTag : ""
            }`}
          >
            {CONDITION_LABEL[productCondition] ?? productCondition}
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

        <div className={styles.passportStrip}>
          <span>Mã Simi #{String(id).padStart(3, "0")}</span>
          <span>Đã kiểm định</span>
        </div>
      </div>
    </Link>
  );
};
