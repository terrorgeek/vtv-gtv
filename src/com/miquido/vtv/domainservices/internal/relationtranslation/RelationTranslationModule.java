package com.miquido.vtv.domainservices.internal.relationtranslation;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.util.relationtranslation.RelationTranslator;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 23:25
 * To change this template use File | Settings | File Templates.
 */
public class RelationTranslationModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(new TypeLiteral<RelationTranslator<Friendship, Profile>>() {
    })
        .toInstance(new FriendshipRelationTranslator());
  }
}
