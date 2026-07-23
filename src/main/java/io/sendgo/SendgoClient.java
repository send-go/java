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
    private final SmsService        smsService;

    public SendgoClient(SendgoConfig config) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        ObjectMapper mapper = new ObjectMapper();

        TokenManager      tokenManager = new TokenManager(config, httpClient, mapper);
        SendgoHttpClient  http         = new SendgoHttpClient(config, tokenManager, httpClient, mapper);

        this.alimtalkService   = new AlimtalkService(http, config);
        this.friendtalkService = new FriendtalkService(http, config);
        this.smsService        = new SmsService(http, config);

        // 초기 토큰 발급
        tokenManager.getToken();
    }

    /** 카카오 알림톡 서비스 */
    public AlimtalkService alimtalk() { return alimtalkService; }

    /** 카카오 친구톡 서비스 */
    public FriendtalkService friendtalk() { return friendtalkService; }

    /** SMS / LMS / MMS 서비스 */
    public SmsService sms() { return smsService; }
}
