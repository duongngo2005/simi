import { useRef } from "react";
import {
  useNewestProducts,
  useNewTagProducts,
  useAccessoriesSection,
  useFootWearSection,
} from "../../product/hooks/useProducts";
import { ProductSection } from "../../product/components/ProductSection";
import styles from "./HomePage.module.css";
import { Link } from "react-router";

const FEEDBACKS = [
  {
    id: 1,
    name: "Nguyễn Lan Anh",
    role: "Người mua hàng",
    avatar: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
    content:
      "Đồ ký gửi ở đây siêu mới, mình mua được chiếc Blazer Mango nguyên tag với giá chưa tới một nửa giá gốc.",
  },
  {
    id: 2,
    name: "Trần Minh Đức",
    role: "Người ký gửi",
    avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
    content:
      "Quy trình ký gửi rất nhanh gọn, minh bạch. Gửi đồ 2 tuần đã thấy bán xong và nhận tiền đối soát.",
  },
  {
    id: 3,
    name: "Lê Hoàng Yến",
    role: "Người mua hàng",
    avatar: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150",
    content:
      "Đồ đóng gói rất xinh, sạch sẽ và thơm tho. Sẽ ủng hộ Simi dài dài!",
  },
  {
    id: 4,
    name: "Phạm Hoàng Nam",
    role: "Người ký gửi",
    avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
    content:
      "Tủ đồ chật cứng cuối cùng cũng được giải quyết. Vừa dọn nhà lại vừa có thêm một khoản thu nhập nhỏ.",
  },
  {
    id: 5,
    name: "Đỗ Thu Thảo",
    role: "Người mua hàng",
    avatar: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150",
    content:
      "Săn được đôi sneaker Nike chính hãng tại đây giá cực hời, độ mới cao. Dịch vụ chăm sóc khách hàng tốt.",
  },
  {
    id: 6,
    name: "Vũ Quốc Khánh",
    role: "Người mua hàng",
    avatar: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150",
    content:
      "Các sản phẩm của thương hiệu lớn đều được kiểm định rất kỹ. Cảm giác mua sắm cực kỳ an tâm.",
  },
];

export const HomePage = () => {
  const { data: newestProducts = [], isLoading: newestLoading } =
    useNewestProducts();
  const { data: newTagProducts = [], isLoading: newTagLoading } =
    useNewTagProducts();
  const { data: footwearProducts = [], isLoading: footwearLoading } =
    useFootWearSection();
  const { data: accessoriesProducts = [], isLoading: accessoriesLoading } =
    useAccessoriesSection();

  const feedbackRef = useRef<HTMLDivElement>(null);

  const scrollFeedback = (direction: "left" | "right") => {
    if (feedbackRef.current) {
      const scrollAmount = 340;
      feedbackRef.current.scrollBy({
        left: direction === "left" ? -scrollAmount : scrollAmount,
        behavior: "smooth",
      });
    }
  };

  if (newestLoading || newTagLoading || accessoriesLoading || footwearLoading) {
    return <div className={styles.loading}>Đang tải trang chủ...</div>;
  }

  return (
    <div className={styles.home}>
      <section className={styles.hero}>
        <div className={styles.heroOverlay}>
          <div className={styles.heroContent}>
            <span className={styles.heroSubtitle}>
              Thời trang bền vững & Ký gửi độc bản
            </span>
            <h1 className={styles.heroTitle}>
              FASHION WITH <br />A SECOND LIFE
            </h1>
            <p className={styles.heroDesc}>
              Dọn gọn tủ đồ, chia sẻ phong cách và tìm kiếm những món đồ độc bản
              được tuyển chọn kỹ lưỡng từ Simi.
            </p>
            <div className={styles.heroActions}>
              <Link to="/products" className={styles.btnPrimary}>
                Mua ngay
              </Link>
              <Link to="/consignments" className={styles.btnSecondary}>
                Ký gửi đồ
              </Link>
            </div>
          </div>
        </div>
      </section>

      <div className={styles.mainContainer}>
        <ProductSection
          title="Mới lên kệ"
          products={newestProducts}
          viewAllPath="/products?sort=newest"
        />

        <ProductSection
          title="Hàng nguyên Tag"
          products={newTagProducts}
          viewAllPath="/products?condition=NEW"
        />

        <ProductSection
          title="Phụ kiện thời trang"
          products={accessoriesProducts}
          viewAllPath="/products?category=accessories"
        />

        <ProductSection
          title="Giày dép"
          products={footwearProducts}
          viewAllPath="/products?category=shoes"
        />

        <section className={`${styles.section} ${styles.sectionAlt}`}>
          <h2 className={styles.sectionTitle}>Đánh giá từ khách hàng</h2>
          <div className={styles.carouselWrapper}>
            <button
              onClick={() => scrollFeedback("left")}
              className={`${styles.navBtn} ${styles.navBtnLeft}`}
            >
              ‹
            </button>

            <div ref={feedbackRef} className={styles.carouselTrack}>
              {FEEDBACKS.map((fb) => (
                <div key={fb.id} className={styles.feedbackCard}>
                  <div className={styles.feedbackHeader}>
                    <img
                      src={fb.avatar}
                      alt={fb.name}
                      className={fb.avatar ? styles.feedbackAvatar : ""}
                    />
                    <div className={styles.userInfo}>
                      <span className={styles.userName}>{fb.name}</span>
                      <span className={styles.userRole}>{fb.role}</span>
                    </div>
                  </div>
                  <div className={styles.rating}>★★★★★</div>
                  <p className={styles.feedbackContent}>"{fb.content}"</p>
                </div>
              ))}
            </div>

            <button
              onClick={() => scrollFeedback("right")}
              className={`${styles.navBtn} ${styles.navBtnRight}`}
            >
              ›
            </button>
          </div>
        </section>
      </div>
    </div>
  );
};
