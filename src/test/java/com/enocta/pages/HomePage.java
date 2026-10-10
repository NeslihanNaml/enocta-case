package com.enocta.pages;

import com.enocta.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class HomePage extends BasePage {

    private static final String SEARCH_INPUT_TEST_ID = "search-bar-input";
    private static final By SEARCH_INPUT = By.xpath("//input[@data-test-id='" + SEARCH_INPUT_TEST_ID + "']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        driver.get(ConfigReader.get("base.url"));
        acceptCookiesIfPresent();
        return this;
    }

    /**
     * Ana sayfadaki arama kutusu önce üzeri örtülü bir yer tutucu olarak yüklenir.
     * Kullanıcı gibi üzerine tıklanınca asıl arama kutusu açılır ve odak ona geçer; arama metni bu kutuya yazılır.
     */
    public void searchFor(String keyword) {
        new Actions(driver).moveToElement(waitForVisible(SEARCH_INPUT)).click().perform();
        WebElement activeSearchInput = wait.until(d -> {
            WebElement focused = d.switchTo().activeElement();
            return SEARCH_INPUT_TEST_ID.equals(focused.getAttribute("data-test-id")) ? focused : null;
        });
        activeSearchInput.sendKeys(keyword, Keys.ENTER);
    }
}
