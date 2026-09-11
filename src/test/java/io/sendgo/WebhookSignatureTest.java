package io.sendgo;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebhookSignatureTest {

    @Test
    void acceptsAValidSignatureAndRejectsEverythingElse() throws Exception {
        byte[] body = "{\"event\":\"sender.status_changed\"}".getBytes(StandardCharsets.UTF_8);

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec("k".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = HexFormat.of().formatHex(mac.doFinal(body));

        assertTrue(WebhookService.verifySignature(body, signature, "k"));
        assertFalse(WebhookService.verifySignature(body, "deadbeef", "k"));
        assertFalse(WebhookService.verifySignature(body, null, "k"));
        // 다른 키로 만든 서명은 통과하면 안 된다.
        assertFalse(WebhookService.verifySignature(body, signature, "other"));
    }
}
