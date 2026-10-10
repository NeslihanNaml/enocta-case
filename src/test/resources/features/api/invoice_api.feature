@api
Feature: API Testi Yapılması
  Postman mock server üzerindeki token, viewInvoice ve sendInvoice endpoint'leri test edilir.
  Çağrı başarılı ise (HTTP 2xx ve Result.success = true) response body dosyaya yazılır.

  Background:
    Given "testuser" kullanıcısı "testpass" şifresi ile token alır

  Scenario: viewInvoice çağrısı başarılı ise response body dosyaya yazılır
    When "1234567890" barkodlu fatura viewInvoice ile görüntülenir
    Then çağrı başarılı ise response body dosyaya yazılır

  Scenario: sendInvoice çağrısı başarılı ise response body dosyaya yazılır
    When "1234567890" barkodlu fatura token ile sendInvoice ile gönderilir
    Then çağrı başarılı ise response body dosyaya yazılır
