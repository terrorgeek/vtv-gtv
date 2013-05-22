package com.miquido.vtv.repositories;

import com.google.inject.Singleton;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 */
@Singleton
public class ChannelsRepository {
    private static final Logger logger = LoggerFactory.getLogger(ChannelsRepository.class);

    private List<Channel> codsChannels = null;
    private Map<Id, Channel> idCodsChannelMap = null;

    private List<GtvChannel> gtvChannels = null;
    private Map<String, GtvChannel> callSignGtvChannelMap = null;

    private List<Channel> selectableCodsChannels = null;

    public ChannelsRepository() {
        logger.debug("vTV: ChannelsRepository created");
    }


    /**
     *
     * @return null - not stored yet
     */
    public List<Channel> getSelectableChannels() {
        return selectableCodsChannels;
    }

    public List<Channel> getCodsChannels() {
        return codsChannels;
    }
    public void setCodsChannels(List<Channel> codsChannels) {
        this.codsChannels = copy(codsChannels);
        this.idCodsChannelMap = (codsChannels!=null) ? createIdCodsChannelMap(this.codsChannels) : null;
        this.selectableCodsChannels = evalSelectableChannels(this.codsChannels, this.callSignGtvChannelMap);
    }

    public Channel getCodsChannelForId(Id codsChannelId) {
        return (idCodsChannelMap!=null) ? idCodsChannelMap.get(codsChannelId) : null;
    }


    public void setGtvChannels(List<GtvChannel> gtvChannels) {
        this.gtvChannels = gtvChannels;
        this.callSignGtvChannelMap = (gtvChannels!=null) ? createCallSignGtvChannelMap(gtvChannels) : null;
        this.selectableCodsChannels = evalSelectableChannels(this.codsChannels, this.callSignGtvChannelMap);
    }

    public String getGtvChannelUriForCallSign(String callSign) {
        if (callSign==null)
            return null;
        GtvChannel gtvChannel = callSignGtvChannelMap.get(callSign);
        return (gtvChannel!=null) ? gtvChannel.getChannelUri() : null;
    }

    public void clearAll() {
        codsChannels = null;
        idCodsChannelMap = null;
        gtvChannels = null;
        callSignGtvChannelMap = null;
    }

    protected static List<Channel> copy(List<Channel> channels) {
        if (channels==null)
            return null;
        List<Channel> newChannels = new ArrayList<Channel>(channels.size());
        newChannels.addAll(channels);
        return newChannels;
    }

    protected static Map<Id, Channel> createIdCodsChannelMap(List<Channel> channels) {
        Map<Id, Channel> map = new HashMap<Id, Channel>();
        for (Channel channel : channels) {
            map.put(channel.getId(), channel );
        }
        return map;
    }

    protected static Map<String, GtvChannel> createCallSignGtvChannelMap(List<GtvChannel> gtvChannels) {
        Map<String, GtvChannel> map = new HashMap<String, GtvChannel>();
        for (GtvChannel gtvChannel : gtvChannels) {
            map.put(gtvChannel.getCallsign(), gtvChannel);
        }
        return map;
    }

    protected static List<Channel> evalSelectableChannels(List<Channel> allCodsChannels, Map<String, GtvChannel> callSignGtvChannelMap) {
        if (allCodsChannels==null)
            return null;
        if (callSignGtvChannelMap==null)
            return Collections.emptyList();

        List<Channel> selectableChannels = new ArrayList<Channel>();
        for (Channel codsChannel : allCodsChannels) {
            String callSign = codsChannel.getCallSign();
            if (callSign!=null && !callSign.isEmpty()) {
                logger.debug(String.format("Channel with callSign: %s - %s", callSign, codsChannel.toString()));
            }
          if (callSign != null && !callSign.isEmpty() && callSignGtvChannelMap.containsKey(callSign)) {
            logger.debug(String.format("Selectable Channel number %s with callSign: %s - %s", callSignGtvChannelMap.get(callSign).channelNumber, callSign, codsChannel.toString()));
            /*if ("USA".equals(callSign) || "TNT".equals(callSign)) {
              logger.info("Demo channel: {} with name: {} and CODS ID: {}", new Object[]{callSign, codsChannel.getName(), codsChannel.getId().toString()});*/
              selectableChannels.add(codsChannel);
            /*}*/
          }
        }
        return selectableChannels;
    }


    @Data
    public static class GtvChannel {
        private int id;
        private String channelUri, callsign, channelName, channelNumber;
    }

}
