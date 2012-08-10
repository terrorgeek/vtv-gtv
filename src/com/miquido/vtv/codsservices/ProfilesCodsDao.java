package com.miquido.vtv.codsservices;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.CodsBusinessError;
import com.miquido.vtv.codsservices.dataobjects.PageList;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import com.miquido.vtv.codsservices.internal.CodsErrorsHandler;
import com.miquido.vtv.codsservices.internal.CodsHttpJsonClient;
import com.miquido.vtv.codsservices.internal.jsontransformers.PageListJsonReader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 27.07.12
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class ProfilesCodsDao {

    private static final Logger logger = LoggerFactory.getLogger(ProfilesCodsDao.class);


    public PageList<Profile> getFriends(String sessionId, String profileId) {
        logger.debug("ProfilesCodsDao.getFriends({}, {})", sessionId, profileId);
        String invokeServicePath = String.format("/profiles/%s/friends", profileId);
        return getProfilePageListFromCods(sessionId, invokeServicePath);
    }


    public PageList<Profile> getFriends(String sessionId, String profileId, int numberOfEntries) {
        logger.debug(String.format("ProfilesCodsDao.getFriends(%s, %s, %d)", sessionId, profileId, numberOfEntries));
        String invokeServicePath = String.format("/profiles/%s/friends/%d", profileId, numberOfEntries);
        return getProfilePageListFromCods(sessionId, invokeServicePath);
    }

    /**
     *
     * @param sessionId
     * @param numberOfEntries
     * @param pageNum First page is 1
     * @return
     */
    public PageList<Profile> getFriendsByPageNum(String sessionId, String profileId, int numberOfEntries, int pageNum) {
        logger.debug(String.format("ProfilesCodsDao.getFriendsByPageNum(%s, %s, %d, %d)", sessionId, profileId, numberOfEntries, pageNum));
//        String invokeServicePath = String.format("/profiles/%s/friends/%d/page/%d", profileId, numberOfEntries, pageNum);
        String invokeServicePath = String.format("/profiles/%s/friends?numentries=%d&page=%d", profileId, numberOfEntries, pageNum);
        return getProfilePageListFromCods(sessionId, invokeServicePath);
    }

    public PageList<Profile> getFriendsByOffset(String sessionId, String profileId, int numberOfEntries, int offset) {
        logger.debug(String.format("ProfilesCodsDao.getFriendsByOffset(%s, %s, %d, %d)", sessionId, profileId, numberOfEntries, offset));
//        String invokeServicePath = String.format("/profiles/%s/friends/%d/offset/%d", profileId, numberOfEntries, offset);
        String invokeServicePath = String.format("/profiles/%s/friends?numentries=%d&offset=%d", profileId, numberOfEntries, offset);
        return getProfilePageListFromCods(sessionId, invokeServicePath);
    }


    public List<Profile> getAllFriends(String sessionId, String profileId) {
        PageList<Profile> pageList = getFriendsByPageNum(sessionId, profileId, pageSize, 1);
        List<Profile> friends = new ArrayList<Profile>(pageList.getTotalCount());
        friends.addAll(pageList.getEntries());
        for (int pageNo=2; pageList.getTotalCount()!=pageList.getNextOffset(); pageNo++) {
            pageList = getFriendsByPageNum(sessionId, profileId, pageSize, pageNo);
            friends.addAll(pageList.getEntries());
        }
        return friends;
    }

    // ***************************** Private helper methods ***************************************************

    private PageList<Profile> getProfilePageListFromCods(String sessionId, String invokeServicePath) {
        JSONObject responseObject = codsHttpJsonClient.getWithSession(sessionId, invokeServicePath);

        if (profilePageListJsonTransformer.isCorrectType(responseObject)) {
            return profilePageListJsonTransformer.createObjectFromJson(responseObject);
        } else {
            logger.debug("Cods response is not list of profiles. Trying parsing as error structure.");
            CodsBusinessError codsBusinessError = codsErrorsHandler.handleStandardCodsError(responseObject);
            throw new CodsResponseFormatException(String.format("Unknown error number in jsontransformers response from CODS. CodsBusinessError: %s.", codsBusinessError.toString()));
        }
    }


    // ***************************** DEPENDENCIES ************************************************************
    @Inject
    CodsHttpJsonClient codsHttpJsonClient;
    @Inject
    PageListJsonReader<Profile> profilePageListJsonTransformer;
    @Inject
    private CodsErrorsHandler codsErrorsHandler;

    // ***************************** Configuration ***********************************************************
    private int pageSize = 100;

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
