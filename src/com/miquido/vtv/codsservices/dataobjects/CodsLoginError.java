package com.miquido.vtv.codsservices.dataobjects;

import lombok.Data;

/**
 * Structure returned only by login operation
 */
@Data
public class CodsLoginError {

    private String name;
    private long number;
    private String description;

}
