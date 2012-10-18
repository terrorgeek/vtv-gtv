package com.miquido.vtv.view;

import android.app.Application;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.miquido.android.imageloader.uri.URIBetterLazyImageLoader;
import com.miquido.android.imageloader.uri.URIImageCache;
import com.miquido.android.imageloader.uri.URIImageDownloader;
import com.miquido.android.imageloader.uri.URILazyImageLoader;
import com.miquido.vtv.R;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 23:33
 * To change this template use File | Settings | File Templates.
 */
public class ViewModule extends AbstractModule {

  @Override
  protected void configure() {

    bind(URIImageDownloader.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  URIImageCache provideURIImageCache(Application application) {
    URIImageCache uriImageCache = new URIImageCache(application);
    uriImageCache.cleanCacheDir();
    return uriImageCache;
  }

  @Provides
  URILazyImageLoader provideURILazyImageLoader(URIImageCache uriImageCache, Application application) {
    return new URILazyImageLoader(uriImageCache, application);
  }

  @Provides
  @Named("friendsAvatarsImageLoader")
  URIBetterLazyImageLoader provideFriendsAvatarsImageLoader(URIImageCache uriImageCache, Application application) {
    return new URIBetterLazyImageLoader(uriImageCache, application, R.drawable.no_image, R.drawable.no_image);
  }

}
