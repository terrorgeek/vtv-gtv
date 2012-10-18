/**
 * Created by Krzysztof Biga on 31.05.12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.android.imageloader.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.apache.http.entity.BufferedHttpEntity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class providing methods for decoding and scaling bitmaps to required sizes.
 *
 * @author Krzysztof Biga
 */
public class BitmapHelper {

  public static int calculateInSampleSize(
      BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
      if (width > height) {
        inSampleSize = Math.round((float) height / (float) reqHeight);
      } else {
        inSampleSize = Math.round((float) width / (float) reqWidth);
      }
    }
    return inSampleSize;
  }

  public static Bitmap decodeSampledBitmapFromStream(BufferedHttpEntity entity, int reqWidth, int reqHeight) throws IOException {
    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    InputStream inputStream = null;
    try {
      inputStream = entity.getContent();
      BitmapFactory.decodeStream(inputStream, null, options);

      // Calculate inSampleSize
      options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

      // Decode bitmap with inSampleSize set
      options.inJustDecodeBounds = false;
      inputStream.close();
      inputStream = entity.getContent();
      return BitmapFactory.decodeStream(inputStream, null, options);

    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
  }
}
