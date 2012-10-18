/**
 * Created by Krzysztof Biga on 09.07.12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.android.imageloader.util;

import android.graphics.*;

/**
 * Helper class providing useful methods for manipulating images.
 *
 * @author Krzysztof Biga
 */
public class ImageHelper {
  public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
        .getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    final RectF rectF = new RectF(rect);

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    //canvas.drawRoundRect(rectF, pixels, pixels, paint);
    canvas.drawRoundRect(rectF, bitmap.getWidth() / 16.0f, bitmap.getHeight() / 16.0f, paint);

    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    return output;
  }
}
