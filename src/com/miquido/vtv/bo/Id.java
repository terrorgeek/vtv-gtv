package com.miquido.vtv.bo;

import lombok.EqualsAndHashCode;

import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */
@EqualsAndHashCode
public class Id {
    private final static String ID_FORMAT_REGEXP = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    String value;

    private Id(String idString) {
        this.value = idString;
    }

    public static Id valueOf(String idString) {
        if (idString==null || !Pattern.matches(ID_FORMAT_REGEXP, idString))
            return null;
        return new Id(idString);
    }

    public static Id severeValueOf(String idString) throws ParseException {
        if (idString==null)
            return null;
        if (!Pattern.matches(ID_FORMAT_REGEXP, idString))
            throw new ParseException("Incorrect format of ID: " + idString, 0);
        return new Id(idString);
    }

    public String toString() {
        return value;
    }

}
