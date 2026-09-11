package io.sendgo;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 수신거부(080) 번호 조회. v2 전용, 조회 전용.
 *
 * <p>발송 API 가 알아서 제외하지만 <b>자기 DB 의 수신 상태도 맞춰야</b> 한다 —
 * 그러지 않으면 매번 보내고 매번 걸러지는 것을 반복하고, 자기 화면에서는
 * 여전히 "수신 동의" 로 보인다.
 */
public class RejectedNumberService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    RejectedNumberService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /** 전체 목록 — 서버 기본 조건. */
    public Map<String, Object> list() {
        return list(null, null, null);
    }

    /**
     * 수신거부 번호 목록.
     *
     * <p>{@code since} 로 증분만 가져간다. 전체를 매번 받으면 번호가 쌓일수록
     * 무거워진다.
     */
    public Map<String, Object> list(String since, String search, Integer count) {
        List<String> query = new ArrayList<>();
        if (since != null)  query.add("since=" + encode(since));
        if (search != null) query.add("search=" + encode(search));
        if (count != null)  query.add("count=" + count);

        String url = config.getBaseUrl() + "/api/" + config.getApiVersion() + "/rejected-numbers";
        if (!query.isEmpty()) {
            url += "?" + String.join("&", query);
        }

        return http.get(url);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
