package io.sendgo;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Sendgo Java SDK 메인 클라이언트.
 *
 * <pre>
 * SendgoClient sendgo = new SendgoClient(SendgoConfig.builder()
 *     .accessKey(System.getenv("SENDGO_ACCESS_KEY"))
 *     .secretKey(System.getenv("SENDGO_SECRET_KEY"))
 *     .kakaoSenderKey(System.getenv("SENDGO_KAKAO_KEY"))
 *     .smsSenderKey(System.getenv("SENDGO_SMS_KEY"))
 *     .apiVersion("v2")
 *     .build());
 *
 * sendgo.alimtalk().send(AlimtalkRequest.builder()
 *     .templateCode("ORDER_CONFIRM_001")
 *     .contact(Contact.builder().contact("01012345678").var1("ORD-001").build())
 *     .build());
 * </pre>
 */
public class SendgoClient {

    private final AlimtalkService   alimtalkService;
    private final FriendtalkService friendtalkService;
    private final BrandMessageService brandMessageService;
    private final ShortUrlService  shortUrlService;
    private final SmsService        smsService;

    // 관리 API (v2 전용) — 콘솔에서만 되던 등록·심사.
    private final KakaoSenderService        kakaoSenderService;
    private final NoticeTemplateService     noticeTemplateService;
    private final BrandTemplateService      brandTemplateService;
    private final SenderRegistrationService senderRegistrationService;
    private final MessageTemplateService    messageTemplateService;
    private final KakaoImageService         kakaoImageService;
    private final RejectedNumberService     rejectedNumberService;
    private final WebhookService            webhookService;

    public SendgoClient(SendgoConfig config) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        ObjectMapper mapper = new ObjectMapper();

        TokenManager      tokenManager = new TokenManager(config, httpClient, mapper);
        SendgoHttpClient  http         = new SendgoHttpClient(config, tokenManager, httpClient, mapper);

        this.alimtalkService   = new AlimtalkService(http, config);
        this.friendtalkService = new FriendtalkService(http, config);
        this.brandMessageService = new BrandMessageService(http, config);
        this.shortUrlService  = new ShortUrlService(http, config);
        this.smsService        = new SmsService(http, config);

        this.kakaoSenderService        = new KakaoSenderService(http, config);
        this.noticeTemplateService     = new NoticeTemplateService(http, config);
        this.brandTemplateService      = new BrandTemplateService(http, config);
        this.senderRegistrationService = new SenderRegistrationService(http, config);
        this.messageTemplateService    = new MessageTemplateService(http, config);
        this.kakaoImageService         = new KakaoImageService(http, config);
        this.rejectedNumberService     = new RejectedNumberService(http, config);
        this.webhookService            = new WebhookService(http, config);

        // 초기 토큰 발급
        tokenManager.getToken();
    }

    /** 카카오 알림톡 서비스 */
    public AlimtalkService alimtalk() { return alimtalkService; }

    /**
     * 카카오 친구톡 서비스.
     *
     * @deprecated 친구톡은 2025-12-31 종료. {@link #brandMessage()} 를 사용한다.
     */
    @Deprecated
    public FriendtalkService friendtalk() { return friendtalkService; }

    /** 카카오 브랜드메시지 — 친구톡의 후속 채널. v2 전용. */
    public BrandMessageService brandMessage() { return brandMessageService; }

    /** 짧은 URL — 링크 단축과 클릭 반응 분석. v2 전용. */
    public ShortUrlService shortUrl() { return shortUrlService; }

    /** SMS / LMS / MMS 서비스 */
    public SmsService sms() { return smsService; }

    // ------------------------------------------------------ 관리 API (v2 전용)
    // 발송과 달리 대부분 즉시 완료되지 않는다 — 등록 성공은 "접수됨"이지
    // "사용 가능"이 아니다. 결과는 웹훅으로 받는다.
    //
    // 사람이 개입하는 지점은 카카오 채널 인증번호 하나뿐이고, 그마저도
    // 여러분 화면에서 끝난다 — requestToken() 이 채널 관리자 휴대폰으로 SMS 를
    // 보내고, 사용자가 여러분 화면에 입력한 코드를 create() 가 받는다.
    // 휴대폰 발신번호는 PASS 대신 신분증 사본을 첨부해 접수하면 sendgo 가
    // 대신 심사한다. 어느 쪽도 sendgo.io 콘솔을 거치지 않는다.

    /** 카카오 발신프로필(채널) 등록·동기화. v2 전용, 기업 계정 전용. */
    public KakaoSenderService kakaoSenders() { return kakaoSenderService; }

    /** 알림톡 템플릿 등록·수정·검수 요청. v2 전용, 기업 계정 전용. */
    public NoticeTemplateService noticeTemplates() { return noticeTemplateService; }

    /** 브랜드메시지(구 친구톡) 템플릿 관리. v2 전용, 기업 계정 전용. */
    public BrandTemplateService brandTemplates() { return brandTemplateService; }

    /** 발신번호 등록·심사 접수. v2 전용. */
    public SenderRegistrationService senderRegistration() { return senderRegistrationService; }

    /** 문자 상용구 템플릿. v2 전용. */
    public MessageTemplateService messageTemplates() { return messageTemplateService; }

    /** 카카오 이미지 업로드 — 브랜드메시지 템플릿용 URL 발급. v2 전용, 기업 계정 전용. */
    public KakaoImageService kakaoImages() { return kakaoImageService; }

    /** 수신거부(080) 번호 조회. v2 전용. */
    public RejectedNumberService rejectedNumbers() { return rejectedNumberService; }

    /** 이벤트 웹훅 구독 — 등록·심사 결과를 밀어 받는다. v2 전용. */
    public WebhookService webhook() { return webhookService; }
}
