package com.miquido.vtv.codsservices;

import com.google.inject.AbstractModule;
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
    }
}
