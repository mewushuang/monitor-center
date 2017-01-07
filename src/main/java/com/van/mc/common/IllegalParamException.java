package com.van.mc.common;

/**
 * Created by van on 2016/12/6.
 */
public class IllegalParamException extends InternalException {


    public IllegalParamException(String message) {
        super(message);
    }

    public IllegalParamException(String message, Throwable cause) {
        super(message, cause);
    }


}
