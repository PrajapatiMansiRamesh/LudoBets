package com.game.ludobets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private final List<String> playerStatus;
    private final List<String> challengerStatus;
    private final List<String> playerName;
    private final List<String> getAmount;
    private final String current_user_name;
    private final String current_challenger;
    private final String current_player;
    private final String userid;
    String checkbalance=null;
    public MySimpleArrayAdapter(Context context, List<String> values, List<String> challenger_name, List<String> userStatus, String text_name, List<String> getAmount, String userID, List<String> playerName, List<String> playerStatus, String current_challenger, String current_player, List<String> challengerStatus) {
        super(context, R.layout.betlist_item, values);
        this.context = context;
        this.values = values;
        this.challenger_name = challenger_name;
        this.playerName=playerName;
        this.userStatus=userStatus;
        this.playerStatus=playerStatus;
        this.getAmount=getAmount;
        this.current_user_name=text_name;
        this.userid=userID;
        this.current_challenger=current_challenger;
        this.current_player=current_player;
        this.challengerStatus=challengerStatus;
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
        String p=playerName.get(position);
        String status=userStatus.get(position);

        if (s.equals(current_user_name) && status.startsWith("NA")) {
            buttonPlay.setVisibility(View.GONE);
            buttonView.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
        } else {
            if (s.equals(current_user_name) && status.startsWith("REQUESTED")) {
                buttonPlay.setVisibility(View.GONE);
                buttonView.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
            }
            else if(status.startsWith("REQUESTED") && p!=current_user_name)
            {
                buttonView.setVisibility(View.GONE);
                buttonDelete.setVisibility(View.GONE);
                buttonPlay.setVisibility(View.VISIBLE);

            }
            else if(status.startsWith("NA") && s !=current_user_name)
            {
                buttonView.setVisibility(View.GONE);
                buttonDelete.setVisibility(View.GONE);
                buttonPlay.setVisibility(View.VISIBLE);
            }
            else if(status.startsWith("REQUESTED"))
            {
                buttonView.setVisibility(View.GONE);
                buttonPlay.setText("REQUESTED");
                buttonPlay.setTextColor(Color.WHITE);
                buttonPlay.setBackgroundResource(R.drawable.requestbtn);
                buttonPlay.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
            }
            else if(status.startsWith("COMPLETED"))
            {
                textView.setVisibility(View.GONE);
                buttonDelete.setVisibility(View.GONE);
            }
        }
        if(status.startsWith("ACCEPTED") && s.equals(current_user_name))
        {
            buttonView.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
            buttonPlay.setText("START");
            buttonPlay.setTextColor(Color.WHITE);
            buttonPlay.setBackgroundResource(R.drawable.startbtn);
            buttonPlay.setVisibility(View.VISIBLE);
        }
        else if(status.startsWith("ACCEPTED") && p.equals(current_user_name))
        {
            buttonView.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
            buttonPlay.setText("START");
            buttonPlay.setTextColor(Color.WHITE);
            buttonPlay.setBackgroundResource(R.drawable.startbtn);
            buttonPlay.setVisibility(View.VISIBLE);
        }
        else if(status.startsWith("ACCEPTED"))
        {
            buttonView.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
            buttonPlay.setVisibility(View.VISIBLE);
        }

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String button_status=buttonPlay.getText().toString();
                if(button_status=="START")
                {
                        if(challenger_name.contains(current_user_name))
                        {
                            if(challengerStatus.contains("I WON / मैं जीत गया") || challengerStatus.contains("I LOST / में हार गया") || challengerStatus.contains("Cancel Game/गेम रद्द करें"))
                            {
                                AlertDialog.Builder acceptDialog=new AlertDialog.Builder(v.getContext());
                                acceptDialog.setTitle("Contact "+current_player);
                                acceptDialog.setMessage("Call "+current_player+" instead of Admin");
                                acceptDialog.setCancelable(true);
                                acceptDialog.setPositiveButton("WHATS APP", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CollectionReference msgref=fStore.collection("users");
                                        msgref.whereEqualTo("Name",current_player).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    for(QueryDocumentSnapshot phondocument:task.getResult()){
                                                        String phoneNo="+91"+phondocument.getString("phone");
//                                                                    System.out.println("Phone:"+phoneNo);
                                                        openWhatsApp(phoneNo);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                acceptDialog.setNegativeButton("CALL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CollectionReference msgref=fStore.collection("users");
                                        msgref.whereEqualTo("Name",current_player).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    for(QueryDocumentSnapshot phonedocument:task.getResult()){
                                                        String phoneNo="+91"+phonedocument.getString("phone");
                                                        openCall(phoneNo);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                AlertDialog alertDialog=acceptDialog.create();
                                alertDialog.show();
                            }
                            else
                            {
                                MainActivity.getInstance().DBdata(current_user_name);
                                Intent intent = new Intent(context, StartmatchActivity.class);
                                intent.putExtra("current_user",current_user_name);
                                intent.putExtra("current_user_id",userid);
                                context.startActivity(intent);
                            }

                        }
                        else if(playerName.contains(current_user_name))
                        {
                            if(playerStatus.contains("I WON / मैं जीत गया") || playerStatus.contains("I LOST / में हार गया") || playerStatus.contains("Cancel Game/गेम रद्द करें"))
                            {
                                AlertDialog.Builder acceptDialog=new AlertDialog.Builder(v.getContext());
                                acceptDialog.setTitle("Contact "+current_challenger);
                                acceptDialog.setMessage("Call "+current_challenger+" instead of Admin");
                                acceptDialog.setCancelable(true);
                                acceptDialog.setPositiveButton("WHATS APP", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CollectionReference msgref=fStore.collection("users");
                                        msgref.whereEqualTo("Name",current_challenger).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    for(QueryDocumentSnapshot phondocument:task.getResult()){
                                                        String phoneNo="+91"+phondocument.getString("phone");
//                                                                    System.out.println("Phone:"+phoneNo);
                                                        openWhatsApp(phoneNo);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                acceptDialog.setNegativeButton("CALL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CollectionReference msgref=fStore.collection("users");
                                        msgref.whereEqualTo("Name",current_challenger).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    for(QueryDocumentSnapshot phonedocument:task.getResult()){
                                                        String phoneNo="+91"+phonedocument.getString("phone");
                                                        openCall(phoneNo);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                AlertDialog alertDialog=acceptDialog.create();
                                alertDialog.show();
                            }
                            else
                            {
                                MainActivity.getInstance().DBdata(current_user_name);
                                Intent intent = new Intent(context, StartmatchActivity.class);
                                intent.putExtra("current_user",current_user_name);
                                intent.putExtra("current_user_id",userid);
                                context.startActivity(intent);
                            }
                        }
                }
                else if(button_status=="REQUESTED")
                {
                    Toast.makeText(context,current_user_name+" You Already Requested",Toast.LENGTH_SHORT).show();
//                    notifyDataSetChanged();
                }
                else
               {    int playamunt=Integer.parseInt(getAmount.get(position).toString());
                   DocumentReference documentReference=fStore.collection("users").document(userid);
                   documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                       @Override
                       public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                           checkbalance=(String)documentSnapshot.getString("wallet");
                           if(checkbalance==null)
                           {
                               int current_balance=0;
                               Toast.makeText(context,"You Don't have Sufficient Balance to make this Match.",Toast.LENGTH_SHORT).show();
                           }
                           else
                           {
                               int current_balance=Integer.parseInt(checkbalance);
                               if(playamunt<current_balance)
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
                                                   }
                                               }
                                           }
                                       }
                                   });
                                   MainActivity.getInstance().DBdata(current_user_name);
                               }
                               else
                               {
                                   Toast.makeText(context,"You Don't have Sufficient Balance to make this Match.",Toast.LENGTH_SHORT).show();
                               }
                           }
                       }
                   });

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
                        String checkStatus=document.getString("status");
                        if(checkStatus.equals("NA"))
                        {
                            Map<Object, String> map = new HashMap<>();
                            map.put("status", "REQUESTED");
                            requestRef.document(document.getId()).set(map, SetOptions.merge());
                        }
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
//                                    notifyDataSetChanged();
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

    public void openWhatsApp(String phoneNo){
        try {
            String text = "This is a test";// Replace with your message.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+phoneNo +"&text="+text));
            context.startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openCall(String number)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        context.startActivity(intent);
    }
}