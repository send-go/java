package io.sendgo;

import io.sendgo.model.AlimtalkRequest;
import io.sendgo.model.BrandMessageRequest;
import io.sendgo.model.Contact;
import io.sendgo.model.ShortUrlRequest;
import io.sendgo.model.SmsRequest;
import java.util.List;
import java.util.Map;

/**
 * 문서에 실린 예제가 실제로 컴파일되는지 확인하는 용도.
 * 실행하지 않는다 — 컴파일 자체가 검증이다.
 */
public class DocExamplesCompileTest {

    void alimtalk(SendgoClient sendgo) {
        // 다건: contact() 는 호출할 때마다 누적된다(1.1.0에서 수정됨).
        sendgo.alimtalk().send(AlimtalkRequest.builder()
                .templateCode("ORDER_CONFIRM_001")
                .contact(Contact.builder().contact("01011111111").var1("ORD-001").var2("29,000").build())
                .contact(Contact.builder().contact("01022222222").var1("ORD-002").var8("x").build())
                .build());

        // 전체를 한 번에 지정
        sendgo.alimtalk().send(AlimtalkRequest.builder()
                .templateCode("ORDER_CONFIRM_001")
                .contacts(List.of(Contact.builder().contact("01012345678").build()))
                .build());

        // 예약 + SMS 대체
        sendgo.alimtalk().send(AlimtalkRequest.builder()
                .templateCode("PROMO")
                .scheduleType("SCHEDULED")
                .at("2026-07-28 09:00:00")
                .replaceSms("Y")
                .smsSubject("[shipping]")
                .smsContent("shipped")
                .contact(Contact.builder().contact("01012345678").build())
                .build());

        // 임의 명명 변수
        Contact.builder().contact("01012345678").variable("title", "x").variable("amount", "y").build();
    }

    void brandMessage(SendgoClient sendgo) {
        Map<String, Object> sent = sendgo.brandMessage().send(BrandMessageRequest.builder()
                .targeting("M")
                .messageType("FL")
                .friendTemplateUuid("9cd5460b-6458-4edc-9b11-c26d3013c340")
                .contact(Contact.builder().contact("01012345678").var1("29,000").build())
                .build());

        Map<String, Object> accepted = sendgo.brandMessage().broadcast(BrandMessageRequest.builder()
                .messageType("FW")
                .friendTemplateUuid("9cd5460b-6458-4edc-9b11-c26d3013c340")
                .build());

        sendgo.brandMessage().campaign("uuid");
        sendgo.brandMessage().campaigns("2026-08-01", null, 10);
        sendgo.brandMessage().campaigns();
    }

    void sms(SendgoClient sendgo) {
        // SmsRequest 는 builder() 가 없고 sms()/lms()/mms() 정적 팩토리를 쓴다.
        sendgo.sms().sendSms(SmsRequest.sms()
                .content("code 123456")
                .contact(Contact.builder().contact("01012345678").build()));

        sendgo.sms().sendLms(SmsRequest.lms()
                .subject("[notice]")
                .content("long")
                .contact(Contact.builder().contact("01012345678").build()));

        sendgo.sms().sendMms(SmsRequest.mms()
                .subject("[event]")
                .content("deals")
                .contact(Contact.builder().contact("01012345678").build()));
    }

    void shortUrl(SendgoClient sendgo) {
        Map<String, Object> created = sendgo.shortUrl().create(ShortUrlRequest.builder()
                .targetUrl("https://example.com/promotions/summer-sale")
                .title("Summer sale landing")
                .expiresAt("2026-09-30 23:59:59")
                .forceNew(false)
                .build());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) created.get("data");
        String code = (String) data.get("code");

        sendgo.shortUrl().stats(code, "2026-08-01", null);
        sendgo.shortUrl().stats(code);
        sendgo.shortUrl().list(null, null, 10);
        sendgo.shortUrl().list();
        sendgo.shortUrl().show(code);
        sendgo.shortUrl().deactivate(code);
    }

    void config() {
        new SendgoClient(SendgoConfig.builder()
                .accessKey("a")
                .secretKey("b")
                .kakaoSenderKey("c")
                .smsSenderKey("d")
                .apiVersion("v2")
                .build());
    }
}
