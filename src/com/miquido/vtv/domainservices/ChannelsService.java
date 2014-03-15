package com.miquido.vtv.domainservices;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import com.google.inject.Inject;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.codsservices.ChannelsCodsDao;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.util.PageByPageLoader;
import com.miquido.vtv.components.GuideItem;
import com.miquido.vtv.repositories.ChannelsRepository;
import com.miquido.vtv.repositories.SessionRepository;
import com.miquido.vtv.viewmodel.ChannelsViewModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 06.09.12
 * Time: 03:34
 * To change this template use File | Settings | File Templates.
 */
public class ChannelsService implements ChannelsViewModel {
    private static final Logger logger = LoggerFactory.getLogger(ChannelsService.class);

    public void loadChannels() {
        if (!sessionService.isLoggedIn())
            return;

        loadCodsChannels();
        loadGTVChannels();

        logger.debug("Selectable channels count: " + channelsRepository.getSelectableChannels().size());
    }

    private void loadCodsChannels() {
        try {
            List<Channel> channels = getAllChannelsFromCods(sessionRepository.getSession().getId());
            channelsRepository.setCodsChannels(channels);
            logger.debug("CODS Channels loaded. Count: " + channels.size());
        } catch (RuntimeException e) {
            logger.error("CODS Channels loading error.", e);
            channelsRepository.setCodsChannels(new ArrayList<Channel>());
        }
    }


    private void loadGTVChannels() {
        logger.debug("Loading GTV Channels. ContentResolver: " + contentResolver);
        Uri mProviderUri = Uri.parse("content://com.google.android.tv.provider/channel_listing");
        String[] mProjection = {"_id", "channel_uri", "callsign", "channel_name", "channel_number"};

        List<ChannelsRepository.GtvChannel> gtvChannels = new ArrayList<ChannelsRepository.GtvChannel>();

        Cursor mCursor = contentResolver.query(mProviderUri, mProjection, null, null, "channel_number ASC");
        try {
            if (mCursor==null) {
                logger.error("Error obtaining gtv channel list. mCursor is null");
                return;
            }
            if (mCursor.getCount()==0) {
                logger.error("Error obtaining gtv channel list. mCursor is empty");
                return;
            }

            while (mCursor.moveToNext()) {
                ChannelsRepository.GtvChannel newChannel = new ChannelsRepository.GtvChannel();
                newChannel.setId( mCursor.getInt(0) );
                newChannel.setChannelUri( mCursor.getString(1) );
                newChannel.setCallsign( mCursor.getString(2) );
                newChannel.setChannelName( mCursor.getString(3) );
                newChannel.setChannelNumber( mCursor.getString(4) );
                gtvChannels.add(newChannel);
            }
            logger.debug("GTV Channels loaded. Count: " + gtvChannels.size());
        } finally {
            channelsRepository.setGtvChannels(gtvChannels);
        }
    }

    public void clearAll() {
        channelsRepository.clearAll();
    }

    public List<Channel> getSelectableChannels() {
        return channelsRepository.getSelectableChannels();
    }

    public boolean isSelectableChannel(Id channelId) {
        Channel channel = channelsRepository.getCodsChannelForId(channelId);
        if (channel==null || channel.getCallSign()==null)
            return false;
        String gtvChannelUri = channelsRepository.getGtvChannelUriForCallSign(channel.getCallSign());
        return gtvChannelUri!=null;
    }

    public Channel getChannel(Id channelId){
        return channelsRepository.getCodsChannelForId(channelId);
    }

    private List<Channel> getAllChannelsFromCods(final String sessionId) {

        List<Channel> channels = pageByPageLoader.getWholeCollection(
            new PageByPageLoader.SingleSubcollectionLoader<Channel>() {
                @Override
                public Subcollection<Channel> load(PageParams pageParams) {
                    return channelsCodsDao.getChannels(sessionId, pageParams);
                }
            }
        );
        return channels;
    }
  public String post(String url)
  {
		String strResult = "";
		HttpClient client=new DefaultHttpClient();
		HttpGet myget=new HttpGet(url);
		try {
			HttpResponse response=client.execute(myget);
			strResult=EntityUtils.toString(response.getEntity()); 
		} catch (Exception e) {}
		return strResult;
  }
  public int Handle_Time_Gap(String start_time,String end_time) throws ParseException
  {
	  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   java.util.Date start = df.parse(start_time);
	   java.util.Date end=df.parse(end_time);
	   long l=end.getTime()-start.getTime();
//	   long day=l/(24*60*60*1000);
//	   long hour=(l/(60*60*1000)-day*24);
//	   long min=((l/(60*1000))-day*24*60-hour*60);
//	   long s=(l/1000-day*24*60*60-hour*60*60-min*60);
	//   System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
	//   System.out.println(l/(60*1000));
	   long minutes=l/(60*1000);
//	   if(minutes<=20)
//		   return 3;
 	   double temp=(double)minutes/(double)60;
 	   double double_temp=temp*585;
 	   int result=Integer.parseInt((double_temp+"").substring(0,(double_temp+"").indexOf('.')));
 	   return result;
  }
  public String Handle_Time_Display(String time)
  {
	  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String result="";
	  try {
		Date d=sdf.parse(time);
		d.setHours(d.getHours()-4);
		result=sdf.format(d);
	} catch (ParseException e) {e.printStackTrace();}
	  String hour=result.substring(11, 16);
	  if(hour.charAt(0)=='0' || hour.charAt(0)=='1'&&hour.charAt(1)=='1'||hour.charAt(0)=='1'&&hour.charAt(1)=='0')
		  return hour+" AM";
	  else
		  return hour+" PM";
  }
  //this is to get the future 24 hours which the guides included by CODS
  public String Get_Date_Within_10h()
  {
  //	SimpleDateFormat   df=new   SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
//	SimpleDateFormat df=new   SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
//  	Calendar   calendar=Calendar.getInstance();
//  	calendar.roll(Calendar.DAY_OF_WEEK,1);
//  	return df.format(calendar.getTime());
	  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
		 String result="";
		 Date d=new Date();
		 d.setHours(d.getHours()+10);
		 result=sdf.format(d);
		 return result;
  }
  public String Get_Cur_Time()
  {
	 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
	 String result="";
	 Date d=new Date();
	 d.setHours(d.getHours()+4);
	 result=sdf.format(d);
	 return result;
  }
  public int Convert_Starttime_To_Distance(String start_time) throws ParseException
  {
	//first we need to get the distance by minutes
	   	 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:00:00");
	     String now="";
	     Date d=new Date();
	     d.setHours(d.getHours());
	     now=sdf.format(d);
	     	 
	     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  	 java.util.Date start = df.parse(now);
	  	 java.util.Date end=df.parse(start_time);
         end.setHours(end.getHours()-4);
         
	  	 long l=end.getTime()-start.getTime();
	  	 double minutes=l/(60*1000);
	  	 double hours=minutes/30;
	  	 double a=(double)hours;
	  	 double result=a*292;
	  	 return (int)result;
  }
  public List<GuideItem> Sort_All_Items(List<GuideItem> all_items)
  {
  	for (int i = 0; i < all_items.size()-1; i++) {
			for (int j = 0; j <all_items.size()-i-1 ; j++) {
				if(compare_date(all_items.get(j).start_time, all_items.get(j+1).start_time)==1)
				{
					GuideItem temp=all_items.get(j);
					all_items.set(j, all_items.get(j+1));
					all_items.set(j+1, temp);
				}
			}
		}
  	return all_items;
  }
  public int compare_date(String DATE1, String DATE2) {
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      try {
          Date dt1 = df.parse(DATE1);
          Date dt2 = df.parse(DATE2);
          if (dt1.getTime() > dt2.getTime()) {
              return 1;
          } else if (dt1.getTime() < dt2.getTime()) {
              return -1;
          } else {
              return 0;
          }
      } catch (Exception exception) {
          exception.printStackTrace();
      }
      return 0;
  }
    @Inject
    SessionRepository sessionRepository;
    @Inject
    ChannelsRepository channelsRepository;
    @Inject
    ChannelsCodsDao channelsCodsDao;
    @Inject
    SessionService sessionService;
    @Inject
    PageByPageLoader pageByPageLoader;
    @Inject
    ContentResolver contentResolver;
}

