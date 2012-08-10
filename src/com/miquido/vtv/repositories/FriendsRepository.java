package com.miquido.vtv.repositories;

import com.google.inject.Singleton;
import com.miquido.vtv.bo.Profile;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 */
@Singleton
public class FriendsRepository {
    private static final Logger logger = LoggerFactory.getLogger(FriendsRepository.class);

    private boolean areFriendsLoading;
    @Getter @Setter String loadingErrorMessage;
    private List<Profile> friends = null;
    private Date modifyTime = null;

    public FriendsRepository() {
        logger.debug("vTV: FriendsRepository created");
    }

    public boolean areFriendsLoading() {
        return areFriendsLoading;
    }
    public void setFriendsLoading(boolean areFriendsLoading) {
        this.areFriendsLoading = areFriendsLoading;
    }

    /**
     *
     * @return null - not stored yet
     */
    public synchronized List<Profile> getFriends() {
        return friends;
    }

    public void setFriends(List<Profile> friends) {
        List<Profile> friendsCopy = copy(friends);
        synchronized (this) {
            this.friends = friendsCopy;
        }
    }

    public void clearAll() {
        friends = null;
        loadingErrorMessage = null;
        areFriendsLoading = false;
        modifyTime = null;
    }
//    /**
//     *
//     * @return null - never modified, nothing stored yet
//     */
//    public Date getModifyTime() {
//        return modifyTime;
//    }
//
//    public boolean changedSince(Date sinceTime) {
//        if (sinceTime==null)
//            return (modifyTime!=null);
//
//        if (modifyTime==null)
//            return false;
//
//        return sinceTime.before(modifyTime);
//    }
//
//
//
//    protected boolean equals(List<Profile> friends1, List<Profile> friends2) {
//        List<Profile> friends1SortedCopy = sort(copy(friends1));
//        List<Profile> friends2SortedCopy = sort(copy(friends2));
//
//        if ( friends1SortedCopy.size() != friends2SortedCopy.size() )
//            return false;
//
//        for (int i=0; i<friends1SortedCopy.size(); i++ ) {
//            if (!friends1SortedCopy.get(i).equals(friends2SortedCopy.get(i)))
//                return false;
//        }
//        return true;
//    }
//
//
    protected static List<Profile> copy(List<Profile> profiles) {
        if (profiles==null)
            return null;
        List<Profile> newProfiles = new ArrayList<Profile>(profiles.size());
        newProfiles.addAll(profiles);
        return newProfiles;
    }
//
//    protected static List<Profile> sort(List<Profile> profiles) {
//        Collections.sort(profiles, new Comparator<Profile>() {
//            @Override
//            public int compare(Profile profile1, Profile profile2) {
//                return compareProfilesByIdName(profile1, profile2);
//            }
//        });
//        return profiles;
//    }
//
//    protected static int compareProfilesByIdName(Profile profile1, Profile profile2) {
//        int compareId = compareStrings( profile1.getId(), profile2.getId() );
//        if (compareId!=0)
//            return compareId;
//
//        int compareName = compareStrings( profile1.getName(), profile2.getName());
//        if (compareName!=0)
//            return compareName;
//
//        return 0;
//    }
//
//    protected static int compareStrings(String str1, String str2) {
//        if (str1==null) {
//            return (str2!=null)?-1:0;
//        }
//        if (str2==null)
//            return 1;
//
//        return str1.compareTo(str2);
//    }

}
