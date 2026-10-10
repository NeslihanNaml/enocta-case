package com.enocta.steps;

import com.enocta.api.InvoiceApiClient;
import com.enocta.api.ResponseFileWriter;
import com.enocta.context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Görev 2: API Testi Yapılması.
 * viewInvoice ve sendInvoice çağrıları başarılı ise response body dosyaya yazılır.
 */
public class InvoiceApiSteps {

    private static final String VIEW_INVOICE = "viewInvoice";
    private static final String SEND_INVOICE = "sendInvoice";

    private final ScenarioContext context;
    private final InvoiceApiClient apiClient = new InvoiceApiClient();
    private final ResponseFileWriter responseFileWriter = new ResponseFileWriter();

    public InvoiceApiSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("{string} kullanıcısı {string} şifresi ile token alır")
    public void requestToken(String user, String pass) {
        Response response = apiClient.requestToken(user, pass);
        assertEquals(200, response.statusCode(), "Token alınamadı");

        // sendInvoice çağrısı header'da token gerektirdiği için token'ın dönmüş olması gerekir.
        String token = response.jsonPath().getString("token");
        assertNotNull(token, "Yanıtta token alanı yok");
        assertFalse(token.isBlank(), "Token boş döndü");
        context.setToken(token);
    }

    @When("{string} barkodlu fatura viewInvoice ile görüntülenir")
    public void viewInvoice(String barcode) {
        context.setLastCall(VIEW_INVOICE, apiClient.viewInvoice(barcode));
    }

    @When("{string} barkodlu fatura token ile sendInvoice ile gönderilir")
    public void sendInvoice(String barcode) {
        context.setLastCall(SEND_INVOICE, apiClient.sendInvoice(context.getToken(), barcode));
    }

    @Then("çağrı başarılı ise response body dosyaya yazılır")
    public void writeResponseBodyToFileIfSuccessful() {
        Response response = context.getLastResponse();
        assertTrue(isSuccessful(response), context.getLastEndpointName()
                + " çağrısı başarısız olduğu için response body dosyaya yazılmadı. HTTP "
                + response.statusCode() + ", body: " + response.asString());

        Path file = responseFileWriter.write(context.getLastEndpointName(), response);
        assertTrue(Files.exists(file), "Response body dosyası oluşturulamadı: " + file);
    }

    /** Bir çağrı, HTTP 2xx döndüğünde ve gövdedeki Result.success true olduğunda başarılı sayılır. */
    private static boolean isSuccessful(Response response) {
        boolean httpSuccess = response.statusCode() >= 200 && response.statusCode() < 300;
        return httpSuccess && Boolean.TRUE.equals(response.jsonPath().get("Result.success"));
    }
}
