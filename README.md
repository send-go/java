# sendgo-java

> **Java에서 카카오 알림톡, 친구톡, SMS를 가장 쉽게 발송하는 순수 Java SDK**

[![Maven Central](https://img.shields.io/maven-central/v/io.sendgo/sendgo-java)](https://central.sonatype.com/artifact/io.sendgo/sendgo-java)
[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?logo=openjdk)](https://openjdk.org)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

`sendgo-java`는 [Sendgo](https://sendgo.io) 알림 API를 위한 **순수 Java 코어 SDK**입니다.
Spring, Quarkus 등 특정 프레임워크에 의존하지 않으며, `java.net.http.HttpClient`와 Jackson만 사용합니다.
Spring Boot 프로젝트라면 [`sendgo-spring`](https://github.com/send-go/spring) 패키지를 사용하세요.

---

## 설치

### Maven

```xml
<dependency>
    <groupId>io.sendgo</groupId>
    <artifactId>sendgo-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.sendgo:sendgo-java:1.0.0'
```

---

## 빠른 시작

```java
import io.sendgo.*;
import io.sendgo.model.*;

SendgoClient sendgo = new SendgoClient(SendgoConfig.builder()
    .accessKey(System.getenv("SENDGO_ACCESS_KEY"))
    .secretKey(System.getenv("SENDGO_SECRET_KEY"))
    .kakaoSenderKey(System.getenv("SENDGO_KAKAO_SENDER_KEY"))
    .smsSenderKey(System.getenv("SENDGO_SMS_SENDER_KEY"))
    .apiVersion("v2")
    .build());

// 알림톡 발송
sendgo.alimtalk().send(AlimtalkRequest.builder()
    .templateCode("ORDER_CONFIRM_001")
    .contacts(List.of(
        Contact.builder()
            .contact("01012345678")
            .name("홍길동")
            .var1("ORD-001")
            .var2("29,000원")
            .build()
    ))
    .build());
```

---

## 알림톡 상세 사용법

```java
import io.sendgo.*;
import io.sendgo.model.*;
import java.util.List;

// 다건 발송
sendgo.alimtalk().send(AlimtalkRequest.builder()
    .templateCode("ORDER_CONFIRM_001")
    .contacts(List.of(
        Contact.builder().contact("01011111111").name("홍길동").var1("ORD-001").var2("29,000원").build(),
        Contact.builder().contact("01022222222").name("김철수").var1("ORD-002").var2("15,000원").build(),
        Contact.builder().contact("01033333333").name("이영희").var1("ORD-003").var2("52,000원").build()
    ))
    .build());

// 예약 발송
sendgo.alimtalk().send(AlimtalkRequest.builder()
    .templateCode("PROMO_SUMMER_2026")
    .scheduleType("SCHEDULED")
    .at("2026-07-28 09:00:00")
    .contacts(List.of(
        Contact.builder().contact("01012345678").var1("여름 한정 50% 할인").build()
    ))
    .build());

// SMS 자동 대체 발송
sendgo.alimtalk().send(AlimtalkRequest.builder()
    .templateCode("DELIVERY_START_001")
    .replaceSms("Y")
    .smsSubject("[배송 시작 안내]")
    .smsContent("주문하신 상품이 출고되었습니다.\n송장번호: #{var2}")
    .contacts(List.of(
        Contact.builder().contact("01012345678").var1("ORD-001").var2("1234567890").build()
    ))
    .build());
```

---

## 친구톡 사용법

```java
// 텍스트형
sendgo.friendtalk().send(FriendtalkRequest.builder()
    .content("안녕하세요! 7월 한정 특가 이벤트를 확인해보세요.")
    .contacts(List.of(Contact.builder().contact("01012345678").build()))
    .build());

// 이미지형
sendgo.friendtalk().send(FriendtalkRequest.builder()
    .messageType("FI")
    .content("이번 주 특가 상품을 확인하세요!")
    .imageUrl("https://cdn.example.com/banner.jpg")
    .imageLink("https://example.com/event")
    .contacts(List.of(Contact.builder().contact("01012345678").build()))
    .build());
```

---

## SMS / LMS / MMS 사용법

```java
// SMS
sendgo.sms().sendSms(SmsRequest.sms()
    .content("[Sendgo] 인증번호: 123456 (5분 이내 입력)")
    .contact(Contact.builder().contact("01012345678").build()));

// LMS
sendgo.sms().sendLms(SmsRequest.lms()
    .subject("[중요] 서비스 점검 안내")
    .content("안녕하세요. 서비스 점검이 예정되어 있습니다.\n■ 일시: 2026-07-25 02:00 ~ 06:00")
    .contact(Contact.builder().contact("01012345678").build()));

// MMS
sendgo.sms().sendMms(SmsRequest.mms()
    .subject("[이벤트] 7월 특가")
    .content("이번 달 특가 상품을 확인하세요!")
    .contacts(List.of(
        Contact.builder().contact("01011111111").build(),
        Contact.builder().contact("01022222222").build()
    )));
```

---

## 프레임워크 통합

### Quarkus

```java
// src/main/java/org/acme/SendgoProducer.java
import io.sendgo.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SendgoProducer {

    @ConfigProperty(name = "sendgo.access-key")
    String accessKey;

    @ConfigProperty(name = "sendgo.secret-key")
    String secretKey;

    @ConfigProperty(name = "sendgo.kakao-sender-key")
    String kakaoKey;

    @Produces
    @ApplicationScoped
    public SendgoClient sendgoClient() {
        return new SendgoClient(SendgoConfig.builder()
            .accessKey(accessKey)
            .secretKey(secretKey)
            .kakaoSenderKey(kakaoKey)
            .apiVersion("v2")
            .build());
    }
}

// 서비스에서 주입받아 사용
@ApplicationScoped
public class NotificationService {
    @Inject SendgoClient sendgo;

    public void sendOrderConfirm(String phone, String orderNo) {
        sendgo.alimtalk().send(AlimtalkRequest.builder()
            .templateCode("ORDER_CONFIRM_001")
            .contact(Contact.builder().contact(phone).var1(orderNo).build())
            .build());
    }
}
```

### Micronaut

```java
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.sendgo.*;
import jakarta.inject.Singleton;

@Factory
public class SendgoFactory {

    @Value("${sendgo.access-key}")
    String accessKey;

    @Value("${sendgo.secret-key}")
    String secretKey;

    @Singleton
    public SendgoClient sendgoClient() {
        return new SendgoClient(SendgoConfig.builder()
            .accessKey(accessKey)
            .secretKey(secretKey)
            .apiVersion("v2")
            .build());
    }
}
```

### 순수 Java (싱글톤 패턴)

```java
public final class SendgoHolder {
    private static volatile SendgoClient instance;

    public static SendgoClient get() {
        if (instance == null) {
            synchronized (SendgoHolder.class) {
                if (instance == null) {
                    instance = new SendgoClient(SendgoConfig.builder()
                        .accessKey(System.getenv("SENDGO_ACCESS_KEY"))
                        .secretKey(System.getenv("SENDGO_SECRET_KEY"))
                        .kakaoSenderKey(System.getenv("SENDGO_KAKAO_KEY"))
                        .apiVersion("v2")
                        .build());
                }
            }
        }
        return instance;
    }
}

// 사용
SendgoHolder.get().alimtalk().send(AlimtalkRequest.builder()
    .templateCode("ORDER_CONFIRM_001")
    .contact(Contact.builder().contact("01012345678").var1("ORD-001").build())
    .build());
```

---

## 예외 처리

```java
import io.sendgo.exception.SendgoException;

try {
    sendgo.alimtalk().send(...);
} catch (SendgoException e) {
    System.err.printf("발송 실패: HTTP %d [%s]%n", e.getStatusCode(), e.getErrorCode());

    switch (e.getErrorCode()) {
        case "INVALID_ACCESS_KEY",
             "INVALID_SECRET_KEY"   -> alertOps("Sendgo 인증키를 확인하세요.");
        case "INVALID_TEMPLATE_CODE" -> log.warn("존재하지 않는 템플릿: {}", e.getMessage());
        case "PAYMENT_REQUIRED"      -> alertOps("Sendgo 크레딧이 부족합니다.");
        case "IP_NOT_ALLOWED"        -> alertOps("허용되지 않은 IP");
        default                      -> log.error("알 수 없는 오류", e);
    }
}
```

---

## 설정 옵션

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| `accessKey` | `String` | **필수** | — | Sendgo 액세스 키 |
| `secretKey` | `String` | **필수** | — | Sendgo 시크릿 키 |
| `kakaoSenderKey` | `String` | 선택 | `null` | 카카오 발신프로필 키 |
| `smsSenderKey` | `String` | 선택 | `null` | SMS 발신자 키 |
| `apiVersion` | `String` | 선택 | `"v2"` | API 버전 (`v1` \| `v2`) |
| `baseUrl` | `String` | 선택 | `"https://api.sendgo.io"` | API 기본 URL |

---

## 관련 패키지

| 언어/프레임워크 | 패키지 | GitHub |
|----------------|--------|--------|
| Spring Boot | `io.sendgo:sendgo-spring` | [spring](https://github.com/send-go/spring) |
| Node.js | `@sendgo/node` | [node](https://github.com/send-go/node) |
| Python | `sendgo-python` | [python](https://github.com/send-go/python) |
| PHP | `sendgo/php` | [php](https://github.com/send-go/php) |
| Go | `github.com/send-go/go` | [go](https://github.com/send-go/go) |
| 전체 목록 | — | [send-go GitHub 조직](https://github.com/send-go) |

---

## 라이선스

MIT License © 2026 [Sendgo](https://sendgo.io)

---

*키워드: 카카오 알림톡 Java, 카카오 친구톡 Java, SMS 발송 Java, 알림톡 SDK Maven, Java 카카오 API 연동, Sendgo Java SDK, Maven Central 알림 발송*
