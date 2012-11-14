/**
 * Created by raho on 10/9/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.codsservices;

import com.miquido.vtv.bo.GuideEntry;

public interface ProgramGuideCodsDao {

  public GuideEntry getGuideById(String sessionId, String guideId);

}
