package com.miquido.vtv.codsservices;

import com.google.inject.Singleton;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class CodsConfiguration {


    public String getCodsServerURL() {
        return "http://api.cods.pyctex.net/rest";
    }

}
