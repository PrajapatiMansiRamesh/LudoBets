package com.game.ludobets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    TextView name,betnofound,balance;
    ImageView faq,wallet;
    EditText amount,wallet_amount,withdraw_price,withdraw_id;
    Button set_amount,add_money,withdraw_money,upi_send,paytm_send,proceed_withdraw;
    RadioGroup CheckMethod;
    RadioButton SelectedMethod;
    String userId1,userId2;
    int CountRes=0;
    //    RecyclerView bet_list;
    private RecyclerView.LayoutManager layoutManager;
    ListView betresponse_list,historylist;;
    ListView bet_list;
    String text_name,checkbalance=null,email;
    Dialog bet_response,wellet_dialog,add_dialog,withdraw_dialog;
    List<String> namesList=new ArrayList<>();
    List<String> responseList=new ArrayList<>();
    List<String> challenger_name=new ArrayList<>();
    List<String> challenger_player=new ArrayList<>();
    List<String> player_name=new ArrayList<>();
    List<String> userStatus=new ArrayList<>();
    List<String> playerStatus=new ArrayList<>();
    List<String> challengerStatus=new ArrayList<>();
    List<String> getAmount=new ArrayList<>();
    List<String> msg=new ArrayList<>();
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    String userID=fAuth.getCurrentUser().getUid();
    private static MainActivity instance;
    TextView current_balance;
    String payment_method=null,current_challenger,current_player;
    boolean net=false;
    final int UPI_PAYMENT = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkConnection();
        if(net==true)
        {
            instance = this;
            amount=findViewById(R.id.amout);
            faq=findViewById(R.id.faq);
            wallet=findViewById(R.id.wallet);
            set_amount=findViewById(R.id.set_amount);
            bet_list=findViewById(R.id.bet_list);
            betnofound=findViewById(R.id.betNoFound);
            historylist=findViewById(R.id.history_list);
            balance=findViewById(R.id.balance);
            bet_response=new Dialog(this);
            wellet_dialog=new Dialog(this);
            add_dialog=new Dialog(this);
            withdraw_dialog=new Dialog(this);
            CheckMethod=withdraw_dialog.findViewById(R.id.withdrow_method);
//        balance.setText("₹0.00");
            faq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, HowtouseActivity.class);
                    startActivity(intent);
                }
            });

            DocumentReference documentReference=fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    text_name=(documentSnapshot.getString("Name")).toString();
                    email=(documentSnapshot.getString("Email")).toString();
                    checkbalance=(String)documentSnapshot.getString("wallet");
                    if(checkbalance!=null)
                    {
                        balance.setText("₹"+checkbalance+".00");
                    }
                    else
                    {
                        balance.setText("₹0.00");
                    }
                    DBdata(text_name);
                    automatic_popup();
                }
            });

            wallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, WalletActivity.class);
//                intent.putExtra("current_user_name",text_name);
//                intent.putExtra("current_user_email",email);
//                intent.putExtra("current_user_id",userID);
//                startActivity(intent);
                    wellet_dialog.setContentView(R.layout.activity_wallet);
                    current_balance=wellet_dialog.findViewById(R.id.current_balance);
                    add_money=wellet_dialog.findViewById(R.id.add_money);
                    withdraw_money=wellet_dialog.findViewById(R.id.withdraw_money);
                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                    CollectionReference requestRef = fStore.collection("users");
                    requestRef.whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    checkbalance=(String)document.getString("wallet");
                                    if(checkbalance!=null)
                                    {
                                        current_balance.setText("₹"+checkbalance+".00");
                                    }
                                    else
                                    {
                                        current_balance.setText("₹0.00");
                                    }
                                }
                            }
                        }
                    });
                    add_money.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            wellet_dialog.dismiss();
                            add_dialog.setContentView(R.layout.add_wallet_money);
                            wallet_amount=add_dialog.findViewById(R.id.amount_et);
                            upi_send=add_dialog.findViewById(R.id.upisend);
                            paytm_send=add_dialog.findViewById(R.id.paytmsend);
                            upi_send.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Getting the values from the EditTexts
                                    String amount = wallet_amount.getText().toString();
                                    if(amount.isEmpty())
                                    {
                                        Toast.makeText(MainActivity.this,"Please Add Amount First",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        String name = text_name;
                                        String upiId = "8295350656@paytm";
                                        payUsingUpi(amount,name,upiId);
                                    }
                                }
                            });
                            add_dialog.show();
                        }
                    });



                    withdraw_money.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            wellet_dialog.dismiss();
                            withdraw_dialog.setContentView(R.layout.withdraw_wallet_money);
                            withdraw_price=withdraw_dialog.findViewById(R.id.amount_wd);
                            withdraw_id=withdraw_dialog.findViewById(R.id.withdraw_id);
                            CheckMethod=withdraw_dialog.findViewById(R.id.withdrow_method);
                            CheckMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                                @Override
                                public void onCheckedChanged(RadioGroup radioGroup, int radioButtonID) {
                                    switch(radioButtonID) {
                                        case R.id.Paytm:
                                            payment_method="Paytm";
//                                        Toast.makeText(MainActivity.this, "Selected"+payment_method, Toast.LENGTH_SHORT).show();
                                            break;
                                        case R.id.Googlepay:
                                            payment_method="Google Pay";
//                                        Toast.makeText(MainActivity.this, "Selected"+payment_method , Toast.LENGTH_SHORT).show();
                                            break;
                                        case R.id.Upi:
                                            payment_method="UPI";
//                                        Toast.makeText(MainActivity.this, "Selected"+payment_method, Toast.LENGTH_SHORT).show();
                                            break;
                                        case R.id.Phonepe:
                                            payment_method="Phone Pe";
//                                        Toast.makeText(MainActivity.this, "Selected"+payment_method, Toast.LENGTH_SHORT).show();
                                            break;

                                    }
                                }
                            });
                            proceed_withdraw=withdraw_dialog.findViewById(R.id.proceed_withdraw);
                            proceed_withdraw.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String WithdrawAmount = withdraw_price.getText().toString();
                                    String WithdrawId=withdraw_id.getText().toString();
                                    if(WithdrawAmount.isEmpty())
                                    {
                                        Toast.makeText(MainActivity.this,"Please Enter Amount First.",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        if(WithdrawId.isEmpty())
                                        {
                                            Toast.makeText(MainActivity.this,"Please Enter Payment or UPI ID.",Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {   if(CheckMethod.getCheckedRadioButtonId()== -1 || payment_method==null)
                                        {
                                            Toast.makeText(MainActivity.this,"Please Select Payment Method.",Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {      int current_wihdraw=Integer.parseInt(WithdrawAmount);
                                            int current_balance=Integer.parseInt(checkbalance);
                                            if(current_wihdraw<=current_balance)
                                            {
                                                Map<String,Object> instwithdraw=new HashMap<>();
                                                instwithdraw.put("UserName",text_name);
                                                instwithdraw.put("UserId",userID);
                                                instwithdraw.put("WithdrawAmount",WithdrawAmount);
                                                instwithdraw.put("PaymentId",WithdrawId);
                                                instwithdraw.put("PaymentMethod",payment_method);
                                                instwithdraw.put("Time", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                                                instwithdraw.put("status","NA");
                                                fStore.collection("Withdraw").document(userID).set(instwithdraw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(MainActivity.this,"Withdraw Request Sent",Toast.LENGTH_SHORT).show();
                                                            CollectionReference requestRef = fStore.collection("users");
                                                            requestRef.whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            int current_balanceIns=(current_balance - current_wihdraw);
                                                                            Map<Object,String> map = new HashMap<>();
                                                                            map.put("wallet",Integer.toString(current_balanceIns));
                                                                            requestRef.document(document.getId()).set(map, SetOptions.merge());
                                                                        }
                                                                        withdraw_dialog.dismiss();
                                                                    }
                                                                }
                                                            });

                                                        }
                                                        else {
                                                            Toast.makeText(MainActivity.this,"Withdraw Request Not Sent",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else
                                            {
                                                Toast.makeText(MainActivity.this,"Withdraw Amount Must Less Then Balance.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        }

                                    }
                                }
                            });
                            withdraw_dialog.show();
                        }
                    });
                    wellet_dialog.show();
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
                        int current_balance=Integer.parseInt(checkbalance);
                        if(challenger_name.contains(text_name) && userStatus.contains("REQUESTED") || userStatus.contains("ACCEPTED"))
                        {
                            AlertDialog.Builder acceptDialog=new AlertDialog.Builder(MainActivity.this);
                            acceptDialog.setTitle("Alert !");
                            acceptDialog.setMessage("Please Complete Bet Requested First!");
                            acceptDialog.setCancelable(true);
                            acceptDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alertDialog=acceptDialog.create();
                            alertDialog.show();
                        }
                        else if(checknumber>=30 && checknumber % 5 == 0 && checknumber<=20000)
                        {
                            if(checknumber<=current_balance)
                            {
                                DocumentReference docRef = fStore.collection("BetRequest").document(userID);
                                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot snap, @Nullable FirebaseFirestoreException e) {
                                        if (snap.exists()) {
//                                        Toast.makeText(MainActivity.this,"Bet Already SET.",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
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
                                                        startActivity(getIntent());
                                                    }
                                                    else {
                                                        Toast.makeText(MainActivity.this,"Request Not Sent",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }

                                    }
                                });


                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"You Don't Have Enough Balance To SET Challenge For "+price,Toast.LENGTH_SHORT).show();
                            }

                        }
                        else{
                            Toast.makeText(MainActivity.this,"Challenge Must Be Greater Then 30, And Must Be In Multiple Of 5, Don't Start With 0",Toast.LENGTH_SHORT).show();
                        }
                    }
                    amount.getText().clear();
                }

            });

            Toolbar toolbar=(Toolbar) findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);
            //phone=findViewById(R.id.profilephone);
            drawerLayout=findViewById(R.id.drawer_layout);
            NavigationView navigationView=(NavigationView) findViewById(R.id.drawer);
            navigationView.setNavigationItemSelectedListener(this);
            ActionBarDrawerToggle drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,
                    toolbar,R.string.drawer_open,R.string.drawe_close);
            drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.primary_dark));
            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();

        }


//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);

    }

    private void payUsingUpi(String amount, String name, String upiId) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(MainActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(MainActivity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(MainActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                current_balance.setText("₹"+amount);
                CollectionReference requestRef = fStore.collection("users");
                requestRef.whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                checkbalance=(String)document.getString("wallet");
                                if(checkbalance!=null)
                                {
                                    int current_balance=Integer. parseInt(checkbalance) + Integer. parseInt(wallet_amount.getText().toString());
                                    Map<Object,String> map = new HashMap<>();
                                    map.put("wallet",Integer.toString(current_balance));
                                    map.put("new_amount",wallet_amount.getText().toString());
                                    map.put("Time",new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                                    requestRef.document(document.getId()).set(map, SetOptions.merge());
                                }
                                else
                                {
                                    int current_balance=Integer. parseInt(wallet_amount.getText().toString());
                                    Map<Object,String> map = new HashMap<>();
                                    map.put("wallet",Integer.toString(current_balance));
                                    map.put("new_amount",wallet_amount.getText().toString());
                                    map.put("Time",new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                                    requestRef.document(document.getId()).set(map, SetOptions.merge());
                                }
                            }

                        }
                    }
                });
                Log.d("UPI", "responseStr: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(MainActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    public void onRestart(){
        super.onRestart();
        DocumentReference documentReference=fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                checkbalance=(String)documentSnapshot.getString("wallet");
                text_name = (documentSnapshot.getString("Name")).toString();
                if(checkbalance!=null)
                {
                    balance.setText("₹"+checkbalance+".00");
                }
                else
                {
                    balance.setText("₹0.00");
                }
                DBdata(text_name);
            }
        });
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
                        namesList.clear();playerStatus.clear();player_name.clear();challenger_name.clear();userStatus.clear();getAmount.clear();challengerStatus.clear();
                        challenger_player.clear();
                        for(DocumentSnapshot snapshot:snapshotList){
                            namesList.add(snapshot.getString("Request message"));
                            challenger_name.add(snapshot.getString("Challenger_Name"));
                            userStatus.add(snapshot.getString("status"));
                            getAmount.add(snapshot.getString("amount"));
                            challengerStatus.add(snapshot.getString("Challenger_status"));
                            challenger_player.add(snapshot.getString("player_Name"));
                            userId1=snapshot.getString("userID");
                            fStore.collection("BetRequest").document(userId1).collection("BetResponse").get().
                                    addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document1 : task.getResult()) {
                                                    player_name.add(document1.getString("player_Name"));
                                                    playerStatus.add(document1.getString("status"));
                                                    if (playerStatus.contains("DICLINE")) {
                                                        msg.add(document1.getString("msg"));
                                                    }
                                                }
                                                if (userStatus.contains("ACCEPTED")) {
                                                    fStore.collection("BetRequest").whereEqualTo("player_Name", text_name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot info : task.getResult()) {
                                                                    current_challenger = info.getString("Challenger_Name").toString();
                                                                    current_player = info.getString("player_Name").toString();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                                if (namesList.size() <= 0) {
                                                    betnofound.setVisibility(View.VISIBLE);
                                                    bet_list.setVisibility(View.GONE);
                                                } else {
                                                    Log.d("playerName", String.valueOf(player_name));
                                                    MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(MainActivity.this, namesList, challenger_name, userStatus, text_name, getAmount, userID, player_name, playerStatus, current_challenger, current_player, challengerStatus, challenger_player, msg);
                                                    adapter.notifyDataSetChanged();
                                                    bet_list.setAdapter(adapter);
                                                    refresh(1000,namesList.size());
                                                }
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

    public void refresh(int milliseconds, int size){
        final Handler handler = new Handler();
        if(size<=0)
        {
            bet_list.setVisibility(View.GONE);
            betnofound.setVisibility(View.VISIBLE);
        }
        final Runnable runnable=new Runnable() {
            @Override
            public void run() {
                DBdata(text_name);
                Log.d("Referesh",text_name);
//                if(challenger_name.contains(text_name) && userStatus.contains("REQUESTED") && )
            }
        };
        handler.postDelayed(runnable,milliseconds);
    }

    public void automatic_popup(){
        Log.d("currentUser",text_name);
        fStore.collection("BetRequest").whereEqualTo("Challenger_Name",text_name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String checkChallenger = document.getString("Challenger_Name");
                        if (checkChallenger.equals(text_name)) {
                            bet_response.setContentView(R.layout.betpopup);
                            betresponse_list=(ListView)bet_response.findViewById(R.id.betresponse_list);
                            TextView set_price=(TextView)bet_response.findViewById(R.id.set_price);
                            DocumentReference documentReference=fStore.collection("BetRequest").document(userID);
                            documentReference.addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    {
                                        set_price.setText("Match Amount: "+documentSnapshot.getString("amount").toString());
                                    }
                                }
                            });
                            fStore.collection("BetRequest").document(userID).collection("BetResponse").whereEqualTo("status","REQUESTED").get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                                            responseList.clear();
                                            for(DocumentSnapshot snapshot:snapshotList){
                                                responseList.add(snapshot.getString("player_Name"));
                                            }
                                            if(responseList.size()>0)
                                            {
                                                final MediaPlayer mp=MediaPlayer.create(MainActivity.this,R.raw.btnsound);
                                                mp.start();
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
                    }
                }
            }
        });
    }
    public void Show_popup(View v)
    {

        bet_response.setContentView(R.layout.betpopup);
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
                            final MediaPlayer mp=MediaPlayer.create(MainActivity.this,R.raw.btnsound);
                            mp.start();
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
                    {   //delete data from DB
                        if(challenger_name.contains(text_name))
                        {
                            fStore.collection("BetRequest").document(userID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DBdata(text_name);
                                    Toast.makeText(MainActivity.this,"Challege Deleted Successfull!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            fStore.collection("BetRequest").document(userId1).collection("BetResponse").document(userID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DBdata(text_name);
                                    Toast.makeText(MainActivity.this,"Request Deleted Successfull!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
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
        if(id==R.id.action_play){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=VExoJ0NkR1w&feature=youtu.be")));
            Log.i("Video", "Video Playing...."); }
//        else if(id==R.id.action_name){
//            item.setTitle(text_name); }
//        else if(id==R.id.action_pay){ return true;
//        }
        return super.onOptionsItemSelected(item);
    }


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
            case R.id.item_b:{
                Intent intent = new Intent(MainActivity.this, ChangepasswordActivity.class);
                startActivity(intent);
            }break;
            case R.id.item_c:{
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }break;
            case R.id.item_d:{
                AlertDialog.Builder acceptDialog=new AlertDialog.Builder(MainActivity.this);
                acceptDialog.setTitle("Invite a Friend");
                acceptDialog.setMessage("Copy the link below and send it to the Friend you want to Invite on WhatsApp.\n\n" +
                        "https://drive.google.com/folderview?id=1G0XifsYzBhjKB0K1a2bAMot_mD8DZPvC");
                acceptDialog.setCancelable(true);
                acceptDialog.setPositiveButton("COPY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String link="https://drive.google.com/folderview?id=1G0XifsYzBhjKB0K1a2bAMot_mD8DZPvC";
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
            case R.id.item_g: {Intent intent = new Intent(MainActivity.this, HowtouseActivity.class);
                startActivity(intent);
            }break;
            case R.id.item_h:{
                FirebaseAuth.getInstance().signOut(); //logout to the user
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }break;
            case R.id.item_i:{
                Intent intent = new Intent(MainActivity.this, PandingmatchesActivity.class);
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
    public void checkConnection(){
        ConnectivityManager manager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=manager.getActiveNetworkInfo();
        if(null!=activeNetwork){
            net=true;
        }
        else
        {
            AlertDialog.Builder acceptDialog=new AlertDialog.Builder(MainActivity.this);
            acceptDialog.setTitle("No Internet");
            acceptDialog.setMessage("Unable to connect to the Internet.Please check your Connection.");
            acceptDialog.setCancelable(true);
            acceptDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(getIntent());
                }
            });
            AlertDialog alertDialog=acceptDialog.create();
            alertDialog.show();
        }
    }
}
