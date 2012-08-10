package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.miquido.vtv.bo.Profile;

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
        bind(new TypeLiteral<JsonReader<Profile>>() {}).toInstance(new ProfileJsonReader());
    }
}
