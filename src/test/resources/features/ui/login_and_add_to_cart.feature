@ui
Feature: Kullanıcı Girişi Yaparak Sepete Ürün Eklenmesi
  Not: Case'in 2. adımı olan "Kullanıcı giriş işlemi yapılır" adımı,
  Hepsiburada'nın yeni cihaz girişlerinde e-posta doğrulama kodu istemesi nedeniyle
  senaryoya dahil edilmemiştir. Ayrıntılar README dosyasındadır.

  Scenario: Kullanıcı filtrelenen üründe en düşük puanlı satıcının ürününü sepete ekler
    Given Kullanıcı bir e-ticaret sitesini ziyaret eder
    When Kullanıcı, "cep telefonu" araması yapar
    And Arama sonuçlarında fiyat aralığı olarak 15000 – 20000 TL filtrelemesi yapılır
    And Filtrelenen sonuçlarda en alt satırdan rastgele bir ürün seçilir ve ürün detayına gidilir
    And Ürün detayında en düşük puanlı satıcının ürünü sepete eklenir
    Then Ürünün sepete doğru eklendiği kontrol edilir
