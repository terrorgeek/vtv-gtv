/**
 * Created by raho on 10/9/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.bo;

import lombok.Data;

import java.util.List;

@Data
public class Program extends Entity {

  private String name;
  private String description;
  private String tagline;
  private String seasonSubtitle;
  private String seasonName;
  private String seasonDescription;
  private List<Actor> actors;

}
