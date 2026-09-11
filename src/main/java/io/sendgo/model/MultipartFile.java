package io.sendgo.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * multipart 업로드에 붙일 파일.
 *
 * <p>서류·이미지 첨부가 있는 관리 API(발신번호 등록, 이미지 템플릿, 검수 첨부)는
 * JSON 으로 보낼 수 없다. 서버 검증이 확장자를 보므로 파일명은 반드시 실제
 * 확장자를 포함해야 한다.
 *
 * <pre>
 * MultipartFile csu = MultipartFile.of("csuCertificate", Path.of("csu.pdf"), "application/pdf");
 * </pre>
 */
public class MultipartFile {

    private final String fieldName;
    private final String fileName;
    private final String contentType;
    private final byte[] content;

    public MultipartFile(String fieldName, String fileName, String contentType, byte[] content) {
        this.fieldName   = fieldName;
        this.fileName    = fileName;
        this.contentType = contentType == null ? "application/octet-stream" : contentType;
        this.content     = content;
    }

    /** 파일 경로에서 읽어 만든다. */
    public static MultipartFile of(String fieldName, Path path, String contentType) throws IOException {
        return new MultipartFile(fieldName, path.getFileName().toString(), contentType, Files.readAllBytes(path));
    }

    /** 이미 메모리에 있는 바이트로 만든다. */
    public static MultipartFile of(String fieldName, String fileName, String contentType, byte[] content) {
        return new MultipartFile(fieldName, fileName, contentType, content);
    }

    /** 같은 내용으로 필드 이름만 바꾼 복사본. 서비스가 필드명을 정할 때 쓴다. */
    public MultipartFile withFieldName(String newFieldName) {
        return new MultipartFile(newFieldName, fileName, contentType, content);
    }

    public String getFieldName()   { return fieldName; }
    public String getFileName()    { return fileName; }
    public String getContentType() { return contentType; }
    public byte[] getContent()     { return content; }
}
