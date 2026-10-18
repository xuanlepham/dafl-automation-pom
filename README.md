# DAFL Continuous Testing & Quality Gates Pipeline Framework

Dự án kiểm thử tự động toàn diện (**Automation Functional Testing**) và kiểm thử hiệu năng (**Performance Testing**) tích hợp trực tiếp vào **CI/CD Jenkins Pipeline** với 2 cổng **Quality Gate tự động** cho nền tảng thương mại điện tử **DAFL**.

---

## 1. KIẾN TRÚC REPOSITORY & TỔ CHỨC MÃ NGUỒN

Repository này là trung tâm điều phối toàn bộ hoạt động kiểm thử và CI/CD của hệ sinh thái DAFL:

```
dafl-automation-pom/
├── Jenkinsfile                  # Master Pipeline CI/CD (Auto Test -> Gate #1 -> PT -> Gate #2 -> Deploy Prod)
├── Jenkinsfile.auto             # Pipeline chuyên biệt cho Automation Functional Testing (Selenium + Quality Gate #1)
├── Jenkinsfile.pt               # Pipeline chuyên biệt cho Performance Testing (JMeter + Quality Gate #2)
├── pom.xml                      # Cấu hình Maven, Java 17, Selenium 4.18.1, TestNG 7.9.0, Allure 2.25.0
├── testng-flow.xml              # Suite kịch bản E2E Customer Journey (8 bước mua hàng tự nhiên)
├── testng-negative.xml          # Suite 21 kịch bản ngoại lệ & bắt lỗi (Negative & Exception Scenarios)
├── testng.xml                   # Suite hồi quy toàn bộ hệ thống (Regression Test Suite)
├── src/                         # Mã nguồn Automation Functional Testing (Chuẩn Page Object Model)
│   ├── main/java/vn/dafl/
│   │   ├── commons/             # BasePage, BaseTest, GlobalConstants
│   │   ├── driver/              # DriverManager (ThreadLocal thread-safe)
│   │   ├── pageObjects/         # Nghiệp vụ thao tác từng màn hình (Auth, Catalog, Cart, Checkout, Orders)
│   │   ├── pageUIs/             # Bộ định danh Selector (data-testid, css, xpath)
│   │   └── reportConfig/        # AllureTestListener (Tự động capture screenshot khi fail)
│   └── test/java/vn/dafl/tests/
│       ├── EndToEndShoppingFlowTest.java        # Luồng mua hàng hoàn chỉnh 8 bước
│       ├── NegativeAndExceptionScenariosTest.java # 21 kịch bản ngoại lệ không làm gãy luồng
│       └── ...
└── tests/performance-jmeter/    # Bộ kịch bản và công cụ kiểm thử hiệu năng
    ├── dafl-loadtest.jmx        # Kịch bản JMeter bắn tải đồng thời Backend API
    ├── check_performance_gate.sh# Script Shell đánh giá Quality Gate #2 (Error Rate & Response Time)
    └── check_performance_gate.py# Phân tích thống kê kết quả kiểm thử tải
```

---

## 2. CHI TIẾT 2 PIPELINE: AUTO VÀ PT

### A. Pipeline Auto (`Jenkinsfile.auto` - Functional Testing)
- **Mục tiêu**: Đảm bảo toàn bộ tính năng và 21 luồng ngoại lệ hoạt động chính xác 100% trước khi triển khai.
- **Quy trình thực thi**:
  1. **Pull Code**: Jenkins tự động clone/pull mã nguồn mới nhất từ GitHub.
  2. **Healthcheck SUT**: Kiểm tra container Frontend (`http://dafl-frontend:80`) sẵn sàng qua HTTP 200.
  3. **Execute Tests**: Khởi chạy container `markhobson/maven-chrome:jdk-17` chạy TestNG suite theo tham số.
  4. **Functional Quality Gate (#1)**: Quét file XML Surefire. Nếu phát hiện `failures > 0` hoặc `errors > 0`, **dừng ngay pipeline và đánh dấu FAILED**.
  5. **Báo cáo**: Xuất **Allure Visual Test Report** trực quan trên giao diện Jenkins.

### B. Pipeline PT (`Jenkinsfile.pt` - Performance Testing)
- **Mục tiêu**: Đảm bảo hệ thống chịu được tải thực tế, không bị treo hoặc phản hồi chậm vượt ngưỡng cam kết SLA.
- **Quy trình thực thi**:
  1. **Pull Code**: Jenkins tự động kéo kịch bản JMeter `dafl-loadtest.jmx` mới nhất.
  2. **Healthcheck Backend**: Kiểm tra Backend API phản hồi (`dafl-backend:8080/api/products`).
  3. **Execute JMeter**: Khởi chạy container `justb4/jmeter:5.5` Non-GUI (RAM 768MB, Heap 512MB) bắn tải.
  4. **Performance Quality Gate (#2)**: Script `check_performance_gate.sh` kiểm tra 2 chỉ số SLA:
     - Tỉ lệ lỗi (**Error Rate**): Phải $\le 2.0\%$.
     - Thời gian phản hồi trung bình (**Average Response Time**): Phải $\le 1000	ext{ ms}$.
     - Nếu vi phạm bất kỳ chỉ số nào $ightarrow$ **dừng pipeline ngay lập tức**.
  5. **Báo cáo**: Xuất **JMeter HTML Dashboard Report** biểu đồ trực quan (Throughput, Latency, Response Time percentiles).

---

## 3. 3 CƠ CHẾ KÍCH HOẠT (TRIGGERS) TRÊN JENKINS

Cả hai pipeline (`Jenkinsfile.auto`, `Jenkinsfile.pt`) và Master `Jenkinsfile` đều được tích hợp sẵn 3 cơ chế kích hoạt tự động theo tiêu chuẩn quốc tế:

```groovy
triggers {
    // 1. TỰ ĐỘNG CHẠY KHI CÓ CODE MỚI (Push event / GitHub Webhook)
    githubPush()

    // 2. TỰ ĐỘNG QUÉT KIỂM TRA THAY ĐỔI TRÊN GIT (Poll SCM định kỳ)
    pollSCM('H/5 * * * *')

    // 3. TỰ ĐỘNG CHẠY THEO NGÀY GIỜ CỐ ĐỊNH (Cron Schedule / Nightly Build)
    // Pipeline Auto: Chạy lúc 02:00 sáng từ Thứ Hai đến Thứ Sáu
    cron('0 2 * * 1-5')
    // Pipeline PT  : Chạy lúc 03:00 sáng từ Thứ Hai đến Thứ Sáu
    // cron('0 3 * * 1-5')
}
```

### Cơ chế 1: Tự động chạy khi có code mới (Git Push / Webhook)
- Bất cứ khi nào bạn hoặc developer `git push` commit mới lên branch `main`, GitHub sẽ gửi Webhook tới Jenkins hoặc Jenkins phát hiện thay đổi (`pollSCM`) để tự động kích hoạt build và test ngay.

### Cơ chế 2: Tự động chạy theo ngày giờ cố định (Cron Schedule / Nightly Build)
- Hệ thống thiết lập lịch định kỳ tự động chạy vào ban đêm mà không cần can thiệp:
  - **Auto Test**: Tự động chạy vào lúc **02:00 sáng** hàng ngày (`0 2 * * 1-5`).
  - **Performance Test**: Tự động chạy vào lúc **03:00 sáng** hàng ngày (`0 3 * * 1-5`).
  - Toàn bộ báo cáo kết quả sẽ có sẵn vào buổi sáng để team kiểm tra.

### Cơ chế 3: Tự nhấn nút chạy thủ công (Manual / Build with Parameters)
- Khi bạn cần kiểm tra ngay lập tức:
  1. Truy cập Jenkins tại: `http://localhost:8088`.
  2. Chọn Job tương ứng (`dafl-auto-test` hoặc `dafl-performance-test`).
  3. Bấm **"Build with Parameters"** hoặc **"Build Now"**.
  4. Jenkins sẽ **tự động pull code mới nhất về và thực thi** theo các tham số bạn chọn (chọn Test Suite, ngưỡng Error Rate, v.v.).

---

## 4. HƯỚNG DẪN TẠO JOB TRÊN JENKINS UI

1. Vào `http://localhost:8088` $ightarrow$ Chọn **New Item**.
2. Nhập tên:
   - `dafl-auto-pipeline` (cho Functional Auto Test)
   - `dafl-pt-pipeline` (cho Performance Test)
3. Chọn kiểu: **Pipeline** $ightarrow$ Bấm **OK**.
4. Trong mục **Build Triggers**: Tích chọn **GitHub hook trigger for GITScm polling** và **Poll SCM**.
5. Trong mục **Pipeline**:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: `https://github.com/xuanlepham/dafl-automation-pom.git`
   - Branch Specifier: `*/main`
   - Script Path:
     - Nhập `Jenkinsfile.auto` cho Job Auto Test.
     - Nhập `Jenkinsfile.pt` cho Job Performance Test.
     - Nhập `Jenkinsfile` cho Master Continuous Testing Pipeline.
6. Bấm **Save** $ightarrow$ Pipeline đã sẵn sàng hoạt động tự động 24/7!
