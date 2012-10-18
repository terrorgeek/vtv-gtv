package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.events.modelchanges.CurrentChannelChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 07.09.12
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
public class CurrentChannelInfoFragment extends RoboFragment {
    private static final Logger logger = LoggerFactory.getLogger(FriendsFragment.class);

    @InjectView(R.id.currentChannelNameTextView)
    TextView currentChannelNameTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger.debug("CurrentChannelInfoFragment onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logger.debug("CurrentChannelInfoFragment onCreateView");
        return inflater.inflate(R.layout.current_channel_info_fragment, container, false);
    }

    public void onCurrentChannelChanged(@Observes CurrentChannelChanged currentChannelChanged) {
        logger.debug("CurrentChannelInfoFragment onCurrentChannelChanged");
        Channel currentChannel = currentChannelChanged.getCurrentChannelViewModel().getCurrentlyWatchedChannel();
        logger.debug("currentChannel="+currentChannel);
        if (currentChannel!=null) {
            this.getView().setVisibility(View.VISIBLE);
            currentChannelNameTextView.setText(currentChannel.getName());
        } else {
            this.getView().setVisibility(View.INVISIBLE);
            currentChannelNameTextView.setText("");
        }
    }

}
