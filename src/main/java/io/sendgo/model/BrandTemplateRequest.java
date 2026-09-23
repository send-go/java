package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 브랜드메시지(구 친구톡) 템플릿 등록·수정 요청.
 *
 * <p>{@code templateType} 은 친구톡 표기(FT/FI/FW/FL/FC/FM/FP/FA)를 그대로 쓴다 —
 * 서버가 chatBubbleType 으로 변환한다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrandTemplateRequest {
    private final String folderUuid;
    /** 등록 시 폴더 지정. 이동은 templateFolders().assign을 사용합니다. */
    public String getFolderUuid() { return folderUuid; }
    private final String kakaoSenderKey;
    private final String templateName;
    private final String templateType;
    private final String templateContent;
    private final Boolean adult;
    private final String header;
    private final String additionalContent;
    private final String imageUrl;
    private final String imageLink;
    private final List<Map<String, Object>> buttons;
    private final Map<String, Object> coupon;
    private final Map<String, Object> item;
    private final Map<String, Object> commerce;
    private final List<Map<String, Object>> list;
    private final Map<String, Object> head;
    private final Map<String, Object> tail;
    private final Map<String, Object> video;
    private final Map<String, Object> mainWideItem;
    private final List<Map<String, Object>> subWideItemList;

    private BrandTemplateRequest(Builder b) {
        this.folderUuid = b.folderUuid;
        this.kakaoSenderKey    = b.kakaoSenderKey;
        this.templateName      = b.templateName;
        this.templateType      = b.templateType;
        this.templateContent   = b.templateContent;
        this.adult             = b.adult;
        this.header            = b.header;
        this.additionalContent = b.additionalContent;
        this.imageUrl          = b.imageUrl;
        this.imageLink         = b.imageLink;
        this.buttons           = b.buttons.isEmpty() ? null : b.buttons;
        this.coupon            = b.coupon;
        this.item              = b.item;
        this.commerce          = b.commerce;
        this.list              = b.list.isEmpty() ? null : b.list;
        this.head              = b.head;
        this.tail              = b.tail;
        this.video             = b.video;
        this.mainWideItem      = b.mainWideItem;
        this.subWideItemList   = b.subWideItemList.isEmpty() ? null : b.subWideItemList;
    }

    public String  getKakaoSenderKey()  { return kakaoSenderKey; }
    public String  getTemplateName()    { return templateName; }
    public String  getTemplateType()    { return templateType; }
    public String  getTemplateContent() { return templateContent; }
    public Boolean getAdult()           { return adult; }
    public String  getHeader()          { return header; }

    /** 서버는 이 필드만 snake_case 로 받는다. */
    @JsonProperty("additional_content")
    public String getAdditionalContent() { return additionalContent; }

    public String getImageUrl()  { return imageUrl; }
    public String getImageLink() { return imageLink; }
    public List<Map<String, Object>> getButtons()         { return buttons; }
    public Map<String, Object>       getCoupon()          { return coupon; }
    public Map<String, Object>       getItem()            { return item; }
    public Map<String, Object>       getCommerce()        { return commerce; }
    public List<Map<String, Object>> getList()            { return list; }
    public Map<String, Object>       getHead()            { return head; }
    public Map<String, Object>       getTail()            { return tail; }
    public Map<String, Object>       getVideo()           { return video; }
    public Map<String, Object>       getMainWideItem()    { return mainWideItem; }
    public List<Map<String, Object>> getSubWideItemList() { return subWideItemList; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String folderUuid;
        public Builder folderUuid(String value) { folderUuid = value; return this; }
        private String kakaoSenderKey;
        private String templateName;
        private String templateType = "FT";
        private String templateContent;
        private Boolean adult;
        private String header;
        private String additionalContent;
        private String imageUrl;
        private String imageLink;
        private final List<Map<String, Object>> buttons = new ArrayList<>();
        private Map<String, Object> coupon;
        private Map<String, Object> item;
        private Map<String, Object> commerce;
        private final List<Map<String, Object>> list = new ArrayList<>();
        private Map<String, Object> head;
        private Map<String, Object> tail;
        private Map<String, Object> video;
        private Map<String, Object> mainWideItem;
        private final List<Map<String, Object>> subWideItemList = new ArrayList<>();

        public Builder kakaoSenderKey(String v) { kakaoSenderKey = v; return this; }
        public Builder templateName(String v)   { templateName = v; return this; }

        /** FT 텍스트 / FI 이미지 / FW 와이드 / FL 리스트 / FC 캐러셀 / FM 커머스 / FP 동영상 / FA 캐러셀커머스. */
        public Builder templateType(String v) { templateType = v; return this; }

        /** 본문. FW·FP 는 76자, FI 는 400자, FT 는 1000자 제한. */
        public Builder templateContent(String v) { templateContent = v; return this; }

        public Builder adult(boolean v)             { adult = v; return this; }
        public Builder header(String v)             { header = v; return this; }
        public Builder additionalContent(String v)  { additionalContent = v; return this; }

        /** FI·FW 는 필수. */
        public Builder imageUrl(String v)  { imageUrl = v; return this; }
        public Builder imageLink(String v) { imageLink = v; return this; }

        public Builder button(Map<String, Object> v)          { buttons.add(v); return this; }
        public Builder coupon(Map<String, Object> v)          { coupon = v; return this; }
        public Builder item(Map<String, Object> v)            { item = v; return this; }
        public Builder commerce(Map<String, Object> v)        { commerce = v; return this; }
        public Builder listItem(Map<String, Object> v)        { list.add(v); return this; }
        public Builder head(Map<String, Object> v)            { head = v; return this; }
        public Builder tail(Map<String, Object> v)            { tail = v; return this; }
        public Builder video(Map<String, Object> v)           { video = v; return this; }
        public Builder mainWideItem(Map<String, Object> v)    { mainWideItem = v; return this; }
        public Builder subWideItem(Map<String, Object> v)     { subWideItemList.add(v); return this; }

        public BrandTemplateRequest build() { return new BrandTemplateRequest(this); }
    }
}
