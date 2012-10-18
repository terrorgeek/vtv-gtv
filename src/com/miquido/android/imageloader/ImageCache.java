/**
 * Created by raho on 12/7/11.
 *
 * Copyright 2011 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.android.imageloader;

import android.graphics.Bitmap;

/**
 * Image cache interface. Implement it in order to create compatible image cache
 * that you can provide to lazy image downloader.
 * <p/>
 * Implementation should be thread safe, it means that cache can be used by different threads.
 * <p/>
 * Cache should always return the same value for the same key. In case someone will put an image bitmap
 * with already cached key, the old bitmap should be replaced by the new bitmap.
 *
 * @param <T> - key object, it is an object that will be used as the image key.
 *            For example it can be URL, String, ID (for example long value)
 */
public interface ImageCache<T> {

  /**
   * Returns bitmap instance for given key. It should always return the same value for the same key.
   *
   * @param key - key object, it is an object that will be used as the image key.
   *            For example it can be URL, String, ID (for example long value)
   * @return - bitmap object that was stored in cache
   */
  Bitmap get(T key);

  /**
   * Puts given bitmap into cache.
   * <p/>
   * In case there is already a bitmap stored in cache with an existing key that is equal to the key parameter
   * of this method, the old bitmap value should be replaced by the new value.
   *
   * @param key    - key object, it is an object that will be used as the image key.
   *               For example it can be URL, String, ID (for example long value)
   * @param bitmap - bitmap object that will be stored in cache.
   */
  void put(T key, Bitmap bitmap);
}
