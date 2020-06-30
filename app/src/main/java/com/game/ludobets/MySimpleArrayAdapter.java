package com.game.ludobets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    private final Context context;
    private final List<String> values;
   private final List<String> challenger_name;
    private final List<String> userStatus;
    private final List<String> getAmount;
    private final String current_user_name;
    private final String userid;

    public MySimpleArrayAdapter(Context context, List<String> values, List<String> challenger_name, List<String> userStatus, String text_name, List<String> getAmount, String userID) {
        super(context, R.layout.betlist_item, values);
        this.context = context;
        this.values = values;
        this.challenger_name = challenger_name;
        this.userStatus=userStatus;
        this.getAmount=getAmount;
        this.current_user_name=text_name;
        this.userid=userID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.betlist_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.bet_item);
        Button buttonView = (Button) rowView.findViewById(R.id.view_bet);
        Button buttonPlay=(Button) rowView.findViewById(R.id.play_bet);
        Button buttonDelete=(Button)rowView.findViewById(R.id.bet_delete);
//        Button buttonRequest=(Button)rowView.findViewById(R.id.request_bet);
        textView.setText(values.get(position));
        String s=challenger_name.get(position);
        String status=userStatus.get(position);

        if (s.equals(current_user_name) && status.startsWith("NA")) {
            buttonPlay.setVisibility(View.GONE);
            buttonView.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        } else {
            if(status.startsWith("REQUESTED") && s !=current_user_name)
            {
                buttonView.setVisibility(View.GONE);
                buttonPlay.setText("REQUESTED");
                buttonPlay.setTextColor(Color.WHITE);
                buttonPlay.setBackgroundResource(R.drawable.requestbtn);
                buttonPlay.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }
            else if(status.startsWith("NA") && s !=current_user_name)
            {
                buttonView.setVisibility(View.GONE);
                buttonDelete.setVisibility(View.GONE);
                buttonPlay.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }
        }
        if(status.startsWith("ACCEPTED"))
        {
            buttonView.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
            buttonPlay.setText("START");
            buttonPlay.setTextColor(Color.WHITE);
            buttonPlay.setBackgroundResource(R.drawable.startbtn);
            buttonPlay.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String button_status=buttonPlay.getText().toString();
                if(button_status=="START")
                {
                            MainActivity.getInstance().DBdata(current_user_name);
                            Intent intent = new Intent(context, StartmatchActivity.class);
                            intent.putExtra("current_user",current_user_name);
                            intent.putExtra("current_user_id",userid);
                            context.startActivity(intent);
                             notifyDataSetChanged();
                }
                else if(button_status=="REQUESTED")
                {
                    Toast.makeText(context,current_user_name+" You Already Requested",Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
                else
               {
                   change_status(position);
                   CollectionReference requestRef = fStore.collection("BetRequest");
                   requestRef.whereEqualTo("status", "REQUESTED").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           if (task.isSuccessful()) {
                               for (QueryDocumentSnapshot document : task.getResult()) {
                                   String cur_status= (String) document.get("status");
                                   if(cur_status.startsWith("REQUESTED"))
                                   {
                                       buttonView.setVisibility(View.GONE);
                                       buttonPlay.setText("REQUESTED");
                                       buttonPlay.setTextColor(Color.WHITE);
                                       buttonPlay.setBackgroundResource(R.drawable.requestbtn);
                                       buttonPlay.setVisibility(View.VISIBLE);
                                       buttonDelete.setVisibility(View.VISIBLE);
                                       notifyDataSetChanged();
                                   }
                               }
                           }
                       }


                   });
                   MainActivity.getInstance().DBdata(current_user_name);
               }
            }
        });
      return rowView;
    }


    public void change_status(int position){
        CollectionReference requestRef = fStore.collection("BetRequest");
        requestRef.whereEqualTo("Request message", values.get(position)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<Object, String> map = new HashMap<>();
                        map.put("status", "REQUESTED");
                        requestRef.document(document.getId()).set(map, SetOptions.merge());

                        Map<String,String> res_map=new HashMap<>();
                        res_map.put("player_Name",current_user_name);
                        res_map.put("userID",userid);
                        res_map.put("ChallengerID",document.getString("userID"));
                        res_map.put("amount",getAmount.get(position));
                        res_map.put("status","REQUESTED");
                        fStore.collection("BetRequest").document(document.getString("userID")).collection("BetResponse").document(userid).set(res_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(context,current_user_name+" has been Requested",Toast.LENGTH_SHORT).show();
                                    MainActivity.getInstance().DBdata(current_user_name);
                                    notifyDataSetChanged();
                                }
                                else {
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}