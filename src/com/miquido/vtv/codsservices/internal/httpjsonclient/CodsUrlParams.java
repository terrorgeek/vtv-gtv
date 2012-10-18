package com.miquido.vtv.codsservices.internal.httpjsonclient;

import com.miquido.vtv.codsservices.dataobjects.PageParams;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.08.12
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
public class CodsUrlParams extends UrlParams {

    public static CodsUrlParams createNew() {
        return new CodsUrlParams();
    }

    public CodsUrlParams add(String paramName, String paramValue) {
        super.add(paramName, paramValue);
        return this;
    }

    public CodsUrlParams addSession(String sessionId) {
        super.add("session", sessionId);
        return this;
    }

    public CodsUrlParams addPageParams(PageParams pageParams) {
        if (pageParams!=null) {
            if (pageParams.getNumberOfEntries()!=null) {
                add("numentries", pageParams.getNumberOfEntries().toString());
            }
            if (pageParams.getOffset()!=null) {
                add("offset", pageParams.getOffset().toString());
            } else if (pageParams.getPageNum()!=null) {
                add("page", pageParams.getPageNum().toString());
            }
        }
        return this;
    }
}
