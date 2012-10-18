package com.miquido.android.imageloader.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static junit.framework.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 17.08.12
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
public class WaitingViewsRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(WaitingViewsRepositoryTest.class);

    @Test
    public void testAddExplicitely() throws Exception {
        WaitingViewsRepository<String, Integer> waitingViewsRepository = new WaitingViewsRepository<String, Integer>();
        Integer imageView1 = new Integer(1);
        Integer imageView2 = new Integer(2);
        Integer imageView3 = new Integer(3);

        assertEquals(0, waitingViewsRepository.getImageViewsWaitingFor("url1").size());

        waitingViewsRepository.addExplicitely("url1", imageView1);
        assertEquals(1, waitingViewsRepository.getImageViewsWaitingFor("url1").size());
        logger.debug("After add1. ViewsWaiting for url1: {}", integerListToString(waitingViewsRepository.getImageViewsWaitingFor("url1")));

        waitingViewsRepository.addExplicitely("url1", imageView1);
        assertEquals(1, waitingViewsRepository.getImageViewsWaitingFor("url1").size());
        logger.debug("After add2. ViewsWaiting for url1: {}", integerListToString(waitingViewsRepository.getImageViewsWaitingFor("url1")));

        waitingViewsRepository.addExplicitely("url1", imageView2);
        assertEquals(2, waitingViewsRepository.getImageViewsWaitingFor("url1").size());
        logger.debug("After add3. ViewsWaiting for url1: {}", integerListToString(waitingViewsRepository.getImageViewsWaitingFor("url1")));

        waitingViewsRepository.addExplicitely("url2", imageView1);
        assertEquals(1, waitingViewsRepository.getImageViewsWaitingFor("url1").size());
        assertEquals(1, waitingViewsRepository.getImageViewsWaitingFor("url2").size());
        logger.debug("After add4. ViewsWaiting for url1: {}", integerListToString(waitingViewsRepository.getImageViewsWaitingFor("url1")));
        logger.debug("After add4. ViewsWaiting for url2: {}", integerListToString(waitingViewsRepository.getImageViewsWaitingFor("url2")));

        waitingViewsRepository.addExplicitely("url2", imageView3);
        assertEquals(1, waitingViewsRepository.getImageViewsWaitingFor("url1").size());
        assertEquals(2, waitingViewsRepository.getImageViewsWaitingFor("url2").size());
        logger.debug("After add5. ViewsWaiting for url1: {}", integerListToString(waitingViewsRepository.getImageViewsWaitingFor("url1")));
        logger.debug("After add5. ViewsWaiting for url2: {}", integerListToString(waitingViewsRepository.getImageViewsWaitingFor("url2")));

        waitingViewsRepository.remove(imageView1);
        logger.debug("After remove1. ViewsWaiting for url1: {}", integerListToString(waitingViewsRepository.getImageViewsWaitingFor("url1")));
        logger.debug("After remove1. ViewsWaiting for url2: {}", integerListToString(waitingViewsRepository.getImageViewsWaitingFor("url2")));
        assertEquals(1, waitingViewsRepository.getImageViewsWaitingFor("url1").size());
        assertEquals(1, waitingViewsRepository.getImageViewsWaitingFor("url2").size());

    }

    private String integerListToString(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i=0; i<list.size(); i++) {
            if (i>0)
                sb.append(", ");
            sb.append(list.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
