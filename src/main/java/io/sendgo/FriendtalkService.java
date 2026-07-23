package io.sendgo;

import io.sendgo.model.FriendtalkRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 카카오 친구톡 발송 서비스.
 */
public class FriendtalkService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    FriendtalkService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    public Map<String, Object> send(FriendtalkRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content",        request.getContent());
        body.put("messageType",    request.getMessageType());
        body.put("scheduleType",   request.getScheduleType());
        body.put("at",             request.getAt());
        body.put("buttons",        request.getButtons());
        body.put("imageUrl",       request.getImageUrl());
        body.put("imageLink",      request.getImageLink());
        body.put("adFlag",         request.getAdFlag());
        body.put("wide",           request.getWide());
        body.put("replaceSms",     request.getReplaceSms());
        body.put("smsContent",     request.getSmsContent());
        body.put("kakaoSenderKey", config.getKakaoSenderKey());
        body.put("senderKey",      config.getSmsSenderKey());
        body.values().removeIf(v -> v == null);

        return http.post(buildUrl("friends"), body);
    }

    private String buildUrl(String resource) {
        return config.getBaseUrl() + "/api/" + config.getApiVersion() + "/" + resource + "/send";
    }
}
