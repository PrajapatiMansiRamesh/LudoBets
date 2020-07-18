package com.game.ludobets;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HistoryArrayAdapter extends ArrayAdapter<String>{
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    private final Context context;
    private final List<String> historyList;
    private final List<String> historyTime;
    private final String type;
    public HistoryArrayAdapter(Context context, List<String> historyList, List<String> historyTime, String type) {
        super(context, R.layout.historylist_item,historyList);
        this.context = context;
        this.historyList = historyList;
        this.historyTime = historyTime;
        this.type=type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.historylist_item, parent, false);
        TextView msg = (TextView) rowView.findViewById(R.id.history_item);
        TextView msg_time = (TextView) rowView.findViewById(R.id.history_time);
        if(type.equals("cancel"))
        {
            msg.setText(historyList.get(position));
            msg.setTextColor(Color.parseColor("#9ca731"));
            msg_time.setText("Date-Time: "+historyTime.get(position));
        }
        else if(type.equals("won"))
        {
            msg.setText(historyList.get(position));
            msg.setTextColor(Color.parseColor("#499543"));
            msg_time.setText("Date-Time: "+historyTime.get(position));
        }
        else if(type.equals("loss"))
        {
            msg.setText(historyList.get(position));
            msg.setTextColor(Color.parseColor("#d80018"));
            msg_time.setText("Date-Time: "+historyTime.get(position));
        }
        else if(type.equals("add"))
        {
            msg.setText(historyList.get(position));
            msg.setTextColor(Color.parseColor("#000000"));
            msg_time.setText("Date-Time: "+historyTime.get(position));
        }
        else if(type.equals("withdraw"))
        {
            msg.setText(historyList.get(position));
            msg.setTextColor(Color.parseColor("#000000"));
            msg_time.setText("Date-Time: "+historyTime.get(position));
        }
        return rowView;
    }

}
