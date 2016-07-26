package com.shaker.link.core.exception;

/**
 * shaker link exception
 * Created by yinghuihong on 16/7/26.
 */
public class ShakerLinkException extends Exception {

    private int code;

    public ShakerLinkException(int code) {
        this.code = code;
    }

    public ShakerLinkException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ShakerLinkException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
