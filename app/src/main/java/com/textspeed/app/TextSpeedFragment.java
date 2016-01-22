package com.textspeed.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.textspeed.app.message.Message;
import com.textspeed.app.message.MessageUtil;
import com.textspeed.app.ui.AdapterFormat;
import com.textspeed.app.ui.AdvancedAdapter;
import com.textspeed.app.ui.Filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by crimson on 04.04.2015.
 */
public class TextSpeedFragment extends Fragment {

    private final int MAX_MPH = 15;

    private static final double METERS_PER_SECOND2MILES_PER_HOUR = 2.23693629205d;
    private static final double MILES_PER_HOUR2METERS_PER_SECOND = 0.44704d;

    private final float SPEED_LIMIT = (float) (MAX_MPH * MILES_PER_HOUR2METERS_PER_SECOND);
    private boolean speedFilterEnabled = false;

    private List<Message> messages = new ArrayList<Message>();
    private Map<Integer, Float> messageSpeeds;
    private Map<Long, String> datesReadable = new HashMap<Long, String>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    private ServiceReadyListener serviceReadyListener = new ServiceReadyListener() {
        @Override
        public void onServiceReady() {
            try {
                messageSpeeds = TextSpeedService.getMessageSpeeds();
                if(adapter!=null) {
                    adapter.filter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private AdapterFormat<Message> adapterFormat = new AdapterFormat<Message>() {
        @Override
        public void setData(Message data, View view) {
            try {
                boolean incoming = data.isIncoming();
                setTextToView(view, R.id.smsText, data.getText());
                setTextToView(view, R.id.smsOpponent,
                        incoming?
                                "From: " + data.getOpponent() :
                                "To: " + data.getOpponent());
                if(!datesReadable.containsKey(data.getDate())) {
                    Date date = new Date(data.getDate());
                    datesReadable.put(data.getDate(), dateFormat.format(date));
                }
                setTextToView(view, R.id.smsTime, datesReadable.get(data.getDate()));
                if(!incoming) {
                    if(messageSpeeds.containsKey(data.getId())) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Speed: ")
                                .append(getRoundSpeed(data.getId()))
                                .append(" MPH");
                        setTextToView(view, R.id.smsSpeed, builder.toString());
                    } else {
                        setTextToView(view, R.id.smsSpeed, "Unknown speed");
                    }
                } else {
                    setTextToView(view, R.id.smsSpeed, "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private double getRoundSpeed(int id) {
        return Math.round((messageSpeeds.get(id)
                * METERS_PER_SECOND2MILES_PER_HOUR) * 100d)/100d;
    };

    private Filter<Message> speedFilter = new Filter<Message>() {
        @Override
        public boolean isOk(Message item) {
            return !speedFilterEnabled || (item.isOutgoing() &&
                    messageSpeeds.containsKey(item.getId()) &&
                    messageSpeeds.get(item.getId()) > SPEED_LIMIT);
        }
    };

    private CompoundButton.OnCheckedChangeListener changeListener =
            new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            speedFilterEnabled = isChecked;
            if(adapter!=null) {
                adapter.filter();
            }
        }
    };

    private AdvancedAdapter<Message, AdapterFormat<Message>> adapter;

    public TextSpeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new AdvancedAdapter<Message, AdapterFormat<Message>>(getActivity(),
                R.layout.sms_element, messages, adapterFormat,
                speedFilter, Message.LAST_ON_TOP);
        ListView MessageList = (ListView) rootView.findViewById(R.id.MessageList);
        MessageList.setAdapter(adapter);
        Switch filterSwitch = (Switch) rootView.findViewById(R.id.highSpeedSwitch);
        filterSwitch.setOnCheckedChangeListener(changeListener);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        messages = MessageUtil.loadMessages(getActivity());
        if(adapter != null) {
            adapter.setList(messages);
        }
        TextSpeedService.setServiceReadyListener(serviceReadyListener);
    }

    private void setTextToView(View root, int id, String text) throws Exception {
        View view = root.findViewById(id);
        if(view == null) {
            throw new Exception("No view with such id in this root view");
        } else {
            TextView textView = (TextView) view;
            textView.setText(text);
        }
    }

}
