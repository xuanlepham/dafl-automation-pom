package vn.dafl.pageObjects;

import io.qameta.allure.Step;
import vn.dafl.commons.BasePage;
import vn.dafl.pageUIs.AuthModalPageUI;

public class AuthModalPageObject extends BasePage {

    @Step("Đợi Auth Modal hiển thị hoàn tất")
    public void waitForAuthModalVisible() {
        waitForElementVisible(AuthModalPageUI.AUTH_MODAL);
    }

    @Step("Kiểm tra Auth Modal có hiển thị hay không")
    public boolean isAuthModalDisplayed() {
        return isElementDisplayed(AuthModalPageUI.AUTH_MODAL);
    }

    @Step("Lấy tiêu đề Modal Đăng nhập / Đăng ký")
    public String getModalTitle() {
        return getElementText(AuthModalPageUI.AUTH_MODAL_TITLE);
    }

    @Step("Chuyển sang Tab Đăng nhập")
    public void switchToLoginTab() {
        clickToElement(AuthModalPageUI.TAB_LOGIN);
        waitForElementVisible(AuthModalPageUI.LOGIN_EMAIL_INPUT);
    }

    @Step("Chuyển sang Tab Đăng ký")
    public void switchToRegisterTab() {
        clickToElement(AuthModalPageUI.TAB_REGISTER);
        waitForElementVisible(AuthModalPageUI.REGISTER_NAME_INPUT);
    }

    @Step("Đăng nhập với Email: {0} và Password: {1}")
    public void login(String email, String password) {
        switchToLoginTab();
        sendKeyToElement(AuthModalPageUI.LOGIN_EMAIL_INPUT, email);
        sendKeyToElement(AuthModalPageUI.LOGIN_PASSWORD_INPUT, password);
        clickToElement(AuthModalPageUI.LOGIN_SUBMIT_BTN);
    }

    @Step("Đăng nhập bằng tài khoản Demo (demo@dafl.vn)")
    public void clickDemoAccountButton() {
        login("demo@dafl.vn", "Password123!");
        waitForElementInvisible(AuthModalPageUI.AUTH_MODAL);
        sleepInSecond(1);
    }

    @Step("Đăng ký tài khoản mới với Tên: {0}, Email: {1}")
    public void register(String name, String email, String password, String confirmPassword, String phone, String address) {
        switchToRegisterTab();
        sendKeyToElement(AuthModalPageUI.REGISTER_NAME_INPUT, name);
        sendKeyToElement(AuthModalPageUI.REGISTER_EMAIL_INPUT, email);
        sendKeyToElement(AuthModalPageUI.REGISTER_PASSWORD_INPUT, password);
        sendKeyToElement(AuthModalPageUI.REGISTER_CONFIRM_PASSWORD_INPUT, confirmPassword);
        if (phone != null && !phone.isEmpty()) {
            sendKeyToElement(AuthModalPageUI.REGISTER_PHONE_INPUT, phone);
        }
        if (address != null && !address.isEmpty()) {
            sendKeyToElement(AuthModalPageUI.REGISTER_ADDRESS_INPUT, address);
        }
        clickToElement(AuthModalPageUI.REGISTER_SUBMIT_BTN);
    }

    @Step("Kiểm tra thông báo lỗi Authentication có hiển thị không")
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayedWithWait(AuthModalPageUI.AUTH_ERROR_MSG, 3);
    }

    @Step("Lấy thông báo lỗi Authentication")
    public String getErrorMessage() {
        waitForElementVisible(AuthModalPageUI.AUTH_ERROR_MSG);
        return getElementText(AuthModalPageUI.AUTH_ERROR_MSG);
    }

    @Step("Đóng Auth Modal")
    public void closeModal() {
        if (isElementDisplayed(AuthModalPageUI.AUTH_CLOSE_BTN)) {
            clickToElement(AuthModalPageUI.AUTH_CLOSE_BTN);
            waitForElementInvisible(AuthModalPageUI.AUTH_MODAL);
            sleepInSecond(1);
        }
    }
}
