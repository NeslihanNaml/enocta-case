# Enocta – Yazılım Test Mühendisi Case

Hepsiburada üzerinde UI otomasyonu ve Postman mock server üzerinde API testleri.
Testler BDD yaklaşımıyla (Cucumber) yazılmıştır.

## Case Gereksinimleri

| # | Gereksinim | Durum | Nerede |
|---|---|---|---|
| 1 | Kullanıcı girişi yaparak sepete ürün eklenmesi | ✅ (giriş adımı hariç, bkz. "Kapsam Dışı Bırakılan Adım") | `features/ui/login_and_add_to_cart.feature` |
| 2 | API testi (mock server, yanıtların dosyaya yazılması) | ✅ | `features/api/invoice_api.feature`, `postman/` |
| 3 | Tarayıcıların parametrik ayarlanması | ✅ | `-Dbrowser=chrome\|firefox\|edge` |
| 4 | BDD yaklaşımı | ✅ | Cucumber feature dosyaları |
| 5 | Test sonuçlarının rapor olarak görüntülenmesi | ✅ | `target/cucumber-reports/` |
| 6 | Hata anında ekran görüntüsü (bonus) | ➖ Yapılmadı | |
| 7 | Docker ile çalıştırma (bonus) | ➖ Yapılmadı | |
| 8 | Paralel koşum (bonus) | ✅ | `junit-platform.properties` |

## Kullanılan Teknolojiler

| Araç | Amaç |
|---|---|
| Java 17 | Programlama dili |
| Maven | Bağımlılık yönetimi ve test koşumu |
| Selenium 4 | UI otomasyonu |
| Cucumber 7 + JUnit 5 | BDD senaryoları ve test koşucusu |
| PicoContainer | Step sınıfları arasında senaryo bazlı veri paylaşımı |
| REST Assured | API testleri |
| Postman | Mock server |

## Gereksinimler

- **Java 17** veya üzeri (`java -version`)
- **Maven 3.9** veya üzeri (`mvn -v`)
- **Google Chrome** (varsayılan tarayıcı). Firefox veya Edge ile koşmak için ilgili tarayıcının kurulu olması gerekir.

Tarayıcı driver'larını ayrıca kurmaya gerek yoktur. Selenium Manager uygun driver'ı otomatik indirir.

## Testleri Çalıştırma

Proje klasöründe:

```bash
mvn clean test
```

### Tarayıcı Seçimi

Tarayıcı parametre ile seçilir: `chrome` (varsayılan), `firefox`, `edge`.

```bash
mvn clean test "-Dbrowser=firefox"
mvn clean test "-Dbrowser=edge"
mvn clean test "-Dheadless=true"
```

### Sadece Belirli Testleri Çalıştırma

```bash
mvn clean test "-Dcucumber.filter.tags=@ui"    # sadece UI senaryosu
mvn clean test "-Dcucumber.filter.tags=@api"   # sadece API senaryoları (tarayıcı açılmaz)
```

### Paralel Koşum

Senaryolar paralel koşar; varsayılan olarak aynı anda en fazla **3 senaryo** çalışır.
Her senaryo kendi tarayıcısını ve kendi verisini kullandığı için senaryolar birbirini etkilemez.
Ayarlar `src/test/resources/junit-platform.properties` dosyasındadır:

```properties
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.config.fixed.parallelism=3
```

Komut satırından da değiştirilebilir:

```bash
mvn clean test "-Dcucumber.execution.parallel.enabled=false"                 # sıralı koşum
mvn clean test "-Dcucumber.execution.parallel.config.fixed.parallelism=2"    # aynı anda 2 senaryo
```

## Test Raporu

Koşum bittikten sonra raporlar şu klasörde oluşur:

| Rapor | Dosya | İçerik |
|---|---|---|
| Cucumber HTML raporu | `target/cucumber-reports/cucumber-report.html` | Senaryolar, adımlar, hata mesajları |
| Timeline raporu | `target/cucumber-reports/timeline/index.html` | Senaryoların hangi thread'de ve ne zaman koştuğu (paralel koşumun görünümü) |

Dosyaları tarayıcıda açmanız yeterlidir.

## Senaryolar

### Görev 1 – Kullanıcı Girişi Yaparak Sepete Ürün Eklenmesi (`@ui`)

`src/test/resources/features/ui/login_and_add_to_cart.feature`

| # | Case adımı | Durum |
|---|---|---|
| 1 | Kullanıcı bir e-ticaret sitesini ziyaret eder | ✅ |
| 2 | Kullanıcı giriş işlemi yapılır | ⚠️ Kapsam dışı (aşağıya bakınız) |
| 3 | Kullanıcı, "cep telefonu" araması yapar | ✅ |
| 4 | Arama sonuçlarında fiyat aralığı olarak 15000 – 20000 TL filtrelemesi yapılır | ✅ |
| 5 | Filtrelenen sonuçlarda en alt satırdan rastgele bir ürün seçilir ve ürün detayına gidilir | ✅ |
| 6 | Ürün detayında en düşük puanlı satıcının ürünü sepete eklenir | ✅ |
| 7 | Ürünün sepete doğru eklendiği kontrol edilir | ✅ (misafir kullanıcı ile, aşağıya bakınız) |

#### Kapsam Dışı Bırakılan Adım: Giriş İşlemi

Case'in 2. adımı olan **"Kullanıcı giriş işlemi yapılır"** senaryoya dahil edilmemiştir.

**Neden:** Hepsiburada, tanımadığı cihazlardan yapılan girişlerde hesabın e-posta adresine
**6 haneli bir doğrulama kodu** göndermekte ve kod girilmeden girişi tamamlamamaktadır.
Selenium her koşumda yeni ve boş bir tarayıcı profili açtığı için site her seferinde bu doğrulamayı istemektedir.
Bu kodun otomasyon tarafından alınabilmesi için e-posta kutusuna erişim gerekeceğinden,
test giriş yapılmadan **misafir kullanıcı** ile koşturulmaktadır.

**Sepet kontrolüne etkisi:** Hepsiburada'da misafir kullanıcılar sepet sayfasını görüntüleyemez
(sepet sayfası giriş ekranına yönlendirir). Bu nedenle 7. adımdaki kontrol ürün detay sayfasında yapılır:

- **"Ürün sepete eklendi"** bildiriminin görüntülenmesi
- Sitenin üst kısmındaki **sepet sayacının** eklemeden önceki değere göre artması

#### Uygulama Notları

- **Arama:** Hepsiburada'nın arama kutusunda ayrı bir "Ara" butonu bulunmadığından arama Enter tuşu ile başlatılır.
- **Fiyat filtresi:** Filtreye tıklandığında adres çubuğu hemen değişir, ürün listesi ise bir süre sonra yenilenir.
  Filtre öncesi listeden ürün seçilmemesi için eski listenin sayfadan kalkması ve yeni listenin yüklenmesi beklenir.
- **En alt satır:** Sayfa ürün listesinin en altına kaydırılır. Liste aşağı kaydırıldıkça yeni ürün yüklenebildiği için
  (infinite scroll), kaydırma sonrası sayfada yüklü olan listenin son satırı esas alınır.
  Ürün listesi arasına giren reklam karuselleri hariç tutulur.
- **Bekleme süresi:** Sayfanın ve ağın oturması için her UI adımından sonra ve sayfa kaydırıldıktan sonra
  5 saniye beklenir. Süre `config.properties` içindeki `ui.pause.seconds` değeri ile değiştirilebilir.
- **En düşük puanlı satıcı:** Ürün detayındaki "Diğer satıcılar" paneli açılır ve satıcı puanı gösterilen
  satıcılar arasından en düşük puanlı satıcının ürünü sepete eklenir. Ürünün başka satıcısı yoksa
  mevcut satıcının ürünü eklenir.

### Görev 2 – API Testi Yapılması (`@api`)

`src/test/resources/features/api/invoice_api.feature`

#### Mock Server

Endpoint'ler Postman mock server üzerinde çalışmaktadır:

```
https://d947d733-008e-4bc0-b767-b15a327e04ad.mock.pstmn.io
```

Adres `config.properties` içindeki `api.base.url` değerinde tanımlıdır; testleri çalıştırmak için ek ayar gerekmez.

| Endpoint | Metot | İstek | Yanıt |
|---|---|---|---|
| `/token` | POST | Header: `user`, `pass` | `{"token": "..."}` |
| `/viewInvoice?barcode={barcode}` | GET | Query: `barcode` | `{"InvoiceLink": "http://abc.com/invoice.pdf", "Result": {"success": true}}` |
| `/sendInvoice` | POST | Header: `token`, Body: `{"Barcode": "{barcode}"}` | `{"Barcode": "{barcode}", "Result": {"success": true}}` |

Case'de `sendInvoice` yanıtı tanımlanmadığı için diğer endpoint'lerle tutarlı olacak şekilde `Result.success` döndürülmektedir.

#### Senaryolar

Her senaryodan önce `token` endpoint'inden token alınır (`Background`).

| Senaryo | Adımlar |
|---|---|
| viewInvoice | Barkod ile fatura görüntülenir → çağrı başarılı ise response body dosyaya yazılır |
| sendInvoice | Barkod, token ile gönderilir → çağrı başarılı ise response body dosyaya yazılır |

Bir çağrı **HTTP 2xx** döndüğünde **ve** yanıttaki `Result.success` alanı `true` olduğunda başarılı sayılır.
Başarısız yanıt dosyaya yazılmaz ve senaryo hata verir.

#### Yanıt Dosyaları

Başarılı çağrıların response body'leri okunabilir JSON olarak şu klasöre yazılır:

```
test-output/api-responses/
├── viewInvoice_20261010_164235_112_t1.json
└── sendInvoice_20261010_164236_592_t1.json
```

Dosya adı `<endpoint>_<tarih>_<saat>_<milisaniye>_t<thread>.json` biçimindedir; paralel koşumlarda dosyalar çakışmaz.
Klasör `config.properties` içindeki `api.response.dir` değeri ile değiştirilebilir.

#### Postman Koleksiyonu

`postman/enocta-invoice-mock.postman_collection.json` dosyası Postman'e **Import** edilerek istekler Postman üzerinden
de çalıştırılabilir. Koleksiyon; üç isteği, mock server'ın döndürdüğü örnek yanıtları (examples) ve
her istek için Postman testlerini içerir. `baseUrl` değişkeni yukarıdaki mock server adresini gösterir.

Kendi mock server'ınızı kullanmak isterseniz koleksiyonu import edip **Mock collection** ile yeni bir
mock oluşturun ve adresini testlere ortam değişkeni ile verin:

```powershell
$env:API_BASE_URL="https://<mock-id>.mock.pstmn.io"; mvn clean test "-Dcucumber.filter.tags=@api"
```

## Sorun Giderme

| Durum | Çözüm |
|---|---|
| Windows konsolunda Türkçe karakterler bozuk görünüyor (`Kullan�c�`) | Testlerden önce terminalde `chcp 65001` çalıştırın. Raporlar ve yanıt dosyaları bu durumdan etkilenmez. |
| UI senaryosu bir locator'da zaman aşımına uğruyor | Hepsiburada arayüzü güncellenmiş olabilir. Locator'lar `pages/` altındaki ilgili sayfa sınıfının en üstünde tanımlıdır. |
| Yavaş bağlantıda UI adımları zaman aşımına uğruyor | `config.properties` içinde `explicit.wait.seconds` ve `ui.pause.seconds` değerlerini artırın. |

## Proje Yapısı

```
src/test/
├── java/com/enocta/
│   ├── api/        API istemcisi ve yanıtları dosyaya yazan sınıf
│   ├── config/     Ayar okuma (system property > ortam değişkeni > config.properties)
│   ├── context/    Bir senaryonun adımları arasında paylaşılan veriler
│   ├── driver/     Tarayıcı seçimi ve WebDriver oluşturma
│   ├── hooks/      Adımlar arası bekleme ve senaryo sonrası tarayıcının kapatılması
│   ├── pages/      Page Object sınıfları
│   ├── runner/     JUnit 5 Cucumber suite'i
│   ├── steps/      Step tanımları
│   └── utils/      Yardımcı sınıflar (sabit süreli bekleme)
└── resources/
    ├── features/               Feature dosyaları
    ├── config.properties       Varsayılan ayarlar
    └── junit-platform.properties  Cucumber ayarları (glue, rapor, paralel koşum)
postman/            Postman mock server koleksiyonu
test-output/        Koşum sırasında oluşan API yanıt dosyaları
```
