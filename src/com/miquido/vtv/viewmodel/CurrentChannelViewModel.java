package com.miquido.vtv.viewmodel;

import com.miquido.vtv.bo.Channel;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 07.09.12
 * Time: 01:07
 * To change this template use File | Settings | File Templates.
 */
public interface CurrentChannelViewModel {

    Channel getCurrentlyWatchedChannel();
    String getCurrentlyWatchedChannelUri();

}
