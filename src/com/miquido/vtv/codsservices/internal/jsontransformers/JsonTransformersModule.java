package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.miquido.vtv.bo.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public class JsonTransformersModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(new TypeLiteral<JsonReader<Profile>>() {
    }).toInstance(new ProfileJsonReader());
    bind(new TypeLiteral<JsonReader<Channel>>() {
    }).toInstance(new ChannelJsonReader());
    bind(new TypeLiteral<JsonReader<Notification>>() {
    }).toInstance(new NotificationJsonReader());
    bind(new TypeLiteral<JsonReader<Program>>() {
    }).toInstance(new ProgramJsonReader());
    bind(new TypeLiteral<JsonReader<Actor>>() {
    }).toInstance(new ActorJsonReader());
    bind(new TypeLiteral<JsonReader<GuideEntry>>() {
    }).toInstance(new GuideEntryJsonReader());
    bind(new TypeLiteral<JsonReader<ScheduleEntry>>() {
    }).toInstance(new ScheduleJsonReader());
  }
}
