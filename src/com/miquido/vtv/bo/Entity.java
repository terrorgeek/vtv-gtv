package com.miquido.vtv.bo;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
@Data
public abstract  class Entity {

    private Id id;
    private Date dateModified;
    private Date dateEntered;

}
