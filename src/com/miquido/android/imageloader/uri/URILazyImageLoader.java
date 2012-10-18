/**
 * Created by raho on 12/7/11.
 *
 * Copyright 2011 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.android.imageloader.uri;

import android.app.Application;
import com.miquido.android.imageloader.BaseLazyImageLoader;

import java.net.URI;

public class URILazyImageLoader extends BaseLazyImageLoader<URI> {

  public URILazyImageLoader(URIImageCache imageCache, Application application) {
    super(imageCache, new URIImageDownloader(), application);
  }
}