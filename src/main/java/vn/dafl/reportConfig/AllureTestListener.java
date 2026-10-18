package vn.dafl.reportConfig;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import vn.dafl.commons.BaseTest;

public class AllureTestListener implements ITestListener {

    @Attachment(value = "Failure Screenshot: {0}", type = "image/png")
    public static byte[] saveScreenshotPNG(String testName, WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Object currentClass = result.getInstance();
        if (currentClass instanceof BaseTest) {
            WebDriver driver = ((BaseTest) currentClass).getDriverInstance();
            if (driver != null) {
                saveScreenshotPNG(result.getName(), driver);
            }
        }
    }

    @Override
    public void onTestStart(ITestResult result) {}

    @Override
    public void onTestSuccess(ITestResult result) {}

    @Override
    public void onTestSkipped(ITestResult result) {}

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {}

    @Override
    public void onFinish(ITestContext context) {}
}
