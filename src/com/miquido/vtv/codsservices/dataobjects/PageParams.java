package com.miquido.vtv.codsservices.dataobjects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 30.08.12
 * Time: 01:21
 * To change this template use File | Settings | File Templates.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED, suppressConstructorProperties=true)
@ToString
public class PageParams {
    @Getter Integer numberOfEntries = null;
    @Getter Integer pageNum = null;
    @Getter Integer offset = null;

    public static PageParams createForDefault() {
        return new PageParams(null, null, null);
    }

    public static PageParams createForNOE(int numberOfEntries) {
        return new PageParams(numberOfEntries, null, null);
    }

    public static PageParams createForOffset(int numberOfEntries, int offset) {
        return new PageParams(numberOfEntries, null, offset);
    }

    public static PageParams createForPageNum(int numberOfEntries, int pageNum) {
        return new PageParams(numberOfEntries, pageNum, null);
    }

}
