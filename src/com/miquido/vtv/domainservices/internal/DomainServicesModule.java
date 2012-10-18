package com.miquido.vtv.domainservices.internal;

import com.google.inject.AbstractModule;
import com.miquido.vtv.domainservices.internal.relationtranslation.RelationTranslationModule;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 23:33
 * To change this template use File | Settings | File Templates.
 */
public class DomainServicesModule extends AbstractModule {

    @Override
    protected void configure() {
        this.install(new RelationTranslationModule());
    }

}
