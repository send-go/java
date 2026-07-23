package io.sendgo.exception;

/**
 * Sendgo API 호출 중 발생한 예외.
 */
public class SendgoException extends RuntimeException {

    private final int    statusCode;
    private final String errorCode;
    private final String endpoint;
    private final String apiVersion;

    public SendgoException(String message) {
        super(message);
        this.statusCode = 0;
        this.errorCode  = null;
        this.endpoint   = "";
        this.apiVersion = "";
    }

    public SendgoException(String message, int statusCode, String errorCode,
                           String endpoint, String apiVersion) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode  = errorCode;
        this.endpoint   = endpoint;
        this.apiVersion = apiVersion;
    }

    public int    getStatusCode()  { return statusCode; }
    public String getErrorCode()   { return errorCode; }
    public String getEndpoint()    { return endpoint; }
    public String getApiVersion()  { return apiVersion; }
}
