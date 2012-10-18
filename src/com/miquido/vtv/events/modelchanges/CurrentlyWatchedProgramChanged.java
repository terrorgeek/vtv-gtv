/**
 * Created by raho on 10/9/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.events.modelchanges;

import com.miquido.vtv.viewmodel.ProgramsViewModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(suppressConstructorProperties = true)
public class CurrentlyWatchedProgramChanged {

  @Getter
  final private ProgramsViewModel programsViewModel;

}
