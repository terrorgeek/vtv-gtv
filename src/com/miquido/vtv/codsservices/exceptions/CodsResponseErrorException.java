package com.miquido.vtv.codsservices.exceptions;

import com.miquido.vtv.codsservices.dataobjects.CodsError;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 30.08.12
 * Time: 00:57
 * To change this template use File | Settings | File Templates.
 */
public class CodsResponseErrorException extends CodsSystemException {

    public CodsResponseErrorException(int httpStatusCode, String message) {
        super(String.format("Response StatusCode:%d  Message: %s", httpStatusCode, message));
    }
    public CodsResponseErrorException(String detailMessage) {
        super(detailMessage);
    }

    public CodsResponseErrorException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
