package com.miquido.vtv.codsservices.dataobjects;

import com.miquido.vtv.bo.Id;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class IdTest {

    @Test
    public void testCorrectId() {
        Id id = Id.valueOf("938690c3-dd06-0282-ec57-50193b78f3be");
        assertNotNull(id);
        assertEquals("938690c3-dd06-0282-ec57-50193b78f3be", id.toString());
    }

    @Test
    public void testCorrectId2() {
        Id id = Id.valueOf("65672ac9-70a9-c149-cce8-4fff63ea9803");
        assertNotNull(id);
        assertEquals("65672ac9-70a9-c149-cce8-4fff63ea9803", id.toString());
    }

    @Test
    public void testInCorrectId() {
        assertNull(Id.valueOf("65672ac9-70a9-c149-cce8-4fff63ea980")); // shorten
        assertNull(Id.valueOf("65672ac9-70a9-c149-cce84-fff63ea9803")); // - in other place
        assertNull(Id.valueOf("65672ac9-70a9-c149-cce8-4Fff63ea9803")); // capitalic
        assertNull(Id.valueOf("65672gc9-70a9-c149-cce8-4fff63ea9803")); // wrong letter
    }


}
