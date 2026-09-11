package io.sendgo;

import io.sendgo.model.MessageTemplateRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 문자(SMS/LMS/MMS) 상용구 템플릿.
 *
 * <p>v2 전용. 카카오 템플릿과 달리 <b>검수가 없어</b> 만들면 바로 쓸 수 있고,
 * 기업 계정이 아니어도 된다.
 */
public class MessageTemplateService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    MessageTemplateService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /** 목록 조회 — 서버 기본 조건. */
    public Map<String, Object> list() {
        return list(null, null, null);
    }

    /** 목록 조회. */
    public Map<String, Object> list(String messageType, String search, Integer count) {
        List<String> query = new ArrayList<>();
        if (messageType != null) query.add("messageType=" + encode(messageType));
        if (search != null)      query.add("search=" + encode(search));
        if (count != null)       query.add("count=" + count);

        String url = url(null);
        if (!query.isEmpty()) {
            url += "?" + String.join("&", query);
        }

        return http.get(url);
    }

    /** 상세 조회. */
    public Map<String, Object> show(String templateKey) {
        return http.get(url(templateKey));
    }

    /** 등록. LMS·MMS 는 {@code messageTranSubject} 가 필수다. */
    public Map<String, Object> create(MessageTemplateRequest request) {
        return http.post(url(null), request);
    }

    /** 수정. */
    public Map<String, Object> update(String templateKey, MessageTemplateRequest request) {
        return http.put(url(templateKey), request);
    }

    /** 삭제 (소프트 삭제 — 목록에서만 사라진다). */
    public Map<String, Object> delete(String templateKey) {
        return http.delete(url(templateKey));
    }

    private String url(String segment) {
        String base = config.getBaseUrl() + "/api/" + config.getApiVersion() + "/message-templates";
        return segment == null ? base : base + "/" + encode(segment);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
