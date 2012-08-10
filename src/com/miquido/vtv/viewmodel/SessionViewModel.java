package com.miquido.vtv.viewmodel;

import com.miquido.vtv.bo.Profile;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 02:23
 * To change this template use File | Settings | File Templates.
 */
public interface SessionViewModel {

    boolean isLoggedIn();
    boolean isLoggingIn();
    Profile getUserProfile();

}
