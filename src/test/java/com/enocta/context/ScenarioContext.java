package com.enocta.context;

import io.restassured.response.Response;

/**
 * Bir senaryonun adımları arasında paylaşılan veriler.
 * PicoContainer her senaryo için yeni bir örnek oluşturur; paralel senaryolar veri paylaşmaz.
 */
public class ScenarioContext {

    // UI
    private int cartItemCountBeforeAdd;

    // API
    private String token;
    private String lastEndpointName;
    private Response lastResponse;

    public int getCartItemCountBeforeAdd() {
        return cartItemCountBeforeAdd;
    }

    public void setCartItemCountBeforeAdd(int cartItemCountBeforeAdd) {
        this.cartItemCountBeforeAdd = cartItemCountBeforeAdd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLastEndpointName() {
        return lastEndpointName;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    /** Son yapılan API çağrısını, dosya adında kullanılacak endpoint adıyla birlikte saklar. */
    public void setLastCall(String endpointName, Response response) {
        this.lastEndpointName = endpointName;
        this.lastResponse = response;
    }
}
