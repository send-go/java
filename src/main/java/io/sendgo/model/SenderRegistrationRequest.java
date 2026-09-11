package io.sendgo.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 발신번호 등록 신청의 텍스트 필드. 서류는 {@link MultipartFile} 목록으로 따로 넘긴다.
 *
 * <p>휴대폰 계열({@code personal_mobile}, {@code team_representative_mobile},
 * {@code team_emp_mobile})은 PASS 대신 {@code identityDocument}(신분증 사본)를
 * 첨부한다.
 */
public class SenderRegistrationRequest {

    /** API 로 접수할 수 있는 발신번호 유형 — 전부다. */
    public static final String[] REGISTRABLE_TYPES = {
        "personal_mobile",
        "personal_other",
        "team_main",
        "team_representative_mobile",
        "team_emp_mobile",
        "team_other_company",
    };

    /**
     * 신분증 사본({@code identityDocument})이 필요한 유형.
     *
     * <p>콘솔은 PASS 본인인증을 쓰지만 API 는 신분증 사본을 받아 sendgo
     * 운영자가 직접 확인한다. 이 경로로 접수된 건은 자동 승인되지 않는다.
     */
    public static final String[] IDENTITY_DOCUMENT_TYPES =
            {"personal_mobile", "team_representative_mobile", "team_emp_mobile"};

    private final String senderAlias;
    private final String senderNumberType;
    private final String phoneE164;
    private final String duplicationReason;
    private final String acceptanceName;
    private final String delegationName;
    private final String delegationReason;

    private SenderRegistrationRequest(Builder b) {
        this.senderAlias       = b.senderAlias;
        this.senderNumberType  = b.senderNumberType;
        this.phoneE164         = b.phoneE164;
        this.duplicationReason = b.duplicationReason;
        this.acceptanceName    = b.acceptanceName;
        this.delegationName    = b.delegationName;
        this.delegationReason  = b.delegationReason;
    }

    public String getSenderAlias()      { return senderAlias; }
    public String getSenderNumberType() { return senderNumberType; }
    public String getPhoneE164()        { return phoneE164; }

    /**
     * multipart 필드 맵. 빈 값은 넣지 않는다 — 서버가 "빈 값으로 저장"으로 읽는다.
     */
    public Map<String, Object> toFields() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("senderAlias", senderAlias);
        fields.put("senderNumberType", senderNumberType);
        fields.put("phoneE164", phoneE164);

        putIfPresent(fields, "duplicationReason", duplicationReason);
        putIfPresent(fields, "acceptanceName", acceptanceName);
        putIfPresent(fields, "delegationName", delegationName);
        putIfPresent(fields, "delegationReason", delegationReason);

        return fields;
    }

    private static void putIfPresent(Map<String, Object> fields, String key, String value) {
        if (value != null && !value.isEmpty()) {
            fields.put(key, value);
        }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String senderAlias;
        private String senderNumberType;
        private String phoneE164;
        private String duplicationReason;
        private String acceptanceName;
        private String delegationName;
        private String delegationReason;

        /** 계정 안에서 중복될 수 없는 관리용 이름 (20자). */
        public Builder senderAlias(String v) { senderAlias = v; return this; }

        /** 여섯 유형 전부 API 로 접수할 수 있다. 휴대폰 계열은 신분증 사본을 함께 올린다. */
        public Builder senderNumberType(String v) { senderNumberType = v; return this; }

        /** 숫자와 하이픈만. 서버가 E.164 로 정규화한다. */
        public Builder phoneE164(String v) { phoneE164 = v; return this; }

        /** {@code validate()} 의 duplicationReasonRequired 가 true 면 필수. */
        public Builder duplicationReason(String v) { duplicationReason = v; return this; }

        /** team_other_company 필수. acceptance_representative / acceptance_employee. */
        public Builder acceptanceName(String v) { acceptanceName = v; return this; }

        /** team_other_company 필수. delegation_representative / delegation_employee. */
        public Builder delegationName(String v) { delegationName = v; return this; }

        /** team_other_company 필수. */
        public Builder delegationReason(String v) { delegationReason = v; return this; }

        public SenderRegistrationRequest build() { return new SenderRegistrationRequest(this); }
    }
}
