package com.miquido.test.robolectric;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.RoboGuice;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 17:15
 * To change this template use File | Settings | File Templates.
 */
public class RobolectricInjectionTestRunner extends RobolectricTestRunner {
    private static final Logger logger = LoggerFactory.getLogger(RobolectricInjectionTestRunner.class);

    public RobolectricInjectionTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }


    @Override public void prepareTest(Object test) {
        RoboGuice.injectMembers(Robolectric.application, test);
        logger.debug("Injection done");
    }

}
