package io.sendgo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sendgo.exception.SendgoException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/** 서버 전용 계정 API. 에이전트 토큰은 자동 갱신하지 않는다. */
public final class AccountClient {
    private final String agentToken;
    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    public AccountClient(String agentToken) { this(agentToken, "https://sendgo.io"); }

    public AccountClient(String agentToken, String baseUrl) {
        if (agentToken == null || agentToken.isBlank()) throw new IllegalArgumentException("Sendgo: agentToken은 필수입니다.");
        this.agentToken = agentToken;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    /** 계정 상태와 다음 단계 조회. */
    public Map<String, Object> me() {
        return request("GET", "", null);
    }

    /** 조직 목록 조회. */
    public Map<String, Object> organizations() {
        return request("GET", "organizations", null);
    }

    /** 조직 선택. null은 개인 계정. */
    public Map<String, Object> selectOrganization(String organizationId) {
        return request("POST", "organizations/select", payload("organizationId", organizationId));
    }

    /** 현재 조직의 API 키 목록. */
    public Map<String, Object> apiKeys() {
        return request("GET", "api-keys", null);
    }

    /** API 키 발급. secretKey는 이 응답에서만 반환. */
    public Map<String, Object> createApiKey(Map<String, Object> params) {
        return request("POST", "api-keys", params);
    }

    /** API 키 상세 조회. */
    public Map<String, Object> apiKey(String apiKeyId) {
        return request("GET", "api-keys/" + segment(apiKeyId) + "", null);
    }

    /** API 키 이름 변경. */
    public Map<String, Object> updateApiKey(String apiKeyId, String name) {
        return request("PATCH", "api-keys/" + segment(apiKeyId) + "", payload("name", name));
    }

    /** API 키 폐기. */
    public Map<String, Object> deleteApiKey(String apiKeyId) {
        return request("DELETE", "api-keys/" + segment(apiKeyId) + "", null);
    }

    /** 승인된 API 키의 발송용 토큰 발급. */
    public Map<String, Object> issueToken(String apiKeyId) {
        return request("POST", "api-keys/" + segment(apiKeyId) + "/token", Map.of());
    }

    /** 허용 IP 목록과 호출자 IP 조회. */
    public Map<String, Object> allowedIps(String apiKeyId) {
        return request("GET", "api-keys/" + segment(apiKeyId) + "/allowed-ips", null);
    }

    /** 허용 IP 추가. ip와 선택적 description 사용. */
    public Map<String, Object> addAllowedIp(String apiKeyId, Map<String, Object> params) {
        return request("POST", "api-keys/" + segment(apiKeyId) + "/allowed-ips", params);
    }

    /** 허용 IP 삭제. */
    public Map<String, Object> deleteAllowedIp(String apiKeyId, String ipId) {
        return request("DELETE", "api-keys/" + segment(apiKeyId) + "/allowed-ips/" + segment(ipId) + "", null);
    }

    private static Map<String, Object> payload(String key, Object value) {
        Map<String, Object> body = new HashMap<>();
        body.put(key, value);
        return body;
    }

    private static String segment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private Map<String, Object> request(String method, String path, Object body) {
        try {
            HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v2/account" + (path.isEmpty() ? "" : "/" + path)))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + agentToken).header("Accept", "application/json");
            if (body == null) request.method(method, HttpRequest.BodyPublishers.noBody());
            else request.header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
            HttpResponse<String> response = http.send(request.build(), HttpResponse.BodyHandlers.ofString());
            Map<String, Object> data;
            try { data = mapper.readValue(response.body(), new TypeReference<>() {}); }
            catch (com.fasterxml.jackson.core.JsonProcessingException e) { data = Map.of(); }
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String code = (String) data.get("code");
                throw new SendgoException("HTTP " + response.statusCode() + " " + data.getOrDefault("message", "Unknown error"),
                    response.statusCode(), code, path.isEmpty() ? "account" : path, "v2");
            }
            return data;
        } catch (SendgoException e) { throw e; }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SendgoException("Sendgo 요청이 중단되었습니다.");
        } catch (java.io.IOException e) { throw new SendgoException("Sendgo 요청 실패: " + e.getMessage()); }
    }
}
