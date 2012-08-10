package com.miquido.vtv.codsservices.internal;

import android.net.http.AndroidHttpClient;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.codsservices.exceptions.CodsConnectionException;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import com.miquido.vtv.codsservices.CodsConfiguration;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 20:19
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class CodsHttpJsonClient {

    private static final Logger logger = LoggerFactory.getLogger(CodsHttpJsonClient.class);

    private static final String API_VERSION = "v2";
    private static final String USER_AGENT = "vTV Google TV Application";

    @Inject
    private CodsConfiguration codsConfiguration;

    private CookieStore cookieStore = new BasicCookieStore();


    public JSONObject get(String invokeServicePath) {
        logger.debug("CodsHttpJsonClient.get for invokeServicePath: {}", invokeServicePath);

        String serviceUri = String.format("%s/%s%s", codsConfiguration.getCodsServerURL(), API_VERSION, invokeServicePath);
        logger.debug("URL: {}", serviceUri);

        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        AndroidHttpClient httpClient = AndroidHttpClient.newInstance(USER_AGENT);

        return executeHttpRequest(new HttpGet(serviceUri));
    }

    public JSONObject getWithSession(String sessionId, String invokeServicePath) {
        return get(String.format("/%s%s", sessionId, invokeServicePath));
    }

    public JSONObject post(String invokeServicePath, JSONObject jsonMessage) {
        logger.debug("CodsHttpJsonClient.post for invokeServicePath: {}  message:{}", invokeServicePath, jsonMessage.toString());

        String serviceUri = String.format("%s/%s%s", codsConfiguration.getCodsServerURL(), API_VERSION, invokeServicePath);
        logger.debug("URL: {}", serviceUri);

        HttpPost httpPost = new HttpPost(serviceUri);
        try {
            httpPost.setEntity(new StringEntity(jsonMessage.toString(), "utf8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error while creating StringEntity.", e);
        }
        return executeHttpRequest(httpPost);
    }

    protected JSONObject executeHttpRequest(HttpUriRequest httpUriRequest) {

        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        AndroidHttpClient httpClient = AndroidHttpClient.newInstance(USER_AGENT);

        try {
            HttpResponse httpResponse = httpClient.execute(httpUriRequest, httpContext);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(byteArrayOutputStream);
            String charSet = EntityUtils.getContentCharSet(httpResponse.getEntity());
            String responseString = byteArrayOutputStream.toString( ((charSet!=null)? charSet : "utf8") );
            logger.debug("vTV: Request: {} -> Response: {}", httpUriRequest.getRequestLine().toString(), responseString);

            JSONObject responseObject = (JSONObject) new JSONTokener(responseString).nextValue();

            return responseObject;

        } catch (IOException e) {
            throw new CodsConnectionException("Http request to CODS server ends with error.", e);
        } catch (JSONException e) {
            throw new CodsResponseFormatException("Response from CODS server has wrong format. It's not JSON object.", e);
        }

    }

}
