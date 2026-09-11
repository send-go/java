package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 알림톡 템플릿 등록·수정 요청.
 *
 * <p>뒤쪽 정책 필드 일곱 개는 sendgo 자체 게이트다. 카카오 심사와 별개이며,
 * 조합이 본문과 어긋나면 {@code POLICY_VALIDATION_FAILED} 로 거절된다.
 * 확인 플래그 셋은 기본값이 {@code true} 지만, <b>내용을 실제로 검토한 뒤에</b>
 * 그대로 두어야 한다 — 이 값은 법적 확인의 기록이다.
 *
 * <pre>
 * sendgo.noticeTemplates().create(NoticeTemplateRequest.builder()
 *         .kakaoSenderKey(kakaoSenderKey)
 *         .templateName("주문 접수 안내")
 *         .templateContent("#{name}님, 주문 #{orderNo}이 접수되었습니다.")
 *         .templateMessageType("BA")
 *         .templateEmphasizeType("NONE")
 *         .categoryCode("001001")
 *         .messagePurpose("order_delivery")
 *         .legalBasis("transaction")
 *         .benefitOrigin("none")
 *         .expiryType("none")
 *         .build());
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeTemplateRequest {
    private final String kakaoSenderKey;
    private final String templateName;
    private final String templateContent;
    private final String templateMessageType;
    private final String templateEmphasizeType;
    private final String categoryCode;

    private final String templateTitle;
    private final String templateSubtitle;
    private final String templateHeader;
    private final String templateExtra;
    private final Map<String, Object> templateItem;
    private final Map<String, Object> templateItemHighlight;
    private final Map<String, Object> templateRepresentLink;
    private final List<Map<String, Object>> buttons;
    private final List<Map<String, Object>> quickReplies;
    private final Boolean securityFlag;
    private final Boolean adultFlag;

    private final String  messagePurpose;
    private final String  legalBasis;
    private final String  benefitOrigin;
    private final String  expiryType;
    private final boolean optInReviewConfirmed;
    private final boolean ctaClearConfirmed;
    private final boolean policyConfirmed;

    private NoticeTemplateRequest(Builder b) {
        this.kakaoSenderKey        = b.kakaoSenderKey;
        this.templateName          = b.templateName;
        this.templateContent       = b.templateContent;
        this.templateMessageType   = b.templateMessageType;
        this.templateEmphasizeType = b.templateEmphasizeType;
        this.categoryCode          = b.categoryCode;
        this.templateTitle         = b.templateTitle;
        this.templateSubtitle      = b.templateSubtitle;
        this.templateHeader        = b.templateHeader;
        this.templateExtra         = b.templateExtra;
        this.templateItem          = b.templateItem;
        this.templateItemHighlight = b.templateItemHighlight;
        this.templateRepresentLink = b.templateRepresentLink;
        this.buttons               = b.buttons.isEmpty() ? null : b.buttons;
        this.quickReplies          = b.quickReplies.isEmpty() ? null : b.quickReplies;
        this.securityFlag          = b.securityFlag;
        this.adultFlag             = b.adultFlag;
        this.messagePurpose        = b.messagePurpose;
        this.legalBasis            = b.legalBasis;
        this.benefitOrigin         = b.benefitOrigin;
        this.expiryType            = b.expiryType;
        this.optInReviewConfirmed  = b.optInReviewConfirmed;
        this.ctaClearConfirmed     = b.ctaClearConfirmed;
        this.policyConfirmed       = b.policyConfirmed;
    }

    public String  getKakaoSenderKey()        { return kakaoSenderKey; }
    public String  getTemplateName()          { return templateName; }
    public String  getTemplateContent()       { return templateContent; }
    public String  getTemplateMessageType()   { return templateMessageType; }
    public String  getTemplateEmphasizeType() { return templateEmphasizeType; }
    public String  getCategoryCode()          { return categoryCode; }
    public String  getTemplateTitle()         { return templateTitle; }
    public String  getTemplateSubtitle()      { return templateSubtitle; }
    public String  getTemplateHeader()        { return templateHeader; }
    public String  getTemplateExtra()         { return templateExtra; }
    public Map<String, Object> getTemplateItem()          { return templateItem; }
    public Map<String, Object> getTemplateItemHighlight() { return templateItemHighlight; }
    public Map<String, Object> getTemplateRepresentLink() { return templateRepresentLink; }
    public List<Map<String, Object>> getButtons()      { return buttons; }
    public List<Map<String, Object>> getQuickReplies() { return quickReplies; }
    public Boolean getSecurityFlag() { return securityFlag; }
    public Boolean getAdultFlag()    { return adultFlag; }
    public String  getMessagePurpose()          { return messagePurpose; }
    public String  getLegalBasis()              { return legalBasis; }
    public String  getBenefitOrigin()           { return benefitOrigin; }
    public String  getExpiryType()              { return expiryType; }
    public boolean isOptInReviewConfirmed()     { return optInReviewConfirmed; }
    public boolean isCtaClearConfirmed()        { return ctaClearConfirmed; }
    public boolean isPolicyConfirmed()          { return policyConfirmed; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String kakaoSenderKey;
        private String templateName;
        private String templateContent;
        private String templateMessageType   = "BA";
        private String templateEmphasizeType = "NONE";
        private String categoryCode;

        private String templateTitle;
        private String templateSubtitle;
        private String templateHeader;
        private String templateExtra;
        private Map<String, Object> templateItem;
        private Map<String, Object> templateItemHighlight;
        private Map<String, Object> templateRepresentLink;
        private final List<Map<String, Object>> buttons      = new ArrayList<>();
        private final List<Map<String, Object>> quickReplies = new ArrayList<>();
        private Boolean securityFlag;
        private Boolean adultFlag;

        private String  messagePurpose;
        private String  legalBasis;
        private String  benefitOrigin;
        private String  expiryType;
        private boolean optInReviewConfirmed = true;
        private boolean ctaClearConfirmed    = true;
        private boolean policyConfirmed      = true;

        /** 발신프로필 키. 수정 시에는 무시된다 (변경 불가). */
        public Builder kakaoSenderKey(String v) { kakaoSenderKey = v; return this; }

        public Builder templateName(String v) { templateName = v; return this; }

        /** 본문. 변수는 {@code #{name}} 형식으로 쓴다. */
        public Builder templateContent(String v) { templateContent = v; return this; }

        /** BA 기본형 / EX 부가정보형 / AD 채널추가형 / MI 복합형. */
        public Builder templateMessageType(String v) { templateMessageType = v; return this; }

        /** NONE / TEXT 강조표기 / ITEM_LIST / IMAGE. */
        public Builder templateEmphasizeType(String v) { templateEmphasizeType = v; return this; }

        /** 6자리 숫자. {@code categories()} 로 조회한다. */
        public Builder categoryCode(String v) { categoryCode = v; return this; }

        public Builder templateTitle(String v)    { templateTitle = v; return this; }
        public Builder templateSubtitle(String v) { templateSubtitle = v; return this; }
        public Builder templateHeader(String v)   { templateHeader = v; return this; }
        public Builder templateExtra(String v)    { templateExtra = v; return this; }
        public Builder templateItem(Map<String, Object> v)          { templateItem = v; return this; }
        public Builder templateItemHighlight(Map<String, Object> v) { templateItemHighlight = v; return this; }
        public Builder templateRepresentLink(Map<String, Object> v) { templateRepresentLink = v; return this; }
        public Builder button(Map<String, Object> v)     { buttons.add(v); return this; }
        public Builder quickReply(Map<String, Object> v) { quickReplies.add(v); return this; }
        public Builder securityFlag(boolean v) { securityFlag = v; return this; }
        public Builder adultFlag(boolean v)    { adultFlag = v; return this; }

        /**
         * 메시지 목적. order_delivery / reservation_booking / payment_billing /
         * account_auth / service_ops / policy_notice / benefit_notice /
         * customer_support / other.
         */
        public Builder messagePurpose(String v) { messagePurpose = v; return this; }

        /** 발송 근거. transaction / paid_purchase / event_entry / contract / policy_notice. */
        public Builder legalBasis(String v) { legalBasis = v; return this; }

        /** 혜택 발생 경위. none / paid / event / contract / promo / free. */
        public Builder benefitOrigin(String v) { benefitOrigin = v; return this; }

        /** 소멸 유형. none / rights_based / promo. */
        public Builder expiryType(String v) { expiryType = v; return this; }

        /** 사전동의 검토 확인. 기본 true — 실제로 검토한 뒤에 그대로 둔다. */
        public Builder optInReviewConfirmed(boolean v) { optInReviewConfirmed = v; return this; }

        /** 유도 문구 미포함 확인. 기본 true. */
        public Builder ctaClearConfirmed(boolean v) { ctaClearConfirmed = v; return this; }

        /** 발신자 정책 확인. 기본 true. false 면 검수를 요청할 수 없다. */
        public Builder policyConfirmed(boolean v) { policyConfirmed = v; return this; }

        public NoticeTemplateRequest build() { return new NoticeTemplateRequest(this); }
    }
}
