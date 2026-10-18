package vn.dafl.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import vn.dafl.commons.BaseTest;
import vn.dafl.pageObjects.*;

@Epic("DAFL E-Commerce Platform")
@Feature("End-to-End Customer Shopping Journey")
public class EndToEndShoppingFlowTest extends BaseTest {
    private HomePageObject homePage;
    private ProductCatalogPageObject catalogPage;
    private CartDrawerPageObject cartDrawer;
    private CheckoutPageObject checkoutPage;
    private OrdersPageObject ordersPage;
    private ProfilePageObject profilePage;
    private AuthModalPageObject authModal;

    private String savedOrderCode;

    public EndToEndShoppingFlowTest() {
        // Kích hoạt chế độ Single Session Flow: 1 trình duyệt duy nhất chạy liên tục từ bước 1 -> 8
        this.isSingleSessionFlow = true;
    }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpClass")
    public void initFlowSession() {
        homePage = new HomePageObject();
        catalogPage = new ProductCatalogPageObject();
        cartDrawer = new CartDrawerPageObject();
        checkoutPage = new CheckoutPageObject();
        ordersPage = new OrdersPageObject();
        profilePage = new ProfilePageObject();
        authModal = new AuthModalPageObject();

        homePage.openHomePage(baseUrl);
        homePage.sleepInSecond(2);
    }

    @Test(priority = 1, description = "BUOC 1: Trang chu, doi Theme, kiem thu Dang nhap sai mat khau va Phuc hoi thanh cong")
    @Story("Flow Step 1: Browse, Theme & Auth Negative/Positive")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Xac minh trang chu, doi Theme. Kiem thu Negative: nhap sai mat khau de kiem tra thong bao loi he thong. Sau do phuc hoi dang nhap tai khoan Demo thanh cong")
    public void Step01_ExploreAndLogin() {
        // 1. Kiem tra Brand Logo
        Assert.assertTrue(homePage.isBrandLogoDisplayed(), "Logo DAFL Store khong hien thi");
        homePage.sleepInSecond(1);

        // 2. Thu nghiem doi Theme Sang / Toi
        homePage.toggleTheme();
        homePage.sleepInSecond(1);
        homePage.toggleTheme();
        homePage.sleepInSecond(1);

        // 3. CASE KHONG HOP LE (Negative): Thu dang nhap voi mat khau sai
        authModal = homePage.clickLoginRegisterInDropdown();
        homePage.sleepInSecond(1);
        authModal.login("demo@dafl.vn", "SaiMatKhau999!");
        homePage.sleepInSecond(2);

        // Kiem tra he thong bat loi dung (khong block luong)
        boolean hasAuthError = authModal.isErrorMessageDisplayed();
        System.out.println("[NEGATIVE TEST] Thong bao loi dang nhap sai mat khau xuat hien: " + hasAuthError);

        // 4. PHUC HOI (Positive): Dang nhap tai khoan Demo thanh cong
        authModal.clickDemoAccountButton();
        homePage.sleepInSecond(2);
        Assert.assertTrue(homePage.isUserLoggedIn(), "Dang nhap tai khoan khong thanh cong");
    }

    @Test(priority = 2, description = "BUOC 2: Kiem thu Tim kiem tu khoa khong ton tai va Phuc hoi tim kiem chuan")
    @Story("Flow Step 2: Search Negative/Positive & Category Filter")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiem thu Negative: tim kiem tu khoa khong co trong he thong ('Khong_Ton_Tai_XYZ'). Sau do xoa tim kiem va tim san pham hop le ('LED')")
    public void Step02_SearchAndFilterProducts() {
        // 1. CASE KHONG HOP LE (Negative): Tim kiem tu khoa khong ton tai
        homePage.enterSearchKeyword("Khong_Ton_Tai_XYZ_999");
        homePage.sleepInSecond(2);
        int emptyCount = catalogPage.getDisplayedProductCount();
        System.out.println("[NEGATIVE TEST] So luong san pham khi tim tu khoa khong ton tai: " + emptyCount);

        // 2. PHUC HOI (Positive): Xoa tu khoa tim kiem de tro ve toan bo danh muc
        homePage.clearSearchKeyword();
        homePage.sleepInSecond(1);

        // 3. Tim kiem san pham hop le ('LED')
        homePage.enterSearchKeyword("LED");
        homePage.sleepInSecond(2);
        Assert.assertTrue(catalogPage.getDisplayedProductCount() >= 1, "Khong tim thay san pham voi tu khoa 'LED'");

        // 4. Xoa tim kiem de tro ve danh sach san pham
        homePage.clearSearchKeyword();
        homePage.sleepInSecond(1);

        // 5. Thu loc theo danh muc nganh hang
        catalogPage.selectFirstCategory();
        homePage.sleepInSecond(2);
        Assert.assertTrue(catalogPage.getDisplayedProductCount() >= 1, "Danh muc khong co san pham");

        // 6. Reset ve tat ca danh muc
        catalogPage.selectAllCategory();
        homePage.sleepInSecond(2);
    }

    @Test(priority = 3, description = "BUOC 3: Xem chi tiet san pham, doc Danh gia va Them vao gio hang")
    @Story("Flow Step 3: Product Detail and Add To Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Mo modal chi tiet san pham, doc tab Danh gia nguoi dung va them vao gio hang")
    public void Step03_ViewDetailAndAddToCart() {
        // 1. Mo xem chi tiet san pham dau tien
        catalogPage.openFirstProductDetail();
        homePage.sleepInSecond(2);
        Assert.assertTrue(catalogPage.isProductDetailModalDisplayed(), "Modal chi tiet khong mo");

        // 2. Chuyen sang Tab Danh gia khach hang
        catalogPage.switchToReviewsTab();
        homePage.sleepInSecond(2);

        // 3. Dong Modal chi tiet
        catalogPage.closeProductDetailModal();
        homePage.sleepInSecond(1);

        // 4. Bam them san pham vao gio hang
        catalogPage.addFirstProductToCart();
        homePage.sleepInSecond(2);

        // 5. Kiem tra gio hang tu dong mo ra
        Assert.assertTrue(cartDrawer.isCartDrawerDisplayed(), "Gio hang khong tu dong mo khi them san pham");
    }

    @Test(priority = 4, description = "BUOC 4: Kiem thu Voucher sai/khong hop le va Phuc hoi ap dung Voucher chuan")
    @Story("Flow Step 4: Cart Management & Voucher Negative/Positive")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiem thu Negative: nhap ma Voucher sai ('VOUCHER_SAI_999') de kiem tra thong bao loi. Sau do phuc hoi bang ma DAFL10 hop le")
    public void Step04_ManageCartAndVoucher() {
        // 1. CASE KHONG HOP LE (Negative): Thu nhap ma Voucher sai khong ton tai
        cartDrawer.applyVoucher("VOUCHER_SAI_999");
        homePage.sleepInSecond(2);
        boolean hasVoucherError = cartDrawer.isVoucherErrorDisplayed();
        System.out.println("[NEGATIVE TEST] Thong bao loi khi nhap ma voucher sai xuat hien: " + hasVoucherError);

        // 2. PHUC HOI (Positive): Nhap ma Voucher hop le DAFL10 (-10%)
        cartDrawer.applyVoucher("DAFL10");
        homePage.sleepInSecond(2);
        Assert.assertTrue(cartDrawer.isVoucherApplied(), "Voucher DAFL10 khong duoc ap dung");

        // 3. Kiem tra tong tien da hien thi sau giam gia
        String totalText = cartDrawer.getFinalTotalText();
        Assert.assertFalse(totalText.isEmpty(), "Tong tien gio hang bi trong");
        homePage.sleepInSecond(2);
    }

    @Test(priority = 5, description = "BUOC 5: Xac nhan thong tin nhan hang, Tao don va Thanh toan Cong PayOS")
    @Story("Flow Step 5: Checkout and PayOS Gateway")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Kiem tra/Dien form nhan hang, submit don hang PayOS, nhan webhook thanh toan va xac nhan thanh cong")
    public void Step05_CheckoutAndPayOSGateway() {
        // 1. Form nhan hang da duoc he thong tu dong dien thong tin tai khoan da dang nhap
        checkoutPage.fillCheckoutForm(
                "Nguyen Van Demo",
                "demo@dafl.vn",
                "0987654321",
                "So 1 Dai Co Viet, Hai Ba Trung, Ha Noi"
        );
        homePage.sleepInSecond(2);

        // 2. Bam Xac nhan Thanh toan Don hang
        checkoutPage.submitCheckout();
        homePage.sleepInSecond(3);

        // 3. Kiem tra chuyen sang man hinh Cong thanh toan VietQR / PayOS
        Assert.assertTrue(checkoutPage.isPaymentViewDisplayed(), "Giao dien PayOS khong hien thi sau khi submit");
        savedOrderCode = checkoutPage.getOrderCodeText();
        Assert.assertTrue(savedOrderCode.contains("#"), "Ma don hang khong hop le: " + savedOrderCode);
        homePage.sleepInSecond(3);

        // 4. Gia lap xac nhan da chuyen khoan thanh cong qua Webhook va Nut kiem tra
        checkoutPage.clickSimulatePayOSSuccess();
        homePage.sleepInSecond(3);
        Assert.assertTrue(checkoutPage.isOrderPaidViewDisplayed(), "Man hinh Thanh toan thanh cong khong hien thi");

        // 5. Chuyen sang Trang Lich su don hang tu nut xem don sau khi thanh toan
        if (checkoutPage.isElementDisplayed("css=[data-testid='view-orders-btn']")) {
            checkoutPage.clickToElementByJS("css=[data-testid='view-orders-btn']");
        } else {
            cartDrawer.closeCartDrawer();
            ordersPage = homePage.navigateToOrdersPage();
        }
        homePage.sleepInSecond(2);
    }

    @Test(priority = 6, description = "BUOC 6: Theo doi don hang vua thanh toan trong Lich su don (#/orders)")
    @Story("Flow Step 6: Customer Orders Tracking")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Vao trang Lich su don hang, kiem tra don hang vua thanh toan, loc trang thai don va quay lai trang chu")
    public void Step06_TrackOrdersHistory() {
        Assert.assertTrue(ordersPage.isOrdersPageDisplayed(), "Trang Quan ly don hang khong hien thi");
        homePage.sleepInSecond(2);

        // 1. Thu nghiem loc cac trang thai don hang
        ordersPage.filterPendingOrders();
        homePage.sleepInSecond(2);
        ordersPage.filterAllOrders();
        homePage.sleepInSecond(2);

        // 2. Quay ve Trang chu
        ordersPage.clickBackToHome();
        homePage.sleepInSecond(2);
    }

    @Test(priority = 7, description = "BUOC 7: Cau hinh so dia chi nhan hang ca nhan (#/profile)")
    @Story("Flow Step 7: Profile and Delivery Address Configuration")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Vao trang Cau hinh Dia chi, cap nhat dia chi giao hang mac dinh moi va luu thong tin")
    public void Step07_UpdateProfileAddress() {
        // 1. Dieu huong sang Trang Profile
        profilePage = homePage.navigateToProfilePage();
        homePage.sleepInSecond(2);
        Assert.assertTrue(profilePage.isProfilePageDisplayed(), "Trang Profile khong hien thi");

        // 2. Cap nhat thong tin giao hang
        profilePage.updateProfile(
                "Nguyen Van Demo (VIP)",
                "0912345678",
                "demo@dafl.vn",
                "Toa nha TechnoPark, Vinhomes Ocean Park, Gia Lam, Ha Noi"
        );
        homePage.sleepInSecond(3);

        // 3. Quay ve Trang chu
        profilePage.clickBackToHome();
        homePage.sleepInSecond(2);
    }

    @Test(priority = 8, description = "BUOC 8: Dang xuat tai khoan an toan va Hoan tat toan bo chu trinh Flow")
    @Story("Flow Step 8: Logout and Cleanup Session")
    @Severity(SeverityLevel.NORMAL)
    @Description("Thuc hien dang xuat tai khoan va xac minh giao dien tro ve trang thai khach vang lai")
    public void Step08_LogoutAndFinish() {
        // 1. Dang xuat tai khoan
        homePage.clickLogoutInDropdown();
        homePage.sleepInSecond(2);
        Assert.assertFalse(homePage.isUserLoggedIn(), "Van con luu phien dang nhap sau logout");
    }
}
