package vn.dafl.commons;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.*;
import vn.dafl.driver.DriverManager;

import java.time.Duration;

public abstract class BaseTest {
    protected String baseUrl;
    protected boolean isSingleSessionFlow = false;

    protected String determineBaseUrl(String appUrl) {
        String envUrl = System.getenv("APP_URL");
        String propUrl = System.getProperty("app.url");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            return envUrl.trim();
        } else if (propUrl != null && !propUrl.trim().isEmpty()) {
            return propUrl.trim();
        } else if (appUrl != null && !appUrl.trim().isEmpty()) {
            return appUrl.trim();
        } else {
            return "http://localhost:3000";
        }
    }

    @Parameters({"browser", "appUrl"})
    @BeforeClass(alwaysRun = true)
    public void setUpClass(@Optional("chrome") String browserName, @Optional("") String appUrl) {
        if (isSingleSessionFlow) {
            baseUrl = determineBaseUrl(appUrl);
            WebDriver driver = initBrowser(browserName);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(GlobalConstants.SHORT_TIMEOUT));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(GlobalConstants.LONG_TIMEOUT));

            boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", System.getenv("HEADLESS") != null ? System.getenv("HEADLESS") : "false"));
            if (!isHeadless) {
                driver.manage().window().maximize();
            }

            DriverManager.setDriver(driver);
        }
    }

    @Parameters({"browser", "appUrl"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browserName, @Optional("") String appUrl) {
        if (isSingleSessionFlow) {
            return;
        }
        baseUrl = determineBaseUrl(appUrl);
        WebDriver driver = initBrowser(browserName);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(GlobalConstants.SHORT_TIMEOUT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(GlobalConstants.LONG_TIMEOUT));

        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", System.getenv("HEADLESS") != null ? System.getenv("HEADLESS") : "false"));
        if (!isHeadless) {
            driver.manage().window().maximize();
        }

        DriverManager.setDriver(driver);
    }

    protected WebDriver initBrowser(String browserName) {
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", System.getenv("HEADLESS") != null ? System.getenv("HEADLESS") : "false"));

        switch (browserName.toLowerCase()) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (isHeadless) {
                    firefoxOptions.addArguments("-headless");
                }
                return new FirefoxDriver(firefoxOptions);

            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (isHeadless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--ignore-certificate-errors");

                String chromeBin = System.getenv("CHROME_BIN");
                if (chromeBin != null && !chromeBin.isEmpty()) {
                    chromeOptions.setBinary(chromeBin);
                }
                return new ChromeDriver(chromeOptions);
        }
    }

    public WebDriver getDriverInstance() {
        return DriverManager.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (isSingleSessionFlow) {
            return;
        }
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", System.getenv("HEADLESS") != null ? System.getenv("HEADLESS") : "false"));
        if (!isHeadless) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignored) {
            }
        }
        DriverManager.quit();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        if (isSingleSessionFlow) {
            boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", System.getenv("HEADLESS") != null ? System.getenv("HEADLESS") : "false"));
            if (!isHeadless) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
            DriverManager.quit();
        }
    }
}
