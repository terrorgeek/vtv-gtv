package com.miquido.vtv.codsservices.internal.impl;

import com.google.inject.Inject;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.codsservices.ChannelsCodsDao;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsErrorsHandler;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsHttpJsonClient;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsUrlParams;
import com.miquido.vtv.codsservices.internal.jsontransformers.SubcollectionJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 06.09.12
 * Time: 03:00
 * To change this template use File | Settings | File Templates.
 */
public class ChannelsCodsDaoImpl implements ChannelsCodsDao {

    private static final Logger logger = LoggerFactory.getLogger(ChannelsCodsDaoImpl.class);

    @Override
    public Subcollection<Channel> getChannels(String sessionId) {
        return getChannels(sessionId, null);
    }

        @Override
    public Subcollection<Channel> getChannels(String sessionId, PageParams pageParams) {
        logger.debug(String.format("ChannelsCodsDaoImpl.getChannels(%s, %s)", sessionId, (pageParams!=null)?pageParams.toString():"null"));

        CodsHttpJsonClient.JsonResponse jsonResponse = codsHttpJsonClient.get(
                "/channels", CodsUrlParams.createNew().addSession(sessionId).addPageParams(pageParams));

        codsErrorsHandler.handleCodsError(jsonResponse);

        return channelSubcollectionJsonReader.createObjectFromJson(jsonResponse.getJsonObject());
    }



    // ***************************** DEPENDENCIES ************************************************************
    @Inject
    CodsHttpJsonClient codsHttpJsonClient;
    @Inject
    private CodsErrorsHandler codsErrorsHandler;
    @Inject
    SubcollectionJsonReader<Channel> channelSubcollectionJsonReader;

}
