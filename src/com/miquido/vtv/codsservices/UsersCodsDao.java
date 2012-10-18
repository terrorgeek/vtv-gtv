package com.miquido.vtv.codsservices;

import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.Session;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 16.08.12
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public interface UsersCodsDao {
    Session login(String userName, String hashedPassword);

    Profile getCurrentUserProfile(String sessionId);
}
