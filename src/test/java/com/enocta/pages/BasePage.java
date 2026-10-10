package com.enocta.pages;

import com.enocta.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchShadowRootException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Tüm sayfa nesnelerinin ortak davranışları: bekleme, tıklama, yazma, kaydırma ve çerez onayı.
 */
public abstract class BasePage {

    private static final By COOKIE_BANNER_HOST = By.cssSelector("efilli-layout-dynamic");
    private static final By COOKIE_ACCEPT_BUTTON = By.cssSelector("#hb-accept-all");
    private static final Duration SHORT_WAIT = Duration.ofSeconds(5);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
        // Site listeleri arka arkaya yeniden çizebilir; bekleme sırasında yenilenen element hata sayılmaz,
        // koşul bir sonraki denemede yeniden aranır.
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        scrollIntoView(element);
        element.click();
    }

    /** Başka bir katmanın (banner, sticky header vb.) önüne geçtiği elementler için. */
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    protected boolean isDisplayed(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Çerez onay penceresi bir shadow DOM içinde açılır. Görünürse "Kabul Et" butonuna tıklar,
     * görünmezse sessizce devam eder.
     */
    public void acceptCookiesIfPresent() {
        if (!isDisplayed(COOKIE_BANNER_HOST, SHORT_WAIT)) {
            return;
        }
        try {
            SearchContext shadowRoot = driver.findElement(COOKIE_BANNER_HOST).getShadowRoot();
            jsClick(shadowRoot.findElement(COOKIE_ACCEPT_BUTTON));
        } catch (NoSuchElementException | NoSuchShadowRootException ignored) {
            // Banner daha önce kapatılmış veya farklı bir yapıda; akışı engellemez.
        }
    }
}
