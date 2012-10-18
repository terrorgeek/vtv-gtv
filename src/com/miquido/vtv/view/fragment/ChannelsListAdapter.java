package com.miquido.vtv.view.fragment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.inject.Inject;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: ljazgar
* Date: 17.08.12
* Time: 19:12
* To change this template use File | Settings | File Templates.
*/
public class ChannelsListAdapter extends BaseAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ChannelsListAdapter.class);


    static class ChannelRowViewHolder {
        public TextView channelNameListField;
    }

    final Activity activity;
    List<Channel> channels;

    @Inject
    public ChannelsListAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return (channels!=null)?channels.size():0;
    }

    @Override
    public Object getItem(int i) {
        return channels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return channels.get(i).getId().hashCode();
    }

    public void setChannels(List<Channel> channels) {
        this.channels = (channels!=null) ? channels : new ArrayList<Channel>();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        return getRowView(i, channels.get(i), convertView, viewGroup);
    }

    protected View getRowView(int i, Channel channel, View convertView, ViewGroup viewGroup) {
        View rowView = convertView;
        if (rowView==null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.channels_list_channel_entry, null);
            ChannelRowViewHolder rowViewHolder = new ChannelRowViewHolder();
            rowViewHolder.channelNameListField = (TextView)rowView.findViewById(R.id.channelNameListField);
            rowView.setTag(rowViewHolder);
        }

        ChannelRowViewHolder rowViewHolder = (ChannelRowViewHolder)rowView.getTag();
        rowViewHolder.channelNameListField.setText(channel.getName());

        return rowView;
    }

}
