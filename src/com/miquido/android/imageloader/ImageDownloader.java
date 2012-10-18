/**
 * Created by raho on 12/7/11.
 *
 * Copyright 2011 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.android.imageloader;

import android.graphics.Bitmap;

/**
 * Image downloader interface. Implement it to create your own image downloader implementation.
 * <p/>
 * This is a generic interface, it means that it's implementation can use different data types as source.
 * For example image can be downloaded by providing URL, String value or even a number (something like image ID).
 * <p/>
 * It's up to you what will be the source type you will use to download image.
 *
 * @param <T> - the source that will be used to download image, you can chose the type as you want (see above)
 */
public interface ImageDownloader<T> {

  /**
   * Downloads bitmap for the provided key/source.
   *
   * @param src       - source from which the bitmap will be downloaded, it can be a URL, String or even number.
   *                  It's up to you what type do you chose.
   * @param reqWidth  required image width in pixels
   * @param reqHeight required image height in pixels
   * @return - returns downloaded bitmap, in case bitmap doesn't exist it should return null
   */
  Bitmap downloadBitmap(T src, Integer reqWidth, Integer reqHeight);

}
