/**
 * Created by raho on 10/9/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.viewmodel;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Program;

public interface ProgramsViewModel {

  void updateCurrentlyWatchedProgram();

  Program getCurrentlyWatchedProgram();
}
