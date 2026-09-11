package io.sendgo;

import io.sendgo.model.WebhookSubscriptionRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

/**
 * 이벤트 웹훅 구독 — 등록·심사 결과를 밀어 받는다.
 *
 * <p>v2 전용. 심사는 비동기라 폴링 말고는 방법이 없었다. 구독해 두면 상태가
 * 바뀔 때마다 도착한다.
 */
public class WebhookService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    WebhookService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /**
     * 현재 구독 설정. 마지막 전송 결과({@code lastStatus})도 함께 온다 —
     * 내 엔드포인트가 실제로 받고 있는지 확인할 수 있어야 한다.
     */
    public Map<String, Object> show() {
        return http.get(url(null));
    }

    /** 구독 생성·수정. */
    public Map<String, Object> subscribe(WebhookSubscriptionRequest request) {
        return http.put(url(null), request);
    }

    /** 테스트 이벤트 발송. 구독 목록과 무관하게 도착하므로 배선 확인에 쓴다. */
    public Map<String, Object> test() {
        return http.post(url("test"), Map.of());
    }

    /** 구독 해지. */
    public Map<String, Object> unsubscribe() {
        return http.delete(url(null));
    }

    /**
     * 수신한 웹훅의 서명을 검증한다.
     *
     * <p><b>{@code rawBody} 는 받은 바이트 그대로</b>여야 한다. 파싱한 뒤 다시
     * 인코딩한 값으로 계산하면 키 순서나 이스케이프 차이로 검증이 깨진다.
     * Spring 이라면 {@code @RequestBody byte[]} 로 받는다.
     */
    public static boolean verifySignature(byte[] rawBody, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

            String expected = HexFormat.of().formatHex(mac.doFinal(rawBody));

            return MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.UTF_8),
                    signature == null ? new byte[0] : signature.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
    }

    private String url(String segment) {
        String base = config.getBaseUrl() + "/api/" + config.getApiVersion() + "/webhook";

        return segment == null ? base : base + "/" + segment;
    }
}
