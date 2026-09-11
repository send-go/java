package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * 이벤트 웹훅 구독 생성·수정 요청.
 *
 * <pre>
 * sendgo.webhook().subscribe(WebhookSubscriptionRequest.builder()
 *         .url("https://reseller.example.com/hooks/sendgo")
 *         .event(WebhookSubscriptionRequest.EVENT_SENDER_STATUS)
 *         .build());
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebhookSubscriptionRequest {

    /** 발신번호 심사 상태가 바뀜. */
    public static final String EVENT_SENDER_STATUS = "sender.status_changed";

    /** 알림톡 검수 상태가 바뀜. */
    public static final String EVENT_NOTICE_TEMPLATE_INSPECTION =
            "notice_template.inspection_status_changed";

    /** 카카오 채널의 차단·휴면·프로필 상태가 바뀜. */
    public static final String EVENT_KAKAO_SENDER_STATUS = "kakao_sender.status_changed";

    /** 브랜드메시지 M/N 신청 상태가 바뀜. */
    public static final String EVENT_BRAND_MESSAGE_TARGETING =
            "kakao_sender.brand_message_status_changed";

    /** 구독할 수 있는 이벤트 전체. */
    public static final List<String> EVENTS = List.of(
            EVENT_SENDER_STATUS,
            EVENT_NOTICE_TEMPLATE_INSPECTION,
            EVENT_KAKAO_SENDER_STATUS,
            EVENT_BRAND_MESSAGE_TARGETING);

    private final String url;
    private final String secret;
    private final List<String> events;
    private final boolean enabled;

    private WebhookSubscriptionRequest(Builder b) {
        this.url = b.url;
        this.secret = b.secret;
        this.events = b.events.isEmpty() ? null : b.events;
        this.enabled = b.enabled;
    }

    public String getUrl() { return url; }
    public String getSecret() { return secret; }
    public List<String> getEvents() { return events; }
    public boolean isEnabled() { return enabled; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String url;
        private String secret;
        private final List<String> events = new ArrayList<>();
        private boolean enabled = true;

        /** 이벤트를 받을 주소. <b>https 만 허용</b>됩니다. */
        public Builder url(String v) { url = v; return this; }

        /**
         * 서명 키 (16~128자). 비우면 서버가 만들어 응답에서 한 번만 돌려줍니다.
         * 이미 있는 상태에서 비우면 기존 값을 유지합니다.
         */
        public Builder secret(String v) { secret = v; return this; }

        /** 구독할 이벤트. 하나도 지정하지 않으면 전체 구독입니다. */
        public Builder event(String v) { events.add(v); return this; }

        public Builder enabled(boolean v) { enabled = v; return this; }

        public WebhookSubscriptionRequest build() { return new WebhookSubscriptionRequest(this); }
    }
}
