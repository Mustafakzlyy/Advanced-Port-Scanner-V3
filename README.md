# 🛡️ Advanced Java Port Scanner (Ghost-Port Filtered)
----------------------------------------------------------
Bu proje, ağ üzerindeki cihazların açık kapılarını (portlarını) tespit eden, yüksek performanslı ve akıllı filtreleme özelliğine sahip bir güvenlik aracıdır. Java 25'in modern özelliklerini kullanarak geliştirilmiştir.

## 🚀 Öne Çıkan Özellikler
--------------------------------
* **Akıllı Filtreleme (Anti-Ghosting):** ISS (İnternet Sağlayıcılar) ve Antivirüs yazılımlarının gönderdiği sahte "Açık" (ACK) paketlerini ayırt etmek için "Output Stream Validation" tekniğini kullanır.
* **Yüksek Performans:** Java `ExecutorService` ile paralel iş parçacıkları (threading) kullanarak hızlı tarama yapar.
* **Servis Tanımlama:** Bulunan yaygın portların (80, 443, 22 vb.) hangi servislere ait olduğunu otomatik olarak gösterir.
* **Modern Java:** Java 25 (LTS) özellikleriyle uyumlu, derleme gerektirmeden çalışabilen yapı.

## 🛠️ Nasıl Çalışır?
-------------------------
Geleneksel tarayıcılar sadece bağlantı kurulup kurulmadığına bakar. Bu araç ise:
1. Hedef port ile bir TCP bağlantısı kurar.
2. Bağlantı kurulsa bile, içeriye **1-byte test verisi** gönderir.
3. Eğer veri gönderimi başarılıysa portu **"DOĞRULANMIŞ"** olarak işaretler. Bu sayede "False Positive" sonuçları %100'e yakın oranda engeller.

**## 💻 Kurulum ve Çalıştırma**
### Gereksinimler
* Java 25 veya üzeri sürüm.

**🔍 Geliştirme Sürecinde Karşılaşılan Zorluklar ve Çözümler**
Bu projenin geliştirilmesi sırasında, siber güvenlik araçlarının gerçek dünyada karşılaştığı bazı tipik engellerle karşılaşıldı ve bu durumlar teknik çözümlerle aşıldı:

1. "Ghost Ports" (Hayalet Portlar) Sorunu
Hata: İlk testlerde Google (8.8.8.8) üzerinde 1024 portun tamamı "Açık" olarak göründü.

Neden: ISP (İnternet Servis Sağlayıcı) ve güvenlik duvarlarının, port tarama saldırılarını yanıltmak için her isteğe sahte bir "Açık" (ACK) paketi göndermesi (Transparent Proxy / Port Spoofing).

Çözüm: Sadece TCP bağlantısı kurmakla yetinmeyip, bağlantı kurulduktan sonra içeriye 1-byte veri gönderme testi eklendi. Sahte sistemler bu veriyi kabul edemediği için gerçek açık portlar hatasız şekilde ayırt edildi.

2. Java Sınıf ve Paket Hataları (ClassNotFoundException)
Hata: Terminal üzerinden çalıştırırken Java'nın dosya yollarını ve paket yapısını yanlış algılaması sonucu programın başlatılamaması.

Çözüm: Java 25 (LTS) sürümünün "Single-File Source Code" özelliği kullanılarak, derleme (javac) işlemine gerek kalmadan java src/Tarayici.java komutuyla doğrudan kaynak kod üzerinden çalıştırma yöntemi optimize edildi.

3. Zaman Aşımı (Timeout) ve Hız Dengesi
Hata: Çok hızlı taramalarda (100+ thread) ağın tıkanması, paket kayıpları veya hedef sistemin koruma mekanizmalarına takılarak yanlış sonuçlar alınması.

Çözüm: ExecutorService thread havuzu 100'den 15-20 seviyelerine çekilerek daha "hassas" bir tarama sağlandı. Port başına bekleme süresi (Timeout) 500ms'den 2000ms'ye çıkarılarak kararlı sonuçlar elde edildi.
                                                                                                                                                                       
  GELISMIS PORT ANALIZI (V3)                                                             

Hedef IP (Orn: 8.8.8.8): 192.168.1.1                                      
                                                                     
 192.168.1.1 icin derin analiz yapiliyor...                                               
[+] DOGRULANMIS PORT: 53 (DNS Servisi)
[+] DOGRULANMIS PORT: 80 (HTTP Web)
[+] DOGRULANMIS PORT: 443 (HTTPS Güvenli Web)
                                                                                
ANALIZ TAMAMLANDI.                                                    
Gerçek Açık Port Sayısı: 3                                             

### Çalıştırma
Projeyi klonladıktan sonra ana klasörde terminali açın ve şu komutu çalıştırın:

```bash
java src/PortScanner.java
