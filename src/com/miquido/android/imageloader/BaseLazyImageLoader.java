/**
 * Created by raho on 12/7/11.
 *
 * Copyright 2011 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.android.imageloader;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.miquido.android.imageloader.util.ImageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

public abstract class BaseLazyImageLoader<T> {
  private static final Logger logger = LoggerFactory.getLogger(BaseLazyImageLoader.class);

  private final ImageCache<T> imageCache;
  private final ImageDownloader<T> imageDownloader;
  private final Application application;

  public BaseLazyImageLoader(ImageCache<T> imageCache, ImageDownloader<T> imageDownloader, Application application) {
    this.imageCache = imageCache;
    this.imageDownloader = imageDownloader;
    this.application = application;
  }

  public void scheduleForLoad(ImageView imageView, T src) {
    scheduleForLoad(imageView, src, new ColorDrawable(Color.TRANSPARENT));
  }

  public void scheduleForLoad(ImageView imageView, T src, Drawable noImageDrawable) {
    scheduleInLoadingQueue(imageView, src, noImageDrawable, 0, null, null, null, false);
  }

  public void scheduleForLoad(ImageView imageView, T src, Drawable noImageDrawable, Matrix matrix, boolean roundCorners) {
    scheduleInLoadingQueue(imageView, src, noImageDrawable, 0, null, null, matrix, roundCorners);
  }

  public void scheduleForLoad(ImageView imageView, T src, Drawable noImageDrawable, Integer reqWidth, Integer reqHeight) {
    scheduleInLoadingQueue(imageView, src, noImageDrawable, 0, reqWidth, reqHeight, null, false);
  }

  public void scheduleForLoad(ImageView imageView, T src, Drawable noImageDrawable, int imageLoadingResource) {
    scheduleInLoadingQueue(imageView, src, noImageDrawable, imageLoadingResource, null, null, null, false);
  }

  private void scheduleInLoadingQueue(ImageView imageView, T src, Drawable noImageDrawable, int imageLoadingResource,
                                      Integer reqWidth, Integer reqHeight, Matrix matrix, boolean roundCorners) {
    Bitmap btm = imageCache.get(src);
    if (btm != null) {
//        logger.debug("scheduleInLoadingQueue: got from cache to view:{}  url:{}", imageView.toString(), src.toString());
        cancelPotentialDownload(imageView, src);
      if (matrix == null) {
        if (roundCorners) {
          Bitmap roundedCornersBtm = ImageHelper.getRoundedCornerBitmap(btm);
          imageView.setImageBitmap(roundedCornersBtm);
        } else {
          imageView.setImageBitmap(btm);
        }
      } else {
        Bitmap matrixBitmap = Bitmap.createBitmap(btm, 0, 0, btm.getWidth(), btm.getHeight());
        if (roundCorners) {
          matrixBitmap = ImageHelper.getRoundedCornerBitmap(matrixBitmap);
        }
        Bitmap matrixBitmapWithMatrix = Bitmap.createBitmap(matrixBitmap, 0, 0, matrixBitmap.getWidth(), matrixBitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(matrixBitmapWithMatrix);
      }

    } else if (cancelPotentialDownload(imageView, src)) {
      BitmapDownloaderTask task;
      if (reqWidth != null && reqHeight != null) {
        task = new BitmapDownloaderTask(imageView, src, noImageDrawable, reqWidth, reqHeight, matrix, roundCorners);
      } else {
        task = new BitmapDownloaderTask(imageView, src, noImageDrawable, matrix, roundCorners);
      }
      Bitmap bitmap = null;
      if (imageLoadingResource != 0) {
        bitmap = BitmapFactory.decodeResource(application.getResources(), imageLoadingResource);
      }
      DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task, bitmap);
      imageView.setImageDrawable(downloadedDrawable);
//        logger.debug(String.format("scheduleInLoadingQueue: execute download task view:%s  url:%s   task:%s downloadDrawable: %s ",
//                imageView.toString(), src.toString(), task.toString(), downloadedDrawable.toString()));
        task.execute(src);
    }
  }

  public void cancelPotentialDownloadTo(ImageView imageView) {
    cancelPotentialDownload(imageView, null);
  }

  private boolean cancelPotentialDownload(ImageView imageView, T src) {
    BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
//      logger.debug(String.format("cancelPotentialDownload: imageView:%s  uri:%s  bitmapDownloaderTask:%s",
//              imageView.toString(), (src!=null)?src.toString():"null", bitmapDownloaderTask));

    if (bitmapDownloaderTask != null) {
      T bitmapSrc = bitmapDownloaderTask.src;
      if ((src==null) || (bitmapSrc == null) || (!bitmapSrc.equals(src))) {
//          logger.debug("cancelPotentialDownload: Download cancelled of {} to view:{}", imageView.toString(), (src!=null)?src.toString():"null");
        bitmapDownloaderTask.cancel(true);
      } else {
        return false;
      }
    }
    return true;
  }

  private BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
    if (imageView != null) {
      Drawable drawable = imageView.getDrawable();
//        logger.debug(String.format("getBitmapDownloaderTask: imageView:%s  drawable:%s",    imageView.toString(), (drawable!=null)?drawable.toString():""));
        if (drawable instanceof BaseLazyImageLoader.DownloadedDrawable) {
        DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
        return downloadedDrawable.getBitmapDownloaderTask();
      }
    }
    return null;
  }

  private class BitmapDownloaderTask extends AsyncTask<T, Void, Bitmap> {

    private final T src;
    private final WeakReference<ImageView> imageViewReference;
    private final Drawable noImageDrawable;
    private Integer reqWidth;
    private Integer reqHeight;
    private Matrix matrix;
    private boolean roundCorners;

    public BitmapDownloaderTask(ImageView imageView, T src, Drawable noImageDrawable, Matrix matrix, boolean roundCorners) {
      this.imageViewReference = new WeakReference<ImageView>(imageView);
      this.noImageDrawable = noImageDrawable;
      this.src = src;
      this.matrix = matrix;
      this.roundCorners = roundCorners;
    }

    public BitmapDownloaderTask(ImageView imageView, T src, Drawable noImageDrawable, Integer reqWidth, Integer reqHeight, Matrix matrix, boolean roundCorners) {
      this(imageView, src, noImageDrawable, matrix, roundCorners);
      this.reqWidth = reqWidth;
      this.reqHeight = reqHeight;
    }

    @Override
    protected Bitmap doInBackground(T... src) {
      T key = src[0];
      Bitmap bitmap = imageCache.get(key);
      if (bitmap != null) {
        return bitmap;
      } else {
        bitmap = imageDownloader.downloadBitmap(key, reqWidth, reqHeight);
        if (bitmap != null) {
          imageCache.put(key, bitmap);
        }
        return bitmap;
      }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      if (isCancelled()) {
        bitmap = null;
        logger.debug("DownloadTask: Downloaded from {} but cancelled. view: {}", src, imageViewReference.get());
      }
      if (imageViewReference != null) {
        ImageView imageView = imageViewReference.get();
        if (imageView != null) {
            logger.debug("DownloadTask: Downloaded from {} set view: {}", src, imageViewReference.get());
          if (bitmap != null) {
            if (roundCorners) {
              bitmap = ImageHelper.getRoundedCornerBitmap(bitmap);
            }
            if (matrix == null) {
              imageView.setImageBitmap(bitmap);
            } else {
              bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
              imageView.setImageBitmap(bitmap);
            }
          } else {
            imageView.setImageDrawable(noImageDrawable);
//              logger.debug("DownloadTask:       ... setting \"noImage\" to view: {}", imageView);
          }
        }
      }
    }
  }

  class DownloadedDrawable extends BitmapDrawable {
    private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

    public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, Bitmap bitmap) {
      super(bitmap);
      bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
    }

    public BitmapDownloaderTask getBitmapDownloaderTask() {
      return bitmapDownloaderTaskReference.get();
    }
  }

}
