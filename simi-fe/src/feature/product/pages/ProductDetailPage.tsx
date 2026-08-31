import { useParams, Link, useNavigate, useLocation } from "react-router";
import { useState } from "react";
import styles from "./ProductDetailPage.module.css";
import { useProductDetail } from "../hooks/useProducts";
import { CONDITION_COLOR, CONDITION_LABEL } from "../../../utils/condition";
import { formatPrice } from "../../../utils/formatPrice";
import { useAddToCart, useGetMyCart } from "../../cart/hook/useCart";
import { useAuthStore } from "../../../store/useAuthStore";
import { getServerError } from "../../../utils/getMessageError";

const GENDER_LABEL: Record<string, string> = {
  MEN: "Đồ Nam",
  WOMEN: "Đồ Nữ",
  UNISEX: "Unisex (Nam/Nữ)",
};

export const ProductDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const productId = id ? Number(id) : 0;
  const { data: product, isLoading, isError } = useProductDetail(productId);
  const [activeImage, setActiveImage] = useState(0);
  const { mutateAsync: addToCart, isPending } = useAddToCart();
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated());

  const { data: myCart } = useGetMyCart(isAuthenticated);

  const isInCart = myCart?.cartItemResponses?.some(
    (item) => item.productSummaryResponse?.id === productId
  );

  const nav = useNavigate();
  const location = useLocation();

  const handleBuyNow = () => {
    if (!isAuthenticated) {
      nav("/login", { state: { from: location.pathname } });
      return;
    }

    const cartItem = myCart?.cartItemResponses?.find(
      (item) => item.productSummaryResponse?.id === product?.id,
    );
    nav("/checkout", {
      state: {
        productIds: [product?.id],
        cartItemIds: cartItem ? [cartItem.id] : undefined,
      },
    });
  };

  const handleAddToCart = async (id: number) => {
    if (!isAuthenticated) {
      nav("/login", { state: { from: location.pathname } });
      return;
    }

    if (isInCart) {
      nav("/cart");
      return;
    }

    try {
      await addToCart(id);
      alert("Đã thêm sản phẩm vào giỏ hàng");
    } catch (error: unknown) {
      alert(getServerError(error, "Lỗi thêm vào giỏ hàng"));
    }
  };

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
        <Link to="/products" className={styles.backLink}>
          ← Quay lại cửa hàng
        </Link>
      </div>
    );
  }

  const images = product.productImageResponses ?? [];
  const isAvailable = product.productStatus === "AVAILABLE";
  const activeImageUrl = images[activeImage]?.imageUrl;
  const listedDate = product.createdDate && !Number.isNaN(Date.parse(product.createdDate))
    ? new Intl.DateTimeFormat("vi-VN", { day: "2-digit", month: "2-digit", year: "numeric" }).format(new Date(product.createdDate))
    : null;

  return (
    <div className={styles.page}>
      <nav className={styles.breadcrumb}>
        <Link to="/">Trang chủ</Link>
        <span>/</span>
        <Link to="/products">Cửa hàng</Link>
        <span>/</span>
        <span>{product.name}</span>
      </nav>

      <div className={styles.container}>
        <div className={styles.gallery}>
          <div className={styles.mainImageWrapper}>
            {activeImageUrl ? (
              <img
                src={activeImageUrl}
                alt={product.name}
                className={styles.mainImage}
              />
            ) : (
              <div className={styles.imagePlaceholder}>Ảnh sản phẩm đang được cập nhật</div>
            )}
            <span
              className={styles.conditionBadge}
              style={{
                backgroundColor: CONDITION_COLOR[product.productCondition],
              }}
            >
              {CONDITION_LABEL[product.productCondition]}
            </span>
          </div>

          {images.length > 1 && (
            <div className={styles.thumbnailStrip}>
              {images.map((img, idx) => (
                <button
                  key={idx}
                  type="button"
                  className={`${styles.thumbnail} ${
                    idx === activeImage ? styles.thumbnailActive : ""
                  }`}
                  onClick={() => setActiveImage(idx)}
                >
                  <img src={img.imageUrl} alt={`${product.name} — ảnh ${idx + 1}`} />
                </button>
              ))}
            </div>
          )}
        </div>

        <div className={styles.info}>
          <h1 className={styles.productName}>{product.name}</h1>

          <div className={styles.priceSection}>
            <span className={styles.price}>
              {formatPrice(product.currentPrice)}
            </span>
          </div>

          <hr className={styles.divider} />

          <section className={styles.passportStrip} aria-label="Hộ chiếu món đồ">
            <div className={styles.passportHeading}>
              <span>Hộ chiếu món đồ</span>
              <strong>MÃ SIMI #{String(product.id).padStart(4, "0")}</strong>
            </div>
            <div className={styles.passportFields}>
              <div>
                <span>Tình trạng</span>
                <strong>{CONDITION_LABEL[product.productCondition]}</strong>
              </div>
              <div>
                <span>Lên kệ</span>
                <strong>{listedDate ?? "Đang cập nhật"}</strong>
              </div>
              <div>
                <span>Khả dụng</span>
                <strong className={isAvailable ? styles.available : styles.unavailable}>
                  {isAvailable ? "Sẵn sàng mua" : "Không còn sẵn"}
                </strong>
              </div>
            </div>
          </section>

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

            {product.gender && (
              <div className={styles.detailItem}>
                <span className={styles.detailLabel}>Giới tính</span>
                <span className={styles.detailValue}>
                  {GENDER_LABEL[product.gender] || product.gender}
                </span>
              </div>
            )}

            {product.material && (
              <div className={styles.detailItem}>
                <span className={styles.detailLabel}>Chất liệu</span>
                <span className={styles.detailValue}>{product.material}</span>
              </div>
            )}

            <div className={styles.detailItem}>
              <span className={styles.detailLabel}>Tình trạng</span>
              <span className={styles.detailValue}>
                {CONDITION_LABEL[product.productCondition]}
              </span>
            </div>
            {product.brand && (
              <div className={styles.detailItem}>
                <span className={styles.detailLabel}>Thương hiệu</span>
                <span className={styles.detailValue}>{product.brand}</span>
              </div>
            )}
          </div>

          {product.tagNames?.length > 0 && (
            <div className={styles.tags}>
              {product.tagNames.map((tag) => (
                <span key={tag} className={styles.tag}>
                  #{tag}
                </span>
              ))}
            </div>
          )}

          {product.description && (
            <div className={styles.descriptionSection}>
              <h3 className={styles.descLabel}>Mô tả sản phẩm</h3>
              <p className={styles.description}>{product.description}</p>
            </div>
          )}

          <div className={styles.actions}>
            <button
              onClick={() => handleAddToCart(product.id)}
              disabled={isPending || !isAvailable}
              className={styles.btnAddToCart}
            >
              {!isAvailable ? "Sản phẩm không còn sẵn" : isInCart ? "Đã có trong giỏ hàng" : "Thêm vào giỏ hàng"}
            </button>
            <button className={styles.btnBuyNow} onClick={handleBuyNow} disabled={!isAvailable}>
              Mua ngay
            </button>
          </div>

          <div className={styles.guarantees}>
            <div className={styles.guarantee}>
              <span className={styles.guaranteeIcon}>✓</span>
              <span>Sản phẩm đã được kiểm định chất lượng</span>
            </div>
            <div className={styles.guarantee}>
              <span className={styles.guaranteeIcon}>✓</span>
              <span>Giao hàng toàn quốc theo phí vận chuyển hiển thị khi thanh toán</span>
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
