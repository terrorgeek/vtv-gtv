package com.miquido.vtv.codsservices.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 11:53
 * To change this template use File | Settings | File Templates.
 */
public class CodsConnectionException extends CodsSystemException {

    public CodsConnectionException(String detailMessage) {
        super(detailMessage);
    }

    public CodsConnectionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
