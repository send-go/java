package io.sendgo;

import io.sendgo.model.ShortUrlRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 짧은 URL — 메시지에 넣는 링크를 줄이고 클릭 반응을 집계한다.
 *
 * <p>v2 전용이다.
 *
 * <pre>
 * Map&lt;String, Object&gt; created = sendgo.shortUrl().create(ShortUrlRequest.builder()
 *         .targetUrl("https://example.com/promotions/summer-sale")
 *         .title("여름 세일 랜딩")
 *         .build());
 *
 * // created 의 data.shortUrl 을 문자/알림톡 본문에 넣는다.
 * </pre>
 */
public class ShortUrlService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    ShortUrlService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /**
     * 짧은 URL 을 만든다.
     *
     * <p>같은 원본 URL 을 다시 줄이면 기존 링크가 그대로 반환된다.
     * 캠페인별로 반응을 분리해 집계하려면 {@code forceNew(true)} 를 쓴다.
     */
    public Map<String, Object> create(ShortUrlRequest request) {
        return http.post(resourceUrl(null), request);
    }

    /** 목록 조회. null 인 조건은 서버 기본값이 적용된다. */
    public Map<String, Object> list(String from, String to, Integer count) {
        List<String> query = new ArrayList<>();
        if (from != null)  query.add("from=" + encode(from));
        if (to != null)    query.add("to=" + encode(to));
        if (count != null) query.add("count=" + count);

        String url = resourceUrl(null);
        if (!query.isEmpty()) {
            url += "?" + String.join("&", query);
        }

        return http.get(url);
    }

    /** 목록 조회 — 서버 기본 기간. */
    public Map<String, Object> list() {
        return list(null, null, null);
    }

    /** 상세 조회. */
    public Map<String, Object> show(String code) {
        return http.get(resourceUrl(code));
    }

    /** 반응 통계. 일별 추이와 디바이스/유입경로/국가별 분해를 반환한다. */
    public Map<String, Object> stats(String code, String from, String to) {
        List<String> query = new ArrayList<>();
        if (from != null) query.add("from=" + encode(from));
        if (to != null)   query.add("to=" + encode(to));

        String url = resourceUrl(code) + "/stats";
        if (!query.isEmpty()) {
            url += "?" + String.join("&", query);
        }

        return http.get(url);
    }

    /** 반응 통계 — 서버 기본 기간(최근 30일). */
    public Map<String, Object> stats(String code) {
        return stats(code, null, null);
    }

    /**
     * 리다이렉트를 중지한다.
     *
     * <p>링크는 삭제되지 않고 누적 통계도 남는다. 이후 그 링크로 들어오면
     * 410 Gone 이 반환된다.
     */
    public Map<String, Object> deactivate(String code) {
        return http.delete(resourceUrl(code));
    }

    private String resourceUrl(String path) {
        String base = config.getBaseUrl() + "/api/" + config.getApiVersion() + "/short-urls";
        return path == null ? base : base + "/" + encode(path);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
