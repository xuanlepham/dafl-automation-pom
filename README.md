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
│       ├── EndToEndShoppingFlowTest   # ⭐ Luồng E2E 7 bước liên hoàn trên 1 trình duyệt duy nhất
│       ├── AuthenticationTest         # Kiểm thử đăng ký / đăng nhập
│       ├── SearchAndFilterTest        # Kiểm thử tìm kiếm, lọc danh mục
│       ├── CartAndCheckoutTest        # Kiểm thử giỏ hàng, mã voucher
│       └── OrdersAndProfileTest       # Kiểm thử lịch sử đơn hàng, cập nhật sổ địa chỉ
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
4. IntelliJ sẽ tự động nhận diện `pom.xml`, đồng bộ thư viện Maven và nạp 2 cấu hình chạy sẵn:
   - **`Run E2E Flow (Chrome GUI)`**: Chạy trực quan có giao diện Chrome.
   - **`Run E2E Flow (Headless CI)`**: Chạy ngầm tốc độ cao.
5. Bấm nút **Run (nút tam giác xanh)** trên thanh công cụ để thực thi!

---

## ⚡ Hướng dẫn thực thi từ Command Line / Batch Script

### Cách 1: Chạy trực tiếp có giao diện quan sát (Khuyên dùng)
Click đúp file `run_test_with_gui.bat` trên Windows Explorer, hoặc chạy:
```cmd
run_test_with_gui.bat
```

### Cách 2: Chạy chế độ Headless ngầm (Dành cho Jenkins CI/CD)
```cmd
run_test_headless.bat
```

### Cách 3: Lệnh Maven thủ công
```bash
mvn test -Dtest=EndToEndShoppingFlowTest -Dheadless=false -Dapp.url=http://localhost:3000
```
