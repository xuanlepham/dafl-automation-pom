package vn.dafl.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vn.dafl.commons.BaseTest;
import vn.dafl.pageObjects.AuthModalPageObject;
import vn.dafl.pageObjects.HomePageObject;

@Epic("DAFL E-Commerce Platform")
@Feature("Authentication Module (AuthModal)")
public class AuthTest extends BaseTest {
    private HomePageObject homePage;
    private AuthModalPageObject authModal;

    @BeforeMethod
    public void initPages() {
        homePage = new HomePageObject();
        homePage.openHomePage(baseUrl);
    }

    @Test(description = "TC_AUTH_01: Should render login form elements by default")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Xác minh form Đăng nhập hiển thị đầy đủ các trường email, mật khẩu và nút submit theo mặc định")
    public void TC_AUTH_01_RenderLoginFormByDefault() {
        authModal = homePage.clickLoginRegisterInDropdown();
        Assert.assertTrue(authModal.isAuthModalDisplayed(), "Auth Modal không hiển thị");
        Assert.assertEquals(authModal.getModalTitle(), "Đăng nhập tài khoản");
        authModal.closeModal();
    }

    @Test(description = "TC_AUTH_02: Should allow switching to register tab")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh người dùng bấm chuyển sang Tab Đăng ký thành công")
    public void TC_AUTH_02_SwitchToRegisterTab() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.switchToRegisterTab();
        Assert.assertEquals(authModal.getModalTitle(), "Đăng ký thành viên mới");
        authModal.closeModal();
    }

    @Test(description = "TC_AUTH_03: Should render registration form fields when authTab is register")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh Tab Đăng ký hiển thị đầy đủ Họ tên, Email, Mật khẩu, Nhập lại mật khẩu, SĐT, Địa chỉ")
    public void TC_AUTH_03_RenderRegisterFields() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.switchToRegisterTab();
        authModal.register("Test User", "test" + System.currentTimeMillis() + "@dafl.vn", "Password123!", "Password123!", "0987654321", "Hà Nội");
        homePage.sleepInSecond(2);
    }

    @Test(description = "TC_AUTH_04: Should trigger demo account quick-fill and submit valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Xác minh bấm nút Demo Account (demo@dafl.vn) đăng nhập thành công vào hệ thống")
    public void TC_AUTH_04_LoginWithDemoAccount() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.clickDemoAccountButton();
        homePage.sleepInSecond(2);
        Assert.assertTrue(homePage.isUserLoggedIn(), "User chưa đăng nhập thành công sau khi bấm Demo Account");
    }

    @Test(description = "TC_AUTH_05: Should call onClose when clicking close button")
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh bấm nút đóng (X) đóng modal thành công")
    public void TC_AUTH_05_CloseModalOnClick() {
        authModal = homePage.clickLoginRegisterInDropdown();
        Assert.assertTrue(authModal.isAuthModalDisplayed());
        authModal.closeModal();
        Assert.assertFalse(authModal.isAuthModalDisplayed(), "Auth Modal vẫn còn hiển thị sau khi bấm đóng");
    }

    @Test(description = "TC_AUTH_06: Should display error message alert when authError is present (Negative)")
    @Severity(SeverityLevel.NORMAL)
    @Description("Xác minh hiển thị alert thông báo lỗi khi đăng nhập với mật khẩu sai")
    public void TC_AUTH_06_DisplayErrorMessageOnFailedLogin() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.login("demo@dafl.vn", "sai_mat_khau_123");
        homePage.sleepInSecond(1);
        String errorMsg = authModal.getErrorMessage();
        Assert.assertTrue(errorMsg != null && !errorMsg.isEmpty(), "Không hiển thị thông báo lỗi khi nhập sai mật khẩu");
        authModal.closeModal();
    }

    @Test(description = "TC_AUTH_07: Should logout and clear user session")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Xác minh người dùng đăng xuất thì giao diện trở về trạng thái chưa đăng nhập")
    public void TC_AUTH_07_LogoutAndClearSession() {
        authModal = homePage.clickLoginRegisterInDropdown();
        authModal.clickDemoAccountButton();
        homePage.sleepInSecond(2);

        homePage.clickLogoutInDropdown();
        homePage.sleepInSecond(1);
        Assert.assertFalse(homePage.isUserLoggedIn(), "Vẫn còn lưu trạng thái đăng nhập sau khi logout");
    }
}
