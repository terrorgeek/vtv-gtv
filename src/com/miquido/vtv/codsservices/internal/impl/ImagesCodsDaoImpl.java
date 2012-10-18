package com.miquido.vtv.codsservices.internal.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.codsservices.ImagesCodsDao;
import com.miquido.vtv.codsservices.internal.CodsConfiguration;

@Singleton
public class ImagesCodsDaoImpl implements ImagesCodsDao {

  @Inject
  private CodsConfiguration codsConfiguration;

  @Override
  public String getImageURL(Id imageId) {
    return String.format("%s/%s/images/%s?format=image",
        codsConfiguration.getCodsServerURL(), codsConfiguration.getApiVersion(), imageId.toString());

  }

}
