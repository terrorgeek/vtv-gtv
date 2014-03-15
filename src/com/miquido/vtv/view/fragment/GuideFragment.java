package com.miquido.vtv.view.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.inject.Inject;
import com.miquido.DemoChannels;
import com.miquido.MockData;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.bo.Session;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsHttpJsonClient;
import com.miquido.vtv.codsservices.internal.impl.ChannelsCodsDaoImpl;
import com.miquido.vtv.codsservices.internal.impl.UsersCodsDaoImpl;
import com.miquido.vtv.components.GuideItem;
import com.miquido.vtv.components.ObservableScrollView;
import com.miquido.vtv.domainservices.ChannelsService;
import com.miquido.vtv.domainservices.CurrentChannelService;
import com.miquido.vtv.events.commands.ClickedChannelCommand;
import com.miquido.vtv.events.modelchanges.ChannelsModelChanged;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.utils.ResourceHelper;
import com.miquido.vtv.viewmodel.PanelsStateViewModel;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectView;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GuideFragment extends RoboListFragment implements ObservableScrollView.ScrollViewListener {
  private static final Logger logger = LoggerFactory.getLogger(GuideFragment.class);

  @Inject
  ChannelsListAdapter channelsListAdapter;
  @Inject
  EventManager eventManager;

  @InjectView(R.id.currently_watching_thumbnail)
  ImageView currentlyWatchingThumbnail;

  @InjectView(R.id.currently_watching_description)
  TextView currentlyWatchingDescription;

  @InjectView(R.id.current_program_title)
  TextView programTitle;

  @InjectView(R.id.current_program_title)
  TextView programHeader;

  @InjectView(R.id.you_are_watching)
  TextView programTime;

  @InjectView(R.id.programsContainer)
  LinearLayout programsContainer;

  @InjectView(R.id.programsList)
  ObservableScrollView programsList;

  @InjectView(R.id.channelsList)
  ScrollView channelsList;

  @Inject
  CurrentChannelService currentChannelService;
  List<Channel> channels = null;
  GuideItem programToSelect;
  //created by Yu Song
  ArrayList<List<GuideItem>> channel_list=new ArrayList<List<GuideItem>>();
  ChannelsService channelsService=new ChannelsService();
  Handler build_guide_handler=new Handler();
  int channel_count=0;
  //end
  public GuideFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    logger.debug("vTV: Channels Fragment onCreate");
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    logger.debug("vTV: Channels Fragment onCreateView");
    this.setListAdapter(channelsListAdapter);
    return inflater.inflate(R.layout.channels_fragment, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    buildGuide();
    programsList.setScrollViewListener(this);
    channelsList.setVerticalScrollBarEnabled(false);
  }

  @Override
  public void onStart() {
    super.onStart();
    logger.debug("onStart");
  }

  @Override
  public void onResume() {
    super.onResume();
    logger.debug("onResume");
//    setAppropriateChannel();
  }

  private void setAppropriateChannel() {
    final Channel currentlyWatchedChannel = currentChannelService.getCurrentlyWatchedChannel();
    if (currentlyWatchedChannel == null) {
      return;
    }
    logger.info("Channels fragment. Current channel is: {}", currentlyWatchedChannel.getName());
    Program currentProgram = MockData.getProgramForChannel(currentlyWatchedChannel.getId());
    if (DemoChannels.CHANNEL_TNT.equals(currentlyWatchedChannel.getId())) {
//      channelsImageView.setImageResource(R.drawable.guide_mentalist);
      currentlyWatchingThumbnail.setImageResource(R.drawable.mentalist);
    } else {
//      channelsImageView.setImageResource(R.drawable.guide_ncis);
      currentlyWatchingThumbnail.setImageResource(R.drawable.ncis);
    }
    currentlyWatchingDescription.setText(currentProgram.getDescription());
    programTitle.setText(currentProgram.getName());
    programHeader.setText(currentProgram.getName());
//    channelsImageView.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        logger.info("Current channel is: {}", currentlyWatchedChannel.getName());
//        if (DemoChannels.CHANNEL_TNT.equals(currentlyWatchedChannel.getId())) {
//          logger.info("Changing channel to USA");
//          channelsImageView.setImageResource(R.drawable.guide_ncis);
//          Program currentProgram = MockData.getProgramForChannel(DemoChannels.CHANNEL_USA);
//          programTitle.setText(currentProgram.getName());
//          currentlyWatchingDescription.setText(currentProgram.getDescription());
//          currentlyWatchingThumbnail.setImageResource(R.drawable.ncis);
//          eventManager.fire(new ClickedChannelCommand(DemoChannels.CHANNEL_USA));
//        } else {
//          logger.info("Changing channel to TNT");
//          channelsImageView.setImageResource(R.drawable.guide_mentalist);
//          Program currentProgram = MockData.getProgramForChannel(DemoChannels.CHANNEL_TNT);
//          programTitle.setText(currentProgram.getName());
//          currentlyWatchingDescription.setText(currentProgram.getDescription());
//          currentlyWatchingThumbnail.setImageResource(R.drawable.mentalist);
//          eventManager.fire(new ClickedChannelCommand(DemoChannels.CHANNEL_TNT));
//        }
//      }
//    });
  }

  public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
    logger.debug("onPanelsStateChanged");
    PanelsStateViewModel panelsStateViewModel = panelsStateChanged.getPanelsStateViewModel();
    boolean isChannelPanelVisible = panelsStateViewModel.isApplicationVisible() && panelsStateViewModel.isChannelsPanelOn();
    getView().setVisibility(isChannelPanelVisible ? View.VISIBLE : View.INVISIBLE);
  }

  public void onChannelsModelChanged(@Observes ChannelsModelChanged channelsModelChanged) {
    logger.debug("onChannelsModelChanged");
    channels = channelsModelChanged.getChannelsViewModel().getSelectableChannels();
    if (channels != null && channels.size() > 0) {
      channelsListAdapter.setChannels(channelsModelChanged.getChannelsViewModel().getSelectableChannels());
    } else {
      channelsListAdapter.setChannels(null);
    }
      Get_Data_Thread get_Data_Thread=new Get_Data_Thread();
	  Thread t=new Thread(get_Data_Thread);
	  t.start();
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    logger.debug("vTV: GuideFragment onListItemClick");
    Channel clickedChannel = channels.get(position);
    logger.debug("Clicked channel: " + clickedChannel);
    if (clickedChannel != null) {
      eventManager.fire(new ClickedChannelCommand(clickedChannel.getId()));
    }
  }
  //Created by Yu Song
  class Get_Data_Thread implements Runnable
  {
  	@Override
  	public void run() {
  		String url="http://api.cods-dev.ngb.biz/rest/v3/login?login=bob.smith@cods.pyctex.net&password=103891baca2751a856b094db796e3fee";
		String json=channelsService.post(url);
		JSONObject jsonObject = null;
		String session_id="";
		try {
			jsonObject = new JSONObject(json);
			session_id=jsonObject.getString("id");
		//	String channel_json=channelsService.post("http://api.cods-dev.ngb.biz/rest/v3/channels?session="+session_id);
		//	JSONObject o1=new JSONObject(channel_json);
		//	JSONArray channel_array=o1.getJSONArray("entry_list");
		//	channel_count=channel_array.length();
			channel_count=channels.size();
		//	System.out.println("this is the length of channles:"+channel_array.length());
			for(int i=0;i<channel_count;i++)
			{
			//	JSONObject channel_temp_obj=(JSONObject) channel_array.opt(i);
			//	System.out.println(channel_temp_obj.getString("name")+"-----"+channel_temp_obj.getString("id"));
				Channel channel=channels.get(i);
		//		String guide_json=channelsService.post("http://api.cods-dev.ngb.biz/rest/v3/channels/"+channel_temp_obj.getString("id")+"/guide?session="+session_id);
				String guide_json=channelsService.post("http://api.cods-dev.ngb.biz/rest/v3/channels/"+channel.getId()+"/guide?session="+session_id+"&start_time="+channelsService.Get_Cur_Time()+"&end_time="+channelsService.Get_Date_Within_10h());
		//		String guide_json=channelsService.post("http://api.cods-dev.ngb.biz/rest/v3/channels/"+channel.getId()+"/guide?session="+session_id);
				JSONObject single_guide_obj=new JSONObject(guide_json);
				JSONArray guide_Array_relationship_list=single_guide_obj.getJSONArray("relationship_list");
				//JSONArray gudie_Array_entry_list=single_guide_obj.getJSONArray("entry_list");
				List<GuideItem> all_guideItem=new ArrayList<GuideItem>();
				for(int j=0;j<guide_Array_relationship_list.length();j++)
				{
					JSONObject guide_temp_obj_relationship_list=(JSONObject) guide_Array_relationship_list.opt(j);
					String program_json=channelsService.post("http://api.cods-dev.ngb.biz/rest/v3/programs/"+guide_temp_obj_relationship_list.getString("program_id")+"?session="+session_id);
					JSONObject program_obj=new JSONObject(program_json);
					
					
					int width=channelsService.Handle_Time_Gap(guide_temp_obj_relationship_list.getString("start_time"),guide_temp_obj_relationship_list.getString("end_time"));
					GuideItem program=new GuideItem(getActivity(),width);
					program.programTitle_Display=program_obj.getString("name");
					if(width<=147&&program_obj.getString("name").length()>10)
						program.setProgramTitle(program_obj.getString("name").substring(0, 7)+"....");
					else if(width>147&&width<305&&program_obj.getString("name").length()>=15)
						program.setProgramTitle(program_obj.getString("name").substring(0,10)+"....");
					else
						program.setProgramTitle(program_obj.getString("name"));
			    //  program.setProgramTime(guide_temp_obj_relationship_list.getString("start_time")+"-"+guide_temp_obj_relationship_list.getString("end_time"));
			        program.setProgramTime(channelsService.Handle_Time_Display(guide_temp_obj_relationship_list.getString("start_time"))+" - "+channelsService.Handle_Time_Display(guide_temp_obj_relationship_list.getString("end_time")));
			        program.setWidth(channelsService.Handle_Time_Gap(guide_temp_obj_relationship_list.getString("start_time"),guide_temp_obj_relationship_list.getString("end_time")));
			        String descr = program_obj.getString("description");
			        if(descr.startsWith("Lorem ipsum") || descr == null || (descr != null && descr.equals("null"))){
			        	descr = "The team investigates after a homeless teenager is found burned to death outside a restaurant. The victim's father tells them there could be a link with some violent videos uploaded to the internet, so the team sets up a sting operation which eventually leads them to the same school the dead man had attended - and two groups of girls who are very single-minded";
			        }
			        program.description=descr;
			        //this start_time is based on Poland, not convert to local time yet!
			        program.start_time=guide_temp_obj_relationship_list.getString("start_time");
			        if(width<=60)
			        {
						program.setProgramTitle(program_obj.getString("name").substring(0,2)+"...");
						program.setProgramTime("");
			        }
					all_guideItem.add(program);
				//	System.out.println("the guideitmesize is:"+all_guideItem.size());
				}
				//JSONObject guide_temp_obj_entry_list=(JSONObject) gudie_Array_entry_list.opt(0);
				List<GuideItem> newguideitemlist=channelsService.Sort_All_Items(all_guideItem);
				channel_list.add(newguideitemlist);
			//	System.out.println("this is channel:"+channel_list.size());
			}
		} catch (JSONException e) {e.printStackTrace();} 
		  catch (ParseException e) {e.printStackTrace();}
		
  			Build_Guide_Thread build_Guide_Thread=new Build_Guide_Thread();
  			build_guide_handler.post(build_Guide_Thread);
  	}  
  }
  class Build_Guide_Thread implements Runnable
  {
	@Override
	public void run() {
		Real_buildGuide();
	}  
  }
  private void buildGuide() {
	  
//	  Get_Data_Thread get_Data_Thread=new Get_Data_Thread();
//	  Thread t=new Thread(get_Data_Thread);
//	  t.start();
//	  build_guide_handler.post(build_guide_thread);
  }
  public void Real_buildGuide()
  {
	  
	  //first we need to login, and get the session
	    logger.debug("Building guide");
	    
	    final int rowsNo = this.channel_list.size();
	    final int programsInRowNo = 1;
/*	    List<GuideItem> all_guideitems=this.all_guideItem;*/
    	for(int i=0;i<rowsNo;i++)
    	{
    		LinearLayout guideRow = new LinearLayout(getActivity());
    		for(int j=0;j<this.channel_list.get(i).size();j++)
    		{
    		//	System.out.println("this is guide size:"+this.channel_list.get(i).size());
        		//GuideItem program=all_guideitems.get(i);
        		GuideItem program=this.channel_list.get(i).get(j);
        		final int finalRowNo = i;
     	        final int finalItemNo = j;
     	        program.setOnClickListener(new View.OnClickListener() {
     	          @Override
     	          public void onClick(View view) {
     	            logger.debug("Guide item clicked");
     	            if (view.isSelected()) {
     	              //programTime.setVisibility(View.VISIBLE);
     	              Toast.makeText(getActivity(), "changing channel...", Toast.LENGTH_SHORT).show();
     	              logger.debug("vTV: GuideFragment changing channel");
     	              if (channels != null) {
     	                Channel clickedChannel;
//     	                if (finalRowNo == 0) {
//     	                  clickedChannel = findChannelByCallSign("TNT");
//     	                } else if (finalRowNo == 3) {
//     	                  clickedChannel = findChannelByCallSign("USA");
//     	                } else {
     	                  clickedChannel = channels.get(finalRowNo);
     	               // }
     	                logger.debug("Clicked channel: " + clickedChannel);
     	                if (clickedChannel != null) {
     	                  eventManager.fire(new ClickedChannelCommand(clickedChannel.getId()));
     	                }
     	              }
     	            } else {
     	   //           updateProgramInfo(((GuideItem) view).getProgramTitle(), ResourceHelper.getResourceString("guide_program_desc_" + finalRowNo + "_" + finalItemNo, getActivity()), ((GuideItem) view).getProgramTime(), ResourceHelper.getResourceDrawable(ResourceHelper.getResourceString("guide_program_thumbnail_" + finalRowNo + "_" + finalItemNo, getActivity()), getActivity()));
     	              updateProgramInfo(((GuideItem) view).programTitle_Display,((GuideItem)view).description , ((GuideItem) view).getProgramTime(), ResourceHelper.getResourceDrawable(ResourceHelper.getResourceString("guide_program_thumbnail_" + finalRowNo + "_" + finalItemNo, getActivity()), getActivity()));
     	              removeSelectionFromAllItems(rowsNo, programsInRowNo);
     	              view.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide_item_background_selected));
     	              view.setSelected(true);
     	              Toast toast = Toast.makeText(getActivity(), "Press again to switch to this channel", Toast.LENGTH_SHORT);
     	              toast.setGravity(0, 0, Gravity.CENTER);
     	              toast.show();
     	            }
     	          }
     	        });
     	        //Add program to the row
     	       LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(program.getLayoutParams());
     	        if(j==0)
     	        {
					try {
						params.leftMargin=70+channelsService.Convert_Starttime_To_Distance(program.start_time);
						System.out.println("the start time is:"+program.start_time+"------"+"the distance is:"+channelsService.Convert_Starttime_To_Distance(program.start_time));
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
     	        }
				else
					params.leftMargin=0;
//					try {
//						params.leftMargin=channelsService.Convert_Starttime_To_Distance(program.start_time);
//					} catch (ParseException e) {e.printStackTrace();}
     	       try {
				System.out.println("start_time is:"+program.start_time+","+channelsService.Convert_Starttime_To_Distance(program.start_time));
			} catch (ParseException e) {e.printStackTrace();}
     	       
     	        guideRow.addView(program,params);
     	        if (i == 0 ) program.requestFocus();
     	        if (i == 3 ) {
     	          programToSelect = program;
     	        }
     	      }
    		
    		 //Add row to the container
    		
  	       programsContainer.addView(guideRow);      
    }
    	updateProgramInfo(programToSelect.programTitle_Display, programToSelect.description, programToSelect.getProgramTime(), ResourceHelper.getResourceDrawable(ResourceHelper.getResourceString("guide_program_thumbnail_" + 3 + "_" + 1, getActivity()), getActivity()));
	    removeSelectionFromAllItems(rowsNo, programsInRowNo);
	    programToSelect.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide_item_background_selected));
	    programToSelect.setSelected(true);
	    programToSelect.requestFocus();
}
    	//---------------------------------------------------------------------------

	    
  private Channel findChannelByCallSign(String callSign) {
    for (Channel channel : channels) {
      if (callSign.equalsIgnoreCase(channel.getCallSign())) {
        return channel;
      }
    }
    return null;
  }

  private void updateProgramInfo(String title, String description, String time, Drawable drawable) {
    //programTime.setVisibility(View.INVISIBLE);
    Calendar calendar = Calendar.getInstance();
    int dayNo = calendar.get(Calendar.DAY_OF_WEEK);
    String dayOfTheWeek = "";
    if (dayNo == 2) {
      dayOfTheWeek = "Mon";
    } else if (dayNo == 3) {
      dayOfTheWeek = "Tue";
    } else if (dayNo == 4) {
      dayOfTheWeek = "Wed";
    } else if (dayNo == 5) {
      dayOfTheWeek = "Thu";
    } else if (dayNo == 6) {
      dayOfTheWeek = "Fry";
    } else if (dayNo == 7) {
      dayOfTheWeek = "Sat";
    } else if (dayNo == 1) {
      dayOfTheWeek = "Sun";
    }
    programTime.setText(dayOfTheWeek + ", " + time);
    programTitle.setText(title);
    currentlyWatchingDescription.setText(description);
    if (drawable != null)
      currentlyWatchingThumbnail.setImageDrawable(drawable);
  }

  private void removeSelectionFromAllItems(int rowsNo, int itemsNo) {
	  itemsNo=1;
    for (int j = 0; j < rowsNo; j++) {
      LinearLayout row = (LinearLayout) programsContainer.getChildAt(j);
      for (int i = 0; i < itemsNo; i++) {
        LinearLayout item = (LinearLayout) row.getChildAt(i);
        item.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide_item_background));
        item.setSelected(false);
      }
    }
  }

  @Override
  public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
    logger.debug("Scrolling to: " + x + ", " + y);
    channelsList.smoothScrollTo(x, y);
  }
}
