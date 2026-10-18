package vn.dafl.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vn.dafl.commons.BaseTest;
import vn.dafl.pageObjects.*;
import vn.dafl.pageUIs.AuthModalPageUI;
import vn.dafl.pageUIs.CheckoutPageUI;

@Epic("DAFL E-Commerce Platform")
@Feature("Negative & Exception Scenarios Across All Flows")
public class NegativeAndExceptionScenariosTest extends BaseTest {
    private HomePageObject homePage;
    private AuthModalPageObject authModal;
    private ProductCatalogPageObject catalogPage;
    private CartDrawerPageObject cartDrawer;
    private CheckoutPageObject checkoutPage;
    private OrdersPageObject ordersPage;

    @BeforeMethod
    public void initPages() {
        homePage = new HomePageObject();
        authModal = new AuthModalPageObject();
        catalogPage = new ProductCatalogPageObject();
        cartDrawer = new CartDrawerPageObject();
        checkoutPage = new CheckoutPageObject();
        ordersPage = new OrdersPageObject();
        homePage.openHomePage(baseUrl);
    }

    private void ensureProductInCart() {
        catalogPage.addFirstProductToCart();
        cartDrawer = homePage.openCartDrawer();
    }

    // =========================================================================
    // FLOW 1: AUTHENTICATION & REGISTRATION EXCEPTION CASES (7 CASES)
    // =========================================================================

    @Test(description = "TC_EX_01: Login with incorrect password displays error message and preserves modal", groups = {"negative", "auth"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh đăng nhập với mật khẩu sai hiển thị cảnh báo lỗi, không tự đóng modal và không crash ứng dụng")
    public void TC_EX_01_Auth_LoginWrongPassword() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.login("demo@dafl.vn", "SaiMatKhau@9999");
        homePage.sleepInSecond(1);

        Assert.assertTrue(authModal.isErrorMessageDisplayed(), "Không hiển thị thông báo lỗi khi nhập sai mật khẩu");
        String errorMsg = authModal.getErrorMessage();
        Assert.assertTrue(errorMsg != null && !errorMsg.trim().isEmpty(), "Nội dung thông báo lỗi bị rỗng");
        Assert.assertTrue(authModal.isAuthModalDisplayed(), "Modal bị đóng bất thường khi đăng nhập thất bại");
        authModal.closeModal();
    }

    @Test(description = "TC_EX_02: Login with non-existent email displays authentication error", groups = {"negative", "auth"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh đăng nhập tài khoản email chưa tồn tại trong hệ thống hiển thị thông báo lỗi xác thực")
    public void TC_EX_02_Auth_LoginNonExistentEmail() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.login("ghost_user_" + System.currentTimeMillis() + "@dafl.vn", "SomePass123!");
        homePage.sleepInSecond(1);

        Assert.assertTrue(authModal.isErrorMessageDisplayed(), "Không hiển thị thông báo lỗi tài khoản không tồn tại");
        authModal.closeModal();
    }

    @Test(description = "TC_EX_03: Login with empty email triggers HTML5 required validation", groups = {"negative", "auth"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh bỏ trống Email khi đăng nhập kích hoạt validation của trình duyệt, không gửi request rác")
    public void TC_EX_03_Auth_LoginEmptyEmailValidation() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.switchToLoginTab();
        authModal.sendKeyToElement(AuthModalPageUI.LOGIN_PASSWORD_INPUT, "Password123!");
        authModal.clickToElement(AuthModalPageUI.LOGIN_SUBMIT_BTN);

        boolean isValid = authModal.isInputValid(AuthModalPageUI.LOGIN_EMAIL_INPUT);
        Assert.assertFalse(isValid, "Trường Email rỗng nhưng vẫn vượt qua validation form");
        Assert.assertFalse(homePage.isUserLoggedIn(), "User đăng nhập được dù để trống email");
        authModal.closeModal();
    }

    @Test(description = "TC_EX_04: Login with empty password triggers HTML5 required validation", groups = {"negative", "auth"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh bỏ trống Mật khẩu khi đăng nhập kích hoạt validation và form không submit")
    public void TC_EX_04_Auth_LoginEmptyPasswordValidation() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.switchToLoginTab();
        authModal.sendKeyToElement(AuthModalPageUI.LOGIN_EMAIL_INPUT, "demo@dafl.vn");
        authModal.clickToElement(AuthModalPageUI.LOGIN_SUBMIT_BTN);

        boolean isValid = authModal.isInputValid(AuthModalPageUI.LOGIN_PASSWORD_INPUT);
        Assert.assertFalse(isValid, "Trường Password rỗng nhưng form vẫn hợp lệ");
        authModal.closeModal();
    }

    @Test(description = "TC_EX_05: Login with malformed email triggers email format validation", groups = {"negative", "auth"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh nhập email sai định dạng (thiếu @ hoặc domain) bị chặn bởi email validation")
    public void TC_EX_05_Auth_LoginInvalidEmailFormat() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.switchToLoginTab();
        authModal.sendKeyToElement(AuthModalPageUI.LOGIN_EMAIL_INPUT, "invalid-email-without-at-sign");
        authModal.sendKeyToElement(AuthModalPageUI.LOGIN_PASSWORD_INPUT, "Password123!");
        authModal.clickToElement(AuthModalPageUI.LOGIN_SUBMIT_BTN);

        boolean isValid = authModal.isInputValid(AuthModalPageUI.LOGIN_EMAIL_INPUT);
        Assert.assertFalse(isValid, "Email sai định dạng không bị chặn validation");
        authModal.closeModal();
    }

    @Test(description = "TC_EX_06: Register with mismatching password and confirm password", groups = {"negative", "auth"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh đăng ký khi mật khẩu xác nhận không khớp hiển thị thông báo lỗi rõ ràng")
    public void TC_EX_06_Auth_RegisterPasswordMismatch() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.register("Test User", "test_mismatch@dafl.vn", "Password123!", "DifferentPassword999!", "0987654321", "Ha Noi");
        homePage.sleepInSecond(1);

        Assert.assertTrue(authModal.isErrorMessageDisplayed(), "Không hiển thị thông báo lỗi khi 2 mật khẩu không khớp");
        String errorMsg = authModal.getErrorMessage();
        Assert.assertTrue(errorMsg.toLowerCase().contains("khớp") || errorMsg.toLowerCase().contains("mật khẩu"),
                "Thông báo lỗi không nhắc đến mật khẩu không khớp: " + errorMsg);
        authModal.closeModal();
    }

    @Test(description = "TC_EX_07: Register with already registered email triggers duplicate error", groups = {"negative", "auth"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh đăng ký tài khoản với email đã tồn tại (demo@dafl.vn) bị hệ thống từ chối")
    public void TC_EX_07_Auth_RegisterDuplicateEmail() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.register("Demo Existing", "demo@dafl.vn", "Password123!", "Password123!", "0987654321", "Ha Noi");
        homePage.sleepInSecond(1);

        Assert.assertTrue(authModal.isErrorMessageDisplayed(), "Không hiển thị lỗi khi đăng ký trùng email đã có");
        authModal.closeModal();
    }

    // =========================================================================
    // FLOW 2: SEARCH, FILTER & CATALOG EXCEPTION CASES (4 CASES)
    // =========================================================================

    @Test(description = "TC_EX_08: Search with non-existent junk keyword shows 0 products safely", groups = {"negative", "search"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh tìm kiếm từ khóa rác không làm trắng màn hình, hiển thị trạng thái 0 sản phẩm")
    public void TC_EX_08_Search_JunkKeywordNoResults() {
        homePage.enterSearchKeyword("XYZ_KHONG_TON_TAI_99999");
        homePage.sleepInSecond(1);

        int count = catalogPage.getDisplayedProductCount();
        Assert.assertEquals(count, 0, "Tìm kiếm từ khóa rác nhưng vẫn trả về kết quả sản phẩm");
        homePage.clearSearchKeyword();
    }

    @Test(description = "TC_EX_09: Search with SQL injection and XSS payload is sanitized safely", groups = {"negative", "search"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh hệ thống xử lý an toàn chuỗi XSS / SQL injection payload, không gây crash hoặc popup lỗi")
    public void TC_EX_09_Search_SqlXssInjectionPayload() {
        homePage.enterSearchKeyword("<script>alert('xss')</script>' OR '1'='1");
        homePage.sleepInSecond(1);

        Assert.assertTrue(homePage.isBrandLogoDisplayed(), "Giao diện bị phá vỡ sau khi inject payload");
        homePage.clearSearchKeyword();
        homePage.sleepInSecond(1);
    }

    @Test(description = "TC_EX_10: Search with whitespace-only input does not crash the catalog", groups = {"negative", "search"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh nhập toàn khoảng trắng trong ô tìm kiếm không gây exception runtime")
    public void TC_EX_10_Search_WhitespaceOnlyHandling() {
        homePage.enterSearchKeyword("     ");
        homePage.sleepInSecond(1);

        Assert.assertTrue(homePage.isBrandLogoDisplayed());
        homePage.clearSearchKeyword();
    }

    @Test(description = "TC_EX_11: Clearing search after empty results smoothly restores all products", groups = {"negative", "search"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh bấm nút xóa tìm kiếm sau khi nhận 0 kết quả sẽ phục hồi lại danh sách sản phẩm đầy đủ")
    public void TC_EX_11_Search_RecoveryAfterEmptyResult() {
        homePage.enterSearchKeyword("ABCXYZ_EMPTY_RESULT");
        homePage.sleepInSecond(1);
        Assert.assertEquals(catalogPage.getDisplayedProductCount(), 0);

        homePage.clearSearchKeyword();
        homePage.sleepInSecond(1);
        Assert.assertTrue(catalogPage.getDisplayedProductCount() > 0, "Danh mục không phục hồi sau khi xóa từ khóa");
    }

    // =========================================================================
    // FLOW 3: CART & VOUCHER EXCEPTION CASES (5 CASES)
    // =========================================================================

    @Test(description = "TC_EX_12: Opening cart drawer when empty displays empty state without checkout form", groups = {"negative", "cart"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh giỏ hàng rỗng hiển thị thông báo giỏ trống và không hiển thị form checkout rác")
    public void TC_EX_12_Cart_EmptyCartStateVerification() {
        cartDrawer = homePage.openCartDrawer();
        Assert.assertTrue(cartDrawer.isCartDrawerDisplayed(), "Cart Drawer không mở");
        Assert.assertTrue(cartDrawer.isCartEmpty(), "Giỏ hàng không ở trạng thái rỗng");
        Assert.assertFalse(checkoutPage.isCheckoutFormDisplayed(), "Checkout Form vẫn hiển thị khi giỏ hàng rỗng");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_EX_13: Applying invalid non-existent voucher displays error and keeps total unchanged", groups = {"negative", "cart"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh áp dụng voucher không tồn tại hiển thị thông báo lỗi và không trừ tiền giỏ hàng")
    public void TC_EX_13_Cart_ApplyNonExistentVoucher() {
        ensureProductInCart();

        cartDrawer.applyVoucher("SAI_VOUCHER_CODE_99999");
        homePage.sleepInSecond(1);

        Assert.assertTrue(cartDrawer.isVoucherErrorDisplayed(), "Không hiển thị thông báo lỗi voucher không hợp lệ");
        String errorMsg = cartDrawer.getVoucherErrorMessage();
        Assert.assertTrue(errorMsg.contains("không hợp lệ") || errorMsg.contains("Ma"),
                "Nội dung lỗi voucher không chính xác: " + errorMsg);
        Assert.assertFalse(cartDrawer.isVoucherApplied(), "Voucher sai nhưng lại được áp dụng thành công");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_EX_14: Applying empty voucher code does not trigger discount", groups = {"negative", "cart"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh bấm áp dụng voucher khi ô mã để trống không gây áp dụng giảm giá ảo")
    public void TC_EX_14_Cart_ApplyEmptyVoucherCode() {
        ensureProductInCart();
        cartDrawer.applyVoucher("");
        homePage.sleepInSecond(1);

        Assert.assertFalse(cartDrawer.isVoucherApplied(), "Voucher rỗng nhưng lại được áp dụng");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_EX_15: Removing applied voucher cleanly resets discount to 0", groups = {"negative", "cart"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh áp dụng voucher hợp lệ rồi bấm Bỏ chọn sẽ xóa voucher và khôi phục giá gốc")
    public void TC_EX_15_Cart_RemoveVoucherRestoresPrice() {
        ensureProductInCart();
        cartDrawer.applyVoucher("DAFL10");
        homePage.sleepInSecond(1);
        Assert.assertTrue(cartDrawer.isVoucherApplied(), "Voucher DAFL10 chưa được áp dụng");

        cartDrawer.removeVoucher();
        homePage.sleepInSecond(1);
        Assert.assertFalse(cartDrawer.isVoucherApplied(), "Voucher vẫn còn tồn tại sau khi bấm Bỏ chọn");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_EX_16: Decreasing item quantity to 0 removes product without negative quantity", groups = {"negative", "cart"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh giảm số lượng sản phẩm về 0 sẽ tự động xóa sản phẩm khỏi giỏ, không bị số âm (-1)")
    public void TC_EX_16_Cart_DecreaseQuantityToZeroRemovesItem() {
        ensureProductInCart();
        cartDrawer.clickToElement("css=[data-testid^='cart-item-minus-']");
        homePage.sleepInSecond(1);

        Assert.assertTrue(cartDrawer.isCartEmpty(), "Sản phẩm không được xóa khỏi giỏ khi số lượng về 0");
        cartDrawer.closeCartDrawer();
    }

    // =========================================================================
    // FLOW 4: CHECKOUT FORM & VALIDATION EXCEPTION CASES (4 CASES)
    // =========================================================================

    @Test(description = "TC_EX_17: Checkout with empty customer name is blocked by validation", groups = {"negative", "checkout"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh bỏ trống Họ tên người nhận bị validation chặn, không thể tạo đơn hàng sang PayOS")
    public void TC_EX_17_Checkout_EmptyCustomerNameValidation() {
        ensureProductInCart();

        checkoutPage.fillCheckoutForm("", "demo@dafl.vn", "0987654321", "Số 1 Đại Cồ Việt, Hà Nội");
        checkoutPage.submitCheckout();
        homePage.sleepInSecond(1);

        boolean isNameValid = checkoutPage.isInputValid(CheckoutPageUI.CHECKOUT_NAME_INPUT);
        Assert.assertFalse(isNameValid, "Trường Họ tên rỗng nhưng vẫn hợp lệ");
        Assert.assertFalse(checkoutPage.isPaymentViewDisplayed(), "Đã chuyển sang PayOS dù Họ tên bị rỗng");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_EX_18: Checkout with malformed email is blocked by email validation", groups = {"negative", "checkout"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh nhập email người nhận sai cú pháp bị validation chặn không cho thanh toán")
    public void TC_EX_18_Checkout_InvalidEmailFormatValidation() {
        ensureProductInCart();

        checkoutPage.fillCheckoutForm("Nguyễn Văn Test", "email_sai_dinh_dang", "0987654321", "Hà Nội");
        checkoutPage.submitCheckout();
        homePage.sleepInSecond(1);

        boolean isEmailValid = checkoutPage.isInputValid(CheckoutPageUI.CHECKOUT_EMAIL_INPUT);
        Assert.assertFalse(isEmailValid, "Email không hợp lệ nhưng vẫn vượt qua validation");
        Assert.assertFalse(checkoutPage.isPaymentViewDisplayed(), "Đã tạo đơn PayOS với email sai định dạng");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_EX_19: Checkout with empty shipping address is blocked by required validation", groups = {"negative", "checkout"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh bỏ trống Địa chỉ giao hàng bị chặn tại checkout, không thể tạo đơn hàng không có địa chỉ")
    public void TC_EX_19_Checkout_EmptyShippingAddressValidation() {
        ensureProductInCart();

        checkoutPage.fillCheckoutForm("Nguyễn Văn Test", "demo@dafl.vn", "0987654321", "");
        checkoutPage.submitCheckout();
        homePage.sleepInSecond(1);

        boolean isAddressValid = checkoutPage.isInputValid(CheckoutPageUI.CHECKOUT_ADDRESS_INPUT);
        Assert.assertFalse(isAddressValid, "Địa chỉ rỗng nhưng form vẫn cho submit");
        Assert.assertFalse(checkoutPage.isPaymentViewDisplayed(), "Đã chuyển sang cổng PayOS dù thiếu địa chỉ giao hàng");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_EX_20: Checkout with empty phone number is blocked by validation", groups = {"negative", "checkout"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh bỏ trống Số điện thoại người nhận bị validation chặn")
    public void TC_EX_20_Checkout_EmptyPhoneValidation() {
        ensureProductInCart();

        checkoutPage.fillCheckoutForm("Nguyễn Văn Test", "demo@dafl.vn", "", "Hà Nội");
        checkoutPage.submitCheckout();
        homePage.sleepInSecond(1);

        boolean isPhoneValid = checkoutPage.isInputValid(CheckoutPageUI.CHECKOUT_PHONE_INPUT);
        Assert.assertFalse(isPhoneValid, "Số điện thoại rỗng nhưng form vẫn submit được");
        Assert.assertFalse(checkoutPage.isPaymentViewDisplayed());
        cartDrawer.closeCartDrawer();
    }

    // =========================================================================
    // FLOW 5: ORDERS HISTORY EXCEPTION FILTER (1 BONUS CASE)
    // =========================================================================

    @Test(description = "TC_EX_21: Filtering orders by empty status (Cancelled) renders clean state without crash", groups = {"negative", "orders"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh lọc đơn hàng theo trạng thái không có đơn (Đã hủy) hiển thị giao diện mượt mà không lỗi")
    public void TC_EX_21_Order_FilterCancelledOrdersState() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.clickDemoAccountButton();
        homePage.sleepInSecond(1);

        ordersPage = homePage.navigateToOrdersPage();
        Assert.assertTrue(ordersPage.isOrdersPageDisplayed(), "Trang Orders không mở");

        ordersPage.clickToElement("xpath=//div[contains(@class,'order-filter-tabs')]//button[contains(.,'hủy') or contains(.,'Cancelled')]");
        homePage.sleepInSecond(1);

        Assert.assertTrue(ordersPage.isOrdersPageDisplayed(), "Giao diện Orders bị lỗi sau khi lọc trạng thái trống");
        ordersPage.clickBackToHome();
    }
}