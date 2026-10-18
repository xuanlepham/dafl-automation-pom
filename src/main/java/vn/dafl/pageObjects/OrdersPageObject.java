package vn.dafl.pageObjects;

import io.qameta.allure.Step;
import vn.dafl.commons.BasePage;
import vn.dafl.pageUIs.OrdersPageUI;

public class OrdersPageObject extends BasePage {

    @Step("Kiểm tra Trang quản lý đơn hàng có hiển thị không")
    public boolean isOrdersPageDisplayed() {
        return isElementDisplayedWithWait(OrdersPageUI.ORDERS_PAGE, 5);
    }

    @Step("Bấm nút Tiếp tục mua sắm để quay về Trang chủ")
    public void clickBackToHome() {
        clickToElementByJS(OrdersPageUI.BACK_TO_HOME_BTN);
        sleepInSecond(1);
    }

    @Step("Lọc đơn hàng theo tab: Tất cả")
    public void filterAllOrders() {
        clickToElement(OrdersPageUI.FILTER_BTN_ALL);
        sleepInSecond(1);
    }

    @Step("Lọc đơn hàng theo tab: Chờ thanh toán")
    public void filterPendingOrders() {
        clickToElement(OrdersPageUI.FILTER_BTN_PENDING);
        sleepInSecond(1);
    }
}
