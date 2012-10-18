package com.miquido.vtv.codsservices.internal.impl.mocks;

import com.miquido.vtv.bo.Id;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class IdRandomizerTest {
    private static final Logger logger = LoggerFactory.getLogger(IdRandomizerTest.class);

    IdRandomizer idRandomizer;
    @Before
    public void setup() {
        idRandomizer = new IdRandomizer();
    }


    @Test
    public void testGetNewId() throws Exception {
        for (int i = 0; i < 10; i++) {
            Id id = idRandomizer.getNewId();
            assertNotNull(id);
            logger.debug("ID: " + id);
        }
    }

    @Test
    public void testRandomizeByteHex() throws Exception {

        String hexByte = idRandomizer.randomizeByteHex();
        logger.debug("HexByte:" + hexByte);
        assertEquals(2, hexByte.length());

    }

}
