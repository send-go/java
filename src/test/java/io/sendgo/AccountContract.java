package io.sendgo;
import java.util.Map;
import io.sendgo.exception.SendgoException;
public class AccountContract {
 public static void main(String[] args) {
 String url = System.getenv("SENDGO_TEST_URL") + "/";
 try { new AccountClient(""); throw new AssertionError("빈 토큰 허용"); } catch (IllegalArgumentException e) {}
 AccountClient c = new AccountClient("test-agent", url);
check(c.me());
check(c.organizations());
check(c.selectOrganization(null));
check(c.selectOrganization("team-id"));
check(c.apiKeys());
check(c.createApiKey(Map.of("name", "한글 이름", "ipAddresses", java.util.List.of(Map.of("ip", "192.0.2.1", "description", "서버")))));
check(c.apiKey("key/id ?"));
check(c.updateApiKey("key/id ?", "새 이름"));
check(c.deleteApiKey("key/id ?"));
check(c.issueToken("key/id ?"));
check(c.allowedIps("key/id ?"));
check(c.addAllowedIp("key/id ?", Map.of("ip", "192.0.2.1", "description", "서버")));
check(c.deleteAllowedIp("key/id ?", "ip/id ?"));
for (String token : new String[]{"expired", "forbidden"}) {
 try { new AccountClient(token, url).me(); throw new AssertionError("오류가 발생하지 않음"); }
 catch (SendgoException e) {
  if (e.getStatusCode() != (token.equals("expired") ? 401 : 403) || !e.getErrorCode().equals(token.equals("expired") ? "AGENT_TOKEN_EXPIRED" : "AGENT_ABILITY_MISSING")) throw e;
 }
}
 }
 static void check(Map<String, Object> data) { if (!"Success".equals(data.get("message"))) throw new AssertionError(data); }
}
