package com.miquido.vtv.codsservices.internal.impl.mocks;

import android.content.res.AssetManager;
import com.google.inject.Inject;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.ScheduleEntry;
import com.miquido.vtv.codsservices.ProfilesCodsDao;
import com.miquido.vtv.codsservices.dataobjects.*;
import com.miquido.vtv.codsservices.internal.jsontransformers.SubcollectionJsonReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 16.08.12
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class ProfilesCodsDaoMockImpl implements ProfilesCodsDao {

  private List<Profile> profiles;

  private List<Profile> getProfiles() {
    if (profiles == null) {
      this.profiles = createMockProfileList();
    }
    return profiles;
  }

  public ProfilesCodsDaoMockImpl() {
  }

  @Override
  public RelatedDataCollection<Profile> getFriends(String sessionId, Id profileId) {
    return getFriends(sessionId, profileId, null);
  }

  @Override
  public RelatedDataCollection<Profile> getFriends(String sessionId, Id profileId, PageParams pageParams) {
    int numEntries = 1000000;
    int offset = 0;
    if (pageParams != null) {
      if (pageParams.getNumberOfEntries() != null)
        numEntries = pageParams.getNumberOfEntries();
      if (pageParams.getOffset() != null)
        offset = pageParams.getOffset();
      else if (pageParams.getPageNum() != null)
        offset = numEntries * (pageParams.getPageNum() - 1);
    }
    RelatedDataCollection<Profile> relatedDataCollection = new RelatedDataCollection<Profile>();
    List<Profile> allFriends = getProfiles();
    int total = allFriends.size();
    List<Profile> friends = allFriends;
    int resultCount = total;
    if (numEntries < total || offset != 0) {
      resultCount = ((offset + numEntries) <= total) ? numEntries : total - offset;
      friends = allFriends.subList(offset, offset + resultCount);
    }
    relatedDataCollection.setEntries(getProfiles());
    relatedDataCollection.setResultCount(resultCount);
    relatedDataCollection.setTotalCount(total);
    relatedDataCollection.setNextOffset(offset + resultCount);

    RelationshipList relationshipList = new RelationshipList();
    for (Profile friend : friends) {
      Relationship relationship = new Relationship();
      relationship.setId(idRandomizer.getNewId());
      relationship.setEntryId(profileId);
      relationship.setRelatedEntryId(friend.getId());
      relationship.setType(Relationship.Type.Friend);
      relationship.setStatus("new");
      relationship.setDateEntered(new Date());
      relationship.setDateModified(new Date());
      relationshipList.put(friend.getId(), relationship);
    }
    relatedDataCollection.setRelationshipList(relationshipList);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
    }
    return relatedDataCollection;
  }

  @Override
  public Profile update(String sessionId, Id profileId, Profile profileData, Profile dataToUpdateIndicator) {
    return profileData;
  }

  @Override
  public Profile update(String sessionId, Id profileId, Profile profileData) {
    return profileData;
  }

  @Override
  public Subcollection<Notification> getProfileNotifications(String sessionId, Id profileId) {
    return new Subcollection<Notification>();
  }

  @Override
  public Subcollection<Notification> getProfileNotifications(String sessionId, Id profileId, PageParams pageParams) {
    return new Subcollection<Notification>();
  }

  @Override
  public Subcollection<ScheduleEntry> getProfileSchedule(String sessionId, Id profileId) {
    return new Subcollection<ScheduleEntry>();
  }

  @Override
  public Subcollection<ScheduleEntry> getProfileSchedule(String sessionId, Id profileId, PageParams pageParams) {
    return new Subcollection<ScheduleEntry>();
  }

  private List<Profile> createMockProfileList() {
    try {
      InputStream input = assetManager.open("codsmocks/profiles.json");
      String jsonStr = readFully(input);
      JSONObject jsonObject = (JSONObject) new JSONTokener(jsonStr).nextValue();
      Subcollection<Profile> profileSubcollection = profileSubcollectionJsonTransformer.createObjectFromJson(jsonObject);
      return profileSubcollection.getEntries();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  private String readFully(InputStream inputStream)
      throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int length = 0;
    while ((length = inputStream.read(buffer)) != -1) {
      baos.write(buffer, 0, length);
    }
    return new String(baos.toByteArray());
  }


  @Inject
  SubcollectionJsonReader<Profile> profileSubcollectionJsonTransformer;
  @Inject
  AssetManager assetManager;
  @Inject
  IdRandomizer idRandomizer;
}
