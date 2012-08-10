package com.miquido.vtv.codsservices.dataobjects;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 21:01
 * To change this template use File | Settings | File Templates.
 */
@Data
public class CodsBusinessError {

    private String name;
    private long number;
    private String description;

}
