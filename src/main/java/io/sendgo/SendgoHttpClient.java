package io.sendgo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sendgo.exception.SendgoException;

import io.sendgo.model.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        return request("POST", url, body, false);
    }

    /** GET without a request body — used by the campaign lookup endpoints. */
    Map<String, Object> get(String url) {
        return request("GET", url, null, false);
    }

    Map<String, Object> put(String url, Object body) {
        return request("PUT", url, body, false);
    }

    Map<String, Object> patch(String url, Object body) {
        return request("PATCH", url, body, false);
    }

    /** DELETE without a request body — used to stop a short URL redirecting. */
    Map<String, Object> delete(String url) {
        return request("DELETE", url, null, false);
    }

    /**
     * multipart/form-data POST — 서류·이미지 첨부가 있는 관리 API 전용.
     *
     * <p>발신번호 등록과 이미지 템플릿은 JSON 으로 보낼 수 없다. multipart 에는
     * 배열도 불리언도 없으므로, 컬렉션·맵 값은 JSON 문자열로 눌러 보낸다 —
     * 서버가 그렇게 받아 읽는다.
     */
    Map<String, Object> postMultipart(String url, Map<String, Object> fields, List<MultipartFile> files) {
        return multipartRequest(url, fields, files, false);
    }

    private Map<String, Object> multipartRequest(String url, Map<String, Object> fields,
                                                 List<MultipartFile> files, boolean isRetry) {
        try {
            String boundary = "----SendgoBoundary" + UUID.randomUUID().toString().replace("-", "");
            byte[] body     = buildMultipartBody(fields, files, boundary);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", makeBearerAuth(tokenManager.getToken()))
                    .header("Accept", "application/json")
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    // 파일 업로드는 JSON 요청보다 오래 걸린다.
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), new TypeReference<>() {});

            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                String errorCode = result.containsKey("code") ? (String) result.get("code") : null;
                String endpoint  = extractEndpoint(url);

                if (!isRetry && tokenManager.shouldRefresh(resp.statusCode(), errorCode)) {
                    tokenManager.invalidate();
                    return multipartRequest(url, fields, files, true);
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

    private byte[] buildMultipartBody(Map<String, Object> fields, List<MultipartFile> files, String boundary)
            throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if (fields != null) {
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                Object value = entry.getValue();
                if (value == null) {
                    continue;
                }

                String encoded;
                if (value instanceof Boolean bool) {
                    encoded = bool ? "1" : "0";
                } else if (value instanceof CharSequence || value instanceof Number) {
                    encoded = value.toString();
                } else {
                    encoded = mapper.writeValueAsString(value);
                }

                out.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
                out.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n")
                        .getBytes(StandardCharsets.UTF_8));
                out.write(encoded.getBytes(StandardCharsets.UTF_8));
                out.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }
        }

        if (files != null) {
            for (MultipartFile file : files) {
                out.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
                out.write(("Content-Disposition: form-data; name=\"" + file.getFieldName()
                        + "\"; filename=\"" + file.getFileName() + "\"\r\n")
                        .getBytes(StandardCharsets.UTF_8));
                out.write(("Content-Type: " + file.getContentType() + "\r\n\r\n")
                        .getBytes(StandardCharsets.UTF_8));
                out.write(file.getContent());
                out.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }
        }

        out.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        return out.toByteArray();
    }

    private Map<String, Object> request(String method, String url, Object body, boolean isRetry) {
        try {
            String bearer = makeBearerAuth(tokenManager.getToken());

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", bearer);

            // `method` used to be ignored here — the verb was inferred from whether a
            // body was present, so DELETE could not be expressed at all.
            if (body == null) {
                builder.method(method, HttpRequest.BodyPublishers.noBody());
            } else {
                builder.header("Content-Type", "application/json")
                        .method(method, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
            }

            HttpRequest req = builder.build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), new TypeReference<>() {});

            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                String errorCode = result.containsKey("code") ? (String) result.get("code") : null;
                String endpoint  = extractEndpoint(url);

                if (!isRetry && tokenManager.shouldRefresh(resp.statusCode(), errorCode)) {
                    tokenManager.invalidate();
                    return request(method, url, body, true);
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
