package com.miquido.vtv.codsservices;

import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 06.09.12
 * Time: 02:58
 * To change this template use File | Settings | File Templates.
 */
public interface ChannelsCodsDao {

    Subcollection<Channel> getChannels(String sessionId);
    Subcollection<Channel> getChannels(String sessionId, PageParams pageParams);

}
