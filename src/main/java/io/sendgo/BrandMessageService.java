package io.sendgo;

import io.sendgo.model.BrandMessageRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 카카오 브랜드메시지 발송 서비스.
 *
 * <p>브랜드메시지는 친구톡의 후속 채널로, 친구톡과 달리 채널 친구가 아닌
 * 수신자에게도 보낼 수 있고({@code targeting="N"}), 수신 동의한 전체 채널
 * 친구에게 동보 발송할 수 있다({@code targeting="F"}). v2 전용.
 *
 * <pre>{@code
 * // 단건 발송 — 채널 친구 대상
 * client.brandMessage().send(BrandMessageRequest.builder()
 *         .targeting("M")
 *         .messageType("FL")
 *         .friendTemplateUuid("9cd5460b-6458-4edc-9b11-c26d3013c340")
 *         .contact(Contact.of("01012345678"))
 *         .build());
 *
 * // 동보 발송 — 수신 동의한 전체 채널 친구
 * client.brandMessage().broadcast(BrandMessageRequest.builder()
 *         .messageType("FW")
 *         .friendTemplateUuid("9cd5460b-6458-4edc-9b11-c26d3013c340")
 *         .build());
 * }</pre>
 */
public class BrandMessageService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    BrandMessageService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /**
     * 브랜드메시지를 발송한다.
     *
     * <p>{@code targeting} 이 M/N/I 이면 {@code contacts} 가 필요하고 응답 data 에
     * 발송 건수(sentCount)가 담긴다. F 는 동보 발송이라 {@code contacts} 없이
     * 접수 여부(accepted)만 반환되므로, 그 경우 {@link #broadcast} 가 더 명확하다.
     */
    public Map<String, Object> send(BrandMessageRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("friendTemplateUuid", request.getFriendTemplateUuid());
        body.put("targeting",          request.getTargeting());
        body.put("messageType",        request.getMessageType());
        body.put("scheduleType",       request.getScheduleType());
        body.put("at",                 request.getAt());
        body.put("content",            request.getContent());
        body.put("buttons",            request.getButtons());
        body.put("imageUrl",           request.getImageUrl());
        body.put("imageLink",          request.getImageLink());
        body.put("adFlag",             request.getAdFlag());
        body.put("adult",              request.getAdult());
        body.put("pushAlarm",          request.getPushAlarm());
        body.put("header",             request.getHeader());
        body.put("coupon",             request.getCoupon());
        body.put("item",               request.getItem());
        body.put("commerce",           request.getCommerce());
        body.put("list",               request.getList());
        body.put("head",               request.getHead());
        body.put("tail",               request.getTail());
        body.put("video",              request.getVideo());
        body.put("additionalContent",  request.getAdditionalContent());
        body.put("friendGroupKey",     request.getFriendGroupKey());
        body.put("replaceSms",         request.getReplaceSms());
        body.put("smsSubject",         request.getSmsSubject());
        body.put("smsContent",         request.getSmsContent());
        body.put("rejectServiceId",    request.getRejectServiceId());
        body.put("webhooks",           request.getWebhooks());
        body.put("kakaoSenderKey",     config.getKakaoSenderKey());
        body.put("senderKey",          config.getSmsSenderKey());

        // 동보는 수신자 목록이 없다. 빈 리스트를 보내면 잘못된 요청으로 거절되므로
        // 키 자체를 넣지 않는다.
        if (!"F".equals(request.getTargeting())) {
            body.put("contacts", request.getContacts());
        }

        body.values().removeIf(v -> v == null);

        return http.post(sendUrl(), body);
    }

    /**
     * 동보 발송 — 수신 동의한 전체 채널 친구({@code targeting="F"}).
     *
     * <p>결과는 즉시 알 수 없으므로 {@link #campaigns} / {@link #campaign} 으로 확인한다.
     */
    public Map<String, Object> broadcast(BrandMessageRequest request) {
        BrandMessageRequest broadcastRequest = BrandMessageRequest.builder()
                .friendTemplateUuid(request.getFriendTemplateUuid())
                .targeting("F")
                .messageType(request.getMessageType())
                .scheduleType(request.getScheduleType())
                .at(request.getAt())
                .content(request.getContent())
                .buttons(request.getButtons())
                .imageUrl(request.getImageUrl())
                .imageLink(request.getImageLink())
                .adFlag(request.getAdFlag())
                .adult(request.getAdult())
                .pushAlarm(request.getPushAlarm())
                .header(request.getHeader())
                .coupon(request.getCoupon())
                .item(request.getItem())
                .commerce(request.getCommerce())
                .list(request.getList())
                .head(request.getHead())
                .tail(request.getTail())
                .video(request.getVideo())
                .additionalContent(request.getAdditionalContent())
                .friendGroupKey(request.getFriendGroupKey())
                .replaceSms(request.getReplaceSms())
                .smsSubject(request.getSmsSubject())
                .smsContent(request.getSmsContent())
                .rejectServiceId(request.getRejectServiceId())
                .webhooks(request.getWebhooks())
                .build();

        return send(broadcastRequest);
    }

    /**
     * 브랜드메시지 캠페인 목록을 조회한다.
     *
     * @param from  조회 시작일. null 이면 서버 기본값(90일 전)
     * @param to    조회 종료일. null 이면 서버 기본값(현재)
     * @param count 페이지당 개수. null 이면 서버 기본값(30)
     */
    public Map<String, Object> campaigns(String from, String to, Integer count) {
        List<String> query = new ArrayList<>();
        if (from != null)  query.add("from="  + encode(from));
        if (to != null)    query.add("to="    + encode(to));
        if (count != null) query.add("count=" + count);

        String url = resourceUrl(null);
        if (!query.isEmpty()) {
            url += "?" + String.join("&", query);
        }

        return http.get(url);
    }

    /** 기본 조건으로 브랜드메시지 캠페인 목록을 조회한다. */
    public Map<String, Object> campaigns() {
        return campaigns(null, null, null);
    }

    /**
     * 브랜드메시지 캠페인 상세를 조회한다.
     *
     * @param campaignId 발송 응답의 campaignId (UUID)
     */
    public Map<String, Object> campaign(String campaignId) {
        return http.get(resourceUrl(campaignId));
    }

    private String sendUrl() {
        return resourceUrl(null) + "/send";
    }

    private String resourceUrl(String path) {
        String base = config.getBaseUrl() + "/api/" + config.getApiVersion() + "/brand-messages";
        return path == null ? base : base + "/" + path;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
