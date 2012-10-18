package com.miquido.vtv.events.commands;

import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 14.09.12
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class InviteToWatchTogetherButtonClicked {
    @Getter final Friendship friendship;

}
