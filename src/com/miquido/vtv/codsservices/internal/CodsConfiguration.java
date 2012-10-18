package com.miquido.vtv.codsservices.internal;

import com.google.inject.Singleton;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class CodsConfiguration {

    @Getter private String codsServerURL = "http://api.cods.pyctex.net/rest";
    @Getter private String apiVersion = "v3";
    @Getter private String userAgent = "vTV Google TV Application";

}
