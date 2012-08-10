package com.miquido.vtv.codsservices.internal.jsontransformers;

import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 19:19
 * To change this template use File | Settings | File Templates.
 */
public interface JsonReader<Type> {

    boolean isCorrectType(JSONObject jsonObject);

    Type createObjectFromJson(JSONObject jsonObject);

}
