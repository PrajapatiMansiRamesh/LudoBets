package com.game.ludobets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class PandingmatchesActivity extends AppCompatActivity {
    Toolbar toolbar;
    ListView pandinglist;
    TextView pandingnotFound;
    List<String> pandingList=new ArrayList<>();
    List<String> challengerList=new ArrayList<>();
    List<String> playerList=new ArrayList<>();
    List<String> cstatusList=new ArrayList<>();
    List<String> pstatusList=new ArrayList<>();
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    String userID=fAuth.getCurrentUser().getUid();
    String userName,amount=null,challenger_name,challengerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandingmatches);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pandinglist=findViewById(R.id.panding_list);
        pandingnotFound=findViewById(R.id.pandingNoFound);
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
            }
        });

        fStore.collection("BetRequest").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                        pandingList.clear();
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
                                    String player = documentSnapshot.getString("player_Name").toString();
                                    CollectionReference player_coltn = documentReference.collection("BetResponse");
                                    player_coltn.whereEqualTo("player_Name", player).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot statusdoc : task.getResult()) {
                                                    pstatusList.add((String) statusdoc.getString("status"));
                                                }
                                                pandingList.add(userName +" VS "+player+ " for "+amount);
                                               if(cstatusList.contains("I WON / मैं जीत गया") && pstatusList.contains("Cancel Game/गेम रद्द करें"))
                                                {
                                                    if(pandingList.size()<=0)
                                                    {
                                                        pandingnotFound.setVisibility(View.VISIBLE);
                                                        pandinglist.setVisibility(View.GONE);
                                                    }
                                                    else
                                                    {
                                                        ArrayAdapter<String> adapter =new ArrayAdapter<String>(PandingmatchesActivity.this,R.layout.pandinglist_item,R.id.panding_item,pandingList);
                                                        adapter.notifyDataSetChanged();
                                                        pandinglist.setAdapter(adapter);
                                                    }
                                                }
                                                else if(cstatusList.contains("Cancel Game/गेम रद्द करें") && pstatusList.contains("I WON / मैं जीत गया"))
                                                {
                                                    if(pandingList.size()<=0)
                                                    {
                                                        pandingnotFound.setVisibility(View.VISIBLE);
                                                        pandinglist.setVisibility(View.GONE);
                                                    }
                                                    else
                                                    {
                                                        ArrayAdapter<String> adapter =new ArrayAdapter<String>(PandingmatchesActivity.this,R.layout.pandinglist_item,R.id.panding_item,pandingList);
                                                        adapter.notifyDataSetChanged();
                                                        pandinglist.setAdapter(adapter);
                                                    }
                                                }
                                               else if(cstatusList.contains("I WON / मैं जीत गया") && pstatusList.contains("I WON / मैं जीत गया"))
                                               {
                                                   if(pandingList.size()<=0)
                                                   {
                                                       pandingnotFound.setVisibility(View.VISIBLE);
                                                       pandinglist.setVisibility(View.GONE);
                                                   }
                                                   else
                                                   {
                                                       ArrayAdapter<String> adapter =new ArrayAdapter<String>(PandingmatchesActivity.this,R.layout.pandinglist_item,R.id.panding_item,pandingList);
                                                       adapter.notifyDataSetChanged();
                                                       pandinglist.setAdapter(adapter);
                                                   }
                                               }
                                               else
                                               {
                                                   pandingnotFound.setVisibility(View.VISIBLE);
                                                   pandinglist.setVisibility(View.GONE);
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
                                        }
                                        pandingList.add(challenger_name +" VS "+userName+ " for "+amount);
                                        if(cstatusList.contains("I WON / मैं जीत गया") && pstatusList.contains("Cancel Game/गेम रद्द करें"))
                                        {
                                            if(pandingList.size()<=0)
                                            {
                                                pandingnotFound.setVisibility(View.VISIBLE);
                                                pandinglist.setVisibility(View.GONE);
                                            }
                                            else
                                            {
                                                ArrayAdapter<String> adapter =new ArrayAdapter<String>(PandingmatchesActivity.this,R.layout.pandinglist_item,R.id.panding_item,pandingList);
                                                adapter.notifyDataSetChanged();
                                                pandinglist.setAdapter(adapter);
                                            }
                                        }
                                        else if(cstatusList.contains("Cancel Game/गेम रद्द करें") && pstatusList.contains("I WON / मैं जीत गया"))
                                        {
                                            if(pandingList.size()<=0)
                                            {
                                                pandingnotFound.setVisibility(View.VISIBLE);
                                                pandinglist.setVisibility(View.GONE);
                                            }
                                            else
                                            {
                                                ArrayAdapter<String> adapter =new ArrayAdapter<String>(PandingmatchesActivity.this,R.layout.pandinglist_item,R.id.panding_item,pandingList);
                                                adapter.notifyDataSetChanged();
                                                pandinglist.setAdapter(adapter);
                                            }
                                        }
                                        else if(cstatusList.contains("I WON / मैं जीत गया") && pstatusList.contains("I WON / मैं जीत गया"))
                                        {
                                            if(pandingList.size()<=0)
                                            {
                                                pandingnotFound.setVisibility(View.VISIBLE);
                                                pandinglist.setVisibility(View.GONE);
                                            }
                                            else
                                            {
                                                ArrayAdapter<String> adapter =new ArrayAdapter<String>(PandingmatchesActivity.this,R.layout.pandinglist_item,R.id.panding_item,pandingList);
                                                adapter.notifyDataSetChanged();
                                                pandinglist.setAdapter(adapter);
                                            }
                                        }
                                        else
                                        {
                                            pandingnotFound.setVisibility(View.VISIBLE);
                                            pandinglist.setVisibility(View.GONE);
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