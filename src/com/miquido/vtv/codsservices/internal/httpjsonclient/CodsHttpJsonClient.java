package com.miquido.vtv.codsservices.internal.httpjsonclient;

import android.net.http.AndroidHttpClient;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.codsservices.exceptions.CodsConnectionException;
import com.miquido.vtv.codsservices.exceptions.CodsSystemException;
import com.miquido.vtv.codsservices.internal.CodsConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 20:19
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class CodsHttpJsonClient {

    @Data @AllArgsConstructor(suppressConstructorProperties = true)
    public static class JsonResponse {
        int statusCode;
        JSONObject jsonObject;
    }

    private static final Logger logger = LoggerFactory.getLogger(CodsHttpJsonClient.class);

    private AndroidHttpClient httpClient;

    private final CodsConfiguration codsConfiguration;

    @Inject
    public CodsHttpJsonClient(CodsConfiguration codsConfiguration) {
        this.codsConfiguration = codsConfiguration;
        this.httpClient = AndroidHttpClient.newInstance(codsConfiguration.getUserAgent());
    }

    public JsonResponse get(String invokeServicePath, CodsUrlParams codsUrlParams) {
        logger.debug("CodsHttpJsonClient.get for invokeServicePath: {}", invokeServicePath);
        try {
            URI serviceUri = evalServiceUri(invokeServicePath, codsUrlParams);
            logger.debug("URL: {}", serviceUri);

            return executeHttpRequest(new HttpGet(serviceUri));

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private URI evalServiceUri(String invokeServicePath, CodsUrlParams codsUrlParams) throws URISyntaxException {
        return new URI(String.format("%s/%s%s%s", codsConfiguration.getCodsServerURL(), codsConfiguration.getApiVersion(),
                invokeServicePath, (codsUrlParams!=null)?codsUrlParams.toString():""));
    }

    public JsonResponse post(String invokeServicePath, CodsUrlParams codsUrlParams, JSONObject jsonMessage) {
        logger.debug("CodsHttpJsonClient.post for invokeServicePath: {}  message:{}", invokeServicePath, jsonMessage.toString());
        return postOrPut(new HttpPost(), invokeServicePath, codsUrlParams, jsonMessage);
    }
    public JsonResponse put(String invokeServicePath, CodsUrlParams codsUrlParams, JSONObject jsonMessage) {
        logger.debug("CodsHttpJsonClient.put for invokeServicePath: {}  message:{}", invokeServicePath, jsonMessage.toString());
        return postOrPut(new HttpPut(), invokeServicePath, codsUrlParams, jsonMessage);
    }

    private JsonResponse postOrPut(HttpEntityEnclosingRequestBase httpRequest, String invokeServicePath, CodsUrlParams codsUrlParams, JSONObject jsonMessage) {

        URI serviceUri = null;
        try {
            serviceUri = evalServiceUri(invokeServicePath, codsUrlParams);
            logger.debug("URL: {}", serviceUri);

            httpRequest.setURI(serviceUri);
            httpRequest.setEntity(new StringEntity(jsonMessage.toString(), "utf8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error while creating StringEntity.", e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return executeHttpRequest(httpRequest);
    }

    protected JsonResponse executeHttpRequest(HttpUriRequest httpUriRequest) {

        String responseString = null;
        int statusCode = 0;
        try {
            HttpResponse httpResponse = httpClient.execute(httpUriRequest);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(byteArrayOutputStream);
            String charSet = EntityUtils.getContentCharSet(httpResponse.getEntity());
            responseString = byteArrayOutputStream.toString( ((charSet!=null)? charSet : "utf8") );

            statusCode =  httpResponse.getStatusLine().getStatusCode();
        } catch (IOException e) {
            throw new CodsConnectionException("Http request to CODS server ends with error.", e);
        }
        logger.debug(String.format("vTV: Request: %s -> Response Code:%d  Body:%s",
                httpUriRequest.getRequestLine().toString(), statusCode, responseString));


        JSONObject responseObject = null;
        if (responseString!=null) {
            try {
                responseObject = (JSONObject) new JSONTokener(responseString).nextValue();
            } catch (JSONException e) {
                if (!isErrorStatusCode(statusCode))
                    throw new CodsSystemException("Response from CODS server has wrong format. It's not JSON object.", e);
            }
        }

        return new JsonResponse(statusCode, responseObject);
    }

    private boolean isErrorStatusCode(int statusCode) {
        return (statusCode>300);
    }

}
