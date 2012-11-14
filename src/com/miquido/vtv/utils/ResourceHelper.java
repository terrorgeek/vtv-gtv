/**
 * Created by Krzysztof Biga on 19.06.12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class providing methods for getting resources.
 *
 * @author Krzysztof Biga
 */
public class ResourceHelper {

  private static final Logger logger = LoggerFactory.getLogger(ResourceHelper.class);

  public static final String STRING_RESOURCE_NAME = "string";
  public static final String DRAWABLE_RESOURCE_NAME = "drawable";

  public static String getResourceString(String name, Context context) {
    int nameResourceID = getResourceId(name, context, STRING_RESOURCE_NAME);
    if (nameResourceID == 0) {
      return name;
    } else {
      return context.getString(nameResourceID);
    }
  }

  public static Drawable getResourceDrawable(String name, Context context) {
    logger.debug("Trying to load drawable with name: " + name);
    int nameResourceID = getResourceId(name, context, DRAWABLE_RESOURCE_NAME);
    if (nameResourceID == 0) {
      return null;
    } else {
      return context.getResources().getDrawable(nameResourceID);
    }
  }

  private static int getResourceId(String name, Context context, String type) {
    return context.getResources().getIdentifier(name, type, context.getPackageName());
  }

}
