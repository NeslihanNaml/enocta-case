package com.enocta.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;

/**
 * Driver binary'leri Selenium Manager tarafından otomatik indirilir.
 */
public final class DriverFactory {

    private static final String WINDOW_SIZE = "--window-size=1920,1080";

    private DriverFactory() {
    }

    public static WebDriver create(BrowserType browserType, boolean headless) {
        return switch (browserType) {
            case CHROME -> new ChromeDriver(chromeOptions(headless));
            case FIREFOX -> new FirefoxDriver(firefoxOptions(headless));
            case EDGE -> new EdgeDriver(edgeOptions(headless));
        };
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(WINDOW_SIZE, "--lang=tr-TR", "--disable-notifications",
                "--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("intl.accept_languages", "tr-TR");
        options.addPreference("dom.webnotifications.enabled", false);
        options.addArguments("--width=1920", "--height=1080");
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments(WINDOW_SIZE, "--lang=tr-TR", "--disable-notifications");
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }
}
