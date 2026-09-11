package io.sendgo;

import io.sendgo.model.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 카카오 이미지 업로드 — 브랜드메시지 템플릿에 넣을 URL 발급.
 *
 * <p>v2 전용, 기업 계정 전용. 브랜드메시지 템플릿의 {@code imageUrl} 은 아무
 * URL 이나 되는 게 아니라 <b>카카오가 호스팅하는 URL</b> 이어야 하고, 그 URL 을
 * 얻는 방법이 이 업로드뿐이다.
 */
public class KakaoImageService {

    /** 파일 하나를 올리고 URL 하나를 받는 유형. */
    public static final List<String> SINGLE_TYPES = List.of(
            "alimtalk", "alimtalk_highlight", "default", "wide", "wide_item_list_first");

    /** 파일 여러 개를 올리는 유형과 최대 개수. */
    public static final Map<String, Integer> MULTI_TYPES = Map.of(
            "wide_item_list", 4,
            "carousel_feed", 10,
            "carousel_commerce", 11);

    private final SendgoHttpClient http;
    private final SendgoConfig     config;

    KakaoImageService(SendgoHttpClient http, SendgoConfig config) {
        this.http   = http;
        this.config = config;
    }

    /** 업로드 가능한 유형과 제약. */
    public Map<String, Object> types() {
        return http.get(url("types"));
    }

    /** 단일 이미지 업로드. jpg/png, 2MB 이하. {@code data.imageUrl} 을 받는다. */
    public Map<String, Object> upload(String type, MultipartFile image) {
        return http.postMultipart(url(type), Map.of(), List.of(image.withFieldName("image")));
    }

    /** 다중 이미지 업로드. 유형별 최대 개수가 다르다. */
    public Map<String, Object> uploadMany(String type, List<MultipartFile> images) {
        // 서버는 images[0], images[1] 형태를 기대한다.
        List<MultipartFile> named = new ArrayList<>(images.size());
        for (int i = 0; i < images.size(); i++) {
            named.add(images.get(i).withFieldName("images[" + i + "]"));
        }

        return http.postMultipart(url(type), Map.of(), named);
    }

    private String url(String segment) {
        return config.getBaseUrl() + "/api/" + config.getApiVersion()
                + "/kakao-images/" + URLEncoder.encode(segment, StandardCharsets.UTF_8);
    }
}
