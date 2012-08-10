package com.miquido.vtv.codsservices.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
public class CodsSystemException extends RuntimeException {

    public CodsSystemException(String detailMessage) {
        super(detailMessage);
    }

    public CodsSystemException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
