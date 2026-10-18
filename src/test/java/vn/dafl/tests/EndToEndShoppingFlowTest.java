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
        // Kích hoạt chế độ Single Session Flow: 1 trình duyệt duy nhất chạy liên tục từ bước 1 -> 7
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

    @Test(priority = 1, description = "BUOC 1: Kham pha trang chu, doi Theme Sang/Toi va Tim kiem, loc nganh hang")
    @Story("Flow Step 1: Browse, Search and Filter")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xac minh trang chu hien thi, thu nghiem chuyen theme, tim kiem san pham 'LED', reset va loc danh muc")
    public void Step01_ExploreAndSearchProducts() {
        // 1. Kiem tra Brand Logo
        Assert.assertTrue(homePage.isBrandLogoDisplayed(), "Logo DAFL Store khong hien thi");
        homePage.sleepInSecond(1);

        // 2. Thu nghiem doi Theme Sang / Toi
        homePage.toggleTheme();
        homePage.sleepInSecond(1);
        homePage.toggleTheme();
        homePage.sleepInSecond(1);

        // 3. Tim kiem san pham bang tu khoa thuc te trong DB ('LED')
        homePage.enterSearchKeyword("LED");
        homePage.sleepInSecond(2);
        Assert.assertTrue(catalogPage.getDisplayedProductCount() >= 1, "Khong tim thay san pham voi tu khoa 'LED'");

        // 4. Xoa tu khoa tim kiem de quay ve toan bo danh muc
        homePage.clearSearchKeyword();
        homePage.sleepInSecond(1);

        // 5. Thu loc theo danh muc nganh hang
        catalogPage.selectFirstCategory();
        homePage.sleepInSecond(2);
        Assert.assertTrue(catalogPage.getDisplayedProductCount() >= 1);

        // 6. Reset ve tat ca danh muc
        catalogPage.selectAllCategory();
        homePage.sleepInSecond(2);
    }

    @Test(priority = 2, description = "BUOC 2: Mo xem chi tiet san pham, xem Danh gia va Them vao gio hang")
    @Story("Flow Step 2: Product Detail and Add To Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Mo modal chi tiet san pham dau tien, duyet tab Danh gia nguoi dung va bam Them vao gio")
    public void Step02_ViewDetailAndAddToCart() {
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

        // 5. Kiem tra gio hang mo ra
        Assert.assertTrue(cartDrawer.isCartDrawerDisplayed(), "Gio hang khong tu dong mo khi them san pham");
    }

    @Test(priority = 3, description = "BUOC 3: Quan ly gio hang va Ap dung ma giam gia Voucher")
    @Story("Flow Step 3: Cart Management and Voucher")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ap dung ma Voucher DAFL10 trong gio hang va kiem tra tinh toan tong tien thanh toan")
    public void Step03_ManageCartAndVoucher() {
        // 1. Ap dung ma Voucher DAFL10
        cartDrawer.applyVoucher("DAFL10");
        homePage.sleepInSecond(2);
        Assert.assertTrue(cartDrawer.isVoucherApplied(), "Voucher DAFL10 khong duoc ap dung");

        // 2. Kiem tra tong tien da hien thi
        String totalText = cartDrawer.getFinalTotalText();
        Assert.assertFalse(totalText.isEmpty(), "Tong tien gio hang bi trong");
        homePage.sleepInSecond(2);
    }

    @Test(priority = 4, description = "BUOC 4: Dien thong tin giao hang va Tao don hang chuyen sang Cong PayOS")
    @Story("Flow Step 4: Checkout and PayOS Gateway")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Dien form nhan hang, submit don hang PayOS, kiem tra ma don hang va gia lap thanh toan thanh cong")
    public void Step04_CheckoutAndGeneratePayOSOrder() {
        // 1. Dien form nhan hang
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

        // 5. Dong gio hang de tro ve trang chu
        if (checkoutPage.isElementDisplayed("css=[data-testid='continue-shopping-btn']")) {
            checkoutPage.clickToElementByJS("css=[data-testid='continue-shopping-btn']");
        } else {
            cartDrawer.closeCartDrawer();
        }
        homePage.sleepInSecond(2);
    }

    @Test(priority = 5, description = "BUOC 5: Dang nhap tai khoan va Kiem tra Don hang trong Lich su (#/orders)")
    @Story("Flow Step 5: Customer Login and Orders Tracking")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Dang nhap tai khoan demo@dafl.vn, vao trang Quan ly don hang, loc don va quay lai trang chu")
    public void Step05_LoginAndTrackOrders() {
        // 1. Dang nhap voi tai khoan demo@dafl.vn
        authModal = homePage.clickLoginRegisterInDropdown();
        homePage.sleepInSecond(1);
        authModal.clickDemoAccountButton();
        homePage.sleepInSecond(2);
        Assert.assertTrue(homePage.isUserLoggedIn(), "Chua dang nhap thanh cong");

        // 2. Dieu huong sang Trang Lich su Don hang
        ordersPage = homePage.navigateToOrdersPage();
        homePage.sleepInSecond(2);
        Assert.assertTrue(ordersPage.isOrdersPageDisplayed(), "Trang Quan ly don hang khong hien thi");

        // 3. Thu nghiem loc cac trang thai don hang
        ordersPage.filterPendingOrders();
        homePage.sleepInSecond(2);
        ordersPage.filterAllOrders();
        homePage.sleepInSecond(2);

        // 4. Bam quay lai Trang chu
        ordersPage.clickBackToHome();
        homePage.sleepInSecond(2);
    }

    @Test(priority = 6, description = "BUOC 6: Cau hinh so dia chi nhan hang ca nhan (#/profile)")
    @Story("Flow Step 6: Profile and Delivery Address Configuration")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Vao trang Cau hinh Dia chi, cap nhat dia chi giao hang mac dinh moi va luu thong tin")
    public void Step06_UpdateProfileAddress() {
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

    @Test(priority = 7, description = "BUOC 7: Dang xuat tai khoan an toan va Hoan tat toan bo chu trinh Flow")
    @Story("Flow Step 7: Logout and Cleanup Session")
    @Severity(SeverityLevel.NORMAL)
    @Description("Thuc hien dang xuat tai khoan va xac minh giao dien tro ve trang thai khach vang lai")
    public void Step07_LogoutAndFinish() {
        // 1. Dang xuat tai khoan
        homePage.clickLogoutInDropdown();
        homePage.sleepInSecond(2);
        Assert.assertFalse(homePage.isUserLoggedIn(), "Van con luu phien dang nhap sau logout");
    }
}
