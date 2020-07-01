package com.game.ludobets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    TextView tvInfo,name,email,betnofound;
    EditText amount;
    Button set_amount;
    ListView bet_list,betresponse_list;
    String text_name;
    Dialog bet_response;
    List<String> namesList=new ArrayList<>();
    List<String> responseList=new ArrayList<>();
    List<String> challenger_name=new ArrayList<>();
    List<String> userStatus=new ArrayList<>();
    List<String> getAmount=new ArrayList<>();
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    String userID=fAuth.getCurrentUser().getUid();
    private static MainActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        amount=findViewById(R.id.amout);
        set_amount=findViewById(R.id.set_amount);
        bet_list=findViewById(R.id.bet_list);
        betnofound=findViewById(R.id.betNoFound);
        bet_response=new Dialog(this);
        DocumentReference documentReference=fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
              text_name=(documentSnapshot.getString("Name")).toString();
            }
        });

        set_amount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String price=amount.getText().toString();

                if(price.isEmpty())
                {
                    Toast.makeText(MainActivity.this,"Please Enter Amount!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int checknumber=Integer.parseInt(price);
                   if(checknumber>=30 && checknumber % 5 == 0 && checknumber<=20000)
                {
                    Map<String,String> map=new HashMap<>();
                    map.put("Challenger_Name",text_name);
                    map.put("userID",userID);
                    map.put("Request message",text_name+" wants to play for "+price);
                    map.put("amount",price);
                    map.put("status","NA");
                    fStore.collection("BetRequest").document(userID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                DBdata(text_name);
                                Toast.makeText(MainActivity.this,"Request Sent",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this,"Request Not Sent",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this,"Challenge Must Be Greater Then 30, And Must Be In Multiple Of 5, Don't Start With 0",Toast.LENGTH_SHORT).show();
                }
                }
                amount.getText().clear();
                }

        });
        DBdata(text_name);
        Toolbar toolbar=(Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        //phone=findViewById(R.id.profilephone);
        drawerLayout=findViewById(R.id.drawer_layout);
        tvInfo=findViewById(R.id.tv_info);
        NavigationView navigationView=(NavigationView) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,
                toolbar,R.string.drawer_open,R.string.drawe_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void DBdata(String text_name){
        fStore.collection("BetRequest").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                        namesList.clear();
                        for(DocumentSnapshot snapshot:snapshotList){
                            namesList.add(snapshot.getString("Request message"));
                            challenger_name.add(snapshot.getString("Challenger_Name"));
                            userStatus.add(snapshot.getString("status"));
                            getAmount.add(snapshot.getString("amount"));

                        }
//                        ArrayAdapter<String>adapter =new ArrayAdapter<String>(MainActivity.this,R.layout.betlist_item,R.id.bet_item,namesList);
//                        getView(bet_list);
                        if(namesList.size()<=0)
                        {
                            betnofound.setVisibility(View.VISIBLE);
                            bet_list.setVisibility(View.GONE);
                        }
                        else
                        {
                            MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(MainActivity.this, namesList, challenger_name,userStatus,text_name,getAmount,userID);
                            adapter.notifyDataSetChanged();
                            bet_list.setAdapter(adapter);
                            refresh(1000);
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void refresh(int milliseconds){
        final Handler handler = new Handler();
        if(bet_list.getVisibility()==View.GONE)
        {
            bet_list.setVisibility(View.VISIBLE);
            betnofound.setVisibility(View.GONE);
        }
        final Runnable runnable=new Runnable() {
            @Override
            public void run() {
                DBdata(text_name);
            }
        };
        handler.postDelayed(runnable,milliseconds);
    }

    public void Show_popup(View v)
    {
        bet_response.setContentView(R.layout.betpopup);
//        Button accept_challengebtn=(Button)bet_response.findViewById(R.id.accept_btn);
////        TextView bet_response_name=(TextView)bet_response.findViewById(R.id.response_bet);
         betresponse_list=(ListView)bet_response.findViewById(R.id.betresponse_list);
        TextView set_price=(TextView)bet_response.findViewById(R.id.set_price);
        DocumentReference documentReference=fStore.collection("BetRequest").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                {
                    set_price.setText("Match Amount: "+documentSnapshot.getString("amount").toString());
                }
            }
        });
        fStore.collection("BetRequest").document(userID).collection("BetResponse").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                        responseList.clear();
                        for(DocumentSnapshot snapshot:snapshotList){
                           responseList.add(snapshot.getString("player_Name"));
                       }
                        if(responseList.size()<=0)
                        {
                            Toast.makeText(MainActivity.this,"No One Accept Your Request Yet",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            ResPopupArrayAdapter res_adapter = new ResPopupArrayAdapter(MainActivity.this, responseList,text_name,bet_response);
                            res_adapter.notifyDataSetChanged();
                            betresponse_list.setAdapter(res_adapter);
                            bet_response.show();
                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    public void Delete_btn(View v)
    {
        View parentRow = (View) v.getParent();
        bet_list = (ListView) parentRow.getParent();
        final int position = bet_list.getPositionForView(parentRow);
        String CurentItemName = (String) bet_list.getItemAtPosition(position);
//        Toast.makeText(MainActivity.this,"item name"+CurentItemName+" Position: "+position,Toast.LENGTH_SHORT).show();
        removeItemFromList(position,CurentItemName);
    }


       protected void removeItemFromList(int position,String CurentItemName)
    {
        final int deletePosition = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Alert!");
        builder.setMessage("Do you want delete this item?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {

                        //delete data from DB
                        fStore.collection("BetRequest").whereEqualTo("Request message",CurentItemName).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        WriteBatch batch=fStore.batch();
                                        List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                                        for(DocumentSnapshot snapshot:snapshotList){
                                            batch.delete(snapshot.getReference());
                                        }
                                        batch.commit()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        DBdata(text_name);
                                                        Toast.makeText(MainActivity.this,"Challege Deleted Successfull!",Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity.this,"Cannot be Delete",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog  alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_settings){
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent); }
        else if(id==R.id.action_search){ tvInfo.setText("Search"); return true;}
        else if(id==R.id.action_pay){ tvInfo.setText("pay"); return true;
        }
        else if(id==R.id.action_logout){ FirebaseAuth.getInstance().signOut(); //logout to the user
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);}

        return super.onOptionsItemSelected(item);
    }

//    public void logout(View view) {
//        FirebaseAuth.getInstance().signOut(); //logout to the user
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String itemName=(String) item.getTitle();
//        tvInfo.setText(itemName);
        closeDrawer();
        switch (item.getItemId()){
            case R.id.item_a:{
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("current_user_id",userID);
                startActivity(intent);
            } break;
            case R.id.item_b:break;
            case R.id.item_c:break;
            case R.id.item_d:{
                AlertDialog.Builder acceptDialog=new AlertDialog.Builder(MainActivity.this);
                acceptDialog.setTitle("Invite a Friend");
                acceptDialog.setMessage("Copy the link below and send it to the Friend you want to Invite on WhatsApp.");
                acceptDialog.setCancelable(true);
                acceptDialog.setPositiveButton("COPY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String link="link";
                        ClipboardManager clipboard=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip=ClipData.newPlainText("Code",link);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(MainActivity.this, "Link Copied!", Toast.LENGTH_SHORT).show();
                    }
                });
                acceptDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog=acceptDialog.create();
                alertDialog.show();
            } break;
            case R.id.item_e:{
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }break;
            case R.id.item_f: {Intent intent = new Intent(MainActivity.this, termsandconditionActivity.class);
                startActivity(intent);
            }break;


        }

        return true;
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    private void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            closeDrawer();
        }
        super.onBackPressed();
    }

    public boolean OnCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

}
