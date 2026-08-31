import { Link } from "react-router";
import aboutHeroImage from "../../../assets/images/about-simi-hero.jpg";
import styles from "./AboutPage.module.css";

const journey = [
  {
    number: "01",
    title: "Gửi đồ",
    description: "Mang món đồ bạn không còn sử dụng đến Simi.",
  },
  {
    number: "02",
    title: "Kiểm tra & định giá",
    description: "Simi tiếp nhận, kiểm tra và thống nhất giá bán.",
  },
  {
    number: "03",
    title: "Bán",
    description: "Món đồ xuất hiện trên Simi để gặp người mua mới.",
  },
  {
    number: "04",
    title: "Quyết toán",
    description: "Khi giao dịch hoàn tất, người gửi nhận phần tiền của mình.",
  },
];

export const AboutPage = () => {
  return (
    <div className={styles.page}>
      <section className={styles.hero} aria-labelledby="about-title">
        <div className={styles.heroCopy}>
          <p className={styles.eyebrow}>Về Simi</p>
          <h1 id="about-title">Những món đồ đẹp xứng đáng có một hành trình thứ hai.</h1>
          <p className={styles.lead}>
            Simi là nền tảng thời trang ký gửi, kết nối những món đồ không còn được sử dụng với những người đang tìm kiếm chúng.
            Mỗi lô ký gửi có một hành trình rõ ràng, từ tiếp nhận đến khi được bán và quyết toán.
          </p>
          <Link to="/products" className={styles.primaryLink}>
            Khám phá sản phẩm đang có sẵn
          </Link>
        </div>

        <div className={styles.heroVisual}>
          <div className={styles.heroImageFrame}>
            <img src={aboutHeroImage} alt="Những món đồ được trao cho một hành trình mới" />
          </div>
        </div>
      </section>

      <section className={styles.storySection} aria-labelledby="why-simi-title">
        <div className={styles.sectionIntro}>
          <p className={styles.eyebrow}>Vì sao Simi tồn tại</p>
          <h2 id="why-simi-title">Có những món đồ không còn phù hợp với bạn, nhưng vẫn còn rất nhiều giá trị.</h2>
        </div>
        <div className={styles.storyCopy}>
          <p>
            Simi muốn biến việc bán lại đồ cũ thành một trải nghiệm rõ ràng và tử tế hơn.
            Người gửi theo dõi được trạng thái lô ký gửi; người mua biết rõ tình trạng và giá hiện tại của món đồ.
          </p>
          <div className={styles.principleList}>
            <article>
              <span>GIỮ LẠI GIÁ TRỊ</span>
              <p>Một món đồ tốt không nhất thiết phải kết thúc hành trình ở người chủ đầu tiên.</p>
            </article>
            <article>
              <span>MINH BẠCH</span>
              <p>Tình trạng, giá bán và trạng thái món đồ được thể hiện rõ ràng.</p>
            </article>
            <article>
              <span>TIẾP TỤC HÀNH TRÌNH</span>
              <p>Mỗi món đồ được trao thêm một cơ hội để được sử dụng và yêu thích.</p>
            </article>
          </div>
        </div>
      </section>

      <section className={styles.passportSection} aria-labelledby="passport-title">
        <div className={styles.passportCopy}>
          <p className={styles.eyebrow}>Simi Passport</p>
          <h2 id="passport-title">Mỗi món đồ có một câu chuyện. Simi lưu lại hành trình của nó.</h2>
          <p>
            Passport không phải một tính năng tách rời: đó là cách Simi hiển thị những dữ liệu cần thiết để người mua và người gửi hiểu rõ một món đồ.
          </p>
        </div>

        <div className={styles.passportCard}>
          <div className={styles.passportHeading}>
            <span>SIMI PASSPORT</span>
            <strong>01</strong>
          </div>
          <p className={styles.passportName}>Thông tin được ghi nhận cho từng món đồ.</p>
          <dl className={styles.passportData}>
            <div>
              <dt>Tình trạng</dt>
              <dd>Mô tả rõ ràng</dd>
            </div>
            <div>
              <dt>Giá hiện tại</dt>
              <dd>Niêm yết minh bạch</dd>
            </div>
            <div>
              <dt>Trạng thái</dt>
              <dd>Theo dõi được</dd>
            </div>
          </dl>
        </div>
      </section>

      <section className={styles.journeySection} id="consignment-journey" aria-labelledby="journey-title">
        <div className={styles.sectionIntro}>
          <p className={styles.eyebrow}>Hành trình ký gửi</p>
          <h2 id="journey-title">Một quy trình ngắn gọn, với trạng thái rõ ràng ở từng chặng.</h2>
        </div>
        <ol className={styles.journeyList}>
          {journey.map((step) => (
            <li key={step.number}>
              <span>{step.number}</span>
              <h3>{step.title}</h3>
              <p>{step.description}</p>
            </li>
          ))}
        </ol>
      </section>

      <section className={styles.closing}>
        <p className={styles.eyebrow}>Một hành trình mới</p>
        <h2>Một món đồ cũ với bạn, có thể là món đồ đúng lúc với người khác.</h2>
        <div className={styles.closingActions}>
          <Link to="/products" className={styles.primaryLink}>Khám phá sản phẩm</Link>
          <a href="#consignment-journey" className={styles.secondaryLink}>Xem quy trình ký gửi</a>
        </div>
      </section>
    </div>
  );
};
