/**
 * Created by raho on 10/11/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido;

import com.miquido.vtv.bo.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoChannels {

  private static final Logger logger = LoggerFactory.getLogger(DemoChannels.class);

  public static final Id CHANNEL_TNT = Id.valueOf("506c49f0-2431-d88b-1524-cd12e2374ee0");
  public static final Id CHANNEL_USA = Id.valueOf("506c49f0-af3d-5af4-b90b-2e0c1aa56f5b");

  private static Boolean isChangingRemoteChannel = false;

  public static boolean isChangingRemoteChannel() {
    logger.info("Returning isChangingRemoteChannel: {}", isChangingRemoteChannel);
    synchronized (DemoChannels.class) {
      return isChangingRemoteChannel;
    }
  }

  public static void setIsChangingRemoteChannel(boolean isChangingRemoteChannel) {
    logger.info("Setting isChangingRemoteChannel: {}", isChangingRemoteChannel);
    synchronized (DemoChannels.class) {
      DemoChannels.isChangingRemoteChannel = isChangingRemoteChannel;
    }
  }

}
