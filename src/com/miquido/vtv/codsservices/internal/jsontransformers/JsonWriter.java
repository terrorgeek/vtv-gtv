package com.miquido.vtv.codsservices.internal.jsontransformers;

import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public interface JsonWriter<Type> {

    JSONObject createJsonFromObject(Type object);

}
