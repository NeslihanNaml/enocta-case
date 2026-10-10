package com.enocta.driver;

import com.enocta.config.ConfigReader;
import org.openqa.selenium.WebDriver;

/**
 * PicoContainer senaryo başına yeni bir örnek oluşturduğu için paralel senaryolar tarayıcı paylaşmaz.
 * Driver ilk ihtiyaç anında açılır; API senaryoları tarayıcı başlatmaz.
 */
public class DriverManager {

    private WebDriver driver;

    public WebDriver getDriver() {
        if (driver == null) {
            BrowserType browserType = BrowserType.from(ConfigReader.get("browser"));
            driver = DriverFactory.create(browserType, ConfigReader.getBoolean("headless"));
        }
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
