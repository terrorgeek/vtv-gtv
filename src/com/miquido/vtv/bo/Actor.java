/**
 * Created by raho on 10/10/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.bo;

import lombok.Data;

@Data
public class Actor extends Entity {

  private String name;
  private Id photoId;
  private String description;
  private String nickname;
  private String bio;
  private int photoResourceId;
}
