package com.miquido.vtv.events.commands;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 16.08.12
 * Time: 19:43
 * To change this template use File | Settings | File Templates.
 */
public class ChannelsButtonToggledCommand {
    @Getter
    private final boolean isChannelsButtonChecked;

    public ChannelsButtonToggledCommand(boolean channelsButtonChecked) {
        isChannelsButtonChecked = channelsButtonChecked;
    }
}
