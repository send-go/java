package io.sendgo;

import io.sendgo.model.AlimtalkRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 카카오 알림톡 발송 서비스.
 *
 * <pre>
 * sendgo.alimtalk().send(AlimtalkRequest.builder()
 *     .templateCode("ORDER_CONFIRM_001")
 *     .contact(Contact.builder().contact("01012345678").var1("ORD-001").build())
 *     .build());
 * </pre>
 */
public class AlimtalkService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    AlimtalkService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /** 알림톡 발송 */
    public Map<String, Object> send(AlimtalkRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("templateCode",   request.getTemplateCode());
        body.put("contacts",       request.getContacts());
        body.put("scheduleType",   request.getScheduleType());
        body.put("at",             request.getAt());
        body.put("replaceSms",     request.getReplaceSms());
        body.put("smsSubject",     request.getSmsSubject());
        body.put("smsContent",     request.getSmsContent());
        body.put("kakaoSenderKey", config.getKakaoSenderKey());
        body.put("senderKey",      config.getSmsSenderKey());

        return http.post(buildUrl("notices"), body);
    }

    private String buildUrl(String resource) {
        return config.getBaseUrl() + "/api/" + config.getApiVersion() + "/" + resource + "/send";
    }
}
