package com.miquido.vtv.codsservices;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.dataobjects.PageParams;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 16.08.12
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public interface ProfilesCodsDao {
    RelatedDataCollection<Profile> getFriends(String sessionId, Id profileId);

    RelatedDataCollection<Profile> getFriends(String sessionId, Id profileId, PageParams pageParams);

    Profile update(String sessionId, Id profileId, Profile profileData, Profile dataToUpdateIndicator);
    Profile update(String sessionId, Id profileId, Profile profileData);

    Subcollection<Notification> getProfileNotifications(String sessionId, Id profileId);
    Subcollection<Notification> getProfileNotifications(String sessionId, Id profileId, PageParams pageParams);

}
