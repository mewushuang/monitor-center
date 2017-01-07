package com.van.mc.common;

/**
 * 业务异常
 */
public class InternalException extends RuntimeException{
    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
