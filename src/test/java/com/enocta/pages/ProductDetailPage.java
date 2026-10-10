package com.enocta.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ProductDetailPage extends BasePage {

    private static final By MAIN_ADD_TO_CART_BUTTON = By.xpath("//button[@data-test-id='addToCart']");
    private static final By SHOW_ALL_SELLERS_BUTTON =
            By.xpath("//div[@data-test-id='other-merchants']//button[normalize-space()='Tümünü gör']");
    private static final By CART_ITEM_COUNT = By.id("cartItemCount");
    private static final By ADDED_TO_CART_NOTIFICATION =
            By.xpath("//div[contains(@class,'hb-toast-text') and contains(normalize-space(),'Ürün sepete eklendi')]");

    private static final String ADD_TO_CART_TEXT = "Sepete ekle";
    // "Diğer satıcılar" panelindeki satıcı satırları: satıcı adı, puanı ve satıra ait "Sepete ekle" butonu
    private static final By SELLER_ROWS = By.xpath("//div[div//a[@data-test-id='merchant-name']"
            + " and div/button[normalize-space()='" + ADD_TO_CART_TEXT + "']]");
    private static final By SELLER_RATING = By.xpath(".//span[@data-test-id='merchant-rating']");
    private static final By ROW_ADD_TO_CART_BUTTON = By.xpath("./div/button[normalize-space()='" + ADD_TO_CART_TEXT + "']");

    private static final Duration SELLERS_BUTTON_TIMEOUT = Duration.ofSeconds(5);

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public int getCartItemCount() {
        List<WebElement> counter = driver.findElements(CART_ITEM_COUNT);
        if (counter.isEmpty() || counter.get(0).getText().isBlank()) {
            return 0;
        }
        return Integer.parseInt(counter.get(0).getText().trim());
    }

    /**
     * "Diğer satıcılar" panelini açar, puanı olan satıcılar arasından en düşük puanlıyı bulur ve
     * o satıcının ürününü sepete ekler. Ürünün başka satıcısı yoksa tek satıcının ürünü eklenir.
     */
    public void addLowestRatedSellerProductToCart() {
        Optional<WebElement> lowestRatedRow = findLowestRatedSellerRow();
        if (lowestRatedRow.isPresent()) {
            WebElement addButton = lowestRatedRow.get().findElement(ROW_ADD_TO_CART_BUTTON);
            scrollIntoView(addButton);
            addButton.click();
        } else {
            click(MAIN_ADD_TO_CART_BUTTON);
        }
    }

    public boolean isAddedToCartNotificationDisplayed() {
        return isDisplayed(ADDED_TO_CART_NOTIFICATION, Duration.ofSeconds(10));
    }

    public boolean isCartItemCountIncreasedFrom(int previousCount) {
        try {
            return wait.until(d -> getCartItemCount() > previousCount);
        } catch (TimeoutException e) {
            return false;
        }
    }

    private Optional<WebElement> findLowestRatedSellerRow() {
        if (!isDisplayed(SHOW_ALL_SELLERS_BUTTON, SELLERS_BUTTON_TIMEOUT)) {
            return Optional.empty();
        }
        click(SHOW_ALL_SELLERS_BUTTON);
        wait.until(ExpectedConditions.presenceOfElementLocated(SELLER_ROWS));

        // Puanı gösterilmeyen satıcılar (ör. Hepsiburada'nın kendisi) karşılaştırmaya dahil edilmez.
        return driver.findElements(SELLER_ROWS).stream()
                .filter(row -> !row.findElements(SELLER_RATING).isEmpty())
                .min(Comparator.comparing(ProductDetailPage::getSellerRating));
    }

    /** "9,4" -> 9.4 */
    private static BigDecimal getSellerRating(WebElement sellerRow) {
        String ratingText = sellerRow.findElement(SELLER_RATING).getText();
        return new BigDecimal(ratingText.trim().split("\\s+")[0].replace(',', '.'));
    }
}
