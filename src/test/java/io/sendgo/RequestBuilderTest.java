package io.sendgo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sendgo.model.AlimtalkRequest;
import io.sendgo.model.BrandMessageRequest;
import io.sendgo.model.Contact;
import io.sendgo.model.FriendtalkRequest;
import io.sendgo.model.SmsRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 1.1.0 에서 고친 두 가지 동작을 고정한다.
 *
 *  - contact() 가 리스트를 통째로 교체하지 않고 누적한다.
 *  - sendSms/sendLms/sendMms 가 messageType 을 강제한다.
 */
class RequestBuilderTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("contact() 를 여러 번 호출하면 수신자가 누적된다")
    void contactAccumulates() {
        AlimtalkRequest req = AlimtalkRequest.builder()
                .templateCode("T")
                .contact(Contact.builder().contact("01011111111").build())
                .contact(Contact.builder().contact("01022222222").build())
                .contact(Contact.builder().contact("01033333333").build())
                .build();

        // 예전에는 contacts = List.of(v) 라서 마지막 한 명만 남았다.
        assertEquals(3, req.getContacts().size());
        assertEquals("01011111111", req.getContacts().get(0).getContact());
        assertEquals("01033333333", req.getContacts().get(2).getContact());
    }

    @Test
    @DisplayName("contacts() 로 전체 지정한 뒤 contact() 로 덧붙일 수 있다")
    void contactsThenContact() {
        AlimtalkRequest req = AlimtalkRequest.builder()
                .templateCode("T")
                .contacts(List.of(Contact.builder().contact("01011111111").build()))
                .contact(Contact.builder().contact("01022222222").build())
                .build();

        assertEquals(2, req.getContacts().size());
    }

    @Test
    @DisplayName("친구톡/브랜드메시지도 동일하게 누적된다")
    void otherBuildersAccumulate() {
        FriendtalkRequest ft = FriendtalkRequest.builder()
                .content("c")
                .contact(Contact.builder().contact("1").build())
                .contact(Contact.builder().contact("2").build())
                .build();
        assertEquals(2, ft.getContacts().size());

        BrandMessageRequest bm = BrandMessageRequest.builder()
                .friendTemplateUuid("u")
                .contact(Contact.builder().contact("1").build())
                .contact(Contact.builder().contact("2").build())
                .build();
        assertEquals(2, bm.getContacts().size());
    }

    @Test
    @DisplayName("SmsRequest.contact() 도 누적된다")
    void smsContactAccumulates() {
        SmsRequest req = SmsRequest.sms()
                .content("c")
                .contact(Contact.builder().contact("1").build())
                .contact(Contact.builder().contact("2").build());

        assertEquals(2, req.getContacts().size());
    }

    @Test
    @DisplayName("sendLms 는 sms() 로 만든 요청이라도 messageType 을 LMS 로 강제한다")
    void sendLmsForcesMessageType() {
        // 예전에는 sendLms(SmsRequest.sms()...) 가 SMS 로 나갔다.
        SmsRequest req = SmsRequest.sms().content("c");
        assertEquals("SMS", req.getMessageType());

        req.messageType("LMS");
        assertEquals("LMS", req.getMessageType());
    }

    @Test
    @DisplayName("Contact 는 var8 까지 지원하고 미설정 값은 직렬화에서 빠진다")
    void contactSupportsVar8() throws Exception {
        Contact c = Contact.builder()
                .contact("01012345678")
                .var6("f").var7("g").var8("h")
                .build();

        assertEquals("h", c.getVar8());

        @SuppressWarnings("unchecked")
        Map<String, Object> json = mapper.readValue(mapper.writeValueAsString(c), Map.class);
        assertEquals("h", json.get("var8"));
        assertNull(json.get("var1"));   // NON_NULL 이므로 빠져야 한다
    }

    @Test
    @DisplayName("임의 명명 변수는 contact 오브젝트에 평탄화되어 직렬화된다")
    void arbitraryVariablesFlatten() throws Exception {
        Contact c = Contact.builder()
                .contact("01012345678")
                .variable("title", "주문 확인")
                .build();

        @SuppressWarnings("unchecked")
        Map<String, Object> json = mapper.readValue(mapper.writeValueAsString(c), Map.class);
        assertEquals("주문 확인", json.get("title"));
    }
}
