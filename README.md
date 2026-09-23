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

## 관리 API — 채널·템플릿·발신번호 등록 (v2 전용)

발송은 처음부터 API였지만 **등록과 심사는 콘솔에서만** 되던 것들이 있었습니다.
1.3.0 부터 그 작업도 코드로 처리합니다.

| 서비스 | 하는 일 | 계정 |
| --- | --- | --- |
| `sendgo.kakaoSenders()` | 카카오 채널 인증·등록·동기화, 브랜드메시지 M/N 신청 | 기업 |
| `sendgo.noticeTemplates()` | 알림톡 템플릿 CRUD, 검수 요청·취소, 승인 취소, 휴면 해제 | 기업 |
| `sendgo.brandTemplates()` | 브랜드메시지(구 친구톡) 템플릿 CRUD, 동기화, 가져오기 | 기업 |
| `sendgo.senderRegistration()` | 발신번호 등록 신청, 중복 확인, 유형 안내 | 개인·기업 |
| `sendgo.messageTemplates()` | 문자 상용구 템플릿 CRUD | 개인·기업 |
| `sendgo.kakaoImages()` | 카카오 이미지 업로드 — 템플릿용 URL 발급 | 기업 |
| `sendgo.rejectedNumbers()` | 수신거부(080) 번호 조회 | 개인·기업 |
| `sendgo.webhook()` | 이벤트 웹훅 구독 — 심사 결과 수신 | 개인·기업 |

> **sendgo.io 콘솔에 들어올 일이 없습니다.** 고객의 채널·발신번호·템플릿을
> 여러분 화면만으로 끝까지 처리할 수 있습니다. 휴대폰 발신번호는 콘솔의 PASS
> 본인인증 대신 **신분증 사본(`identityDocument`)을 받아 sendgo 운영자가 대신
> 심사**합니다.
>
> 사람이 개입하는 지점은 **카카오 채널 인증번호 하나**뿐이고, 그마저도
> 여러분 화면에서 입력받으면 됩니다 — 카카오가 관리자 휴대폰으로 직접 보내는
> 확인이라 없앨 수 없습니다.
>
> 심사가 붙는 것들은 **비동기**입니다. 등록 호출이 성공했다는 건 "접수됐다"는
> 뜻이지 "쓸 수 있다"는 뜻이 아닙니다 — 웹훅을 구독해 결과를 받으세요.

### 카카오 채널 등록

```java
// 1단계 — 카카오가 관리자 휴대폰으로 인증번호를 SMS 발송한다 (응답에 번호는 없다)
sendgo.kakaoSenders().requestToken("@my-channel", "01012345678");

// 2단계 — 사람이 받은 인증번호로 발신프로필 생성
Map<String, Object> created = sendgo.kakaoSenders().create(
        KakaoSenderCreateRequest.builder()
                .token("123456")
                .yellowId("@my-channel")
                .phoneNumber("01012345678")
                .categoryCode("001001")   // categories() 로 조회
                .build());

sendgo.kakaoSenders().categories();
sendgo.kakaoSenders().list();
sendgo.kakaoSenders().sync();                   // 전체 상태 동기화 (하루 한 번 권장)
sendgo.kakaoSenders().sync(kakaoSenderKey);     // 단건
```

채널이 카카오 쪽에서 차단되면 발송이 조용히 실패하기 시작합니다. `sync()` 를
주기적으로 돌리고 `block: true` 인 채널을 감시하세요.

### 알림톡 템플릿 등록과 검수

```java
Map<String, Object> created = sendgo.noticeTemplates().create(
        NoticeTemplateRequest.builder()
                .kakaoSenderKey(kakaoSenderKey)
                .templateName("주문 접수 안내")
                .templateContent("#{name}님, 주문 #{orderNo}이 접수되었습니다.")
                .templateMessageType("BA")      // BA 기본형 / EX 부가정보형 / AD 채널추가형 / MI 복합형
                .templateEmphasizeType("NONE")  // NONE / TEXT / ITEM_LIST / IMAGE
                .categoryCode("001001")
                // sendgo 자체 정책 게이트 — 카카오 심사와 별개다
                .messagePurpose("order_delivery")
                .legalBasis("transaction")
                .benefitOrigin("none")
                .expiryType("none")
                .button(Map.of("name", "주문 조회", "linkType", "WL",
                               "linkMo", "https://example.com/orders"))
                .build());

String templateCode = ((Map<?, ?>) ((Map<?, ?>) created.get("data")).get("template"))
        .get("templateCode").toString();

// 검수 요청 — 증빙이 필요하면 파일도 붙인다 (첨부가 있으면 comment 필수)
sendgo.noticeTemplates().requestInspection(templateCode);
sendgo.noticeTemplates().requestInspection(templateCode, "주문 확인 화면 첨부",
        List.of(MultipartFile.of("attachment", Path.of("proof.png"), "image/png")));

// 결과는 비동기다. 웹훅이 없으므로 폴링한다
Map<String, Object> synced = sendgo.noticeTemplates().sync(templateCode);
// data.template.inspectionStatus — REG → REQ → APR / REJ
```

`optInReviewConfirmed` · `ctaClearConfirmed` · `policyConfirmed` 는 빌더 기본값이
`true` 지만, **내용을 실제로 검토한 뒤에** 그대로 두어야 합니다 — 이 값은 법적
확인의 기록입니다.

정책 필드 조합이 본문과 어긋나면 저장 단계에서 `POLICY_VALIDATION_FAILED` 로
막힙니다. 여기서 걸리는 문안은 **카카오 심사에서도 거의 반려**되므로,
며칠 기다렸다 반려당하는 것보다 즉시 아는 편이 낫습니다.

```java
sendgo.noticeTemplates().list(kakaoSenderKey, "APR", null, null);
sendgo.noticeTemplates().show(templateCode);
sendgo.noticeTemplates().update(templateCode, request);   // 본문이 바뀌면 재검수 필요
sendgo.noticeTemplates().cancelInspection(templateCode);
sendgo.noticeTemplates().cancelApproval(templateCode);
sendgo.noticeTemplates().release(templateCode);           // 휴면 해제
sendgo.noticeTemplates().delete(templateCode);            // sendgo 목록에서만 삭제된다
sendgo.noticeTemplates().categories();
```

이미지 템플릿은 multipart 로 나갑니다.

```java
sendgo.noticeTemplates().createWithImage(
        request,
        MultipartFile.of("image", Path.of("banner.jpg"), "image/jpeg"));
```

> **삭제 동작이 채널마다 다릅니다.** 알림톡 템플릿은 카카오에 삭제 API 가 없어
> sendgo 목록에서만 빠지고 동기화하면 되살아납니다. 브랜드메시지 템플릿은
> 카카오 쪽에서도 실제로 삭제됩니다.

### 브랜드메시지 템플릿

```java
sendgo.brandTemplates().create(BrandTemplateRequest.builder()
        .kakaoSenderKey(kakaoSenderKey)
        .templateName("여름 세일 안내")
        .templateType("FI")   // FT/FI/FW/FL/FC/FM/FP/FA — 서버가 chatBubbleType 으로 변환
        .templateContent("여름 세일이 시작되었습니다.")
        .imageUrl("https://mud-kage.kakao.com/....jpg")
        .build());

sendgo.brandTemplates().list(kakaoSenderKey, null, null);
sendgo.brandTemplates().sync(templateCode);
sendgo.brandTemplates().importFromSender(kakaoSenderKey);  // 카카오에 있는 템플릿 가져오기
sendgo.brandTemplates().delete(templateCode);              // 카카오에서도 삭제된다
```

응답의 `containsVariables` 가 true 면 동보 발송(`targeting=F`)에는 쓸 수 없습니다.

### 발신번호 등록 신청

```java
// 계정 종류에 맞는 유형과 유형별 필수 서류
sendgo.senderRegistration().numberTypes();

// 형식·중복 미리 확인
Map<String, Object> check = sendgo.senderRegistration().validate("02-1234-5678", "team_main");

Map<String, Object> created = sendgo.senderRegistration().create(
        SenderRegistrationRequest.builder()
                .senderAlias("고객센터 대표번호")
                .senderNumberType("team_main")   // personal_other / team_main / team_other_company
                .phoneE164("02-1234-5678")
                // check 의 duplicationReasonRequired 가 true 면 필수
                // .duplicationReason("부서별 분리 운영")
                .build(),
        List.of(MultipartFile.of("csuCertificate", Path.of("csu.pdf"), "application/pdf")));

// data.sender.status == "PENDING" — 운영자 승인 후 SUCCESS

sendgo.senderRegistration().list();
sendgo.senderRegistration().update(senderKey, "새 이름");
sendgo.senderRegistration().delete(senderKey);
```

**휴대폰 유형도 API 로 접수할 수 있습니다.** 콘솔의 PASS 본인인증 대신
신분증 사본(`identityDocument`)을 첨부하면 sendgo 운영자가 직접 확인합니다.
이 경로로 접수된 건은 응답의 `identityVerificationMethod` 가 `document` 이고
**자동 승인되지 않습니다** — 운영자 확인 전까지 `PENDING` 입니다.

유형별 필수 서류는 `numberTypes()` 응답의 `requiredDocuments` 로 확인하세요.
반려되면 `rejectionReason` 에 사유가 담깁니다.

### 문자 템플릿

```java
sendgo.messageTemplates().create(MessageTemplateRequest.builder()
        .messageTranType("LMS")
        .messageTranSubject("주문 안내")   // LMS·MMS 는 필수
        .messageTranMsg("주문이 접수되었습니다.")
        .build());

sendgo.messageTemplates().list("LMS", null, null);
sendgo.messageTemplates().update(templateKey, request);
sendgo.messageTemplates().delete(templateKey);
```

### 이벤트 웹훅 — 심사 결과를 밀어 받기

```java
Map<String, Object> created = sendgo.webhook().subscribe(
        WebhookSubscriptionRequest.builder()
                .url("https://reseller.example.com/hooks/sendgo")
                .build());

// 시크릿은 이 응답에서 한 번만 나온다. 즉시 저장한다.

sendgo.webhook().show();          // 구독 설정 + 마지막 전송 결과
sendgo.webhook().test();          // 배선 확인
sendgo.webhook().unsubscribe();
```

받는 쪽에서는 **원본 바이트**로 서명을 검증합니다.

```java
@PostMapping("/hooks/sendgo")
ResponseEntity<Void> receive(@RequestBody byte[] rawBody,
                             @RequestHeader("X-Sendgo-Signature") String signature) {
    if (!WebhookService.verifySignature(rawBody, signature, secret)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // 처리는 비동기로. 여기서 오래 끌면 재시도가 쌓인다.
    events.publish(rawBody);

    return ResponseEntity.noContent().build();
}
```

이벤트 목록은 `WebhookSubscriptionRequest.EVENTS` 로 확인할 수 있습니다.

### 카카오 이미지 업로드

브랜드메시지 템플릿의 `imageUrl` 은 **카카오가 호스팅하는 URL** 이어야 합니다.

```java
Map<String, Object> uploaded = sendgo.kakaoImages().upload("default",
        MultipartFile.of("image", Path.of("banner.jpg"), "image/jpeg"));

sendgo.kakaoImages().uploadMany("carousel_feed", slides);
sendgo.kakaoImages().types();   // 유형별 필드·최대 개수
```

### 수신거부(080) 동기화

```java
// 증분만 가져간다. 하루 한 번이면 충분하다.
sendgo.rejectedNumbers().list("2026-09-01", null, 500);
```

---

## 변경 사항

### 1.3.0 (2026-09-11)

- **관리 API 추가** — 콘솔에서만 되던 등록·심사를 코드로 처리합니다.
  `sendgo.kakaoSenders()`(채널 인증·등록·동기화, 브랜드메시지 M/N 신청),
  `sendgo.noticeTemplates()`(알림톡 템플릿 CRUD·검수 요청·승인 취소·휴면 해제),
  `sendgo.brandTemplates()`(브랜드메시지 템플릿 CRUD·동기화·가져오기),
  `sendgo.senderRegistration()`(발신번호 등록 신청·중복 확인·유형 안내),
  `sendgo.messageTemplates()`(문자 상용구 템플릿 CRUD).
- 요청 모델 추가 — `KakaoSenderCreateRequest`, `NoticeTemplateRequest`,
  `BrandTemplateRequest`, `SenderRegistrationRequest`, `MessageTemplateRequest`,
  그리고 첨부용 `MultipartFile`.
- `SendgoHttpClient` 에 `put`·`patch`·`postMultipart` 를 추가했습니다.
  서류 첨부와 이미지 템플릿은 JSON 으로 보낼 수 없습니다.
- **휴대폰 발신번호도 API 로 접수됩니다.** 콘솔의 PASS 본인인증 대신
  `identityDocument`(신분증 사본)를 첨부하면 sendgo 운영자가 확인합니다.
  이 경로는 자동 승인되지 않고 항상 `PENDING` 으로 시작합니다.
- **이벤트 웹훅** 추가 — 발신번호 승인, 알림톡 검수 결과, 채널 차단,
  브랜드메시지 타겟팅 결과를 구독해 받습니다. 서명은 받은 원본 바이트로
  검증합니다(SDK 에 검증 헬퍼 포함).
- **카카오 이미지 업로드** 추가 — 브랜드메시지 템플릿의 `imageUrl` 은 카카오가
  호스팅하는 URL 이어야 하는데, 그 URL 을 얻는 길이 콘솔에만 있었습니다.
- **수신거부(080) 조회** 추가 — 자기 DB 의 수신 상태를 맞출 수 있습니다.

### 1.2.1 (2026-08-14)

- 레지스트리 목록에 노출되는 패키지 설명에서 친구톡을 브랜드메시지로 교체했습니다.
  npm/PyPI/Packagist/Maven/NuGet/RubyGems 검색 결과에 그대로 찍히는 문자열이라
  종료된 채널을 계속 홍보하고 있었습니다.
- 검색 키워드에 `brand-message` 를 추가했습니다 (`friendtalk` 은 유입 검색어라 유지).

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

## 계정·조직·API 키 관리 (1.5.0)

발송용 `accessKey`/`secretKey`가 없는 단계에서 사용하는 **별도 계정 클라이언트**입니다.
콘솔에서 발급받은 에이전트 토큰(`SENDGO_AGENT_TOKEN`)으로 `/api/v2/account`를 호출합니다.
계정 조회에는 `account:read`, 키·허용 IP 변경에는 `keys:write` 권한이 필요합니다.
토큰 만료나 권한 부족(401/403)은 그대로 예외로 반환하며 자동 갱신·재시도하지 않습니다.

조직 선택은 서버에 저장되는 **사용자 계정의 현재 조직**을 바꿉니다. 같은 사용자로
여러 조직의 설정을 동시에 변경하지 마세요. 개인 계정으로 돌아가려면 조직 ID에
`null`(Python `None`, Ruby `nil`, Go `nil`) 또는 `personal`을 전달합니다.
키 발급 응답의 `data.apiKey.secretKey`는 한 번만 반환되므로 서버의 비밀 저장소에 보관하세요.
허용 IP가 하나라도 등록되면 목록 밖의 IP는 차단됩니다.
에이전트 토큰과 키는 브라우저·모바일 앱에 포함하거나 응답·로그에 출력하지 않습니다.

```java
AccountClient account = new AccountClient(System.getenv("SENDGO_AGENT_TOKEN"));
Map<String, Object> result = account.me();
account.selectOrganization("team-uuid");
Map<String, Object> issued = account.createApiKey(Map.of("name", "서버 연동"));
```

지원 메서드: `me`, `organizations`, `selectOrganization`, `apiKeys`, `createApiKey`, `apiKey`, `updateApiKey`, `deleteApiKey`, `issueToken`, `allowedIps`, `addAllowedIp`, `deleteAllowedIp`.

키 생성 인자는 `name`, 선택적 `ipAddresses: [{ip, description}]`이며, 허용 IP 추가 인자는 `ip`, 선택적 `description`입니다. 키·IP 식별자는 응답의 `id`(UUID)를 사용합니다.

## 템플릿 폴더 (1.5.0)

기업 계정의 발송용 API 키와 `apiVersion=v2` 설정으로 사용하는 서버 전용 API입니다.
폴더는 알림톡·브랜드메시지가 공유하며, 목록의 `templateType`은 `notice` 또는 `brand`입니다.
목록은 `data.folders` 트리와 `total`, `uncategorised` 개수를 반환합니다.
`templateCount`는 하위 폴더를 제외한 해당 폴더의 템플릿 수입니다.

- 생성: `name`, 선택 `parentUuid`. 최대 5단계이며 같은 부모 아래 이름 중복은 409입니다.
- 이동: 동일 발신프로필의 `templateCodes` 1~100개. `folderUuid`는 필수이며 `null`이면 미분류로 이동합니다.
- 템플릿 목록: `folderUuid=none`은 미분류, UUID는 해당 폴더, 생략은 전체입니다.
- 템플릿 등록: 선택 필드 `folderUuid`로 폴더를 지정합니다. 기존 템플릿 수정 API 대신 폴더 이동 API를 사용하세요.

승인되지 않은 키의 `403 ACCESS_KEY_NOT_APPROVED`는 토큰 재발급·재시도 없이 반환합니다.
계정 API의 `autoApprove`는 서버 설정의 실제 승인 정책을 나타냅니다.

```java
client.templateFolders().list("notice", null);
client.templateFolders().create("주문 안내", null);
client.templateFolders().assign("notice", kakaoSenderKey, List.of("ORDER_001"), null);
client.noticeTemplates().list(null, null, null, null, "none");
// 템플릿 생성 빌더의 .folderUuid(folderUuid)로 폴더를 지정합니다.
```
