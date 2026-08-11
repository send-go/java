package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsRequest {
    private String       messageType  = "SMS";
    private String       campaignType = "MESSAGE";
    private String       scheduleType = "DIRECTLY";
    private String       at;
    private String       subject;
    private String       content;
    private List<Contact> contacts;

    public String        getMessageType()  { return messageType; }
    public String        getCampaignType() { return campaignType; }
    public String        getScheduleType() { return scheduleType; }
    public String        getAt()           { return at; }
    public String        getSubject()      { return subject; }
    public String        getContent()      { return content; }
    public List<Contact> getContacts()     { return contacts; }

    public static SmsRequest sms()  { return new SmsRequest(); }
    public static SmsRequest lms()  { SmsRequest r = new SmsRequest(); r.messageType = "LMS"; return r; }
    public static SmsRequest mms()  { SmsRequest r = new SmsRequest(); r.messageType = "MMS"; return r; }

    public SmsRequest messageType(String v)      { this.messageType = v; return this; }
    public SmsRequest content(String v)          { this.content = v; return this; }
    public SmsRequest subject(String v)          { this.subject = v; return this; }
    public SmsRequest contacts(List<Contact> v)  { this.contacts = v; return this; }
    /**
     * 수신자를 하나 추가한다. 여러 번 호출하면 누적된다.
     * (전체를 한 번에 지정하려면 {@link #contacts(List)} 를 사용한다.)
     */
    public SmsRequest contact(Contact v) {
        if (this.contacts == null || !(this.contacts instanceof ArrayList)) {
            this.contacts = this.contacts == null ? new ArrayList<>() : new ArrayList<>(this.contacts);
        }
        this.contacts.add(v);
        return this;
    }
    public SmsRequest at(String v)               { this.at = v; return this; }
    public SmsRequest scheduleType(String v)     { this.scheduleType = v; return this; }
}
