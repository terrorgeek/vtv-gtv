package com.miquido.vtv.domainservices;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import com.google.inject.Inject;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.codsservices.ChannelsCodsDao;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.util.PageByPageLoader;
import com.miquido.vtv.repositories.ChannelsRepository;
import com.miquido.vtv.repositories.SessionRepository;
import com.miquido.vtv.viewmodel.ChannelsViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 06.09.12
 * Time: 03:34
 * To change this template use File | Settings | File Templates.
 */
public class ChannelsService implements ChannelsViewModel {
    private static final Logger logger = LoggerFactory.getLogger(ChannelsService.class);

    public void loadChannels() {
        if (!sessionService.isLoggedIn())
            return;

        loadCodsChannels();
        loadGTVChannels();

        logger.debug("Selectable channels count: " + channelsRepository.getSelectableChannels().size());
    }

    private void loadCodsChannels() {
        try {
            List<Channel> channels = getAllChannelsFromCods(sessionRepository.getSession().getId());
            channelsRepository.setCodsChannels(channels);
            logger.debug("CODS Channels loaded. Count: " + channels.size());
        } catch (RuntimeException e) {
            logger.error("CODS Channels loading error.", e);
            channelsRepository.setCodsChannels(new ArrayList<Channel>());
        }
    }


    private void loadGTVChannels() {
        logger.debug("Loading GTV Channels. ContentResolver: " + contentResolver);
        Uri mProviderUri = Uri.parse("content://com.google.android.tv.provider/channel_listing");
        String[] mProjection = {"_id", "channel_uri", "callsign", "channel_name", "channel_number"};

        List<ChannelsRepository.GtvChannel> gtvChannels = new ArrayList<ChannelsRepository.GtvChannel>();

        Cursor mCursor = contentResolver.query(mProviderUri, mProjection, null, null, "channel_number ASC");
        try {
            if (mCursor==null) {
                logger.error("Error obtaining gtv channel list. mCursor is null");
                return;
            }
            if (mCursor.getCount()==0) {
                logger.error("Error obtaining gtv channel list. mCursor is empty");
                return;
            }

            while (mCursor.moveToNext()) {
                ChannelsRepository.GtvChannel newChannel = new ChannelsRepository.GtvChannel();
                newChannel.setId( mCursor.getInt(0) );
                newChannel.setChannelUri( mCursor.getString(1) );
                newChannel.setCallsign( mCursor.getString(2) );
                newChannel.setChannelName( mCursor.getString(3) );
                newChannel.setChannelNumber( mCursor.getString(4) );
                gtvChannels.add(newChannel);
            }
            logger.debug("GTV Channels loaded. Count: " + gtvChannels.size());
        } finally {
            channelsRepository.setGtvChannels(gtvChannels);
        }
    }

    public void clearAll() {
        channelsRepository.clearAll();
    }

    public List<Channel> getSelectableChannels() {
        return channelsRepository.getSelectableChannels();
    }

    public boolean isSelectableChannel(Id channelId) {
        Channel channel = channelsRepository.getCodsChannelForId(channelId);
        if (channel==null || channel.getCallSign()==null)
            return false;
        String gtvChannelUri = channelsRepository.getGtvChannelUriForCallSign(channel.getCallSign());
        return gtvChannelUri!=null;
    }

    public Channel getChannel(Id channelId){
        return channelsRepository.getCodsChannelForId(channelId);
    }

    private List<Channel> getAllChannelsFromCods(final String sessionId) {

        List<Channel> channels = pageByPageLoader.getWholeCollection(
            new PageByPageLoader.SingleSubcollectionLoader<Channel>() {
                @Override
                public Subcollection<Channel> load(PageParams pageParams) {
                    return channelsCodsDao.getChannels(sessionId, pageParams);
                }
            }
        );
        return channels;
    }

    @Inject
    SessionRepository sessionRepository;
    @Inject
    ChannelsRepository channelsRepository;
    @Inject
    ChannelsCodsDao channelsCodsDao;
    @Inject
    SessionService sessionService;
    @Inject
    PageByPageLoader pageByPageLoader;
    @Inject
    ContentResolver contentResolver;
}
