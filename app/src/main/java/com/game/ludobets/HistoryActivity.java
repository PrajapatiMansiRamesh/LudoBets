package com.game.ludobets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    Toolbar toolbar;
    ListView historylist;
    TextView historynotFound;
    List<String> historyList=new ArrayList<>();
    List<String> historyTime=new ArrayList<>();
    List<String> challengerList=new ArrayList<>();
    List<String> playerList=new ArrayList<>();
    List<String> cstatusList=new ArrayList<>();
    List<String> pstatusList=new ArrayList<>();
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    String userID=fAuth.getCurrentUser().getUid();
    String userName,amount=null,challenger_name,challengerId,type,new_amount=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        historylist=findViewById(R.id.history_list);
        historynotFound=findViewById(R.id.historyNoFound);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("Ludo Bets");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        DocumentReference documentReference=fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userName=(documentSnapshot.getString("Name")).toString();
                new_amount=documentSnapshot.getString("new_amount");
                historyTime.add(documentSnapshot.getString("Time"));
                if(new_amount!=null && historyTime.size()>=0)
                {
                    type="add";
                    historyList.add("Added ₹"+new_amount+" through UPI");
                    HistoryArrayAdapter adapter =new HistoryArrayAdapter(HistoryActivity.this,historyList,historyTime,type);
                    adapter.notifyDataSetChanged();
                    historylist.setAdapter(adapter);
                }
            }
        });
        DocumentReference documentwithdraw=fStore.collection("Withdraw").document(userID);
        documentwithdraw.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    String WithdrawAmount=documentSnapshot.getString("WithdrawAmount").toString();
                    String WithdrawId=documentSnapshot.getString("PaymentId").toString();
                    String WithdrawMethod=documentSnapshot.getString("PaymentMethod").toString();
                    historyTime.add(documentSnapshot.getString("Time"));
                    if(new_amount!=null && historyTime.size()>=0)
                    {
                        type="withdraw";
                        historyList.add("Sended ₹"+WithdrawAmount+" to "+WithdrawId+" through "+WithdrawMethod);
                        HistoryArrayAdapter adapter =new HistoryArrayAdapter(HistoryActivity.this,historyList,historyTime,type);
                        adapter.notifyDataSetChanged();
                        historylist.setAdapter(adapter);
                    }
                }
            }
        });

        fStore.collection("BetRequest").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                        historyList.clear();
                        for(DocumentSnapshot snapshot:snapshotList){
                            challengerList.add(snapshot.getString("Challenger_Name"));
                            playerList.add(snapshot.getString("player_Name"));
                        }
                        if(challengerList.contains(userName))
                        {
                            DocumentReference documentReference=fStore.collection("BetRequest").document(userID);
                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    cstatusList.add((String) documentSnapshot.getString("Challenger_status"));
                                    amount=(String)documentSnapshot.getString("amount");
                                    historyTime.add((String)documentSnapshot.getString("Time"));
                                    String player = documentSnapshot.getString("player_Name").toString();
                                    CollectionReference player_coltn = documentReference.collection("BetResponse");
                                    player_coltn.whereEqualTo("player_Name", player).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot statusdoc : task.getResult()) {
                                                    pstatusList.add((String) statusdoc.getString("status"));
                                                }
                                                if(cstatusList.contains("Cancel Game/गेम रद्द करें") && pstatusList.contains("Cancel Game/गेम रद्द करें"))
                                                {
                                                    type="cancel";
                                                    historyList.add("CANCELLED ₹"+amount+" against "+player);
                                                    HistoryArrayAdapter adapter =new HistoryArrayAdapter(HistoryActivity.this,historyList,historyTime,type);
                                                    adapter.notifyDataSetChanged();
                                                    historylist.setAdapter(adapter);
                                                }
                                                else if(cstatusList.contains("I WON / मैं जीत गया") && pstatusList.contains("I LOST / में हार गया"))
                                                {
                                                    type="won";
                                                    historyList.add("WON ₹"+amount+" by "+player);
                                                    HistoryArrayAdapter adapter =new HistoryArrayAdapter(HistoryActivity.this,historyList,historyTime,type);
                                                    adapter.notifyDataSetChanged();
                                                    historylist.setAdapter(adapter);
                                                }
                                                else if(cstatusList.contains("I LOST / में हार गया") && pstatusList.contains("I WON / मैं जीत गया"))
                                                {
                                                    type="loss";
                                                    historyList.add("LOSS ₹"+amount+" by "+player);
                                                    HistoryArrayAdapter adapter =new HistoryArrayAdapter(HistoryActivity.this,historyList,historyTime,type);
                                                    adapter.notifyDataSetChanged();
                                                    historylist.setAdapter(adapter);
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                        else if(playerList.contains(userName))
                        {
                            CollectionReference playerdetail=fStore.collection("BetRequest");
                            playerdetail.whereEqualTo("player_Name",userName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        for (QueryDocumentSnapshot playerdoc : task.getResult()) {
                                            challenger_name=(String)playerdoc.getString("Challenger_Name");
                                            challengerId=(String)playerdoc.getString("userID");
                                            cstatusList.add((String) playerdoc.getString("Challenger_status"));
                                            amount=(String)playerdoc.getString("amount");
                                        }
                                        CollectionReference playerstatus=fStore.collection("BetRequest").document(challengerId).collection("BetResponse");
                                        playerstatus.whereEqualTo("player_Name",userName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    for(QueryDocumentSnapshot playerStatus:task.getResult()){
                                                        pstatusList.add((String) playerStatus.getString("status"));
                                                        historyTime.add((String)playerStatus.getString("Time"));
                                                    }

                                                    if(cstatusList.contains("Cancel Game/गेम रद्द करें") && pstatusList.contains("Cancel Game/गेम रद्द करें"))
                                                    {
                                                        type="cancel";
                                                        historyList.add("CANCELLED ₹"+amount+" against "+challenger_name);
                                                        HistoryArrayAdapter adapter =new HistoryArrayAdapter(HistoryActivity.this,historyList,historyTime, type);
                                                        adapter.notifyDataSetChanged();
                                                        historylist.setAdapter(adapter);
                                                    }
                                                    else if(cstatusList.contains("I WON / मैं जीत गया") && pstatusList.contains("I LOST / में हार गया"))
                                                    {
                                                        type="loss";
                                                        historyList.add("LOST ₹"+amount+" by "+challenger_name);
                                                        HistoryArrayAdapter adapter =new HistoryArrayAdapter(HistoryActivity.this,historyList,historyTime,type);
                                                        adapter.notifyDataSetChanged();
                                                        historylist.setAdapter(adapter);
                                                    }
                                                    else if(cstatusList.contains("I LOST / में हार गया") && pstatusList.contains("I WON / मैं जीत गया"))
                                                    {
                                                        type="won";
                                                        historyList.add("WON ₹"+amount+" by "+challenger_name);
                                                        HistoryArrayAdapter adapter =new HistoryArrayAdapter(HistoryActivity.this,historyList,historyTime,type);
                                                        adapter.notifyDataSetChanged();
                                                        historylist.setAdapter(adapter);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}