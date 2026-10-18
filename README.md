# DAFL Continuous Testing Automation Framework 🚀

Dự án kiểm thử tự động toàn diện (**Continuous Functional, E2E & Negative Scenarios Testing**) cho nền tảng thương mại điện tử **DAFL Store**, phục vụ Đồ án Tốt nghiệp: *Xây dựng và đánh giá quy trình Continuous Testing có Quality Gate tự động*.

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

## 🎯 3 Bộ Kiểm thử (Test Suites)

| Test Suite File | Tên Suite | Mục tiêu kiểm thử | Số lượng Cases |
| :--- | :--- | :--- | :---: |
| `testng-flow.xml` | **E2E Shopping Flow** | Luồng khách hàng mua sắm xuyên suốt từ đăng nhập đến thanh toán PayOS & cập nhật địa chỉ | **8 Steps** (100% Passed) |
| `testng-negative.xml` | **Negative & Exception Scenarios** | Bộ kiểm thử ngoại lệ, dữ liệu không hợp lệ, biên, bảo mật injection phủ toàn bộ 5 luồng | **21 Cases** (100% Passed) |
| `testng.xml` | **Full Continuous Regression** | Tổng hợp toàn diện E2E Journey, Negative Cases và các Module Regression | **40+ Cases** (100% Passed) |

---

## 🛡️ Chi tiết Bộ 21 Kịch bản Ngoại lệ (Negative & Exception Cases)

### Phân hệ 1: Xác thực & Đăng ký (Authentication & Registration - 7 Cases)
1. **TC_EX_01**: Đăng nhập với mật khẩu sai -> Báo lỗi xác thực, không đóng modal bất thường, giữ nguyên email.
2. **TC_EX_02**: Đăng nhập với tài khoản chưa đăng ký (`ghost_user_xxx@dafl.vn`) -> Báo lỗi tài khoản không tồn tại.
3. **TC_EX_03**: Bỏ trống Email khi đăng nhập -> HTML5 required validation chặn submit, không gửi request rác.
4. **TC_EX_04**: Bỏ trống Mật khẩu khi đăng nhập -> HTML5 validation chặn submit.
5. **TC_EX_05**: Nhập Email sai định dạng (thiếu `@` hoặc domain) -> Trình duyệt kích hoạt validation email.
6. **TC_EX_06**: Đăng ký khi mật khẩu và mật khẩu xác nhận không khớp -> Thông báo lỗi "Mật khẩu xác nhận không khớp".
7. **TC_EX_07**: Đăng ký với Email đã tồn tại (`demo@dafl.vn`) -> Hệ thống từ chối đăng ký trùng lặp.

### Phân hệ 2: Tìm kiếm, Bộ lọc & Danh mục (Search, Filter & Catalog - 4 Cases)
8. **TC_EX_08**: Tìm kiếm từ khóa rác vô nghĩa (`XYZ_KHONG_TON_TAI_99999`) -> Hiển thị 0 sản phẩm an toàn, không vỡ layout/trắng trang.
9. **TC_EX_09**: Tìm kiếm chứa chuỗi SQL Injection & XSS payload (`<script>alert('xss')</script>' OR '1'='1`) -> Hệ thống lọc và xử lý an toàn (sanitization), không lỗi 500.
10. **TC_EX_10**: Tìm kiếm toàn bộ ký tự khoảng trắng (`"     "`) -> Hệ thống xử lý an toàn không gây runtime exception.
11. **TC_EX_11**: Bấm nút xóa tìm kiếm (X) sau khi nhận 0 kết quả -> Danh mục sản phẩm được phục hồi đầy đủ ngay lập tức.

### Phân hệ 3: Giỏ hàng & Khuyến mãi Voucher (Cart & Voucher - 5 Cases)
12. **TC_EX_12**: Mở giỏ hàng khi đang rỗng -> Hiển thị empty state "Giỏ hàng của bạn đang trống", ẩn form checkout rác.
13. **TC_EX_13**: Áp dụng mã Voucher không tồn tại (`SAI_VOUCHER_CODE_99999`) -> Báo lỗi "Mã giảm giá không hợp lệ", tổng tiền giữ nguyên.
14. **TC_EX_14**: Bấm áp dụng voucher khi để trống ô nhập mã -> Không kích hoạt giảm giá ảo.
15. **TC_EX_15**: Áp dụng voucher hợp lệ rồi bấm "Bỏ chọn" -> Gỡ bỏ voucher và khôi phục tổng tiền gốc chính xác.
16. **TC_EX_16**: Giảm số lượng sản phẩm từ 1 về 0 -> Tự động xóa sản phẩm khỏi giỏ, ngăn chặn số lượng âm (-1).

### Phân hệ 4: Thanh toán & Đặt hàng (Checkout Form & PayOS - 4 Cases)
17. **TC_EX_17**: Checkout để trống Họ và tên -> Validation chặn submit, không chuyển sang PayOS Gateway.
18. **TC_EX_18**: Checkout với Email sai định dạng -> Email validation chặn submit.
19. **TC_EX_19**: Checkout để trống Địa chỉ giao hàng -> Validation chặn submit đơn hàng rỗng địa chỉ.
20. **TC_EX_20**: Checkout để trống Số điện thoại người nhận -> Validation chặn submit.

### Phân hệ 5: Lịch sử đơn hàng (Orders History - 1 Case)
21. **TC_EX_21**: Lọc đơn hàng theo trạng thái không có dữ liệu (Đã hủy) -> Hiển thị giao diện mượt mà không bị crash hay lỗi JavaScript.

---

## ⚡ Tích hợp CI/CD Jenkinsfile có Quality Gates Tự động

File `Jenkinsfile` đã được cấu hình hoàn chỉnh với 2 cổng Quality Gate:
1. **Functional Quality Gate (Gate 1)**: Quét báo cáo Surefire XML của Selenium Headless. Nếu có bất kỳ test failure nào, pipeline sẽ **dừng ngay lập tức (ABORT)**.
2. **Performance Quality Gate (Gate 2)**: Chạy JMeter Non-GUI load test, kiểm tra Error Rate (<= 2%) và Avg Response Time (<= 1000ms). Nếu vi phạm, pipeline sẽ **dừng ngay lập tức**.
3. **Artifacts & Reports**: Tự động xuất Allure Visual Report và JMeter HTML Report Dashboard lên giao diện Jenkins.
4. **Deploy Production**: Chỉ triển khai container production khi CẢ HAI Quality Gate đều đạt trạng thái PASSED!

---

## 🚀 Hướng dẫn chạy kiểm thử

### Cách 1: Chạy trực tiếp từ dòng lệnh (Maven)
```bash
# Chạy bộ 21 kịch bản ngoại lệ (Headless):
mvn test -DsuiteXmlFile=testng-negative.xml -Dheadless=true

# Chạy luồng E2E 8 bước (Giao diện Chrome có GUI):
mvn test -DsuiteXmlFile=testng-flow.xml -Dheadless=false

# Mở báo cáo Allure Report:
mvn allure:serve
```

### Cách 2: Chạy từ IntelliJ IDEA
Mở thư mục `dafl_automation_pom` trong IntelliJ IDEA và chọn Run Configuration có sẵn trên thanh công cụ:
- `Run E2E Flow (Chrome GUI)`
- `Run E2E Flow (Headless CI)`
- `Run Negative Scenarios (Chrome GUI)`
- `Run Negative Scenarios (Headless CI)`