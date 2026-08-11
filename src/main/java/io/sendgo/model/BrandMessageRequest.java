package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

/**
 * 카카오 브랜드메시지 발송 요청.
 *
 * <p>브랜드메시지는 친구톡의 후속 채널로, {@code messageType} 에는 친구톡 코드
 * (FT/FI/FW/FL/FC/FM/FP/FA)를 그대로 넘기며 브랜드메시지 코드
 * (BT/BI/BW/BL/BC/BM/BP/BA) 변환은 서버가 처리한다.
 *
 * <p>{@code targeting} 은 M(채널 친구) / N(비친구) / I(전체) / F(동보)이며,
 * F 는 수신자 목록을 카카오 측에서 확장하므로 {@code contacts} 를 넘기지 않는다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrandMessageRequest {
    private String        friendTemplateUuid;
    private String        targeting    = "M";
    private String        messageType  = "FT";
    private String        scheduleType = "DIRECTLY";
    private String        at;
    private String        content;
    private List<?>       buttons;
    private String        imageUrl;
    private String        imageLink;
    private String        adFlag       = "Y";
    private String        adult        = "N";
    private String        pushAlarm    = "Y";
    private String        header;
    private Object        coupon;
    private Object        item;
    private Object        commerce;
    private List<?>       list;
    private Object        head;
    private Object        tail;
    private Object        video;
    private String        additionalContent;
    private String        friendGroupKey;
    private String        replaceSms   = "N";
    private String        smsSubject;
    private String        smsContent;
    private String        rejectServiceId;
    private List<String>  webhooks;
    private List<Contact> contacts;

    public String        getFriendTemplateUuid() { return friendTemplateUuid; }
    public String        getTargeting()          { return targeting; }
    public String        getMessageType()        { return messageType; }
    public String        getScheduleType()       { return scheduleType; }
    public String        getAt()                 { return at; }
    public String        getContent()            { return content; }
    public List<?>       getButtons()            { return buttons; }
    public String        getImageUrl()           { return imageUrl; }
    public String        getImageLink()          { return imageLink; }
    public String        getAdFlag()             { return adFlag; }
    public String        getAdult()              { return adult; }
    public String        getPushAlarm()          { return pushAlarm; }
    public String        getHeader()             { return header; }
    public Object        getCoupon()             { return coupon; }
    public Object        getItem()               { return item; }
    public Object        getCommerce()           { return commerce; }
    public List<?>       getList()               { return list; }
    public Object        getHead()               { return head; }
    public Object        getTail()               { return tail; }
    public Object        getVideo()              { return video; }
    public String        getAdditionalContent()  { return additionalContent; }
    public String        getFriendGroupKey()     { return friendGroupKey; }
    public String        getReplaceSms()         { return replaceSms; }
    public String        getSmsSubject()         { return smsSubject; }
    public String        getSmsContent()         { return smsContent; }
    public String        getRejectServiceId()    { return rejectServiceId; }
    public List<String>  getWebhooks()           { return webhooks; }
    public List<Contact> getContacts()           { return contacts; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String friendTemplateUuid, targeting = "M", messageType = "FT",
                scheduleType = "DIRECTLY", at, content;
        private List<?> buttons;
        private String imageUrl, imageLink, adFlag = "Y", adult = "N", pushAlarm = "Y", header;
        private Object coupon, item, commerce, head, tail, video;
        private List<?> list;
        private String additionalContent, friendGroupKey, replaceSms = "N",
                smsSubject, smsContent, rejectServiceId;
        private List<String> webhooks;
        private List<Contact> contacts;

        public Builder friendTemplateUuid(String v) { friendTemplateUuid = v; return this; }
        public Builder targeting(String v)          { targeting = v; return this; }
        public Builder messageType(String v)        { messageType = v; return this; }
        public Builder scheduleType(String v)       { scheduleType = v; return this; }
        public Builder at(String v)                 { at = v; return this; }
        public Builder content(String v)            { content = v; return this; }
        public Builder buttons(List<?> v)           { buttons = v; return this; }
        public Builder imageUrl(String v)           { imageUrl = v; return this; }
        public Builder imageLink(String v)          { imageLink = v; return this; }
        public Builder adFlag(String v)             { adFlag = v; return this; }
        public Builder adult(String v)              { adult = v; return this; }
        public Builder pushAlarm(String v)          { pushAlarm = v; return this; }
        public Builder header(String v)             { header = v; return this; }
        public Builder coupon(Object v)             { coupon = v; return this; }
        public Builder item(Object v)               { item = v; return this; }
        public Builder commerce(Object v)           { commerce = v; return this; }
        public Builder list(List<?> v)              { list = v; return this; }
        public Builder head(Object v)               { head = v; return this; }
        public Builder tail(Object v)               { tail = v; return this; }
        public Builder video(Object v)              { video = v; return this; }
        public Builder additionalContent(String v)  { additionalContent = v; return this; }
        public Builder friendGroupKey(String v)     { friendGroupKey = v; return this; }
        public Builder replaceSms(String v)         { replaceSms = v; return this; }
        public Builder smsSubject(String v)         { smsSubject = v; return this; }
        public Builder smsContent(String v)         { smsContent = v; return this; }
        public Builder rejectServiceId(String v)    { rejectServiceId = v; return this; }
        public Builder webhooks(List<String> v)     { webhooks = v; return this; }
        public Builder contacts(List<Contact> v)    { contacts = v; return this; }
        /**
         * 수신자를 하나 추가한다. 여러 번 호출하면 누적된다.
         * (전체를 한 번에 지정하려면 {@link #contacts(List)} 를 사용한다.)
         */
        public Builder contact(Contact v) {
            if (contacts == null) contacts = new ArrayList<>();
            else if (!(contacts instanceof ArrayList)) contacts = new ArrayList<>(contacts);
            contacts.add(v);
            return this;
        }

        public BrandMessageRequest build() {
            BrandMessageRequest r = new BrandMessageRequest();
            r.friendTemplateUuid = friendTemplateUuid;
            r.targeting = targeting;
            r.messageType = messageType;
            r.scheduleType = scheduleType;
            r.at = at;
            r.content = content;
            r.buttons = buttons;
            r.imageUrl = imageUrl;
            r.imageLink = imageLink;
            r.adFlag = adFlag;
            r.adult = adult;
            r.pushAlarm = pushAlarm;
            r.header = header;
            r.coupon = coupon;
            r.item = item;
            r.commerce = commerce;
            r.list = list;
            r.head = head;
            r.tail = tail;
            r.video = video;
            r.additionalContent = additionalContent;
            r.friendGroupKey = friendGroupKey;
            r.replaceSms = replaceSms;
            r.smsSubject = smsSubject;
            r.smsContent = smsContent;
            r.rejectServiceId = rejectServiceId;
            r.webhooks = webhooks;
            r.contacts = contacts;
            return r;
        }
    }
}
