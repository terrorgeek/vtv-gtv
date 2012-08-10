package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.codsservices.dataobjects.LoginInput;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 11:55
 * To change this template use File | Settings | File Templates.
 */
public class LoginInputJsonWriter implements JsonWriter<LoginInput> {

//    "login":[login],
//            "password":[password-hash],
//            "application_name":[string] //optional, used to identify your application

    @Override
    public JSONObject createJsonFromObject(final LoginInput loginInput) {

        try {
            final JSONObject jsonObject = new JSONObject();

            jsonObject.put("login", loginInput.getLogin());
            jsonObject.put("password", loginInput.getPasswordHash());
            jsonObject.putOpt("application_name", loginInput.getApplicationName());

            return jsonObject;
        } catch (JSONException e) {
            throw new RuntimeException("Error while creating JsonObject from LoginInput.", e);
        }
    }

}
