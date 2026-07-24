import { useParams, Link, useNavigate } from "react-router";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import api from "../../../lib/http/apiClient";
import type { ApiResponse } from "../../../types/common";
import styles from "./ProductDetailPage.module.css";
import type { ProductDetailResponse } from "../types/product.type";
import { size } from "zod";
import { useProductDetail } from "../hooks/useProducts";

const CONDITION_LABEL: Record<string, string> = {
  NEW_TAG: "Mới nguyên tag",
  LIKE_NEW: "Như mới (95%+)",
  GOOD: "Tốt (85-94%)",
  FAIR: "Khá (70-84%)",
};

const CONDITION_COLOR: Record<string, string> = {
  NEW_TAG: "var(--color-info)",
  LIKE_NEW: "var(--color-success)",
  GOOD: "var(--color-warning)",
  FAIR: "var(--color-divider)",
};

const formatPrice = (price: number) =>
  price?.toLocaleString("vi-VN") + "đ";

export const ProductDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const productId = id ? Number(id) : 0; 
  const { data: product, isLoading, isError } = useProductDetail(productId);
  const [activeImage, setActiveImage] = useState(0);

  const nav = useNavigate()

  const handleBuyNow = () => {
    nav("/checkout", {
      state: {
        product: {
          id: product?.id,
          name: product?.name,
          currentPrice: product?.currentPrice,
          brandName: product?.brandName,
          categoryName: product?.categoryName,
          size: product?.size,
          color: product?.color,
          productCondition: product?.productCondition,
          productImageResponses: product?.productImageResponses
        }
      }
    })
  }

  if (isLoading) {
    return (
      <div className={styles.stateWrapper}>
        <div className={styles.spinner} />
        <p>Đang tải sản phẩm...</p>
      </div>
    );
  }

  if (isError || !product) {
    return (
      <div className={styles.stateWrapper}>
        <p className={styles.errorText}>Không tìm thấy sản phẩm này.</p>
        <Link to="/products" className={styles.backLink}>← Quay lại cửa hàng</Link>
      </div>
    );
  }

  const images = product.productImageResponses ?? [];
  const activeImageUrl = images[activeImage]?.imageUrl ?? "https://via.placeholder.com/600x750?text=No+Image";

  return (
    <div className={styles.page}>
      {/* Breadcrumb */}
      <nav className={styles.breadcrumb}>
        <Link to="/">Trang chủ</Link>
        <span>/</span>
        <Link to="/products">Cửa hàng</Link>
        <span>/</span>
        <span>{product.name}</span>
      </nav>

      <div className={styles.container}>
        {/* ── CỘT TRÁI: ẢNH ── */}
        <div className={styles.gallery}>
          {/* Ảnh chính */}
          <div className={styles.mainImageWrapper}>
            <img
              src={activeImageUrl}
              alt={product.name}
              className={styles.mainImage}
            />
            {/* Badge tình trạng */}
            <span
              className={styles.conditionBadge}
              style={{ backgroundColor: CONDITION_COLOR[product.productCondition] }}
            >
              {CONDITION_LABEL[product.productCondition]}
            </span>
          </div>

          {/* Thumbnail strip */}
          {images.length > 1 && (
            <div className={styles.thumbnailStrip}>
              {images.map((img, idx) => (
                <button
                  key={idx}
                  className={`${styles.thumbnail} ${idx === activeImage ? styles.thumbnailActive : ""}`}
                  onClick={() => setActiveImage(idx)}
                >
                  <img src={img.imageUrl} alt={`Ảnh ${idx + 1}`} />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* ── CỘT PHẢI: THÔNG TIN ── */}
        <div className={styles.info}>
          {/* Brand + Category */}
          <div className={styles.metaRow}>
            {product.brandName && (
              <span className={styles.brand}>{product.brandName}</span>
            )}
            {product.categoryName && (
              <span className={styles.category}>{product.categoryName}</span>
            )}
          </div>

          {/* Tên sản phẩm */}
          <h1 className={styles.productName}>{product.name}</h1>

          {/* Giá */}
          <div className={styles.priceSection}>
            <span className={styles.price}>{formatPrice(product.currentPrice)}</span>
          </div>

          {/* Divider */}
          <hr className={styles.divider} />

          {/* Chi tiết nhanh */}
          <div className={styles.detailGrid}>
            {product.size && (
              <div className={styles.detailItem}>
                <span className={styles.detailLabel}>Size</span>
                <span className={styles.detailValue}>{product.size}</span>
              </div>
            )}
            {product.color && (
              <div className={styles.detailItem}>
                <span className={styles.detailLabel}>Màu sắc</span>
                <span className={styles.detailValue}>{product.color}</span>
              </div>
            )}
            <div className={styles.detailItem}>
              <span className={styles.detailLabel}>Tình trạng</span>
              <span className={styles.detailValue}>
                {CONDITION_LABEL[product.productCondition]}
              </span>
            </div>
            {product.brandName && (
              <div className={styles.detailItem}>
                <span className={styles.detailLabel}>Thương hiệu</span>
                <span className={styles.detailValue}>{product.brandName}</span>
              </div>
            )}
          </div>

          {/* Tags */}
          {product.tagNames?.length > 0 && (
            <div className={styles.tags}>
              {product.tagNames.map((tag) => (
                <span key={tag} className={styles.tag}>#{tag}</span>
              ))}
            </div>
          )}

          {/* Mô tả */}
          {product.description && (
            <div className={styles.descriptionSection}>
              <h3 className={styles.descLabel}>Mô tả sản phẩm</h3>
              <p className={styles.description}>{product.description}</p>
            </div>
          )}

          {/* Nút hành động */}
          <div className={styles.actions}>
            <button className={styles.btnAddToCart}>
              Thêm vào giỏ hàng
            </button>
            <button className={styles.btnBuyNow} onClick={handleBuyNow}>
              Mua ngay
            </button>
          </div>

          {/* Cam kết */}
          <div className={styles.guarantees}>
            <div className={styles.guarantee}>
              <span className={styles.guaranteeIcon}>✓</span>
              <span>Sản phẩm đã được kiểm định chất lượng</span>
            </div>
            <div className={styles.guarantee}>
              <span className={styles.guaranteeIcon}>✓</span>
              <span>Giao hàng toàn quốc, đổi trả trong 3 ngày</span>
            </div>
            <div className={styles.guarantee}>
              <span className={styles.guaranteeIcon}>✓</span>
              <span>Thanh toán an toàn</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
