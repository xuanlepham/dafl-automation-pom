package vn.dafl.pageObjects;

import io.qameta.allure.Step;
import vn.dafl.commons.BasePage;
import vn.dafl.pageUIs.ProfilePageUI;

public class ProfilePageObject extends BasePage {

    @Step("Kiểm tra Trang Cấu hình Địa chỉ & Thông tin Nhận hàng có hiển thị không")
    public boolean isProfilePageDisplayed() {
        return isElementDisplayedWithWait(ProfilePageUI.PROFILE_PAGE, 5);
    }

    @Step("Cập nhật thông tin địa chỉ: Tên={0}, SĐT={1}, Email={2}, Địa chỉ={3}")
    public void updateProfile(String fullName, String phone, String email, String address) {
        sendKeyToElement(ProfilePageUI.PROFILE_FULLNAME_INPUT, fullName);
        sendKeyToElement(ProfilePageUI.PROFILE_PHONE_INPUT, phone);
        sendKeyToElement(ProfilePageUI.PROFILE_EMAIL_INPUT, email);
        sendKeyToElement(ProfilePageUI.PROFILE_ADDRESS_INPUT, address);
        clickToElement(ProfilePageUI.SAVE_PROFILE_BTN);
        sleepInSecond(1);
    }

    @Step("Quay về Trang chủ từ trang Profile")
    public void clickBackToHome() {
        clickToElementByJS(ProfilePageUI.BACK_TO_HOME_BTN);
        sleepInSecond(1);
    }
}
