package io.sendgo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sendgo.exception.SendgoException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;

class SendgoHttpClient {

    private final SendgoConfig config;
    private final TokenManager tokenManager;
    private final HttpClient   httpClient;
    private final ObjectMapper mapper;

    SendgoHttpClient(SendgoConfig config, TokenManager tokenManager,
                     HttpClient httpClient, ObjectMapper mapper) {
        this.config       = config;
        this.tokenManager = tokenManager;
        this.httpClient   = httpClient;
        this.mapper       = mapper;
    }

    Map<String, Object> post(String url, Object body) {
        return doPost(url, body, false);
    }

    private Map<String, Object> doPost(String url, Object body, boolean isRetry) {
        try {
            String json    = mapper.writeValueAsString(body);
            String bearer  = makeBearerAuth(tokenManager.getToken());

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", bearer)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), new TypeReference<>() {});

            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                String errorCode = result.containsKey("code") ? (String) result.get("code") : null;
                String endpoint  = extractEndpoint(url);

                if (!isRetry && tokenManager.shouldRefresh(resp.statusCode(), errorCode)) {
                    tokenManager.invalidate();
                    return doPost(url, body, true);
                }

                String msg = result.containsKey("message") ? (String) result.get("message") : "Unknown error";
                throw new SendgoException(
                        "HTTP " + resp.statusCode() + (errorCode != null ? " [" + errorCode + "]" : "") + " " + msg,
                        resp.statusCode(), errorCode, endpoint, config.getApiVersion());
            }

            return result;

        } catch (SendgoException e) {
            throw e;
        } catch (Exception e) {
            throw new SendgoException("Sendgo 요청 실패: " + e.getMessage());
        }
    }

    private String makeBearerAuth(String token) {
        return "v2".equals(config.getApiVersion())
                ? "Bearer " + token
                : "Bearer " + Base64.getEncoder().encodeToString(token.getBytes());
    }

    private String extractEndpoint(String url) {
        int idx = url.lastIndexOf('/');
        return idx >= 0 ? url.substring(idx + 1) : url;
    }
}
