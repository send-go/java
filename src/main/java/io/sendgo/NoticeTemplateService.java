package io.sendgo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sendgo.model.MultipartFile;
import io.sendgo.model.NoticeTemplateRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 알림톡 템플릿 관리 — 등록 · 수정 · 검수 요청.
 *
 * <p>v2 전용이며 <b>기업(Team) 소유 애플리케이션</b>만 사용할 수 있다.
 *
 * <p>템플릿은 만든 즉시 쓸 수 없다. 카카오 검수를 통과해야 한다.
 *
 * <pre>
 * 등록      inspectionStatus=REG   ← 발송 불가
 * 검수 요청  inspectionStatus=REQ   ← 카카오 심사 중
 * 승인      inspectionStatus=APR   ← 여기부터 발송 가능
 * 반려      inspectionStatus=REJ   ← comments 에 사유
 * </pre>
 *
 * <p>검수 결과는 비동기다. 웹훅이 없으므로 {@link #sync} 로 폴링한다.
 */
public class NoticeTemplateService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;
    private final ObjectMapper     mapper = new ObjectMapper();

    NoticeTemplateService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /** 목록 조회 — 서버 기본 조건. */
    public Map<String, Object> list() {
        return list(null, null, null, null);
    }

    /** 목록 조회. null 인 조건은 적용하지 않는다. */
    public Map<String, Object> list(String kakaoSenderKey, String inspectionStatus, String search, Integer count) {
        List<String> query = new ArrayList<>();
        if (kakaoSenderKey != null)   query.add("kakaoSenderKey=" + encode(kakaoSenderKey));
        if (inspectionStatus != null) query.add("inspectionStatus=" + encode(inspectionStatus));
        if (search != null)           query.add("search=" + encode(search));
        if (count != null)            query.add("count=" + count);

        String url = url(null);
        if (!query.isEmpty()) {
            url += "?" + String.join("&", query);
        }

        return http.get(url);
    }

    /** 상세 조회. 응답의 {@code data.template.policy} 에 정책 검토 상태가 들어 있다. */
    public Map<String, Object> show(String templateCode) {
        return http.get(url(templateCode));
    }

    /** 템플릿 등록. */
    public Map<String, Object> create(NoticeTemplateRequest request) {
        return http.post(url(null), request);
    }

    /**
     * 이미지 템플릿 등록 ({@code templateEmphasizeType} 이 {@code IMAGE} 인 경우).
     *
     * <p>multipart 로 나가므로 buttons 같은 필드는 JSON 문자열로 직렬화해 보낸다.
     */
    public Map<String, Object> createWithImage(NoticeTemplateRequest request, MultipartFile image) {
        return http.postMultipart(url(null), toFields(request), List.of(image.withFieldName("image")));
    }

    /**
     * 템플릿 수정.
     *
     * <p>발신프로필과 템플릿 코드는 바꿀 수 없다. 본문·버튼처럼 카카오에 등록된
     * 내용이 바뀌면 검수 상태가 되돌아가므로 재검수를 요청해야 한다.
     */
    public Map<String, Object> update(String templateCode, NoticeTemplateRequest request) {
        return http.put(url(templateCode), request);
    }

    /**
     * 템플릿 삭제.
     *
     * <p><b>카카오는 템플릿 삭제 API 를 제공하지 않는다.</b> sendgo 목록에서만
     * 지워지고 비즈니스 채널 쪽 템플릿은 남는다. 동기화하면 다시 나타난다.
     */
    public Map<String, Object> delete(String templateCode) {
        return http.delete(url(templateCode));
    }

    /** 카카오에서 검수 상태와 반려 사유를 다시 읽어 온다. */
    public Map<String, Object> sync(String templateCode) {
        return http.post(url(templateCode) + "/sync", Map.of());
    }

    /** 검수 요청 (문의사항 없이). */
    public Map<String, Object> requestInspection(String templateCode) {
        return requestInspection(templateCode, null);
    }

    /**
     * 검수 요청.
     *
     * <p>정책 검토를 통과하지 못한 템플릿은 {@code POLICY_REVIEW_REQUIRED} 로
     * 거절되고 {@code errors.reasons} 에 사유가 담긴다.
     */
    public Map<String, Object> requestInspection(String templateCode, String comment) {
        Map<String, Object> body = comment == null ? Map.of() : Map.of("comment", comment);
        return http.post(url(templateCode) + "/inspection", body);
    }

    /**
     * 검수 요청 (증빙 첨부).
     *
     * <p>첨부가 있으면 {@code comment} 는 필수다. jpg/png/pdf, 각 5MB 이하, 최대 5개.
     */
    public Map<String, Object> requestInspection(String templateCode, String comment, List<MultipartFile> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return requestInspection(templateCode, comment);
        }

        // 서버는 attachments[0], attachments[1] 형태를 기대한다.
        List<MultipartFile> named = new ArrayList<>(attachments.size());
        for (int i = 0; i < attachments.size(); i++) {
            named.add(attachments.get(i).withFieldName("attachments[" + i + "]"));
        }

        Map<String, Object> fields = new LinkedHashMap<>();
        if (comment != null) {
            fields.put("comment", comment);
        }

        return http.postMultipart(url(templateCode) + "/inspection", fields, named);
    }

    /** 검수 요청 취소. 아직 심사 중(REQ)일 때만 통한다. */
    public Map<String, Object> cancelInspection(String templateCode) {
        return http.delete(url(templateCode) + "/inspection");
    }

    /** 승인 취소. 승인(APR)된 템플릿을 되돌린다. 이후에는 발송할 수 없다. */
    public Map<String, Object> cancelApproval(String templateCode) {
        return http.delete(url(templateCode) + "/approval");
    }

    /** 휴면 해제. 오래 안 쓴 템플릿이 dormant 로 잠기면 이걸로 깨운다. */
    public Map<String, Object> release(String templateCode) {
        return http.post(url(templateCode) + "/release", Map.of());
    }

    /** 템플릿 카테고리 코드 전체 조회. */
    public Map<String, Object> categories() {
        return categories(null);
    }

    /** 템플릿 카테고리 코드 조회. */
    public Map<String, Object> categories(String categoryCode) {
        String target = url("categories");
        if (categoryCode != null) {
            target += "?categoryCode=" + encode(categoryCode);
        }
        return http.get(target);
    }

    /**
     * 요청 객체를 multipart 필드 맵으로 바꾼다.
     *
     * <p>Jackson 을 한 번 거치면 {@code @JsonInclude} 와 게터 이름 매핑을 손으로
     * 다시 적지 않아도 된다 — JSON 경로와 multipart 경로가 같은 필드명을 쓴다.
     */
    private Map<String, Object> toFields(NoticeTemplateRequest request) {
        return mapper.convertValue(request, new TypeReference<LinkedHashMap<String, Object>>() {});
    }

    private String url(String segment) {
        String base = config.getBaseUrl() + "/api/" + config.getApiVersion() + "/notice-templates";
        return segment == null ? base : base + "/" + encode(segment);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
