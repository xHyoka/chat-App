# Chat App

Spring Boot ve WebSocket teknolojileri kullanılarak geliştirilmiş gerçek zamanlı çok kanallı sohbet uygulaması.
Aynı ağdaki kullanıcılar farklı kanallara katılabilir, mesajlaşabilir ve online kullanıcıları görebilir.

## Özellikler
- Gerçek zamanlı mesajlaşma (WebSocket / STOMP)
- General ve Java kanalları arasında geçiş
- Kanal bazlı mesaj izolasyonu (farklı kanaldaki kullanıcılar birbirinin mesajını göremez)
- Online kullanıcı listesi
- Mesaj geçmişi (uygulama açılınca veritabanından yüklenir)

## Kullanılan Teknolojiler
- Java / Spring Boot
- WebSocket, STOMP, SockJS
- Spring Data JPA
- PostgreSQL

## Kurulum
1. Repoyu klonla
```bash
   git clone https://github.com/kullaniciadin/chat-app.git
```
2. `application.properties` içindeki PostgreSQL bağlantı bilgilerini düzenle
3. Projeyi çalıştır
```bash
   ./mvnw spring-boot:run
```
4. Tarayıcıdan aç
```
   http://localhost:8080
```