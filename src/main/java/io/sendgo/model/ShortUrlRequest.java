package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 짧은 URL 생성 요청.
 *
 * <pre>
 * sendgo.shortUrl().create(ShortUrlRequest.builder()
 *         .targetUrl("https://example.com/promotions/summer-sale")
 *         .title("여름 세일 랜딩")
 *         .build());
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortUrlRequest {
    private String  targetUrl;
    private String  title;
    private String  expiresAt;
    private boolean forceNew;

    private ShortUrlRequest(Builder b) {
        this.targetUrl = b.targetUrl;
        this.title     = b.title;
        this.expiresAt = b.expiresAt;
        this.forceNew  = b.forceNew;
    }

    public String  getTargetUrl() { return targetUrl; }
    public String  getTitle()     { return title; }
    public String  getExpiresAt() { return expiresAt; }
    public boolean isForceNew()   { return forceNew; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String  targetUrl;
        private String  title;
        private String  expiresAt;
        private boolean forceNew;

        /** 줄일 원본 URL. http/https 만 허용된다. */
        public Builder targetUrl(String v) { targetUrl = v; return this; }

        /** 관리 화면에서 구분하기 위한 이름. */
        public Builder title(String v) { title = v; return this; }

        /** 이 시각 이후에는 리다이렉트하지 않고 410 Gone 을 반환한다. */
        public Builder expiresAt(String v) { expiresAt = v; return this; }

        /**
         * true 면 같은 URL 이라도 새 코드를 만든다.
         * 캠페인별로 반응을 분리해 집계할 때 사용한다.
         */
        public Builder forceNew(boolean v) { forceNew = v; return this; }

        public ShortUrlRequest build() { return new ShortUrlRequest(this); }
    }
}
