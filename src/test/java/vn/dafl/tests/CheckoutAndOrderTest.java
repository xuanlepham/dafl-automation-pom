package vn.dafl.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vn.dafl.commons.BaseTest;
import vn.dafl.pageObjects.*;

@Epic("DAFL E-Commerce Platform")
@Feature("Checkout & Order Processing (CheckoutForm & PayOS)")
public class CheckoutAndOrderTest extends BaseTest {
    private HomePageObject homePage;
    private ProductCatalogPageObject catalogPage;
    private CartDrawerPageObject cartDrawer;
    private CheckoutPageObject checkoutPage;
    private OrdersPageObject ordersPage;
    private ProfilePageObject profilePage;

    @BeforeMethod
    public void initPages() {
        homePage = new HomePageObject();
        catalogPage = new ProductCatalogPageObject();
        cartDrawer = new CartDrawerPageObject();
        checkoutPage = new CheckoutPageObject();
        ordersPage = new OrdersPageObject();
        profilePage = new ProfilePageObject();
        homePage.openHomePage(baseUrl);
    }

    @Test(description = "TC_ORDER_01: Should render all checkout customer form inputs inside cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Kiểm tra form checkout với các trường Họ tên, Email, SĐT, Địa chỉ hiển thị đầy đủ trong Giỏ hàng")
    public void TC_ORDER_01_RenderCheckoutForm() {
        catalogPage.addFirstProductToCart();
        homePage.openCartDrawer();
        Assert.assertTrue(checkoutPage.isCheckoutFormDisplayed(), "Checkout form không hiển thị");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_ORDER_02: Should submit checkout form and navigate to PayOS Gateway")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Điền thông tin giao hàng hợp lệ và bấm thanh toán; kiểm tra chuyển sang Cổng PayOS với mã đơn hàng")
    public void TC_ORDER_02_SubmitOrderToPayOSGateway() {
        catalogPage.addFirstProductToCart();
        homePage.openCartDrawer();
        checkoutPage.fillCheckoutForm("Nguyễn Văn Demo", "demo@dafl.vn", "0987654321", "Số 1 Đại Cồ Việt, Hà Nội");
        checkoutPage.submitCheckout();

        homePage.sleepInSecond(2);
        Assert.assertTrue(checkoutPage.isPaymentViewDisplayed(), "Màn hình PayOS không hiển thị sau khi submit checkout");
        String orderCode = checkoutPage.getOrderCodeText();
        Assert.assertTrue(orderCode.contains("#"), "Mã đơn hàng không chứa ký tự #");
    }

    @Test(description = "TC_ORDER_03: Navigate to Orders Management Page (#/orders)")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra điều hướng sang trang lịch sử đơn hàng và lọc đơn hàng theo trạng thái")
    public void TC_ORDER_03_NavigateToOrdersPage() {
        AuthModalPageObject authModal = homePage.clickLoginRegisterInDropdown();
        authModal.clickDemoAccountButton();
        homePage.sleepInSecond(2);

        ordersPage = homePage.navigateToOrdersPage();
        Assert.assertTrue(ordersPage.isOrdersPageDisplayed(), "Trang Orders không hiển thị");
        ordersPage.filterPendingOrders();
        ordersPage.clickBackToHome();
    }

    @Test(description = "TC_ORDER_04: Navigate to Profile and Address Configuration (#/profile)")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra điều hướng sang trang Cấu hình địa chỉ nhận hàng và cập nhật thông tin")
    public void TC_ORDER_04_NavigateToProfileAndSaveAddress() {
        AuthModalPageObject authModal = homePage.clickLoginRegisterInDropdown();
        authModal.clickDemoAccountButton();
        homePage.sleepInSecond(2);

        profilePage = homePage.navigateToProfilePage();
        Assert.assertTrue(profilePage.isProfilePageDisplayed(), "Trang Profile không hiển thị");
        profilePage.updateProfile("Nguyễn Văn Demo Updated", "0912345678", "demo@dafl.vn", "123 Đường Cầu Giấy, Hà Nội");
        profilePage.clickBackToHome();
    }
}
