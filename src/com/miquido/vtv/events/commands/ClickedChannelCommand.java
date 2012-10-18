package com.miquido.vtv.events.commands;

import com.miquido.vtv.bo.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 02:13
 * To change this template use File | Settings | File Templates.
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class ClickedChannelCommand {
    @Getter private final Id clickedChannelId;
}
