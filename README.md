# DAFL Continuous Testing Automation Framework 🚀

Dự án kiểm thử tự động toàn diện (Continuous Functional & E2E Testing) cho nền tảng thương mại điện tử **DAFL Store**, phục vụ Đồ án Tốt nghiệp: *Xây dựng và đánh giá quy trình Continuous Testing có Quality Gate tự động*.

---

## 🛠️ Công nghệ & Kiến trúc áp dụng

- **Mô hình thiết kế**: Page Object Model (POM) kết hợp Page Factory, Singleton Driver, Factory Pattern.
- **Ngôn ngữ & Runtime**: Java 17 / Java 21 LTS, Apache Maven 3.9+.
- **Thư viện lõi**:
  - **Selenium WebDriver 4.33.0**: Tương thích hoàn toàn Google Chrome v155+, Chromium DevTools Protocol (CDP).
  - **TestNG 7.11.0**: Quản trị luồng kiểm thử, annotations lifecycle (`@BeforeClass`, `@Test(priority)`), parallel execution.
  - **Allure TestNG 2.29.1**: Báo cáo trực quan từng Step, Severity, Epic, Feature, Story, Screenshots.
  - **WebDriverManager 5.9.3**: Tự động tải và đồng bộ driver tương thích với phiên bản trình duyệt.

---

## 🔄 Luồng End-to-End Customer Shopping Journey Chuẩn (8 Bước)

Dự án thiết kế luồng khách hàng mua sắm tự nhiên, thực thi xuyên suốt trên **1 phiên trình duyệt duy nhất**:

1. **Bước 1 (`Step01_ExploreAndLogin`)**: Khám phá trang chủ, thử nghiệm đổi Theme Sáng/Tối & Đăng nhập tài khoản khách hàng.
2. **Bước 2 (`Step02_SearchAndFilterProducts`)**: Tìm kiếm sản phẩm theo từ khóa (`LED`), xóa tìm kiếm & lọc theo danh mục ngành hàng.
3. **Bước 3 (`Step03_ViewDetailAndAddToCart`)**: Xem chi tiết sản phẩm đầu tiên (Modal), xem tab Đánh giá khách hàng & Thêm vào giỏ hàng.
4. **Bước 4 (`Step04_ManageCartAndVoucher`)**: Mở giỏ hàng, áp dụng mã giảm giá Voucher `DAFL10` (-10%) & xác thực tổng tiền đã giảm trừ.
5. **Bước 5 (`Step05_CheckoutAndPayOSGateway`)**: Điền/kiểm tra form nhận hàng đã tự động điền theo profile, tạo đơn hàng chuyển sang Cổng PayOS VietQR, xử lý webhook HMAC-SHA256 chuyển trạng thái `PAID` và xác nhận thanh toán thành công.
6. **Bước 6 (`Step06_TrackOrdersHistory`)**: Mở trang Quản lý đơn hàng (`#/orders`), kiểm tra đơn hàng vừa mua ở trạng thái ĐÃ THANH TOÁN, thử nghiệm lọc các trạng thái đơn.
7. **Bước 7 (`Step07_UpdateProfileAddress`)**: Vào trang Thông tin cá nhân & Sổ địa chỉ (`#/profile`), cập nhật địa chỉ giao hàng mặc định mới.
8. **Bước 8 (`Step08_LogoutAndFinish`)**: Đăng xuất an toàn khỏi hệ thống và hoàn tất chu trình kiểm thử.

---

## 📁 Cấu trúc thư mục dự án

```text
dafl_automation_pom/
├── .idea/                             # Cấu hình dự án IntelliJ IDEA (mở phát ăn ngay)
│   ├── runConfigurations/             # 2 Run Configuration cài sẵn: GUI & Headless
│   ├── compiler.xml
│   ├── encodings.xml
│   └── misc.xml
├── src/
│   ├── main/java/vn/dafl/
│   │   ├── commons/                   # Lớp nền dùng chung: BasePage, BaseTest, GlobalConstants
│   │   ├── driver/                    # DriverManager quản lý WebDriver Thread-safe
│   │   ├── pageObjects/               # 7 Page Objects theo chuẩn POM (Actions & Business flows)
│   │   ├── pageUIs/                   # 7 Page UI Locators (data-testid, XPath, CSS Selectors)
│   │   └── reportConfig/              # Allure Listener chụp ảnh màn hình khi fail
│   └── test/java/vn/dafl/tests/
│       ├── EndToEndShoppingFlowTest   # ⭐ Luồng E2E chuẩn 8 bước liên hoàn trên 1 trình duyệt duy nhất
│       ├── AuthTest                   # Kiểm thử độc lập: Đăng ký / đăng nhập
│       ├── CatalogAndSearchTest       # Kiểm thử độc lập: Tìm kiếm, lọc danh mục
│       ├── CartManagementTest         # Kiểm thử độc lập: Giỏ hàng, mã voucher
│       └── CheckoutAndOrderTest       # Kiểm thử độc lập: Checkout, lịch sử đơn hàng
├── pom.xml                            # File cấu hình Maven Dependencies & Plugins
├── testng-flow.xml                    # File cấu hình TestNG Suite cho E2E Flow
├── testng.xml                         # File cấu hình TestNG Suite cho Regression Test
├── run_test_with_gui.bat              # Script chạy trực tiếp có giao diện Chrome Desktop GUI
├── run_test_headless.bat              # Script chạy ngầm cho Jenkins CI/CD Quality Gate
└── .gitignore                         # Loại bỏ target, allure-results, file rác
```

---

## 💻 Hướng dẫn mở dự án trên IntelliJ IDEA

1. Khởi động **IntelliJ IDEA**.
2. Chọn **Open** (hoặc `File` -> `Open`).
3. Chọn thư mục `D:\DO_AN_TOT_NGHIEP\dafl_automation_pom`.
4. Mở file [`EndToEndShoppingFlowTest.java`](src/test/java/vn/dafl/tests/EndToEndShoppingFlowTest.java).
5. Bấm nút **Play màu xanh lá** cạnh class để chạy trực tiếp!
