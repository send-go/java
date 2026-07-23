package io.sendgo;

import io.sendgo.model.SmsRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SMS / LMS / MMS 발송 서비스.
 *
 * <pre>
 * sendgo.sms().sendSms(SmsRequest.sms()
 *     .content("[인증] 인증번호: 123456")
 *     .contact(Contact.builder().contact("01012345678").build()));
 * </pre>
 */
public class SmsService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    SmsService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /** SMS 전송 (90자 이하) */
    public Map<String, Object> sendSms(SmsRequest request) { return send(request); }

    /** LMS 전송 (장문) */
    public Map<String, Object> sendLms(SmsRequest request) { return send(request); }

    /** MMS 전송 (멀티미디어) */
    public Map<String, Object> sendMms(SmsRequest request) { return send(request); }

    public Map<String, Object> send(SmsRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("messageType",  request.getMessageType());
        body.put("campaignType", request.getCampaignType());
        body.put("scheduleType", request.getScheduleType());
        body.put("at",           request.getAt());
        body.put("subject",      request.getSubject());
        body.put("content",      request.getContent());
        body.put("contacts",     request.getContacts());
        body.put("senderKey",    config.getSmsSenderKey());
        body.values().removeIf(v -> v == null);

        return http.post(buildUrl("messages"), body);
    }

    private String buildUrl(String resource) {
        return config.getBaseUrl() + "/api/" + config.getApiVersion() + "/" + resource + "/send";
    }
}
