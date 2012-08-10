package com.miquido.vtv.view.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import com.miquido.vtv.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 25.07.12
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class NotificationsFragment extends ListFragment {
    private static final Logger logger = LoggerFactory.getLogger(NotificationsFragment.class);

    private List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

    public NotificationsFragment() {
        String [] data = new String[] {"linia 1", "linia 2", "linia 3"};

        for(String line : data) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("text", line);
            listData.add(map);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        this.setListAdapter(new SimpleAdapter(getActivity(), listData,
                R.layout.notifications_list, new String[] {"text"}, new int[] { R.id.textListField}));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
