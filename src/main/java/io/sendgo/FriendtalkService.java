package io.sendgo;

import io.sendgo.model.FriendtalkRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 카카오 친구톡 발송 서비스.
 *
 * @deprecated 친구톡은 카카오 정책에 따라 2025-12-31 로 종료되었다.
 *             2026-01-01 부터 친구톡 발송 요청은 카카오 측에서 브랜드메시지(자유형)로
 *             자동 대체 발송되므로, 이 서비스를 호출해도 실제로 나가는 것은 브랜드메시지다.
 *             신규 연동은 {@link BrandMessageService} 를 사용한다. 다만 자유 본문
 *             타입(FT/FI/FW)을 개별 수신자에게 보내는 경로는 아직 이 서비스뿐이다 —
 *             브랜드메시지 API 는 그 조합에 NOT_A_BRAND_MESSAGE 를 반환한다.
 *             메시지 타입은 1:1 대응된다 — FT→BT, FI→BI, FW→BW, FL→BL, FC→BC, FM→BM, FP→BP, FA→BA.
 */
@Deprecated
public class FriendtalkService {

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    FriendtalkService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /**
     * 친구톡 전송.
     *
     * @deprecated 2025-12-31 종료. {@link BrandMessageService#send} 를 사용한다.
     */
    @Deprecated
    public Map<String, Object> send(FriendtalkRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content",        request.getContent());
        body.put("messageType",    request.getMessageType());
        body.put("scheduleType",   request.getScheduleType());
        body.put("at",             request.getAt());
        body.put("buttons",        request.getButtons());
        body.put("imageUrl",       request.getImageUrl());
        body.put("imageLink",      request.getImageLink());
        body.put("adFlag",         request.getAdFlag());
        body.put("wide",           request.getWide());
        body.put("replaceSms",     request.getReplaceSms());
        body.put("smsContent",     request.getSmsContent());
        body.put("kakaoSenderKey", config.getKakaoSenderKey());
        body.put("senderKey",      config.getSmsSenderKey());
        body.values().removeIf(v -> v == null);

        return http.post(buildUrl("friends"), body);
    }

    private String buildUrl(String resource) {
        return config.getBaseUrl() + "/api/" + config.getApiVersion() + "/" + resource + "/send";
    }
}
