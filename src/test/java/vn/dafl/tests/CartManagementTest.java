package vn.dafl.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vn.dafl.commons.BaseTest;
import vn.dafl.pageObjects.CartDrawerPageObject;
import vn.dafl.pageObjects.HomePageObject;
import vn.dafl.pageObjects.ProductCatalogPageObject;

@Epic("DAFL E-Commerce Platform")
@Feature("Cart & Voucher Module (CartDrawer)")
public class CartManagementTest extends BaseTest {
    private HomePageObject homePage;
    private ProductCatalogPageObject catalogPage;
    private CartDrawerPageObject cartDrawer;

    @BeforeMethod
    public void initPages() {
        homePage = new HomePageObject();
        catalogPage = new ProductCatalogPageObject();
        cartDrawer = new CartDrawerPageObject();
        homePage.openHomePage(baseUrl);
    }

    @Test(description = "TC_CART_01: Should display empty cart view when cart is empty")
    @Severity(SeverityLevel.NORMAL)
    @Description("Mở giỏ hàng khi chưa mua gì, xác nhận hiển thị thông báo giỏ rỗng")
    public void TC_CART_01_DisplayEmptyCartView() {
        cartDrawer = homePage.openCartDrawer();
        Assert.assertTrue(cartDrawer.isCartDrawerDisplayed(), "Cart Drawer không hiển thị");
        Assert.assertTrue(cartDrawer.isCartEmpty(), "Giỏ hàng không ở trạng thái rỗng");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_CART_02: Should render cart items and update badge count when adding product")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Thêm sản phẩm từ trang chủ và xác nhận Badge giỏ hàng trên Header tăng lên")
    public void TC_CART_02_AddProductAndVerifyBadge() {
        catalogPage.addFirstProductToCart();
        int badgeCount = homePage.getCartBadgeCount();
        Assert.assertTrue(badgeCount >= 1, "Badge giỏ hàng không cập nhật");
    }

    @Test(description = "TC_CART_03: Should trigger onApplyVoucher when typing code and clicking apply")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Áp dụng mã voucher hợp lệ DAFL10 hoặc FORDEV")
    public void TC_CART_03_ApplyValidVoucher() {
        catalogPage.addFirstProductToCart();
        cartDrawer = homePage.openCartDrawer();
        cartDrawer.applyVoucher("DAFL10");
        homePage.sleepInSecond(1);
        Assert.assertTrue(cartDrawer.isVoucherApplied(), "Voucher banner không hiển thị sau khi áp dụng DAFL10");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_CART_04: Should display voucher error message when invalid voucher is entered (Negative)")
    @Severity(SeverityLevel.NORMAL)
    @Description("Nhập mã voucher không tồn tại và kiểm tra thông báo lỗi")
    public void TC_CART_04_InvalidVoucherErrorMessage() {
        catalogPage.addFirstProductToCart();
        cartDrawer = homePage.openCartDrawer();
        cartDrawer.applyVoucher("VOUCHER_SAI_12345");
        homePage.sleepInSecond(1);
        String errorMsg = cartDrawer.getVoucherErrorMessage();
        Assert.assertTrue(errorMsg != null && !errorMsg.isEmpty(), "Không hiển thị thông báo lỗi voucher");
        cartDrawer.closeCartDrawer();
    }

    @Test(description = "TC_CART_05: Should allow removing applied voucher and recalculate total")
    @Severity(SeverityLevel.NORMAL)
    @Description("Áp dụng voucher rồi bấm Bỏ chọn để khôi phục tổng tiền gốc")
    public void TC_CART_05_RemoveVoucher() {
        catalogPage.addFirstProductToCart();
        cartDrawer = homePage.openCartDrawer();
        cartDrawer.applyVoucher("DAFL10");
        homePage.sleepInSecond(1);

        cartDrawer.removeVoucher();
        homePage.sleepInSecond(1);
        Assert.assertFalse(cartDrawer.isVoucherApplied(), "Voucher vẫn còn áp dụng sau khi bấm Bỏ chọn");
        cartDrawer.closeCartDrawer();
    }
}
