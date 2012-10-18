package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.codsservices.ImagesCodsDao;
import com.miquido.vtv.repositories.SessionRepository;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 06.09.12
 * Time: 00:57
 * To change this template use File | Settings | File Templates.
 */
public class ImagesService {

    public String getImageURL(Id imageId) {
//        if (sessionRepository.getSession()==null)
//            return null;

        return imagesCodsDao.getImageURL(imageId);
    }


    @Inject
    ImagesCodsDao imagesCodsDao;
    @Inject
    SessionRepository sessionRepository;

}
