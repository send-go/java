package io.sendgo;

import io.sendgo.model.MultipartFile;
import io.sendgo.model.SenderRegistrationRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 발신번호(문자) 등록 · 심사 접수.
 *
 * <p>v2 전용. 카카오와 달리 <b>개인 계정 애플리케이션도</b> 쓸 수 있다.
 *
 * <p>등록하면 곧바로 쓸 수 있는 게 아니라 {@code PENDING} 으로 <b>접수</b>되고,
 * 운영자 승인 후 {@code SUCCESS} 가 된다.
 *
 * <pre>
 * Map&lt;String, Object&gt; check = sendgo.senderRegistration()
 *         .validate("02-1234-5678", "team_main");
 *
 * sendgo.senderRegistration().create(
 *         SenderRegistrationRequest.builder()
 *                 .senderAlias("고객센터 대표번호")
 *                 .senderNumberType("team_main")
 *                 .phoneE164("02-1234-5678")
 *                 .build(),
 *         List.of(MultipartFile.of("csuCertificate", Path.of("csu.pdf"), "application/pdf")));
 * </pre>
 */
public class SenderRegistrationService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    SenderRegistrationService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /** 목록 조회. 심사 상태({@code status})를 여기서 확인한다. */
    public Map<String, Object> list() {
        return http.get(url(null));
    }

    /** 상세 조회. */
    public Map<String, Object> show(String senderKey) {
        return http.get(url(senderKey));
    }

    /**
     * 계정 종류에 맞는 발신번호 유형과 유형별 필수 서류.
     *
     * <p>유형별 {@code identityVerification}({@code none}/{@code document})과
     * 필요한 서류 목록을 준다.
     */
    public Map<String, Object> numberTypes() {
        return http.get(url("number-types"));
    }

    /**
     * 등록 전 형식·중복 확인.
     *
     * <p>응답의 {@code duplicationReasonRequired} 가 true 면 {@link #create} 에
     * {@code duplicationReason} 을 함께 넣어야 한다.
     */
    public Map<String, Object> validate(String phoneE164, String senderNumberType) {
        return http.post(url("validate"),
                Map.of("phoneE164", phoneE164, "senderNumberType", senderNumberType));
    }

    /**
     * 등록 신청. 서류가 붙으므로 multipart 로 나간다.
     *
     * <p>{@code files} 에는 최소한 {@code csuCertificate}(통신서비스 이용증명원)가
     * 있어야 한다. 휴대폰 계열은 {@code identityDocument}(신분증 사본)가,
     * {@code team_other_company} 는 수임·위임 서류가 더 필요하다 —
     * {@link #numberTypes()} 로 확인한다.
     */
    public Map<String, Object> create(SenderRegistrationRequest request, List<MultipartFile> files) {
        return http.postMultipart(url(null), request.toFields(), files);
    }

    /** 별칭 변경. 번호와 심사 상태는 바꿀 수 없다. */
    public Map<String, Object> update(String senderKey, String senderAlias) {
        return update(senderKey, senderAlias, null);
    }

    /** 별칭 변경 / 기본 발신 지정. {@code primaryType} 은 PRIMARY 또는 SECONDARY. */
    public Map<String, Object> update(String senderKey, String senderAlias, String primaryType) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("senderAlias", senderAlias);
        if (primaryType != null) {
            body.put("primaryType", primaryType);
        }

        return http.patch(url(senderKey), body);
    }

    /** 삭제. 기본 발신번호를 지우면 남은 번호 중 하나가 기본으로 승계된다. */
    public Map<String, Object> delete(String senderKey) {
        return http.delete(url(senderKey));
    }

    private String url(String segment) {
        String base = config.getBaseUrl() + "/api/" + config.getApiVersion() + "/senders";
        return segment == null ? base : base + "/" + encode(segment);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
