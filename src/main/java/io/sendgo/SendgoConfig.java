package io.sendgo;

/**
 * Sendgo 클라이언트 설정.
 *
 * <pre>
 * SendgoConfig config = SendgoConfig.builder()
 *     .accessKey("your_access_key")
 *     .secretKey("your_secret_key")
 *     .kakaoSenderKey("your_kakao_key")
 *     .apiVersion("v2")
 *     .build();
 * </pre>
 */
public class SendgoConfig {

    private final String baseUrl;
    private final String accessKey;
    private final String secretKey;
    private final String kakaoSenderKey;
    private final String smsSenderKey;
    private final String apiVersion;

    private SendgoConfig(Builder builder) {
        this.baseUrl        = builder.baseUrl;
        this.accessKey      = builder.accessKey;
        this.secretKey      = builder.secretKey;
        this.kakaoSenderKey = builder.kakaoSenderKey;
        this.smsSenderKey   = builder.smsSenderKey;
        this.apiVersion     = builder.apiVersion;
    }

    public String getBaseUrl()        { return baseUrl; }
    public String getAccessKey()      { return accessKey; }
    public String getSecretKey()      { return secretKey; }
    public String getKakaoSenderKey() { return kakaoSenderKey; }
    public String getSmsSenderKey()   { return smsSenderKey; }
    public String getApiVersion()     { return apiVersion; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String baseUrl        = "https://api.sendgo.io";
        private String accessKey;
        private String secretKey;
        private String kakaoSenderKey;
        private String smsSenderKey;
        private String apiVersion     = "v2";

        public Builder baseUrl(String val)        { baseUrl = val; return this; }
        public Builder accessKey(String val)      { accessKey = val; return this; }
        public Builder secretKey(String val)      { secretKey = val; return this; }
        public Builder kakaoSenderKey(String val) { kakaoSenderKey = val; return this; }
        public Builder smsSenderKey(String val)   { smsSenderKey = val; return this; }
        public Builder apiVersion(String val)     { apiVersion = val; return this; }

        public SendgoConfig build() {
            if (accessKey == null || secretKey == null) {
                throw new IllegalArgumentException("accessKey와 secretKey는 필수입니다.");
            }
            return new SendgoConfig(this);
        }
    }
}
