package io.sendgo;

import io.sendgo.model.KakaoSenderCreateRequest;
import io.sendgo.model.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 카카오 발신프로필(채널) 관리 — 등록 · 동기화 · 브랜드메시지 타겟팅 신청.
 *
 * <p>v2 전용이며 <b>기업(Team) 소유 애플리케이션</b>만 사용할 수 있다.
 *
 * <p>채널 등록은 두 단계다. 카카오가 인증번호를 채널 관리자 <b>휴대폰으로 SMS
 * 발송</b>하므로 완전 무인 자동화는 불가능하다 — 사람이 문자를 받아
 * {@link #create} 에 넣어야 한다.
 *
 * <pre>
 * // 1단계 — 관리자 휴대폰으로 인증번호 발송 (응답에 번호는 없다)
 * sendgo.kakaoSenders().requestToken("&#64;my-channel", "01012345678");
 *
 * // 2단계 — 사람이 받은 인증번호로 발신프로필 생성
 * Map&lt;String, Object&gt; created = sendgo.kakaoSenders().create(
 *         KakaoSenderCreateRequest.builder()
 *                 .token("123456")
 *                 .yellowId("&#64;my-channel")
 *                 .phoneNumber("01012345678")
 *                 .categoryCode("001001")
 *                 .build());
 * </pre>
 */
public class KakaoSenderService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    KakaoSenderService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /**
     * 1단계 — 채널 인증번호 발송.
     *
     * <p>응답에 인증번호는 들어있지 않다. 카카오가 {@code phoneNumber} 로 SMS 를 보낸다.
     */
    public Map<String, Object> requestToken(String yellowId, String phoneNumber) {
        return http.post(url("token"), Map.of("yellowId", yellowId, "phoneNumber", phoneNumber));
    }

    /**
     * 2단계 — 발신프로필 등록.
     *
     * <p>이미 등록된 채널을 다시 등록해도 오류가 아니다. 카카오가 같은 senderKey 를
     * 돌려주고 서버가 기존 행을 갱신한다.
     */
    public Map<String, Object> create(KakaoSenderCreateRequest request) {
        return http.post(url(null), request);
    }

    /** 목록 조회. */
    public Map<String, Object> list() {
        return http.get(url(null));
    }

    /** 상세 조회. */
    public Map<String, Object> show(String kakaoSenderKey) {
        return http.get(url(kakaoSenderKey));
    }

    /** 카테고리 전체 조회. 등록 시 {@code categoryCode} 로 넣을 값이다. */
    public Map<String, Object> categories() {
        return categories(null);
    }

    /** 카테고리 조회. {@code categoryCode} 를 주면 그 코드 하나만 조회한다. */
    public Map<String, Object> categories(String categoryCode) {
        String target = url("categories");
        if (categoryCode != null) {
            target += "?categoryCode=" + encode(categoryCode);
        }
        return http.get(target);
    }

    /**
     * 팀 전체 발신프로필 상태를 카카오에서 다시 읽어 온다.
     *
     * <p>채널이 카카오 쪽에서 차단·휴면되면 발송이 조용히 실패하기 시작한다.
     * 그 사실을 먼저 알 방법은 이 호출뿐이므로 하루 한 번 정도 돌리는 게 좋다.
     */
    public Map<String, Object> sync() {
        return http.post(url("sync"), Map.of());
    }

    /** 발신프로필 단건 동기화. */
    public Map<String, Object> sync(String kakaoSenderKey) {
        return http.post(url(kakaoSenderKey) + "/sync", Map.of());
    }

    /**
     * 브랜드메시지 M 신청에 필요한 광고성 정보 수신동의 증적자료 업로드.
     * jpg/png, 5MB 이하.
     */
    public Map<String, Object> uploadBrandMessageEvidence(String kakaoSenderKey, MultipartFile evidence) {
        return http.postMultipart(
                url(kakaoSenderKey) + "/brand-message/evidence",
                Map.of(),
                List.of(evidence.withFieldName("evidence")));
    }

    /**
     * 브랜드메시지 {@code M}(마케팅) / {@code N}(정보성) 사용 신청.
     *
     * <p>결과는 즉시 확정되지 않는다. 발신프로필의 {@code brandMessageStatus} 로 확인한다.
     */
    public Map<String, Object> applyBrandMessageTargeting(String kakaoSenderKey, String targetType) {
        return http.post(url(kakaoSenderKey) + "/brand-message/apply", Map.of("targetType", targetType));
    }

    private String url(String segment) {
        String base = config.getBaseUrl() + "/api/" + config.getApiVersion() + "/kakao-senders";
        return segment == null ? base : base + "/" + encode(segment);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
