package com.miquido.test.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 22:25
 * To change this template use File | Settings | File Templates.
 */
public abstract class GuiceTest {
    private static final Logger logger = LoggerFactory.getLogger(GuiceTest.class);

    private Injector injector;

    @Before
    public void guiceTestSetup() {
        injector = Guice.createInjector(getModules());
        injector.injectMembers(this);
    }

    protected Iterable<? extends Module> getModules() {
        return new ArrayList<Module>();
    }

    protected Injector getInjector() {
        return injector;
    }

}
