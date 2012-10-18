package com.miquido.vtv.codsservices.internal;

import com.google.inject.AbstractModule;
import com.miquido.vtv.codsservices.*;
import com.miquido.vtv.codsservices.internal.impl.*;
import com.miquido.vtv.codsservices.internal.impl.mocks.ProfilesCodsDaoMockImpl;
import com.miquido.vtv.codsservices.internal.jsontransformers.JsonTransformersModule;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 23:33
 * To change this template use File | Settings | File Templates.
 */
public class CodsDaoModule extends AbstractModule {

  @Override
  protected void configure() {
    this.install(new JsonTransformersModule());
    configureRealCodsServices();
//        configureMockCodsServices();

    bind(UsersCodsDao.class).to(UsersCodsDaoImpl.class);
    bind(ImagesCodsDao.class).to(ImagesCodsDaoImpl.class);
    bind(ChannelsCodsDao.class).to(ChannelsCodsDaoImpl.class);
    bind(NotificationsCodsDao.class).to(NotificationsCodsDaoImpl.class);
    bind(ProgramsCodsDao.class).to(ProgramsCodsDaoImpl.class);
  }


  private void configureRealCodsServices() {
    bind(ProfilesCodsDao.class).to(ProfilesCodsDaoImpl.class);
  }

  private void configureMockCodsServices() {
    bind(ProfilesCodsDao.class).to(ProfilesCodsDaoMockImpl.class);
    //bind(UsersCodsDao.class).to(UsersCodsDaoMockImpl.class);
  }

}
