package com.miquido.vtv.codsservices.internal.httpjsonclient;

import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsUrlParams;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.08.12
 * Time: 23:40
 * To change this template use File | Settings | File Templates.
 */
public class CodsUrlParamsTest {

    @Test
    public void testEmpty() throws Exception {
        assertEquals("", CodsUrlParams.createNew().toString());
    }

    @Test
    public void testOneParam() throws Exception {
        assertEquals("?param1=value1", CodsUrlParams.createNew().add("param1", "value1").toString());
    }

    @Test
    public void testTwoParams() throws Exception {
        assertEquals("?param1=value1&param2=value2",
                     CodsUrlParams.createNew().add("param1", "value1").add("param2", "value2").toString());
    }

}
