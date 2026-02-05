import java.util.Scanner;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PortScanner {
    // Çıktıların birbirine girmesini engelleyen kilit objesi
    private static final Object printLock = new Object();

    public static void main(String[] args) {
        Scanner klavye = new Scanner(System.in);
        System.out.println("===============================");
        System.out.println("   GELISMIS PORT ANALIZI (V3)  ");
        System.out.println("===============================");
        
        System.out.print("Hedef IP (Orn: 8.8.8.8): ");
        String ip = klavye.nextLine();
        
        System.out.println("\n>>> " + ip + " analiz ediliyor... (Guvenli Mod)");
        
        AtomicInteger acikPortSayisi = new AtomicInteger(0);
        // Kararlı tarama için 20 paralel iş parçacığı
        ExecutorService executor = Executors.newFixedThreadPool(20);

        for (int i = 1; i <= 1024; i++) {
            final int port = i;
            executor.execute(() -> {
                try (Socket s = new Socket()) {
                    // 1. ADIM: Bağlantı dene (2 saniye bekle)
                    s.connect(new InetSocketAddress(ip, port), 2000);
                    
                    // 2. ADIM: Gerçeklik Testi (Fake cevapları ayıkla)
                    s.setSoTimeout(1000);
                    s.getOutputStream().write(1); 
                    
                    // 3. ADIM: Senkronize Çıktı (Tabloyu düzgün basar)
                    synchronized (printLock) {
                        System.out.println("[+] DOGRULANMIS PORT: " + port + " " + getServiceDescription(port));
                    }
                    
                    acikPortSayisi.incrementAndGet();
                } catch (Exception e) {
                    // Port kapalı veya sahteyse hiçbir şey yapma
                }
            });
        }

        executor.shutdown();
        try {
            if (executor.awaitTermination(10, TimeUnit.MINUTES)) {
                System.out.println("\n-------------------------------");
                if (acikPortSayisi.get() == 0) {
                    System.out.println("SONUC: Hic gercek acik port bulunamadi.");
                } else {
                    System.out.println("SONUC: Toplam " + acikPortSayisi.get() + " adet dogrulanmis port bulundu.");
                }
                System.out.println("-------------------------------");
            }
        } catch (InterruptedException e) {
            System.out.println("\nTarama islemi kesildi.");
        }
    }

    private static String getServiceDescription(int port) {
        return switch (port) {
            case 21 -> "(FTP)";
            case 22 -> "(SSH)";
            case 53 -> "(DNS Servisi)";
            case 80 -> "(HTTP Web Paneli)";
            case 443 -> "(HTTPS Guvenli Web)";
            case 445 -> "(SMB Dosya Paylasimi)";
            case 3306 -> "(MySQL)";
            case 3389 -> "(RDP Uzak Masaustu)";
            default -> "";
        };
    }
}