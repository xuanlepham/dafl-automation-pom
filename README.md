# DAFL Banking & FinTech Continuous Testing Framework

Hệ thống kiểm thử tự động toàn diện (**Automation Functional Testing**) và kiểm thử tải hiệu năng (**Performance Testing**) tích hợp vào **CI/CD Jenkins Enterprise Pipeline** với 2 cổng **Quality Gate tự động** cho nền tảng thương mại điện tử và thanh toán trực tuyến **DAFL**, thiết kế theo tiêu chuẩn công nghệ ngân hàng (**Banking & FinTech Standard**).

---

## 1. MÔ HÌNH KIẾN TRÚC CHUẨN NGÂN HÀNG (BANKING ARCHITECTURE)

Trong ngành Ngân hàng & FinTech (tuân thủ **PCI-DSS**, **ISO 27001**, và nguyên tắc **Segregation of Duties - SoD**), quy trình CI/CD được tổ chức phân tách rõ ràng giữa **Mã nguồn ứng dụng (Dev)** và **Mã nguồn kiểm thử chất lượng (QA/QC)**:

```text
  [ DEV REPOSITORY ]                               [ QA/QC TEST REPOSITORY ] (Repo này)
  (Mã nguồn App DAFL)                              (https://github.com/xuanlepham/dafl-automation-pom.git)
  - Backend API (.NET 8 Clean Architecture)        - Selenium POM (UI & E2E Customer Journey)
  - React Frontend (Vite)                          - 21 Kịch bản Ngoại lệ & Bắt lỗi Form (Passed 100%)
  - Docker Compose & Configs                       - JMeter Scripts (.jmx) & Script Quality Gate
           │                                                │
           │ (Push Code mới / PR Merge)                     │ (QA cập nhật kịch bản test)
           ▼                                                ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          JENKINS ENTERPRISE CI/CD                               │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  [PIPELINE 1: AUTO TEST] (Jenkinsfile.auto - Functional Quality Gate #1)        │
│  - Chạy trên mỗi Commit hoặc chạy Nightly (02:00 sáng).                         │
│  - Runner: markhobson/maven-chrome:jdk-17 (Headless).                           │
│  - Cổng kiểm soát: 0-Failures Policy. Bất kỳ lỗi nào => ABORT PIPELINE NGAY!    │
│  - Báo cáo trực quan: Allure Test Report & JUnit Reports.                       │
│                                                                                 │
│  [PIPELINE 2: PERFORMANCE TEST] (Jenkinsfile.pt - Performance Quality Gate #2)  │
│  - Chạy định kỳ Nightly (03:00 sáng) khi hệ thống ít tải nhất, hoặc On-Demand.   │
│  - Runner: justb4/jmeter:5.5 Non-GUI (RAM: 768MB, Heap: 512MB).                 │
│  - Cổng kiểm soát SLA: Error Rate <= 2.0% và Average Response Time <= 1000ms.   │
│  - Báo cáo trực quan: JMeter HTML Dashboard Report & result.jtl.                │
│                                                                                 │
│  [MASTER PIPELINE] (Jenkinsfile - End-to-End Continuous Delivery)               │
│  - Tích hợp liên hoàn cả 2 Quality Gate trước khi Deploy Production.            │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. CẤU TRÚC THƯ MỤC CHUẨN HOÁ

Toàn bộ kịch bản kiểm thử được tổ chức tinh gọn, đối xứng và đúng chuẩn công nghiệp:

```text
dafl-automation-pom/
├── Jenkinsfile.auto             # Pipeline chuyên biệt Auto Functional Testing (Selenium + Quality Gate #1)
├── Jenkinsfile.pt               # Pipeline chuyên biệt Performance Testing (JMeter + Quality Gate #2)
├── Jenkinsfile                  # Master Pipeline tích hợp liên hoàn 2 Quality Gate
├── pom.xml                      # Cấu hình Maven: Java 17, Selenium 4.18.1, TestNG 7.9.0, Allure 2.25.0
├── testng-flow.xml              # Kịch bản E2E luồng mua hàng hoàn chỉnh (8 bước)
├── testng-negative.xml          # 21 kịch bản ngoại lệ & bắt lỗi (100% Passed)
├── testng.xml                   # Suite hồi quy toàn bộ hệ thống (Full Regression)
├── src/                         # Toàn bộ mã nguồn Selenium Page Object Model
│   ├── main/java/vn/dafl/
│   │   ├── commons/             # BasePage, BaseTest, GlobalConstants
│   │   ├── driver/              # DriverManager (ThreadLocal thread-safe)
│   │   ├── pageObjects/         # Nghiệp vụ các màn hình (Auth, Catalog, Cart, Checkout, Orders)
│   │   ├── pageUIs/             # Định danh Selector (data-testid, css, xpath)
│   │   └── reportConfig/        # AllureTestListener (Tự động chụp ảnh khi fail)
│   └── test/java/vn/dafl/tests/
│       ├── EndToEndShoppingFlowTest.java        # Luồng E2E 8 bước
│       ├── NegativeAndExceptionScenariosTest.java # 21 kịch bản ngoại lệ
│       └── ...
└── performance-jmeter/          # Bộ kịch bản và công cụ kiểm thử hiệu năng
    ├── dafl-loadtest.jmx        # Kịch bản JMeter bắn tải Backend API
    ├── check_performance_gate.sh# Script shell tự động đánh giá SLA Quality Gate
    └── check_performance_gate.py# Script phân tích chuyên sâu số liệu tải
```

---

## 3. 3 CƠ CHẾ KÍCH HOẠT (TRIGGERS) TRÊN JENKINS

Cả hai pipeline đều được tích hợp sẵn 3 cơ chế kích hoạt theo chuẩn vận hành ngân hàng:

```groovy
triggers {
    // 1. TỰ ĐỘNG CHẠY KHI CÓ CODE MỚI (Push event / GitHub Webhook)
    githubPush()

    // 2. TỰ ĐỘNG QUÉT KIỂM TRA THAY ĐỔI TRÊN GIT (Poll SCM định kỳ mỗi 5-15 phút)
    pollSCM('H/5 * * * *')

    // 3. TỰ ĐỘNG CHẠY THEO NGÀY GIỜ CỐ ĐỊNH (Nightly Build)
    // Pipeline Auto: Chạy lúc 02:00 sáng từ Thứ Hai đến Thứ Sáu
    cron('0 2 * * 1-5')
    // Pipeline PT  : Chạy lúc 03:00 sáng từ Thứ Hai đến Thứ Sáu (tránh giờ cao điểm)
    // cron('0 3 * * 1-5')
}
```

1. **Tự động chạy khi có code mới (Git Push / Webhook)**: Khi push code lên nhánh `main`, Jenkins tự động phát hiện và kích hoạt pipeline.
2. **Tự động chạy theo ngày giờ cố định (Cron Schedule / Nightly Build)**:
   - **Auto Test**: Tự động chạy vào lúc **02:00 sáng** hàng ngày (`0 2 * * 1-5`).
   - **Performance Test**: Tự động chạy vào lúc **03:00 sáng** hàng ngày (`0 3 * * 1-5`) khi hệ thống không có giao dịch thật.
3. **Tự nhấn nút chạy thủ công (Manual / Build with Parameters)**:
   - Truy cập Jenkins tại `http://localhost:8088`.
   - Chọn Job $ightarrow$ Bấm **"Build with Parameters"** hoặc **"Build Now"**.
   - Jenkins sẽ **tự động pull commit mới nhất từ GitHub về và thực thi**.

---

## 4. HƯỚNG DẪN TẠO JOB TRÊN JENKINS UI

1. Vào `http://localhost:8088` $ightarrow$ Bấm **New Item**.
2. **Tạo Job Auto Test**:
   - Tên: `dafl-auto-pipeline` (kiểu **Pipeline**).
   - Mục **Pipeline**: Chọn **Pipeline script from SCM** $ightarrow$ SCM: **Git** $ightarrow$ URL: `https://github.com/xuanlepham/dafl-automation-pom.git`.
   - Branch: `*/main`.
   - Script Path: **`Jenkinsfile.auto`**.
3. **Tạo Job Performance Test**:
   - Tên: `dafl-pt-pipeline` (kiểu **Pipeline**).
   - Cấu hình tương tự, tại mục Script Path nhập: **`Jenkinsfile.pt`**.
4. Bấm **Save** $ightarrow$ Hệ thống hoàn tất!
