import { Link } from "react-router";
import {
  useNewestProducts,
  useNewTagProducts,
  useAccessoriesSection,
  useFootWearSection,
} from "../../product/hooks/useProducts";
import { ProductSection } from "../../product/components/ProductSection";
import { CONDITION_LABEL } from "../../../utils/condition";
import { formatPrice } from "../../../utils/formatPrice";
import styles from "./HomePage.module.css";

export const HomePage = () => {
  const { data: newestProducts = [], isLoading: newestLoading } = useNewestProducts();
  const { data: newTagProducts = [], isLoading: newTagLoading } = useNewTagProducts();
  const { data: footwearProducts = [], isLoading: footwearLoading } = useFootWearSection();
  const { data: accessoriesProducts = [], isLoading: accessoriesLoading } = useAccessoriesSection();

  if (newestLoading || newTagLoading || accessoriesLoading || footwearLoading) {
    return <div className={styles.loading}>Đang tải trang chủ...</div>;
  }

  const featuredProduct = newestProducts[0];

  return (
    <div className={styles.home}>
      <section className={styles.hero}>
        <div className={styles.heroContent}>
          <p className={styles.heroKicker}>Simi · đồ ký gửi đã được chọn lại</p>
          <h1 className={styles.heroTitle}>Món đồ đẹp, tiếp tục được mặc.</h1>
          <p className={styles.heroDesc}>
            Khám phá những món đồ đang có sẵn với tình trạng, giá bán và thông tin rõ ràng trước khi bạn chọn mua.
          </p>
          <div className={styles.heroActions}>
            <Link to="/products" className={styles.btnPrimary}>
              Xem sản phẩm đang có sẵn
            </Link>
          </div>
        </div>

        {featuredProduct ? (
          <Link to={`/products/${featuredProduct.id}`} className={styles.heroPassport}>
            <div className={styles.heroImageFrame}>
              {featuredProduct.thumbnail ? (
                <img src={featuredProduct.thumbnail} alt={featuredProduct.name} />
              ) : (
                <span>Ảnh sản phẩm đang được cập nhật</span>
              )}
            </div>
            <div className={styles.heroPassportMeta}>
              <span>MÃ SIMI #{String(featuredProduct.id).padStart(3, "0")}</span>
              <span>{CONDITION_LABEL[featuredProduct.productCondition ?? ""] ?? "Đang cập nhật"}</span>
            </div>
            <h2>{featuredProduct.name}</h2>
            <p>{formatPrice(featuredProduct.currentPrice)}</p>
          </Link>
        ) : (
          <div className={styles.heroEmpty}>
            <span>MÃ SIMI</span>
            <p>Sản phẩm mới sẽ xuất hiện tại đây sau khi được kích hoạt.</p>
          </div>
        )}
      </section>

      <div className={styles.mainContainer}>
        <ProductSection title="Mới lên kệ" products={newestProducts} viewAllPath="/products?sort=newest" />
        <ProductSection title="Hàng nguyên Tag" products={newTagProducts} viewAllPath="/products?condition=NEW" />
        <ProductSection title="Phụ kiện thời trang" products={accessoriesProducts} viewAllPath="/products?category=accessories" />
        <ProductSection title="Giày dép" products={footwearProducts} viewAllPath="/products?category=shoes" />

        <section className={styles.principles} aria-labelledby="simi-principles-title">
          <div className={styles.principlesHeading}>
            <p className={styles.sectionKicker}>Tiêu chuẩn Simi</p>
            <h2 id="simi-principles-title">Thông tin để bạn quyết định dễ hơn.</h2>
          </div>
          <div className={styles.principleGrid}>
            <article>
              <span>ĐÃ KIỂM ĐỊNH</span>
              <p>Tình trạng món đồ được hiển thị trực tiếp trên từng sản phẩm.</p>
            </article>
            <article>
              <span>GIÁ HIỆN TẠI</span>
              <p>Giá bạn thấy là giá dùng để tạo đơn hàng tại thời điểm mua.</p>
            </article>
            <article>
              <span>CHỈ HÀNG CÓ SẴN</span>
              <p>Danh sách chỉ hiển thị sản phẩm đang có thể đặt mua.</p>
            </article>
          </div>
        </section>
      </div>
    </div>
  );
};
