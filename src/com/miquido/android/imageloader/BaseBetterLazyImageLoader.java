package com.miquido.android.imageloader;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.miquido.android.imageloader.util.WaitingViewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 17.08.12
 * Time: 11:05
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseBetterLazyImageLoader<ImageSource> {

    private static final Logger logger = LoggerFactory.getLogger(BaseLazyImageLoader.class);

    private final ImageCache<ImageSource> imageCache;
    private final ImageDownloader<ImageSource> imageDownloader;
    private final Application application;
    private final WaitingViewsRepository<ImageSource, ImageView> waitingViewsRepository;
    private final Drawable noImageDrawable;
    private final Drawable imageLoadingDrawable;
    private final HashMap<ImageSource, BitmapDownloaderTask<ImageSource>> downloaderTasksMap;

    public BaseBetterLazyImageLoader(ImageCache<ImageSource> imageCache,
                                     ImageDownloader<ImageSource> imageDownloader,
                                     Application application,
                                     int noImageDrawableResourceId, int imageLoadingDrawableResourceId) {
        this.imageCache = imageCache;
        this.imageDownloader = imageDownloader;
        this.application = application;
        waitingViewsRepository = new WaitingViewsRepository<ImageSource, ImageView>();
        downloaderTasksMap = new HashMap<ImageSource, BitmapDownloaderTask<ImageSource>>();

        noImageDrawable = (noImageDrawableResourceId!=0) ?
                application.getResources().getDrawable(noImageDrawableResourceId) : null;
        imageLoadingDrawable = (imageLoadingDrawableResourceId!=0) ?
                application.getResources().getDrawable(imageLoadingDrawableResourceId) : null;

    }

    public void scheduleForLoad(ImageView imageView, ImageSource src) {

        boolean imageSet = setImageIfDownloadNotNeeded(imageView, src);
        if (imageSet)
            return;

        // download needed
        BitmapDownloaderTask<ImageSource> downloadTask = new BitmapDownloaderTask<ImageSource>(this, imageDownloader, src, null, null);
        downloadTask.execute();
        downloaderTasksMap.put(src, downloadTask);
        setImageIfDownloadNotNeeded(imageView, src); // set imageLoadingDrawable
    }

    public void unregisterFromUpdateAfterDownload(ImageView imageView) {
        waitingViewsRepository.remove(imageView);
    }
    /**
     *
     * @param imageView
     * @param src
     * @return true - if image was set, false - if image need to be downloaded
     */
    private boolean setImageIfDownloadNotNeeded(ImageView imageView, ImageSource src) {
        Bitmap bitmap = imageCache.get(src);
        if (bitmap!=null) {
            imageView.setImageBitmap(bitmap);
            return true;
        } else if (isLoading(src)) {
            waitingViewsRepository.addExplicitely(src, imageView);
            imageView.setImageDrawable(imageLoadingDrawable);
            return true;
        } else if (wasLoadingError(src)) {
            imageView.setImageDrawable(noImageDrawable);
            return true;
        } else {
            return false;
        }
    }

    private boolean isLoading(ImageSource src) {
        BitmapDownloaderTask<ImageSource> downloaderTask = downloaderTasksMap.get(src);
        return (downloaderTask!=null && downloaderTask.getStatus()!=AsyncTask.Status.FINISHED);
    }
    private boolean wasLoadingError(ImageSource src) {
        BitmapDownloaderTask<ImageSource> downloaderTask = downloaderTasksMap.get(src);
        return (downloaderTask!=null && downloaderTask.getStatus()==AsyncTask.Status.FINISHED);
    }


    protected void onDownloaded(ImageSource imageSource, Bitmap bitmap) {
        if (bitmap!=null) {
            imageCache.put(imageSource, bitmap);
        }
        for (ImageView imageView : waitingViewsRepository.getImageViewsWaitingFor(imageSource) ) {
            setImageIfDownloadNotNeeded(imageView, imageSource);
            waitingViewsRepository.remove(imageView);
        }
    }

    private static class BitmapDownloaderTask<ImageSource> extends AsyncTask<Void, Void, Bitmap> {

        private final BaseBetterLazyImageLoader imageLoader;
        private final ImageDownloader<ImageSource> imageDownloader;
        private final ImageSource src;
        private Integer reqWidth;
        private Integer reqHeight;

        public BitmapDownloaderTask(BaseBetterLazyImageLoader imageLoader, ImageDownloader<ImageSource> imageDownloader,
                                    ImageSource src, Integer reqWidth, Integer reqHeight) {
            this.imageLoader = imageLoader;
            this.imageDownloader = imageDownloader;
            this.src = src;
            this.reqWidth = reqWidth;
            this.reqHeight = reqHeight;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            logger.debug("DownloadTask: Start downloading from {}", src);
            return imageDownloader.downloadBitmap(src, reqWidth, reqHeight);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            logger.debug("DownloadTask: Downloaded from {}", src);
            imageLoader.onDownloaded(src, bitmap);
        }
    }

}
