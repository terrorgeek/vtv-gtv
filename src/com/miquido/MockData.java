/**
 * Created by raho on 10/11/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido;

import com.miquido.vtv.R;
import com.miquido.vtv.bo.Actor;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Program;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class MockData {

  private static final Logger logger = LoggerFactory.getLogger(MockData.class);

  public static Program getProgramForChannel(Id channelId) {
    if (DemoChannels.CHANNEL_TNT.equals(channelId)) {
      return tntMockProgram();
    } else if (DemoChannels.CHANNEL_USA.equals(channelId)) {
      return usaMockProgram();
    }
    return null;
  }

  private static Program usaMockProgram() {
    List<Actor> actorList = new ArrayList<Actor>();
    actorList.add(createActor("Brian Dietzen", R.drawable.ncis_brian_dietzen));
    actorList.add(createActor("Cote de Pablo", R.drawable.ncis_cote_de_pablo));
    actorList.add(createActor("David McCallum", R.drawable.ncis_david_mccallum));
    actorList.add(createActor("Lauren Holly", R.drawable.ncis_lauren_holly));
    actorList.add(createActor("Mark Harmon", R.drawable.ncis_mark_harmon));
    actorList.add(createActor("Michael Weatherly", R.drawable.ncis_michael_weatherly));
    Program result = new Program();
    result.setId(Id.valueOf("ncis"));
    result.setActors(actorList);
    result.setName("NCIS");
    result.setDescription("American drama following the work of the Naval Criminal Investigative Service.");
    if (isFirstProgram()) {
      result.setTagline("The team's investigation into a commander's death uncovers a long list of enemies, leading to the arrest of one of his ensigns. But suspicions soon arise that it is the wrong man. Unfortunately, the mistake leads to another fatality, and this time it falls to Tony to save his colleagues.");
      result.setSeasonName("Season 2 Episode 8");
      result.setSeasonSubtitle("Heart Break");
      result.setSeasonDescription("The team's investigation into a commander's death uncovers a long list of enemies, leading to the arrest of one of his ensigns. But suspicions soon arise that it is the wrong man. Unfortunately, the mistake leads to another fatality, and this time it falls to Tony to save his colleagues.");
    } else {
      result.setTagline("Tony goes undercover to help a nervous prisoner escape as part of his plan to recover millions of dollars' worth of stolen Iraqi art. However, it transpires the detainee has a record for murder and the team fight to recover the works and ensure the safety of their colleague. Meanwhile, the Deputy Secretary of State struggles to comprehend how an agent has been left alone with a killer and demands constant updates. Frank Whaley guest stars.");
      result.setSeasonName("Season 2 Episode 10");
      result.setSeasonSubtitle("Chained");
      result.setSeasonDescription("Tony goes undercover to help a nervous prisoner escape as part of his plan to recover millions of dollars' worth of stolen Iraqi art. However, it transpires the detainee has a record for murder and the team fight to recover the works and ensure the safety of their colleague. Meanwhile, the Deputy Secretary of State struggles to comprehend how an agent has been left alone with a killer and demands constant updates. Frank Whaley guest stars.");
    }
    return result;
  }

  private static Program tntMockProgram() {
    List<Actor> actorList = new ArrayList<Actor>();
    actorList.add(createActor("Amanda Righetti", R.drawable.mentalist_amanda_righetti));
    actorList.add(createActor("Gregory Itzin", R.drawable.mentalist_gregory_itzin));
    actorList.add(createActor("Owain Yeoman", R.drawable.mentalist_owain_yeoman));
    actorList.add(createActor("Robin Tunney", R.drawable.mentalist_robin_tunney));
    actorList.add(createActor("Simon Baker", R.drawable.mentalist_simon_baker));
    actorList.add(createActor("Tim Kang", R.drawable.mentalist_tim_kang));
    Program result = new Program();
    result.setId(Id.valueOf("mentalist"));
    result.setActors(actorList);
    result.setName("The Mentalist");
    result.setDescription("American drama series based on the work of Patrick Jane, an independent consultant with the California Bureau of Investigation. Once a fraudulent celebrity psychic, he uses his skills of observation and understanding of human behavior to help the CBI to solve its most serious crimes.");
    if (isFirstProgram()) {
      result.setTagline("The case of a murdered jockey leads Jane and the team into the murky underworld of horse racing, where they uncover a number of suspects. Crime drama, guest starring Jim Beaver (Deadwood) and Olesya Rulin (High School Musical), with Simon Baker and Robin Tunney");
      result.setSeasonName("Season 3 Episode 5");
      result.setSeasonSubtitle("The Red Ponies");
      result.setSeasonDescription("The case of a murdered jockey leads Jane and the team into the murky underworld of horse racing, where they uncover a number of suspects. Crime drama, guest starring Jim Beaver (Deadwood) and Olesya Rulin (High School Musical), with Simon Baker and Robin Tunney");
    } else {
      result.setTagline("The disappearance of a federal judge's daughter on the same day as her grandmother dies leads the team to the family's guarded home, where security officers insist the girl never left the property. Convinced she was murdered, Jane searches for the body and concludes the victim was buried in her relative's coffin - prompting him to test his theory on the day of the funeral. Crime drama, guest starring Cristine Rose (Heroes)");
      result.setSeasonName("Season 3 Episode 6");
      result.setSeasonSubtitle("Pink Chanel Suit");
      result.setSeasonDescription("The disappearance of a federal judge's daughter on the same day as her grandmother dies leads the team to the family's guarded home, where security officers insist the girl never left the property. Convinced she was murdered, Jane searches for the body and concludes the victim was buried in her relative's coffin - prompting him to test his theory on the day of the funeral. Crime drama, guest starring Cristine Rose (Heroes)");
    }
    return result;
  }

  static boolean isFirstProgram() {
    DateTime channelChange = new DateTime("2012-10-11T15:00:00.00-04:00");
    DateTime est = DateTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST")));
    boolean before = est.isBefore(channelChange);
    logger.info("Current time with EST timezone: {}, is before 7 PM GMT: {}", est.toString(), before);
    return before;
  }

  private static Actor createActor(String s, int resource) {
    Actor result = new Actor();
    result.setPhotoResourceId(resource);
    result.setName(s);
    return result;
  }
}
