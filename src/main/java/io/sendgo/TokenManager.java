package io.sendgo;

import io.sendgo.exception.SendgoException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
class TokenManager {

    private static final Set<String> NO_REFRESH_CODES = Set.of(
            "INVALID_AUTH_HEADER", "INVALID_BASIC_AUTH", "INVALID_BASIC_AUTH_PAYLOAD",
            "INVALID_ACCESS_KEY", "INVALID_SECRET_KEY", "ACCESS_KEY_NOT_APPROVED",
            "TEAM_REQUIRED_FOR_KAKAO", "IP_NOT_ALLOWED",
            "INVALID_SENDER_KEY", "INVALID_KAKAO_SENDER_KEY"
    );

    private final SendgoConfig config;
    private final HttpClient   httpClient;
    private final com.fasterxml.jackson.databind.ObjectMapper mapper;

    private volatile String cachedToken;
    private volatile long   tokenExpiresAt = 0L;

    TokenManager(SendgoConfig config, HttpClient httpClient,
                 com.fasterxml.jackson.databind.ObjectMapper mapper) {
        this.config     = config;
        this.httpClient = httpClient;
        this.mapper     = mapper;
    }

    synchronized String getToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiresAt) {
            return cachedToken;
        }
        return fetchNewToken();
    }

    synchronized void invalidate() {
        cachedToken    = null;
        tokenExpiresAt = 0L;
        getToken();
    }

    boolean shouldRefresh(int status, String errorCode) {
        if (status != 401 && status != 403) return false;
        if ("v2".equals(config.getApiVersion()) && errorCode != null
                && NO_REFRESH_CODES.contains(errorCode)) {
            return false;
        }
        return true;
    }

    private String fetchNewToken() {
        String tokenUrl = config.getBaseUrl() + "/api/" + config.getApiVersion() + "/token";
        String credentials = config.getAccessKey() + ":" + config.getSecretKey();
        String basic = Base64.getEncoder().encodeToString(credentials.getBytes());

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(tokenUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Basic " + basic)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> body = mapper.readValue(resp.body(), Map.class);

            if (resp.statusCode() < 200 || resp.statusCode() >= 300
                    || !(body.get("data") instanceof Map<?,?> data)
                    || data.get("token") == null) {
                String code = body.containsKey("code") ? (String) body.get("code") : null;
                String msg  = body.containsKey("message") ? (String) body.get("message") : "Token fetch failed";
                throw new SendgoException("HTTP " + resp.statusCode() + (code != null ? " [" + code + "]" : "") + " " + msg,
                        resp.statusCode(), code, "token", config.getApiVersion());
            }

            String token = (String) ((Map<?,?>) body.get("data")).get("token");
            this.cachedToken    = token;
            this.tokenExpiresAt = System.currentTimeMillis() + (50L * 60 * 1000);
            return token;

        } catch (SendgoException e) {
            throw e;
        } catch (Exception e) {
            throw new SendgoException("Sendgo 토큰 발급 실패: " + e.getMessage());
        }
    }
}
