package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendtalkRequest {
    private String       content;
    private String       messageType  = "FT";
    private String       scheduleType = "DIRECTLY";
    private String       at;
    private List<?>      buttons;
    private String       imageUrl;
    private String       imageLink;
    private String       adFlag       = "Y";
    private String       wide         = "N";
    private String       replaceSms   = "N";
    private String       smsContent;
    private List<Contact> contacts;

    public String        getContent()      { return content; }
    public String        getMessageType()  { return messageType; }
    public String        getScheduleType() { return scheduleType; }
    public String        getAt()           { return at; }
    public List<?>       getButtons()      { return buttons; }
    public String        getImageUrl()     { return imageUrl; }
    public String        getImageLink()    { return imageLink; }
    public String        getAdFlag()       { return adFlag; }
    public String        getWide()         { return wide; }
    public String        getReplaceSms()   { return replaceSms; }
    public String        getSmsContent()   { return smsContent; }
    public List<Contact> getContacts()     { return contacts; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String content, messageType = "FT", scheduleType = "DIRECTLY", at;
        private List<?> buttons;
        private String imageUrl, imageLink, adFlag = "Y", wide = "N", replaceSms = "N", smsContent;
        private List<Contact> contacts;

        public Builder content(String v)          { content = v; return this; }
        public Builder messageType(String v)      { messageType = v; return this; }
        public Builder scheduleType(String v)     { scheduleType = v; return this; }
        public Builder at(String v)               { at = v; return this; }
        public Builder buttons(List<?> v)         { buttons = v; return this; }
        public Builder imageUrl(String v)         { imageUrl = v; return this; }
        public Builder imageLink(String v)        { imageLink = v; return this; }
        public Builder adFlag(String v)           { adFlag = v; return this; }
        public Builder wide(String v)             { wide = v; return this; }
        public Builder replaceSms(String v)       { replaceSms = v; return this; }
        public Builder smsContent(String v)       { smsContent = v; return this; }
        public Builder contacts(List<Contact> v)  { contacts = v; return this; }
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
        public FriendtalkRequest build() {
            FriendtalkRequest r = new FriendtalkRequest();
            r.content = content; r.messageType = messageType; r.scheduleType = scheduleType;
            r.at = at; r.buttons = buttons; r.imageUrl = imageUrl; r.imageLink = imageLink;
            r.adFlag = adFlag; r.wide = wide; r.replaceSms = replaceSms;
            r.smsContent = smsContent; r.contacts = contacts;
            return r;
        }
    }
}
