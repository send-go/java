package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 카카오 발신프로필 등록 요청 (2단계).
 *
 * <pre>
 * sendgo.kakaoSenders().create(KakaoSenderCreateRequest.builder()
 *         .token("123456")            // 1단계에서 관리자 휴대폰으로 받은 인증번호
 *         .yellowId("&#64;my-channel")
 *         .phoneNumber("01012345678")
 *         .categoryCode("001001")
 *         .build());
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KakaoSenderCreateRequest {
    private final String token;
    private final String yellowId;
    private final String phoneNumber;
    private final String categoryCode;

    private KakaoSenderCreateRequest(Builder b) {
        this.token        = b.token;
        this.yellowId     = b.yellowId;
        this.phoneNumber  = b.phoneNumber;
        this.categoryCode = b.categoryCode;
    }

    public String getToken()        { return token; }
    public String getYellowId()     { return yellowId; }
    public String getPhoneNumber()  { return phoneNumber; }
    public String getCategoryCode() { return categoryCode; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token;
        private String yellowId;
        private String phoneNumber;
        private String categoryCode;

        /** 1단계에서 관리자 휴대폰으로 받은 인증번호. */
        public Builder token(String v) { token = v; return this; }

        /** 채널 검색용 아이디. {@code @} 는 있어도 없어도 된다. */
        public Builder yellowId(String v) { yellowId = v; return this; }

        /** 채널 관리자 휴대폰 번호. */
        public Builder phoneNumber(String v) { phoneNumber = v; return this; }

        /** {@code categories()} 로 조회한 코드. */
        public Builder categoryCode(String v) { categoryCode = v; return this; }

        public KakaoSenderCreateRequest build() { return new KakaoSenderCreateRequest(this); }
    }
}
