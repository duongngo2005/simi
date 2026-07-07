**ĐỀ CƯƠNG ĐỒ ÁN TỐT NGHIỆP**

**Đại học Mở TP HCM**

**Ngành: Công nghệ Thông tin / Khoa học Máy tính**

# 1. Tên đề tài:

- Xây dựng Website Ký gửi đồ thời trang "Simi"

# 2. Lý do chọn đề tài:

**Vấn đề cần giải quyết:** Hiện nay, hoạt động ký gửi áo quần, giày dép và các phụ kiện thời trang tại nhiều cửa hàng vẫn được quản lý bằng sổ sách, trang tính hoặc các công cụ rời rạc không có tính hệ thống. Điều này gây khó khăn trong quá trình theo dõi từng sản phẩm, thông tin, giá bán, tình trạng và doanh thu. Bên cạnh đó, đối với khách hàng, việc mua hàng đã qua sử dụng thường khó tiếp cận đầy đủ thông tin chi tiết, việc mua bán chủ yếu diễn ra tại cửa hàng hoặc trao đổi trên mạng. Quy trình đặt hàng, giao hàng và thanh toán không đồng bộ, đặc biệt với đồ cũ thì không có gì có thể đảm bảo chất lượng sản phẩm, đồ chính hãng hay giả mạo.

Vì vậy cần xây dựng một hệ thống website giúp quản lý, đồng bộ tất cả quy trình kinh doanh của cửa hàng, bao gồm quản lý các lô hàng, theo dõi hạn, xử lý đơn hàng, giao hàng và quản lý doanh thu. Đồng thời cũng giúp khách hàng dễ tiếp cận, tạo sự tin tưởng khi mua hàng.

**Tính cấp thiết:** Việc xây dựng hệ thống web quản lý ký gửi giúp số hóa quy trình, giảm sai sót, tăng tính minh bạch, nâng cao hiệu quả vận hành và đáp ứng nhu cầu mua hàng thời trang đã qua sử dụng ngày càng tăng.

# 3. Mục tiêu cần đạt được:

- Xây dựng thành công hệ thống website Simi hỗ trợ quản lý và kinh doanh thời trang ký gửi.
- Sản phẩm cuối cùng cho phép khách hàng:
    - Đăng ký, đăng nhập, quản lý tài khoản
    - Tìm kiếm, xem thông tin và mua sắm
    - Thêm sản phẩm vào giỏ hàng, đặt hàng và theo dõi đơn hàng
    - Thanh toán bằng COD, chuyển khoản online
- Cho phép nhân viên:
    - Quản lý lô hàng và từng sản phẩm
    - Cập nhật tình trạng sản phẩm, thời hạn ký gửi
    - Xử lý đơn hàng, giao hàng và thanh toán tiền cho khách
- Cho phép quản trị viên quản lý người dùng, nhân viên, sản phẩm, danh mục, đơn hàng và hoạt động của hệ thống

# 4. Phạm vi dự án:

**Các chức năng sẽ thực hiện (In-scope):**

- Module Người dùng: Đăng ký, đăng nhập, quản lý thông tin cá nhân
- Module Sản phẩm: Quản lý sản phẩm, danh mục, thương hiệu, tình trạng và giá bán
- Module ký gửi: Tiếp nhận lô hàng, quản lý ký gửi, thời hạn ký gửi và trạng thái bán
- Module mua hàng: tìm sản phẩm, xem chi tiết, thêm vào giỏ, đặt hàng và theo dõi đơn hàng
- Module thanh toán và giao hàng: Hỗ trợ COD, chuyển khoản hoặc Momo
- Module quản trị: Quản lý người dùng, sản phẩm, đơn hàng, doanh thu và thanh toán cho người ký gửi

**Các chức năng sẽ KHÔNG thực hiện (Out-of-scope):**

- Ký gửi trực tuyến
- Xây dựng app mobile

# 5. Công nghệ dự kiến sử dụng:

**Ngôn ngữ lập trình:** Java, JavaScript

**Frontend:** TypeScript/React, HTML5, CSS3

**Backend:** Spring Boot

**Cơ sở dữ liệu:** MySQL

**Công cụ khác:** Git/GitHub, IntelliJ IDEA, Visual Studio Code, Postman.

# 6. Kế hoạch thực hiện sơ bộ (10 tuần):

| **Tuần** | **Nội dung công việc chính** | **Mục tiêu/Sản phẩm** |
| --- | --- | --- |
| 1 | Phân tích yêu cầu, Thiết kế hệ thống | Hoàn thành tài liệu SRS, Sơ đồ ERD, thiết kế Wireframe. |
| 2-3 | Xây dựng CSDL, API Backend cho module Người dùng | Hoàn thành đăng ký, đăng nhập, phân quyền và quản lý thông tin cá nhân. |
| 4-5 | Xây dựng Backend cho module Sản phẩm và Ký gửi | Hoàn thành quản lý danh mục, sản phẩm, lô ký gửi, thời hạn và trạng thái sản phẩm. |
| 6 | Xây dựng Backend cho module Giỏ hàng, Đơn hàng và Thanh toán | Hoàn thành chức năng đặt hàng, quản lý đơn hàng và phương thức thanh toán. |
| 7-8 | Xây dựng giao diện Frontend và tích hợp API | Hệ thống hoạt động ổn định với các chức năng chính; dự kiến thử nghiệm tìm kiếm thông minh bằng AI. |
| 9 | Kiểm thử, sửa lỗi và hoàn thiện chức năng | Nộp báo cáo và sẵn sàng cho buổi bảo vệ. |
| 10 | Viết báo cáo, hoàn thiện sản phẩm và chuẩn bị slide demo | Hoàn thành báo cáo, mã nguồn, dữ liệu minh họa |

**Sinh viên thực hiện**

Họ và tên: Ngô Đình Dương

MSSV: 2351050025

Lớp: DH23IT01

**Giảng viên hướng dẫn**

Họ và tên: Nguyễn Văn Bảy
