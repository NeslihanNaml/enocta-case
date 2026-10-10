package com.enocta.steps;

import com.enocta.context.ScenarioContext;
import com.enocta.driver.DriverManager;
import com.enocta.pages.HomePage;
import com.enocta.pages.ProductDetailPage;
import com.enocta.pages.SearchResultsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Görev 1: Kullanıcı Girişi Yaparak Sepete Ürün Eklenmesi.
 * Step cümleleri case dokümanındaki adımlarla birebir aynıdır.
 * Giriş adımı, Hepsiburada'nın e-posta doğrulama kodu zorunluluğu nedeniyle kapsam dışıdır (bkz. README).
 */
public class LoginAndAddToCartSteps {

    private final DriverManager driverManager;
    private final ScenarioContext context;

    public LoginAndAddToCartSteps(DriverManager driverManager, ScenarioContext context) {
        this.driverManager = driverManager;
        this.context = context;
    }

    @Given("Kullanıcı bir e-ticaret sitesini ziyaret eder")
    public void userVisitsSite() {
        new HomePage(driver()).open();
    }

    @When("Kullanıcı, {string} araması yapar")
    public void userSearchesFor(String keyword) {
        new HomePage(driver()).searchFor(keyword);
    }

    @When("Arama sonuçlarında fiyat aralığı olarak {int} – {int} TL filtrelemesi yapılır")
    public void filterResultsByPrice(int minPrice, int maxPrice) {
        new SearchResultsPage(driver()).filterByPrice(minPrice, maxPrice);
    }

    @When("Filtrelenen sonuçlarda en alt satırdan rastgele bir ürün seçilir ve ürün detayına gidilir")
    public void openRandomProductFromLastRow() {
        new SearchResultsPage(driver()).openRandomProductFromLastRow();
    }

    @When("Ürün detayında en düşük puanlı satıcının ürünü sepete eklenir")
    public void addLowestRatedSellerProductToCart() {
        ProductDetailPage productDetailPage = new ProductDetailPage(driver());
        context.setCartItemCountBeforeAdd(productDetailPage.getCartItemCount());
        productDetailPage.addLowestRatedSellerProductToCart();
    }

    @Then("Ürünün sepete doğru eklendiği kontrol edilir")
    public void productShouldBeAddedToCartCorrectly() {
        ProductDetailPage productDetailPage = new ProductDetailPage(driver());
        int countBeforeAdd = context.getCartItemCountBeforeAdd();

        assertAll("Sepet kontrolü",
                () -> assertTrue(productDetailPage.isAddedToCartNotificationDisplayed(),
                        "\"Ürün sepete eklendi\" bildirimi görüntülenmedi"),
                () -> assertTrue(productDetailPage.isCartItemCountIncreasedFrom(countBeforeAdd),
                        "Sepetteki ürün sayısı artmadı (eklemeden önce: " + countBeforeAdd + ")"));
    }

    private WebDriver driver() {
        return driverManager.getDriver();
    }
}
