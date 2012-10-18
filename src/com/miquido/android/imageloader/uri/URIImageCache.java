/**
 * Created by raho on 12/7/11.
 *
 * Copyright 2011 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.android.imageloader.uri;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import com.miquido.android.imageloader.ImageCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

public class URIImageCache implements ImageCache<URI> {

  private static final Logger log = LoggerFactory.getLogger(URIImageCache.class);

  private static final String DEFAULT_CACHE_DIR = "cache_dir";

  public static final int ONE_MB = 1024 * 1024;

  private LruCache<String, Bitmap> cache;
  private File cacheDir;
  private Context context;
  private String cacheDirName;

  public URIImageCache(Context context) {
    this(context, DEFAULT_CACHE_DIR);
  }

  public URIImageCache(Context context, String cacheDirName) {
    this.context = context;
    this.cacheDirName = cacheDirName;
    final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    int maxCacheSizeInBytes = ONE_MB * memClass / 12;
    cache = new LruCache<String, Bitmap>(maxCacheSizeInBytes);
    initCacheDir();
  }

  @Override
  public Bitmap get(URI uri) {
    String key = generateKey(uri);
    Bitmap bitmap = cache.get(key);
    if (bitmap == null) {
      bitmap = loadFromSD(uri);
      if (bitmap != null) {
        cache.put(key, bitmap);
      }
    }
    return bitmap;
  }

  private Bitmap loadFromSD(URI uri) {
    File f = getFile(uri);
    if (f.exists()) {
      return BitmapFactory.decodeFile(f.getPath());
    } else {
      return null;
    }
  }

  @Override
  public void put(URI uri, Bitmap bitmap) {
    writeFile(bitmap, getFile(uri));
    cache.put(generateKey(uri), bitmap);
  }

  private File getFile(URI uri) {
    String filename = generateKey(uri);
    return new File(cacheDir, filename);
  }

  private void writeFile(Bitmap bmp, File file) {
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(file);
      bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
    } catch (Exception e) {
      log.error("Cannot read image stream or compress image!", e);
      //throw new ImageLoaderException("Error while saving image!");
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (Exception ex) {
        log.error("Cannot close output stream!", ex);
        //DON't throw exception, it will hide previous exceptions
      }
    }
  }

  private String generateKey(URI uri) {
    return String.valueOf(uri.hashCode());
  }

  private void initCacheDir() {
    String sdState = android.os.Environment.getExternalStorageState();
    if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
      File sdDir = android.os.Environment.getExternalStorageDirectory();
      cacheDir = new File(sdDir, "data/" + cacheDirName);
    } else {
      cacheDir = context.getCacheDir();
    }
    if (!cacheDir.exists()) {
      if (!cacheDir.mkdirs()) {
        //throw new ImageLoaderException("Cannot create cache directory!");
        log.warn("Cannot create cache dir, images will not be written do disk cache");
      }
    }
  }

  public void cleanCacheDir() {
    if (cacheDir!=null && cacheDir.exists() && cacheDir.isDirectory()) {
      for (File file : cacheDir.listFiles()) {
        deleteRecursively(file);
      }
    }
  }

  private static void deleteRecursively(File file) {
    if (file==null || !file.exists())
      return;
    if (file.isFile()) {
      file.delete();
    } else if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        deleteRecursively(f);
      }
      file.delete();
    }
  }

}
