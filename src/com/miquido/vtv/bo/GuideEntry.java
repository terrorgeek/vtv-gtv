/**
 * Created by raho on 10/9/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.bo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GuideEntry extends Entity {
  private String name;
  private String description;
  private Date startTime;
  private Date endTime;
  private Id channelId;
  private Id programId;
  private Program program;
}
