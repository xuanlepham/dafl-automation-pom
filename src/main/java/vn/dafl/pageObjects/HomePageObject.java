package vn.dafl.pageObjects;

import io.qameta.allure.Step;
import vn.dafl.commons.BasePage;
import vn.dafl.pageUIs.CartDrawerPageUI;
import vn.dafl.pageUIs.HomePageUI;

public class HomePageObject extends BasePage {

    @Step("Mở trang chủ DAFL Store với URL: {0}")
    public void openHomePage(String url) {
        openPageUrl(url);
        waitForElementVisible(HomePageUI.BRAND_LOGO);
    }

    @Step("Kiểm tra hiển thị Brand Logo DAFL Store")
    public boolean isBrandLogoDisplayed() {
        return isElementDisplayed(HomePageUI.BRAND_LOGO);
    }

    @Step("Nhập từ khóa tìm kiếm: {0}")
    public void enterSearchKeyword(String keyword) {
        sendKeyToElement(HomePageUI.SEARCH_INPUT, keyword);
        sleepInSecond(1);
    }

    @Step("Xóa từ khóa tìm kiếm bằng nút (X)")
    public void clearSearchKeyword() {
        if (isElementDisplayed(HomePageUI.SEARCH_CLEAR_BTN)) {
            clickToElementByJS(HomePageUI.SEARCH_CLEAR_BTN);
        } else {
            clearElementText(HomePageUI.SEARCH_INPUT);
        }
        sleepInSecond(1);
    }

    @Step("Kiểm tra nút Xóa tìm kiếm có hiển thị hay không")
    public boolean isSearchClearButtonDisplayed() {
        return isElementDisplayed(HomePageUI.SEARCH_CLEAR_BTN);
    }

    @Step("Bấm nút chuyển đổi Theme Sáng / Tối")
    public void toggleTheme() {
        clickToElementByJS(HomePageUI.THEME_TOGGLE_BTN);
    }

    @Step("Mở Account Dropdown Menu")
    public void openAccountDropdown() {
        waitForElementClickable(HomePageUI.ACCOUNT_TOGGLE_BTN);
        clickToElementByJS(HomePageUI.ACCOUNT_TOGGLE_BTN);
        waitForElementVisible(HomePageUI.ACCOUNT_DROPDOWN_MENU);
    }

    @Step("Bấm nút Đăng nhập / Đăng ký trong Menu Tài khoản")
    public AuthModalPageObject clickLoginRegisterInDropdown() {
        openAccountDropdown();
        waitForElementClickable(HomePageUI.DROPDOWN_LOGIN_BTN);
        clickToElementByJS(HomePageUI.DROPDOWN_LOGIN_BTN);
        AuthModalPageObject modal = new AuthModalPageObject();
        modal.waitForAuthModalVisible();
        return modal;
    }

    @Step("Bấm nút Đăng xuất tài khoản")
    public void clickLogoutInDropdown() {
        openAccountDropdown();
        waitForElementClickable(HomePageUI.DROPDOWN_LOGOUT_BTN);
        clickToElementByJS(HomePageUI.DROPDOWN_LOGOUT_BTN);
        sleepInSecond(1);
    }

    @Step("Điều hướng sang Trang Đơn hàng của tôi (#/orders)")
    public OrdersPageObject navigateToOrdersPage() {
        openAccountDropdown();
        waitForElementClickable(HomePageUI.DROPDOWN_ORDERS_BTN);
        clickToElementByJS(HomePageUI.DROPDOWN_ORDERS_BTN);
        return new OrdersPageObject();
    }

    @Step("Điều hướng sang Trang Thông tin & Địa chỉ (#/profile)")
    public ProfilePageObject navigateToProfilePage() {
        openAccountDropdown();
        waitForElementClickable(HomePageUI.DROPDOWN_PROFILE_BTN);
        clickToElementByJS(HomePageUI.DROPDOWN_PROFILE_BTN);
        return new ProfilePageObject();
    }

    @Step("Kiểm tra trạng thái đã đăng nhập hay chưa")
    public boolean isUserLoggedIn() {
        String accountText = getElementText(HomePageUI.ACCOUNT_TOGGLE_BTN);
        return !accountText.equalsIgnoreCase("Tài khoản") && !accountText.contains("Tài khoản");
    }

    @Step("Mở Giỏ hàng (Cart Drawer)")
    public CartDrawerPageObject openCartDrawer() {
        CartDrawerPageObject cartDrawer = new CartDrawerPageObject();
        if (!cartDrawer.isCartDrawerDisplayed()) {
            clickToElementByJS(HomePageUI.CART_BTN);
            cartDrawer.waitForCartDrawerVisible();
        }
        return cartDrawer;
    }

    @Step("Lấy số lượng sản phẩm trên Badge Giỏ hàng")
    public int getCartBadgeCount() {
        if (isElementDisplayed(HomePageUI.CART_BADGE_COUNT)) {
            try {
                return Integer.parseInt(getElementText(HomePageUI.CART_BADGE_COUNT));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
