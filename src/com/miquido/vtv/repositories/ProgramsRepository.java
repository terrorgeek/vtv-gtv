/**
 * Created by raho on 10/9/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.repositories;

import com.google.inject.Singleton;
import com.miquido.vtv.bo.Program;
import lombok.Getter;
import lombok.Setter;

@Singleton
public class ProgramsRepository {

  @Getter
  @Setter
  private Program currentlyWatchedProgram = null;

}
