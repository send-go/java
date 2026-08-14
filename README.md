# sendgo-java

> **Java에서 카카오 알림톡, 브랜드메시지, SMS를 가장 쉽게 발송하는 순수 Java SDK**

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
    <version>1.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.sendgo:sendgo-java:1.1.0'
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

> ⚠️ **Deprecated — 친구톡은 카카오 정책에 따라 2025-12-31 종료되었습니다.**
> 2026-01-01 부터 친구톡 발송 요청은 카카오 측에서 **브랜드메시지(자유형)** 로 자동 대체 발송됩니다.
> 호출은 계속 성공하며, 자유 본문 타입(`FT`/`FI`/`FW`)을 개별 수신자에게 보내는 경로는
> 현재 이것뿐이므로 기존 코드를 당장 바꿀 필요는 없습니다.
>
> 다음의 경우에는 **브랜드메시지**를 사용하세요.
> - 템플릿 기반 리치 타입 (`FL`/`FC`/`FM`/`FP`/`FA`)
> - 채널 친구가 **아닌** 수신자 (`targeting` = `N` / `I`)
> - 수신 동의한 전체 채널 친구 동보 (`targeting` = `F`)
>
> 메시지 타입은 1:1 대응되며 변환은 서버가 처리합니다 — `FT`→`BT`, `FI`→`BI`, `FW`→`BW`,
> `FL`→`BL`, `FC`→`BC`, `FM`→`BM`, `FP`→`BP`, `FA`→`BA`.

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

## 브랜드메시지 사용법

브랜드메시지는 친구톡의 후속 채널입니다. 메시지 타입이 친구톡과 1:1 대응되며
(`FT`→`BT`, `FI`→`BI`, `FW`→`BW`, `FL`→`BL`, `FC`→`BC`, `FM`→`BM`, `FP`→`BP`, `FA`→`BA`),
요청에는 **친구톡 코드를 그대로** 넘기고 변환은 서버가 처리합니다.

친구톡과 달리 다음이 가능합니다.

- 채널 친구가 **아닌** 수신자에게 발송 (`targeting: N`)
- 수신 동의한 **전체 채널 친구 동보** 발송 (`targeting: F`, 수신자 목록 불필요)
- 리스트·캐러셀·커머스·동영상 등 **템플릿 기반 리치 메시지**

> v2 전용입니다. 자유 본문 타입(`FT`/`FI`/`FW`)을 개별 수신자에게 보낼 때는 여전히 친구톡 API 를 쓰세요 — 이 엔드포인트는 그 조합에 `NOT_A_BRAND_MESSAGE` 를 반환합니다. 친구톡 요청은 카카오 측에서 브랜드메시지(자유형)로 대체 발송됩니다.

```java
import io.sendgo.model.BrandMessageRequest;
import io.sendgo.model.Contact;

// 단건 발송 — 채널 친구 대상
sendgo.brandMessage().send(BrandMessageRequest.builder()
        .targeting("M")
        .messageType("FL")
        .friendTemplateUuid("9cd5460b-6458-4edc-9b11-c26d3013c340")
        .contact(Contact.builder().contact("01012345678").var1("29,000원").build())
        .build());

// 동보 발송 — 수신 동의한 전체 채널 친구 (contacts 불필요)
sendgo.brandMessage().broadcast(BrandMessageRequest.builder()
        .messageType("FW")
        .friendTemplateUuid("9cd5460b-6458-4edc-9b11-c26d3013c340")
        .build());

// 캠페인 조회
var list = sendgo.brandMessage().campaigns(null, null, 10);
var one  = sendgo.brandMessage().campaign("1f0a6d0e-6b3b-4f0f-9b2f-2f6f6a1b7c11");
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
| `baseUrl` | `String` | 선택 | `"https://sendgo.io"` | API 기본 URL |

---

## 1.1.0 변경 사항

- **`contact(...)` 가 누적된다.** 이전에는 `contacts = List.of(v)` 로 리스트를
  통째로 교체했기 때문에 `.contact(a).contact(b)` 로 다건을 넣으면 `a` 가 조용히
  사라졌다. 이제 호출할 때마다 추가된다. 한 번만 호출하던 기존 코드는 동작이 같다.
  전체를 한 번에 지정하려면 여전히 `contacts(List.of(...))` 를 쓴다.
- **`sendSms` / `sendLms` / `sendMms` 가 messageType 을 강제한다.** 이전에는
  세 메서드가 모두 요청을 그대로 넘겼기 때문에 `sendLms(SmsRequest.sms()...)` 가
  SMS 로 발송됐다. 요청 자체의 타입을 그대로 쓰려면 `send(...)` 를 사용한다.
- **`Contact` 에 `var6` ~ `var8` 이 추가됐다.** 다른 언어 SDK(Node/Python/.NET/Go/Flutter)와
  동일한 범위를 지원하도록 맞췄다. 그 이상은 `variable("name", "value")` 를 사용한다.

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

## 짧은 URL

짧은 URL 은 메시지 본문의 링크를 줄이고, 그 링크가 실제로 눌렸는지 집계합니다.
문자는 바이트 수가 요금과 직결되므로 링크를 줄이면 그만큼 본문을 더 쓸 수 있습니다.

같은 원본 URL 을 다시 줄이면 **기존 링크가 그대로 반환**됩니다. 캠페인별로 반응을
따로 집계하려면 `forceNew` 로 새 코드를 만드세요.

`deactivate` 는 링크를 삭제하지 않고 리다이렉트만 중지합니다. 이미 발송한 메시지의
링크를 무효화할 때 쓰며, 누적 통계는 남고 이후 접속은 `410 Gone` 이 됩니다.

```java
// 짧은 URL 생성 (v2 전용)
Map<String, Object> created = sendgo.shortUrl().create(ShortUrlRequest.builder()
        .targetUrl("https://example.com/promotions/summer-sale")
        .title("여름 세일 랜딩")
        .build());

@SuppressWarnings("unchecked")
Map<String, Object> data = (Map<String, Object>) created.get("data");
String code = (String) data.get("code");

// 반응 통계 — 일별 추이 + 디바이스/유입경로/국가별 분해
Map<String, Object> stats = sendgo.shortUrl().stats(code, "2026-08-01", null);

sendgo.shortUrl().list(null, null, 10);
sendgo.shortUrl().show(code);
sendgo.shortUrl().deactivate(code);   // 리다이렉트만 중지, 통계는 남는다
```

`stats` 는 일별 추이(`daily`)와 디바이스(`byDevice`)·유입경로(`byReferer`)·국가(`byCountry`)별
분해를 반환합니다. 일별 추이는 사전 집계 표에서 읽으므로 클릭이 많아도 응답 시간이 일정합니다.

## 변경 사항

### 1.2.0 (2026-08-14)

- **친구톡 Deprecated 표기** — 친구톡은 카카오 정책에 따라 2025-12-31 종료되었고,
  2026-01-01 부터 발송 요청이 브랜드메시지(자유형)로 자동 대체 발송됩니다.
  관련 API 에 각 언어의 표준 deprecation 표기를 달았습니다.
- 자유 본문 타입(`FT`/`FI`/`FW`)의 개별 발송 경로는 아직 친구톡 API 뿐이라는 점을
  문서에 명시했습니다 — 브랜드메시지 API 는 그 조합에 `NOT_A_BRAND_MESSAGE` 를 반환합니다.
- 브랜드메시지 전환 안내와 메시지 타입 1:1 대응표를 README 에 추가했습니다.

## 라이선스

MIT License © 2026 [Sendgo](https://sendgo.io)

---

*키워드: 카카오 알림톡 Java, 카카오 친구톡 Java, SMS 발송 Java, 알림톡 SDK Maven, Java 카카오 API 연동, Sendgo Java SDK, Maven Central 알림 발송*
