package wjt.web;

import lombok.ToString;

@ToString
public class HttpResult {
    private final int errCode;
    private final String errMsg;
    private final Object data;


    public HttpResult(int errCode, String errMsg, Object data) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }


}
