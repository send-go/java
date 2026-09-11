package io.sendgo;

import io.sendgo.model.AlimtalkRequest;
import io.sendgo.model.BrandMessageRequest;
import io.sendgo.model.BrandTemplateRequest;
import io.sendgo.model.Contact;
import io.sendgo.model.KakaoSenderCreateRequest;
import io.sendgo.model.MessageTemplateRequest;
import io.sendgo.model.MultipartFile;
import io.sendgo.model.NoticeTemplateRequest;
import io.sendgo.model.SenderRegistrationRequest;
import io.sendgo.model.ShortUrlRequest;
import io.sendgo.model.WebhookSubscriptionRequest;
import io.sendgo.model.SmsRequest;
import java.io.IOException;
import java.nio.file.Path;
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

    /**
     * 1.3.0 관리 API — 등록 · 심사.
     *
     * <p>인증번호는 채널 관리자 휴대폰으로 가므로 여기서는 발송 트리거까지만
     * 쓴다 — 코드 제출은 여러분 화면에서 받아 create() 로 넘긴다.
     */
    void managementApi(SendgoClient sendgo) throws IOException {
        // --- 카카오 채널 등록 (2단계) ---
        sendgo.kakaoSenders().requestToken("@my-channel", "01012345678");

        Map<String, Object> created = sendgo.kakaoSenders().create(
                KakaoSenderCreateRequest.builder()
                        .token("123456")
                        .yellowId("@my-channel")
                        .phoneNumber("01012345678")
                        .categoryCode("001001")
                        .build());

        Map<?, ?> sender = (Map<?, ?>) ((Map<?, ?>) created.get("data")).get("sender");
        String kakaoSenderKey = sender.get("kakaoSenderKey").toString();

        sendgo.kakaoSenders().categories();
        sendgo.kakaoSenders().list();
        sendgo.kakaoSenders().sync();
        sendgo.kakaoSenders().sync(kakaoSenderKey);
        sendgo.kakaoSenders().applyBrandMessageTargeting(kakaoSenderKey, "N");
        sendgo.kakaoSenders().uploadBrandMessageEvidence(kakaoSenderKey,
                MultipartFile.of("evidence", Path.of("consent.png"), "image/png"));

        // --- 알림톡 템플릿 등록 → 검수 요청 → 폴링 ---
        NoticeTemplateRequest request = NoticeTemplateRequest.builder()
                .kakaoSenderKey(kakaoSenderKey)
                .templateName("주문 접수 안내")
                .templateContent("#{name}님, 주문 #{orderNo}이 접수되었습니다.")
                .templateMessageType("BA")
                .templateEmphasizeType("NONE")
                .categoryCode("001001")
                .messagePurpose("order_delivery")
                .legalBasis("transaction")
                .benefitOrigin("none")
                .expiryType("none")
                .button(Map.of("name", "주문 조회", "linkType", "WL",
                               "linkMo", "https://example.com/orders"))
                .build();

        Map<String, Object> template = sendgo.noticeTemplates().create(request);
        String templateCode = ((Map<?, ?>) ((Map<?, ?>) template.get("data")).get("template"))
                .get("templateCode").toString();

        sendgo.noticeTemplates().requestInspection(templateCode);
        sendgo.noticeTemplates().requestInspection(templateCode, "주문 확인 화면 첨부",
                List.of(MultipartFile.of("attachment", Path.of("proof.png"), "image/png")));
        sendgo.noticeTemplates().sync(templateCode);
        sendgo.noticeTemplates().list(kakaoSenderKey, "APR", null, null);
        sendgo.noticeTemplates().list();
        sendgo.noticeTemplates().show(templateCode);
        sendgo.noticeTemplates().update(templateCode, request);
        sendgo.noticeTemplates().cancelInspection(templateCode);
        sendgo.noticeTemplates().cancelApproval(templateCode);
        sendgo.noticeTemplates().release(templateCode);
        sendgo.noticeTemplates().delete(templateCode);
        sendgo.noticeTemplates().categories();

        // 이미지 템플릿 (multipart)
        sendgo.noticeTemplates().createWithImage(request,
                MultipartFile.of("image", Path.of("banner.jpg"), "image/jpeg"));

        // --- 브랜드메시지 템플릿 ---
        sendgo.brandTemplates().create(BrandTemplateRequest.builder()
                .kakaoSenderKey(kakaoSenderKey)
                .templateName("여름 세일 안내")
                .templateType("FI")
                .templateContent("여름 세일이 시작되었습니다.")
                .imageUrl("https://mud-kage.kakao.com/example.jpg")
                .build());

        sendgo.brandTemplates().list(kakaoSenderKey, null, null);
        sendgo.brandTemplates().list();
        sendgo.brandTemplates().sync("KFT-0001");
        sendgo.brandTemplates().importFromSender(kakaoSenderKey);
        sendgo.brandTemplates().delete("KFT-0001");

        // --- 발신번호 등록 신청 ---
        sendgo.senderRegistration().numberTypes();
        sendgo.senderRegistration().validate("02-1234-5678", "team_main");

        sendgo.senderRegistration().create(
                SenderRegistrationRequest.builder()
                        .senderAlias("고객센터 대표번호")
                        .senderNumberType("team_main")
                        .phoneE164("02-1234-5678")
                        .build(),
                List.of(MultipartFile.of("csuCertificate", Path.of("csu.pdf"), "application/pdf")));

        sendgo.senderRegistration().list();
        sendgo.senderRegistration().update("sender-key", "새 이름");
        sendgo.senderRegistration().delete("sender-key");

        // --- 문자 상용구 템플릿 ---
        sendgo.messageTemplates().create(MessageTemplateRequest.builder()
                .messageTranType("LMS")
                .messageTranSubject("주문 안내")
                .messageTranMsg("주문이 접수되었습니다.")
                .build());

        sendgo.messageTemplates().list("LMS", null, null);
        sendgo.messageTemplates().list();

        // --- 카카오 이미지 업로드 ---
        sendgo.kakaoImages().types();
        sendgo.kakaoImages().upload("default",
                MultipartFile.of("image", Path.of("banner.jpg"), "image/jpeg"));
        sendgo.kakaoImages().uploadMany("carousel_feed", List.of(
                MultipartFile.of("images", Path.of("slide1.jpg"), "image/jpeg"),
                MultipartFile.of("images", Path.of("slide2.jpg"), "image/jpeg")));

        // --- 수신거부 증분 동기화 ---
        sendgo.rejectedNumbers().list("2026-09-01", null, 500);
        sendgo.rejectedNumbers().list();

        // --- 웹훅 구독 ---
        sendgo.webhook().subscribe(WebhookSubscriptionRequest.builder()
                .url("https://reseller.example.com/hooks/sendgo")
                .event(WebhookSubscriptionRequest.EVENT_SENDER_STATUS)
                .build());
        sendgo.webhook().show();
        sendgo.webhook().test();
        sendgo.webhook().unsubscribe();
    }

    /** 수신 측 서명 검증 — 받은 바이트 그대로 넘긴다. */
    boolean verifyWebhook(byte[] rawBody, String signature) {
        return WebhookService.verifySignature(rawBody, signature, System.getenv("SENDGO_WEBHOOK_SECRET"));
    }
}
