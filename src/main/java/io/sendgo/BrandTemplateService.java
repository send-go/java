package io.sendgo;

import io.sendgo.model.BrandTemplateRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 브랜드메시지(구 친구톡) 템플릿 관리.
 *
 * <p>v2 전용이며 <b>기업(Team) 소유 애플리케이션</b>만 사용할 수 있다.
 * 알림톡 템플릿과 달리 <b>검수 요청 단계가 없다.</b> 등록하면 카카오가 바로
 * 상태를 돌려주고 그 값이 {@code status} 로 나온다.
 */
public class BrandTemplateService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    BrandTemplateService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /** 목록 조회 — 서버 기본 조건. */
    public Map<String, Object> list() {
        return list(null, null, null);
    }

    /** 목록 조회. */
    public Map<String, Object> list(String kakaoSenderKey, String search, Integer count) {
        List<String> query = new ArrayList<>();
        if (kakaoSenderKey != null) query.add("kakaoSenderKey=" + encode(kakaoSenderKey));
        if (search != null)         query.add("search=" + encode(search));
        if (count != null)          query.add("count=" + count);

        String url = url(null);
        if (!query.isEmpty()) {
            url += "?" + String.join("&", query);
        }

        return http.get(url);
    }

    /** 상세 조회. sendgo 코드(KFT-...)와 카카오 브랜드 템플릿 코드 둘 다 받는다. */
    public Map<String, Object> show(String templateCode) {
        return http.get(url(templateCode));
    }

    /** 템플릿 등록. */
    public Map<String, Object> create(BrandTemplateRequest request) {
        return http.post(url(null), request);
    }

    /** 템플릿 수정. 발신프로필은 바꿀 수 없다. */
    public Map<String, Object> update(String templateCode, BrandTemplateRequest request) {
        return http.put(url(templateCode), request);
    }

    /** 템플릿 삭제. 알림톡과 달리 카카오 쪽에서도 실제로 삭제된다. */
    public Map<String, Object> delete(String templateCode) {
        return http.delete(url(templateCode));
    }

    /**
     * 동기화. 카카오 쪽에서 이미 삭제됐으면 로컬에서도 제거하고
     * {@code data.deleted: true} 를 반환한다.
     */
    public Map<String, Object> sync(String templateCode) {
        return http.post(url(templateCode) + "/sync", Map.of());
    }

    /** 발신프로필 단위 가져오기 — 카카오 쪽에 이미 있는 템플릿을 들여온다. */
    public Map<String, Object> importFromSender(String kakaoSenderKey) {
        return http.post(url("import"), Map.of("kakaoSenderKey", kakaoSenderKey));
    }

    private String url(String segment) {
        String base = config.getBaseUrl() + "/api/" + config.getApiVersion() + "/brand-templates";
        return segment == null ? base : base + "/" + encode(segment);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
