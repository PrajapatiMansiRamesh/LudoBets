package com.game.ludobets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
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

public class ResPopupArrayAdapter extends ArrayAdapter<String> {
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    private final Context context;
    private final List<String> values;
    private final String current_user_name;
    private final Dialog bet_response;
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    String userID=fAuth.getCurrentUser().getUid();
    public ResPopupArrayAdapter(Context context, List<String> values, String text_name, Dialog bet_response) {
        super(context, R.layout.betreslist_item, values);
        this.context = context;
        this.values = values;
        this.current_user_name=text_name;
        this.bet_response=bet_response;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.betreslist_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.response_bet);
        Button buttonView = (Button) rowView.findViewById(R.id.accept_btn);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(values.get(position)+" wants to play with you");

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder acceptDialog=new AlertDialog.Builder(v.getContext());
                acceptDialog.setTitle(current_user_name);
                acceptDialog.setMessage("Click on Accept to Accept the Challenge");
                acceptDialog.setCancelable(true);
                acceptDialog.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CollectionReference requestRef = fStore.collection("BetRequest");
                        requestRef.whereEqualTo("status", "REQUESTED").whereEqualTo("Challenger_Name",current_user_name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<Object, String> map = new HashMap<>();
                                        map.put("status", "ACCEPTED");
                                        map.put("player_Name",values.get(position));
                                        requestRef.document(document.getId()).set(map, SetOptions.merge());
                                    }
                                }
                            }

                        });
                        CollectionReference responseRef = fStore.collection("BetRequest").document(userID).collection("BetResponse");
                        responseRef.whereEqualTo("status", "REQUESTED").whereEqualTo("player_Name",values.get(position)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<Object, String> map = new HashMap<>();
                                        map.put("status", "ACCEPTED");
                                        responseRef.document(document.getId()).set(map, SetOptions.merge());
                                    }
                                }
                            }

                        });
                        Toast.makeText(v.getContext(),"Accepted. You can Start the Match Now",Toast.LENGTH_SHORT).show();
                        MainActivity.getInstance().DBdata(current_user_name);
//                        notifyDataSetChanged();

                    }
                });
                acceptDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(v.getContext(),"You Cancel Challenge",Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog=acceptDialog.create();
                alertDialog.show();
                bet_response.dismiss();
//                notifyDataSetChanged();
            }
        });
        return rowView;
    }
}
