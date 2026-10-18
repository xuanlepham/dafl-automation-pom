package vn.dafl.pageObjects;

import io.qameta.allure.Step;
import vn.dafl.commons.BasePage;
import vn.dafl.pageUIs.CartDrawerPageUI;

public class CartDrawerPageObject extends BasePage {

    @Step("Đợi Cart Drawer hiển thị hoàn tất")
    public void waitForCartDrawerVisible() {
        waitForElementVisible(CartDrawerPageUI.CART_DRAWER);
    }

    @Step("Kiểm tra Cart Drawer có hiển thị không")
    public boolean isCartDrawerDisplayed() {
        return isElementDisplayed(CartDrawerPageUI.CART_DRAWER);
    }

    @Step("Kiểm tra trạng thái Giỏ hàng rỗng")
    public boolean isCartEmpty() {
        return isElementDisplayed(CartDrawerPageUI.CART_EMPTY_STATE);
    }

    @Step("Lấy số lượng hiển thị của sản phẩm ID: {0} trong giỏ")
    public int getItemQuantity(String productId) {
        String qtyText = getElementText(CartDrawerPageUI.CART_ITEM_QTY_BY_ID, productId);
        return Integer.parseInt(qtyText.trim());
    }

    @Step("Tăng số lượng sản phẩm ID: {0}")
    public void increaseQuantity(String productId) {
        clickToElement(CartDrawerPageUI.CART_ITEM_PLUS_BY_ID, productId);
        sleepInSecond(1);
    }

    @Step("Giảm số lượng sản phẩm ID: {0}")
    public void decreaseQuantity(String productId) {
        clickToElement(CartDrawerPageUI.CART_ITEM_MINUS_BY_ID, productId);
        sleepInSecond(1);
    }

    @Step("Áp dụng mã Voucher: {0}")
    public void applyVoucher(String voucherCode) {
        sendKeyToElement(CartDrawerPageUI.VOUCHER_CODE_INPUT, voucherCode);
        clickToElement(CartDrawerPageUI.VOUCHER_APPLY_BTN);
        sleepInSecond(1);
    }

    @Step("Bỏ chọn Voucher đã áp dụng")
    public void removeVoucher() {
        clickToElement(CartDrawerPageUI.REMOVE_VOUCHER_BTN);
        sleepInSecond(1);
    }

    @Step("Kiểm tra Banner Voucher đã áp dụng thành công")
    public boolean isVoucherApplied() {
        return isElementDisplayed(CartDrawerPageUI.APPLIED_VOUCHER_BANNER);
    }

    @Step("Lấy thông báo lỗi khi áp dụng Voucher không hợp lệ")
    public String getVoucherErrorMessage() {
        waitForElementVisible(CartDrawerPageUI.VOUCHER_ERROR_MSG);
        return getElementText(CartDrawerPageUI.VOUCHER_ERROR_MSG);
    }

    @Step("Lấy tổng tiền thanh toán cuối cùng (Final Total)")
    public String getFinalTotalText() {
        waitForElementVisible(CartDrawerPageUI.CART_FINAL_TOTAL);
        return getElementText(CartDrawerPageUI.CART_FINAL_TOTAL);
    }

    @Step("Đóng Cart Drawer")
    public void closeCartDrawer() {
        if (isElementDisplayed(CartDrawerPageUI.CART_CLOSE_BTN)) {
            clickToElementByJS(CartDrawerPageUI.CART_CLOSE_BTN);
            waitForElementInvisible(CartDrawerPageUI.CART_DRAWER);
            sleepInSecond(1);
        }
    }
}
