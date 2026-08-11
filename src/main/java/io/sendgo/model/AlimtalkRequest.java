package io.sendgo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlimtalkRequest {
    private String       templateCode;
    private List<Contact> contacts;
    private String       scheduleType = "DIRECTLY";
    private String       at;
    private String       replaceSms   = "N";
    private String       smsSubject;
    private String       smsContent;

    public String        getTemplateCode() { return templateCode; }
    public List<Contact> getContacts()     { return contacts; }
    public String        getScheduleType() { return scheduleType; }
    public String        getAt()           { return at; }
    public String        getReplaceSms()   { return replaceSms; }
    public String        getSmsSubject()   { return smsSubject; }
    public String        getSmsContent()   { return smsContent; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String       templateCode;
        private List<Contact> contacts;
        private String       scheduleType = "DIRECTLY";
        private String       at;
        private String       replaceSms   = "N";
        private String       smsSubject;
        private String       smsContent;

        public Builder templateCode(String v)      { templateCode = v; return this; }
        public Builder contacts(List<Contact> v)   { contacts = v; return this; }
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
        public Builder scheduleType(String v)      { scheduleType = v; return this; }
        public Builder at(String v)                { at = v; return this; }
        public Builder replaceSms(String v)        { replaceSms = v; return this; }
        public Builder smsSubject(String v)        { smsSubject = v; return this; }
        public Builder smsContent(String v)        { smsContent = v; return this; }
        public AlimtalkRequest build() {
            AlimtalkRequest r = new AlimtalkRequest();
            r.templateCode = templateCode; r.contacts = contacts;
            r.scheduleType = scheduleType; r.at = at;
            r.replaceSms = replaceSms; r.smsSubject = smsSubject; r.smsContent = smsContent;
            return r;
        }
    }
}
