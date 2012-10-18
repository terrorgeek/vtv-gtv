package com.miquido.vtv.codsservices.dataobjects;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
@Data
public class Subcollection<EntryType> {
    private int resultCount;
    private int totalCount;
    private int nextOffset;

    private List<EntryType> entries = new ArrayList<EntryType>();

}
