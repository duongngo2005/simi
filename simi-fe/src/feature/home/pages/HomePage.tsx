import { useRef } from "react";
import { useNewestProducts, useNewTagProducts, useAccessoriesSection, useFootWearSection } from "../../product/hooks/useProducts";
import { ProductSection } from "../../product/components/ProductSection";
import styles from "./HomePage.module.css";
import { Link } from "react-router";

// Helper sinh dữ liệu giả lập cho các danh mục còn lại chưa có API
const generateProducts = (type: "normal" | "sale" | "accessory" | "shoes", prefix: string, startId: number) => {
  const images = [
    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500", // shirt
    "https://images.unsplash.com/photo-1542272604-787c3835535d?w=500", // jeans
    "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=500", // blazer
    "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=500", // dress
    "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500", // jacket
    "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=500"  // hoodie
  ];

  const shoeImages = [
    "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500",
    "https://images.unsplash.com/photo-1539185441755-769473a23570?w=500",
    "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=500"
  ];

  const brands = ["Zara", "Levi's", "Mango", "Uniqlo", "MLB", "H&M", "Gucci", "Chanel", "Nike", "Adidas"];
  const sizes = ["S", "M", "L", "XL", "38", "39", "40", "41", "42"];

  return Array.from({ length: 20 }, (_, i) => {
    const id = startId + i;
    const brand = brands[id % brands.length];
    let img = images[id % images.length];

    if (type === "shoes") {
      img = shoeImages[id % shoeImages.length];
    }

    return {
      id,
      name: `${prefix} ${brand} #${id}`,
      currentPrice: 150000 + (id % 8) * 70000,
      oldPrice: type === "sale" ? 320000 + (id % 8) * 110000 : undefined,
      discount: type === "sale" ? `-${25 + (id % 4) * 8}%` : undefined,
      size: sizes[id % sizes.length],
      productCondition: type === "normal" && id % 3 === 0 ? "NEW WITH TAG" : `${92 + (id % 3) * 3}%`,
      brandName: brand,
      productImageResponses: [{ imageUrl: img, thumbnail: true }]
    };
  });
};

const HOT_SALES = generateProducts("sale", "Đồ thu đông", 200);
const SHOES = generateProducts("shoes", "Giày thời trang", 500);
const PREMIUM_BRANDS = generateProducts("normal", "Sản phẩm luxury", 600);

const FEEDBACKS = [
  { id: 1, name: "Nguyễn Lan Anh", role: "Người mua hàng", avatar: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150", content: "Đồ ký gửi ở đây siêu mới, mình mua được chiếc Blazer Mango nguyên tag với giá chưa tới một nửa giá gốc." },
  { id: 2, name: "Trần Minh Đức", role: "Người ký gửi", avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", content: "Quy trình ký gửi rất nhanh gọn, minh bạch. Gửi đồ 2 tuần đã thấy bán xong và nhận tiền đối soát." },
  { id: 3, name: "Lê Hoàng Yến", role: "Người mua hàng", avatar: "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150", content: "Đồ đóng gói rất xinh, sạch sẽ và thơm tho. Sẽ ủng hộ Simi dài dài!" },
  { id: 4, name: "Phạm Hoàng Nam", role: "Người ký gửi", avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150", content: "Tủ đồ chật cứng cuối cùng cũng được giải quyết. Vừa dọn nhà lại vừa có thêm một khoản thu nhập nhỏ." },
  { id: 5, name: "Đỗ Thu Thảo", role: "Người mua hàng", avatar: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150", content: "Săn được đôi sneaker Nike chính hãng tại đây giá cực hời, độ mới cao. Dịch vụ chăm sóc khách hàng tốt." },
  { id: 6, name: "Vũ Quốc Khánh", role: "Người mua hàng", avatar: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150", content: "Các sản phẩm của thương hiệu lớn đều được kiểm định rất kỹ. Cảm giác mua sắm cực kỳ an tâm." }
];

export const HomePage = () => {
  // Nạp dữ liệu thực từ API qua React Query hooks
  const { data: newestProducts = [], isLoading: newestLoading } = useNewestProducts();
  const { data: newTagProducts = [], isLoading: newTagLoading } = useNewTagProducts();
  const { data: footwearProducts = [], isLoading: footwearLoading } = useFootWearSection();
  const { data: accessoriesProducts = [], isLoading: accessoriesLoading } = useAccessoriesSection();

  const feedbackRef = useRef<HTMLDivElement>(null);

  const scrollFeedback = (direction: "left" | "right") => {
    if (feedbackRef.current) {
      const scrollAmount = 340;
      feedbackRef.current.scrollBy({
        left: direction === "left" ? -scrollAmount : scrollAmount,
        behavior: "smooth"
      });
    }
  };

  if (newestLoading || newTagLoading || accessoriesLoading || footwearLoading) {
    return <div className={styles.loading}>Đang tải trang chủ...</div>;
  }

  return (
    <div className={styles.home}>
      {/* 1. HERO BANNER */}
      <section className={styles.hero}>
        <div className={styles.heroOverlay}>
          <div className={styles.heroContent}>
            <span className={styles.heroSubtitle}>Thời trang bền vững & Ký gửi độc bản</span>
            <h1 className={styles.heroTitle}>FASHION WITH <br />A SECOND LIFE</h1>
            <p className={styles.heroDesc}>
              Dọn gọn tủ đồ, chia sẻ phong cách và tìm kiếm những món đồ độc bản được tuyển chọn kỹ lưỡng từ Simi.
            </p>
            <div className={styles.heroActions}>
              <Link to="/products" className={styles.btnPrimary}>Mua ngay</Link>
              <Link to="/consignments" className={styles.btnSecondary}>Ký gửi đồ</Link>
            </div>
          </div>
        </div>
      </section>

      <div className={styles.mainContainer}>
        {/* 2. CÁC PHÂN MỤC SẢN PHẨM */}
        <ProductSection title="Mới lên kệ" products={newestProducts} viewAllPath="/products?sort=newest" />
        {/* <ProductSection title="Gian hàng giảm giá" products={HOT_SALES} viewAllPath="/products?sort=discount" isSale /> */}
        <ProductSection title="Hàng nguyên Tag" products={newTagProducts} viewAllPath="/products?condition=NEW" />
        
        {/* Đã cập nhật nạp dữ liệu thực accessoriesProducts vào đây */}
        <ProductSection title="Phụ kiện thời trang" products={accessoriesProducts} viewAllPath="/products?category=accessories" />
        
        <ProductSection title="Giày dép" products={footwearProducts} viewAllPath="/products?category=shoes" />
        {/* <ProductSection title="Thương hiệu yêu thích" products={PREMIUM_BRANDS} viewAllPath="/products?type=premium" /> */}

        {/* 3. FEEDBACK KHÁCH HÀNG */}
        <section className={`${styles.section} ${styles.sectionAlt}`}>
          <h2 className={styles.sectionTitle}>Đánh giá từ khách hàng</h2>
          <div className={styles.carouselWrapper}>
            <button onClick={() => scrollFeedback("left")} className={`${styles.navBtn} ${styles.navBtnLeft}`}>‹</button>
            
            <div ref={feedbackRef} className={styles.carouselTrack}>
              {FEEDBACKS.map((fb) => (
                <div key={fb.id} className={styles.feedbackCard}>
                  <div className={styles.feedbackHeader}>
                    <img src={fb.avatar} alt={fb.name} className={fb.avatar ? styles.feedbackAvatar : ""} />
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

            <button onClick={() => scrollFeedback("right")} className={`${styles.navBtn} ${styles.navBtnRight}`}>›</button>
          </div>
        </section>
      </div>
    </div>
  );
};
