package com.miquido.vtv.codsservices.internal.impl.mocks;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.Session;
import com.miquido.vtv.codsservices.UsersCodsDao;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 16.08.12
 * Time: 11:36
 * To change this template use File | Settings | File Templates.
 */
public class UsersCodsDaoMockImpl implements UsersCodsDao {

    @Override
    public Session login(String userName, String hashedPassword) {
        //{"id":"89b99e2be7e1e835ccff199e1146411a","module_name":"Users","name_value_list":{"user_id":{"name":"user_id","value":"48ca2e78-da4c-11e1-83cd-c263aa2dded5"},"user_name":{"name":"user_name","value":"bob.smith@cods.pyctex.net"}}}
        Session session = new Session();
        session.setId("89b99e2be7e1e835ccff199e1146411a");
        session.setUserId(Id.valueOf("48ca2e78-da4c-11e1-83cd-c263aa2dded5"));
        session.setUserName("bob.smith@cods.pyctex.net");
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        return session;
    }

    @Override
    public Profile getCurrentUserProfile(String sessionId) {
        // "{\"id\":\"12bb9aa9-fbdd-1e0a-c53a-4fff6358f941\",
        // \"name\":\"Bob Smith\",\"date_entered\":\"2012-07-12 23:55:38\",\"date_modified\":\"2012-07-26 16:52:50\",\"description\":\"\",
        // \"first_name\":\"Bob\",\"last_name\":\"Smith\",\"full_name\":\"Bob Smith\",\"email\":\"\",\"email1\":\"\",\"email2\":\"\",
        // \"avatar_id\":\"8083a680-ebf9-2244-b346-4ff74b9c2ae8\",\"facebook_id\":\"\",\"sip_status\":\"offline\",
        // \"avatar_url\":\"http:\\/\\/api.cods.pyctex.net\\/images\\/8083a680-ebf9-2244-b346-4ff74b9c2ae8\"}"
        Profile profile = new Profile();
        profile.setId(Id.valueOf("12bb9aa9-fbdd-1e0a-c53a-4fff6358f941"));
        profile.setName("Bob Smith");
        profile.setFirstName("Bob");
        profile.setLastName("Smith");
        profile.setDateModified(new Date());
        profile.setAvatarId(Id.valueOf("8083a680-ebf9-2244-b346-4ff74b9c2ae8"));
        profile.setDescription(null);
        profile.setFacebookId(null);
        profile.setOnline(true);
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        return profile;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
