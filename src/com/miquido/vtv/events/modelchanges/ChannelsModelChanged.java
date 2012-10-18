package com.miquido.vtv.events.modelchanges;

import com.miquido.vtv.viewmodel.ChannelsViewModel;
import com.miquido.vtv.viewmodel.SessionViewModel;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 02:22
 * To change this template use File | Settings | File Templates.
 */
public class ChannelsModelChanged {
    @Getter final private ChannelsViewModel channelsViewModel;

    public ChannelsModelChanged(ChannelsViewModel channelsViewModel) {
        this.channelsViewModel = channelsViewModel;
    }
}
