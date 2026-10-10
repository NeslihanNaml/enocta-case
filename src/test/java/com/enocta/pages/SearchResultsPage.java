package com.enocta.pages;

import com.enocta.utils.Pause;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SearchResultsPage extends BasePage {

    private static final By PRICE_MIN_INPUT = By.xpath("//input[@aria-label='En az']");
    private static final By PRICE_MAX_INPUT = By.xpath("//input[@aria-label='En çok']");
    private static final By PRICE_FILTER_BUTTON = By.xpath("//button[@aria-label='Filtrele']");
    // "Ürünler" listesindeki ürün kartları. Kartlar doğrudan <li> altındadır; liste arasına giren
    // reklam karuselleri <div> içinde olduğu için li/article koşulu ile hariç tutulur.
    private static final By PRODUCT_CARDS = By.xpath("//div[@role='region' and @aria-label='Ürünler']//li/article");
    private static final By PRODUCT_LINK = By.xpath(".//h2[starts-with(@data-test-id,'title-')]/a");

    private final Random random = new Random();

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Filtreye tıklandığında adres çubuğu hemen değişir ancak ürün listesi bir süre sonra yenilenir.
     * Eski listeden ürün seçilmemesi için filtre öncesi listenin sayfadan kalkması beklenir.
     */
    public void filterByPrice(int minPrice, int maxPrice) {
        WebElement firstCardBeforeFilter = waitForVisible(PRODUCT_CARDS);
        type(PRICE_MIN_INPUT, String.valueOf(minPrice));
        type(PRICE_MAX_INPUT, String.valueOf(maxPrice));
        click(PRICE_FILTER_BUTTON);

        wait.until(ExpectedConditions.stalenessOf(firstCardBeforeFilter));
        waitForAllVisible(PRODUCT_CARDS);
    }

    /**
     * Satırlar kartların sayfadaki dikey konumuna (Y) göre belirlenir. Liste aşağı kaydırıldıkça yeni ürün
     * yüklenebildiği (infinite scroll) için kaydırma sonrası yüklü olan listenin son satırı esas alınır.
     * Kaydırma ve seçim, liste bu sırada yeniden çizilirse baştan denensin diye bekleme içinde yapılır.
     */
    public void openRandomProductFromLastRow() {
        wait.until(d -> scrollToLastProduct());
        Pause.configuredDuration();

        String productUrl = wait.until(d -> selectRandomProductFromLastRow());
        Pause.configuredDuration();

        // Kartlar yeni sekmede açılıyor; aynı sekmede ilerlemek için linkin adresine gidilir.
        driver.get(productUrl);
    }

    private boolean scrollToLastProduct() {
        List<WebElement> cards = driver.findElements(PRODUCT_CARDS);
        if (cards.isEmpty()) {
            return false;
        }
        scrollIntoView(cards.get(cards.size() - 1));
        return true;
    }

    private String selectRandomProductFromLastRow() {
        List<WebElement> lastRow = findProductsInLastRow();
        WebElement selectedCard = lastRow.get(random.nextInt(lastRow.size()));
        scrollIntoView(selectedCard);
        return selectedCard.findElement(PRODUCT_LINK).getAttribute("href");
    }

    private List<WebElement> findProductsInLastRow() {
        List<WebElement> cards = driver.findElements(PRODUCT_CARDS);

        int lastRowY = cards.stream()
                .map(card -> card.getLocation().getY())
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalStateException("Arama sonucunda ürün bulunamadı"));

        return cards.stream()
                .filter(card -> card.getLocation().getY() == lastRowY)
                .toList();
    }
}
