package com.miquido.vtv.events.modelchanges;

import com.miquido.vtv.viewmodel.CurrentChannelViewModel;
import com.miquido.vtv.viewmodel.SessionViewModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 07.09.12
 * Time: 01:22
 * To change this template use File | Settings | File Templates.
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class CurrentChannelChanged {

    @Getter
    final private CurrentChannelViewModel currentChannelViewModel;

}
