package com.miquido.vtv.repositories;

import com.google.inject.Singleton;
import com.miquido.vtv.bo.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 07.09.12
 * Time: 00:38
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class CurrentChannelRepository {

    @Getter @Setter private Id currentlyWatchedChannelId = null;


}
