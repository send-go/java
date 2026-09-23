package io.sendgo;

import java.util.List;
import io.sendgo.model.NoticeTemplateRequest;
import io.sendgo.model.BrandTemplateRequest;
import io.sendgo.exception.SendgoException;

/** 모의 HTTP 서버 전용 계약 검증. */
public class TemplateFolderContract {
    public static void main(String[] args) {
        SendgoClient c = new SendgoClient(SendgoConfig.builder().accessKey("test-access").secretKey("test-secret").apiVersion("v2").baseUrl(System.getenv("SENDGO_TEST_URL")).build());
        String f = "11111111-1111-4111-8111-111111111111", key = "채널 /?";
        c.templateFolders().list();
        c.templateFolders().list("brand", key);
        c.templateFolders().create("주문", null);
        c.templateFolders().create("하위", f);
        for (String kind : List.of("notice", "brand")) {
            c.templateFolders().assign(kind, key, List.of("코드 1", "code/2"), f);
            c.templateFolders().assign(kind, key, List.of("코드 1"), null);
        }
        c.noticeTemplates().list(null, null, null, null, "none");
        c.brandTemplates().list(null, null, null, f);
        c.noticeTemplates().create(NoticeTemplateRequest.builder().templateName("테스트").folderUuid(f).build());
        c.brandTemplates().create(BrandTemplateRequest.builder().templateName("테스트").folderUuid(f).build());
        String[] kinds = {"forbidden", "invalid", "missing"}, codes = {"ACCESS_KEY_NOT_APPROVED", "VALIDATION_FAILED", "TEMPLATE_FOLDER_NOT_FOUND"};
        int[] statuses = {403,422,404};
        for (int i=0; i<kinds.length; i++) {
            try { c.templateFolders().list(kinds[i], null); throw new AssertionError("오류가 발생하지 않음"); }
            catch (SendgoException e) { if (e.getStatusCode()!=statuses[i] || !codes[i].equals(e.getErrorCode())) throw e; }
        }
    }
}
