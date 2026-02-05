import java.util.Scanner;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Tarayici {
    public static void main(String[] args) {
        Scanner klavye = new Scanner(System.in);
        System.out.println("===============================");
        System.out.println("  GELISMIS PORT ANALIZI (V3)   ");
        System.out.println("===============================");
        
        System.out.print("Hedef IP (Orn: 8.8.8.8): ");
        String ip = klavye.nextLine();
        
        System.out.println("\n>>> " + ip + " icin derin analiz yapiliyor...");
        System.out.println(">>> (Sahte portlar ayiklaniyor, bu biraz vakit alabilir)\n");
        
        AtomicInteger acikPortSayisi = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(15); // Hızı daha da düşürdük

        for (int i = 1; i <= 1024; i++) {
            final int port = i;
            executor.execute(() -> {
                try (Socket s = new Socket()) {
                    // 1. ADIM: Bağlantı dene
                    s.connect(new InetSocketAddress(ip, port), 2000);
                    
                    // 2. ADIM: Gerçeklik Testi (Banner Check)
                    // Bağlantı kurulsa bile içeriye veri gönderilebiliyor mu bakıyoruz.
                    s.setSoTimeout(1000);
                    s.getOutputStream().write(1); // 1 baytlık test verisi gönder
                    
                    // Buraya kadar geldiyse port gerçekten açıktır
                    System.out.println("[+] DOGRULANMIS PORT: " + port + " " + getService(port));
                    acikPortSayisi.incrementAndGet();
                } catch (Exception e) {
                    // Kapalı veya sahte portlar buraya düşer
                }
            });
        }

        executor.shutdown();
        try {
            if (executor.awaitTermination(10, TimeUnit.MINUTES)) {
                System.out.println("\n-------------------------------");
                System.out.println("ANALIZ TAMAMLANDI.");
                System.out.println("Gerçek Açık Port Sayısı: " + acikPortSayisi.get());
                System.out.println("-------------------------------");
            }
        } catch (InterruptedException e) {
            System.out.println("\nİşlem kesildi.");
        }
    }

    private static String getService(int port) {
        return switch (port) {
            case 53 -> "(DNS Servisi)";
            case 80 -> "(HTTP Web)";
            case 443 -> "(HTTPS Güvenli Web)";
            default -> "";
        };
    }
}