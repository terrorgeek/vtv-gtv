package com.miquido.android.imageloader.uri;

import android.app.Application;
import com.miquido.android.imageloader.BaseBetterLazyImageLoader;
import com.miquido.android.imageloader.ImageCache;
import com.miquido.android.imageloader.ImageDownloader;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 17.08.12
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class URIBetterLazyImageLoader extends BaseBetterLazyImageLoader<URI> {

    public URIBetterLazyImageLoader(ImageCache<URI> imageCache, Application application, int noImageDrawableResourceId, int imageLoadingDrawableResourceId) {
        super(imageCache, new URIImageDownloader(), application, noImageDrawableResourceId, imageLoadingDrawableResourceId);
    }

}
