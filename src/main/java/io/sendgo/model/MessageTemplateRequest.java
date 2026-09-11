package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 문자(SMS/LMS/MMS) 상용구 템플릿 등록·수정 요청.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageTemplateRequest {
    private final String  messageTranType;
    private final String  messageTranMsg;
    private final String  messageTranSubject;
    private final boolean isFavorite;

    private MessageTemplateRequest(Builder b) {
        this.messageTranType    = b.messageTranType;
        this.messageTranMsg     = b.messageTranMsg;
        this.messageTranSubject = b.messageTranSubject;
        this.isFavorite         = b.isFavorite;
    }

    public String  getMessageTranType()    { return messageTranType; }
    public String  getMessageTranMsg()     { return messageTranMsg; }
    public String  getMessageTranSubject() { return messageTranSubject; }
    /**
     * 서버 필드는 {@code isFavorite} 다.
     *
     * <p>어노테이션이 없으면 Jackson 이 {@code isXxx} 게터에서 "is" 를 떼어
     * {@code favorite} 으로 직렬화해, 값이 서버에 도달하지 않는다.
     */
    @JsonProperty("isFavorite")
    public boolean isFavorite() { return isFavorite; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String  messageTranType = "SMS";
        private String  messageTranMsg;
        private String  messageTranSubject;
        private boolean isFavorite;

        /** SMS / LMS / MMS. */
        public Builder messageTranType(String v) { messageTranType = v; return this; }

        /** 본문 (2,000자). */
        public Builder messageTranMsg(String v) { messageTranMsg = v; return this; }

        /** 제목 (40자). LMS·MMS 는 필수. SMS 에 넣으면 발송 시 버려진다. */
        public Builder messageTranSubject(String v) { messageTranSubject = v; return this; }

        public Builder favorite(boolean v) { isFavorite = v; return this; }

        public MessageTemplateRequest build() { return new MessageTemplateRequest(this); }
    }
}
