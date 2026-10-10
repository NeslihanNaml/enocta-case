package com.enocta.api;

import com.enocta.config.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Postman mock server üzerindeki fatura servislerini çağırır.
 * Endpoint'ler: POST /token, GET /viewInvoice?barcode={barcode}, POST /sendInvoice
 */
public class InvoiceApiClient {

    private static final String TOKEN_PATH = "/token";
    private static final String VIEW_INVOICE_PATH = "/viewInvoice";
    private static final String SEND_INVOICE_PATH = "/sendInvoice";

    // Content-Type tüm isteklerde JSON olarak sabitlenir. Aksi halde REST Assured gövdesiz POST isteklerine
    // form tipi ekler ve mock server bunu geçersiz gövde sayıp 400 döner.
    private final RequestSpecification baseSpec = new RequestSpecBuilder()
            .setBaseUri(ConfigReader.get("api.base.url"))
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public Response requestToken(String user, String pass) {
        return given(baseSpec)
                .header("user", user)
                .header("pass", pass)
                .when()
                .post(TOKEN_PATH);
    }

    public Response viewInvoice(String barcode) {
        return given(baseSpec)
                .queryParam("barcode", barcode)
                .when()
                .get(VIEW_INVOICE_PATH);
    }

    public Response sendInvoice(String token, String barcode) {
        return given(baseSpec)
                .header("token", token)
                .body(Map.of("Barcode", barcode))
                .when()
                .post(SEND_INVOICE_PATH);
    }
}
