/**
 * Created by raho on 12/7/11.
 *
 * Copyright 2011 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.android.imageloader.uri;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.miquido.android.imageloader.ImageDownloader;
import com.miquido.android.imageloader.helper.BitmapHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;

public class URIImageDownloader implements ImageDownloader<URI> {

  private static final Logger log = LoggerFactory.getLogger(URIImageDownloader.class);

  private static HttpClient client = createClient();
  public static final int SO_TIMEOUT_MILLIS = 10000;
  public static final int CONNECTION_TIMEOUT_MILLIS = 10000;

  public Bitmap downloadBitmap(URI uri, Integer reqWidth, Integer reqHeight) {
    final HttpGet getRequest = new HttpGet(uri);
    try {
      HttpResponse response = client.execute(getRequest);
      final int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        return null;
      }

      final HttpEntity entity = response.getEntity();
      if (entity != null) {
        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
        InputStream inputStream = null;
        try {
          if (reqWidth != null && reqHeight != null) {
            return BitmapHelper.decodeSampledBitmapFromStream(bufHttpEntity, reqWidth, reqHeight);
          }
          inputStream = bufHttpEntity.getContent();
          return BitmapFactory.decodeStream(inputStream);
        } finally {
          if (inputStream != null) {
            inputStream.close();
          }
          entity.consumeContent();
        }
      }
    } catch (Exception e) {
      log.error("Cannot download image!", e);
      getRequest.abort();
    }
    return null;
  }

  private static HttpClient createClient() {
    HttpParams params = new BasicHttpParams();
    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    HttpProtocolParams.setContentCharset(params, "utf-8");
    // Set the timeout in milliseconds until a connection is established.
    HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT_MILLIS);

    // Set the default socket timeout (SO_TIMEOUT)
    // in milliseconds which is the timeout for waiting for data.
    HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT_MILLIS);
    params.setBooleanParameter("http.protocol.expect-continue", false);
    params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 20);
    params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(10));

    //registers schemes for both http and https
    SchemeRegistry registry = new SchemeRegistry();
    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
    sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    registry.register(new Scheme("https", sslSocketFactory, 443));

    ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
    return new DefaultHttpClient(manager, params);
  }
}
