package vn.dafl.commons;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import vn.dafl.driver.DriverManager;

import java.time.Duration;
import java.util.List;

public class BasePage {

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    protected WebDriverWait getWait(long timeoutInSeconds) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));
    }

    public void openPageUrl(String url) {
        getDriver().get(url);
    }

    public String getPageTitle() {
        return getDriver().getTitle();
    }

    public String getCurrentPageUrl() {
        return getDriver().getCurrentUrl();
    }

    public By getByLocator(String locator) {
        if (locator.startsWith("xpath=") || locator.startsWith("XPATH=")) {
            return By.xpath(locator.substring(6));
        } else if (locator.startsWith("css=") || locator.startsWith("CSS=")) {
            return By.cssSelector(locator.substring(4));
        } else if (locator.startsWith("id=") || locator.startsWith("ID=")) {
            return By.id(locator.substring(3));
        } else if (locator.startsWith("name=") || locator.startsWith("NAME=")) {
            return By.name(locator.substring(5));
        } else if (locator.startsWith("//") || locator.startsWith("(//")) {
            return By.xpath(locator);
        } else {
            return By.cssSelector(locator);
        }
    }

    public String getDynamicLocator(String locator, String... values) {
        return String.format(locator, (Object[]) values);
    }

    public WebElement getWebElement(String locator) {
        return getDriver().findElement(getByLocator(locator));
    }

    public WebElement getWebElement(String locator, String... values) {
        return getDriver().findElement(getByLocator(getDynamicLocator(locator, values)));
    }

    public List<WebElement> getListWebElements(String locator) {
        return getDriver().findElements(getByLocator(locator));
    }

    public List<WebElement> getListWebElements(String locator, String... values) {
        return getDriver().findElements(getByLocator(getDynamicLocator(locator, values)));
    }

    public void clickToElement(String locator) {
        waitForElementClickable(locator);
        getWebElement(locator).click();
    }

    public void clickToElement(String locator, String... values) {
        waitForElementClickable(locator, values);
        getWebElement(locator, values).click();
    }

    public void clickToElementByJS(String locator) {
        WebElement element = getWebElement(locator);
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
    }

    public void clickToElementByJS(String locator, String... values) {
        WebElement element = getWebElement(locator, values);
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
    }

    public void sendKeyToElement(String locator, String textValue) {
        waitForElementVisible(locator);
        WebElement element = getWebElement(locator);
        element.clear();
        element.sendKeys(textValue);
    }

    public void sendKeyToElement(String locator, String textValue, String... values) {
        waitForElementVisible(locator, values);
        WebElement element = getWebElement(locator, values);
        element.clear();
        element.sendKeys(textValue);
    }

    public void clearElementText(String locator) {
        WebElement element = getWebElement(locator);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
    }

    public String getElementText(String locator) {
        waitForElementVisible(locator);
        return getWebElement(locator).getText().trim();
    }

    public String getElementText(String locator, String... values) {
        waitForElementVisible(locator, values);
        return getWebElement(locator, values).getText().trim();
    }

    public String getElementAttribute(String locator, String attributeName) {
        return getWebElement(locator).getAttribute(attributeName);
    }

    public boolean isElementDisplayedWithWait(String locator, long timeoutInSeconds) {
        try {
            getWait(timeoutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(getByLocator(locator)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementDisplayed(String locator) {
        try {
            return getWebElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementDisplayed(String locator, String... values) {
        try {
            return getWebElement(locator, values).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementSelected(String locator) {
        return getWebElement(locator).isSelected();
    }

    public boolean isElementEnabled(String locator) {
        return getWebElement(locator).isEnabled();
    }

    public void waitForElementVisible(String locator) {
        getWait(GlobalConstants.LONG_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(getByLocator(locator)));
    }

    public void waitForElementVisible(String locator, String... values) {
        getWait(GlobalConstants.LONG_TIMEOUT).until(
                ExpectedConditions.visibilityOfElementLocated(getByLocator(getDynamicLocator(locator, values)))
        );
    }

    public void waitForElementClickable(String locator) {
        getWait(GlobalConstants.LONG_TIMEOUT).until(ExpectedConditions.elementToBeClickable(getByLocator(locator)));
    }

    public void waitForElementClickable(String locator, String... values) {
        getWait(GlobalConstants.LONG_TIMEOUT).until(
                ExpectedConditions.elementToBeClickable(getByLocator(getDynamicLocator(locator, values)))
        );
    }

    public void waitForElementInvisible(String locator) {
        getWait(GlobalConstants.SHORT_TIMEOUT).until(ExpectedConditions.invisibilityOfElementLocated(getByLocator(locator)));
    }

    public void scrollToElement(String locator) {
        WebElement element = getWebElement(locator);
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public void sleepInSecond(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
