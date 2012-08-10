package com.miquido.vtv.codsservices.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 20:38
 * To change this template use File | Settings | File Templates.
 */
public class CodsResponseFormatException extends CodsSystemException {

    public CodsResponseFormatException(String detailMessage) {
        super(detailMessage);
    }

    public CodsResponseFormatException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
