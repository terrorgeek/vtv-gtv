/**
 * Created by Krzysztof Biga on 06.11.12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.view.fragment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.miquido.android.utils.adapter.list.BaseListAdapter;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.ScheduleEntry;
import com.miquido.vtv.utils.ResourceHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Adapter serving upcoming shows list.
 *
 * @author Krzysztof Biga
 */
public class UpcomingShowsAdapter extends BaseListAdapter<ScheduleEntry, UpcomingShowsAdapter.ViewHolder> {

  private List<ScheduleEntry> schedule;
  private final static DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM, K:mm a");

  public UpcomingShowsAdapter(Context context) {
    super(context, R.layout.upcoming_shows_entry);
  }

  @Override
  protected ViewHolder createViewHolder(View view) {
    ViewHolder result = new ViewHolder();
    result.upcomingProgramName = (TextView) view.findViewById(R.id.upcomingProgramName);
    result.upcomingProgramTime = (TextView) view.findViewById(R.id.upcomingProgramTime);
    result.upcomingProgramThumbnail = (ImageView) view.findViewById(R.id.upcomingProgramThumbnail);
    return result;
  }

  @Override
  protected void fillViewHolder(ViewHolder viewHolder, ScheduleEntry scheduleEntry, View view, int i) {
    viewHolder.upcomingProgramName.setText(scheduleEntry.getGuideEntry().getProgram().getName());
    viewHolder.upcomingProgramTime.setText(formatDate(scheduleEntry.getGuideEntry().getStartTime()));
    int resourceIndex = schedule.size() - i;
    viewHolder.upcomingProgramThumbnail.setImageDrawable(ResourceHelper.getResourceDrawable("dashboard_upcoming_sample_" + resourceIndex, context));
  }

  @Override
  protected List<ScheduleEntry> loadData() {
    Collections.sort(schedule);
    return schedule;
  }

  static class ViewHolder {
    public TextView upcomingProgramName;
    public TextView upcomingProgramTime;
    public ImageView upcomingProgramThumbnail;
  }

  public void updateSchedule(List<ScheduleEntry> newSchedule) {
    this.schedule = (newSchedule != null) ? newSchedule : new ArrayList<ScheduleEntry>();
    reloadDataAndNotifyDataSetChanged();
  }

  public String formatDate(Date date) {
    return dateFormat.format(date);
  }
}
