package io.sendgo;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 기업 계정의 템플릿 공용 폴더. v2 전용. */
public class TemplateFolderService {
    private final SendgoHttpClient http;
    private final SendgoConfig config;

    TemplateFolderService(SendgoHttpClient http, SendgoConfig config) {
        this.http = http;
        this.config = config;
    }

    /** 기본 알림톡 폴더 트리 조회. */
    public Map<String, Object> list() { return list(null, null); }

    /** 유형(notice 또는 brand)과 발신프로필로 템플릿 수 조회. */
    public Map<String, Object> list(String templateType, String kakaoSenderKey) {
        List<String> query = new ArrayList<>();
        if (templateType != null) query.add("templateType=" + encode(templateType));
        if (kakaoSenderKey != null) query.add("kakaoSenderKey=" + encode(kakaoSenderKey));
        return http.get(url() + (query.isEmpty() ? "" : "?" + String.join("&", query)));
    }

    /** 루트 또는 하위 폴더 생성. parentUuid가 null이면 루트. */
    public Map<String, Object> create(String name, String parentUuid) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", name);
        body.put("parentUuid", parentUuid);
        return http.post(url(), body);
    }

    /** 1~100개 템플릿 이동. folderUuid가 null이면 미분류로 이동. */
    public Map<String, Object> assign(String templateType, String kakaoSenderKey, List<String> templateCodes, String folderUuid) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("templateType", templateType);
        body.put("kakaoSenderKey", kakaoSenderKey);
        body.put("templateCodes", templateCodes);
        body.put("folderUuid", folderUuid);
        return http.patch(url() + "/templates", body);
    }

    private String url() { return config.getBaseUrl() + "/api/" + config.getApiVersion() + "/template-folders"; }
    private String encode(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
}
