package com.miquido.vtv.bo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 05.09.12
 * Time: 23:32
 * To change this template use File | Settings | File Templates.
 */
@Data
public class Channel extends Entity {

    String name;
    Id logoId;
    String callSign;

}
