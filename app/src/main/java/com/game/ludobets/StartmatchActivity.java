package com.game.ludobets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.internal.Constants;

public class StartmatchActivity extends AppCompatActivity {
    TextView notice_text, title_text, msg_text, rule_text,img_notice,calcelview;
    Button msgbtn, callbtn, setbtn,uploadimgbtn,sendresbtn,code_copy,yesbtn,nobtn,Cyesbtn,Cnobtn;
    EditText code;
    String price,current_user;
    List<String> challenger_name=new ArrayList<String>();
    List<String> player_name=new ArrayList<String>();
    RadioGroup radioGroup;
    RadioButton radioButton,cancelbtn;
    ImageView res_img;
    LinearLayout entercode;
    Uri filePath;
    final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;
    String checkroom=null,RCstatus=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startmatch);
        calcelview=findViewById(R.id.cancelview);
        cancelbtn=findViewById(R.id.cancelbtn);
        yesbtn=findViewById(R.id.yesbtn);
        nobtn=findViewById(R.id.nobtn);
        Cyesbtn=findViewById(R.id.Cyesbtn);
        Cnobtn=findViewById(R.id.Cnobtn);
        title_text = findViewById(R.id.match_title);
        notice_text = findViewById(R.id.notice);
        msg_text = findViewById(R.id.match_msg);
        img_notice=findViewById(R.id.imgnotice);
        rule_text = findViewById(R.id.rule);
        code = findViewById(R.id.code);
        msgbtn = findViewById(R.id.msg);
        callbtn = findViewById(R.id.call);
        setbtn = findViewById(R.id.set_code);
        radioGroup = findViewById(R.id.bet_result);
        entercode=findViewById(R.id.entercode);
        code_copy=findViewById(R.id.copycode);
        uploadimgbtn=findViewById(R.id.uploadimg);
        res_img=findViewById(R.id.SS_result);
        sendresbtn=findViewById(R.id.final_res);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Intent intent = getIntent();
        current_user = intent.getStringExtra("current_user");
        String current_user_id = intent.getStringExtra("current_user_id");
//        System.out.println(current_user_id);
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        AlertDialog.Builder acceptDialog=new AlertDialog.Builder(StartmatchActivity.this);
        acceptDialog.setTitle("Alert !");
        acceptDialog.setMessage("You will lose your Money if:\n 1.You posted Wrong Result.\n 2.You Don't Post Result within 5 min,after the match.\n\n Your Account will also be Blocked.");
        acceptDialog.setCancelable(true);
        acceptDialog.setPositiveButton("I UNDERSTAND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(StartmatchActivity.this,"You read",Toast.LENGTH_SHORT).show();

            }
        });
        AlertDialog alertDialog=acceptDialog.create();
        alertDialog.show();
        CollectionReference responseRef = fStore.collection("BetRequest");
        responseRef.whereEqualTo("status", "ACCEPTED").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        challenger_name.add((String)document.getString("Challenger_Name").toString());
                        player_name.add((String) document.getString("player_Name").toString());
                        price = (String) document.getString("amount").toString();
                        if(challenger_name.contains(current_user))
                        {
                            CollectionReference userlabel=fStore.collection("BetRequest");
                            userlabel.whereEqualTo("Challenger_Name",current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(QueryDocumentSnapshot curdoucument:task.getResult()){
                                            String chalenger=(String)curdoucument.getString("Challenger_Name").toString();
                                            String player=(String)curdoucument.getString("player_Name").toString();
                                            String amount=(String)curdoucument.getString("amount").toString();
                                            checkroom= (String)curdoucument.getString("room_Code");
                                            RCstatus=(String)curdoucument.getString("RCstatus");
                                            roomCode(checkroom,RCstatus,current_user,challenger_name,player_name);
                                            title_text.setText(chalenger + " VS " + player  + " for " + amount );
                                        }
                                    }
                                }
                            });

                        }
                        if(player_name.contains(current_user))
                        {
                            CollectionReference userlabel=fStore.collection("BetRequest");
                            userlabel.whereEqualTo("player_Name",current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(QueryDocumentSnapshot resdoucument:task.getResult()){
                                            String chalenger=(String)resdoucument.getString("Challenger_Name").toString();
                                            String player=(String)resdoucument.getString("player_Name").toString();
                                            String amount=(String)resdoucument.getString("amount").toString();
                                            title_text.setText(chalenger + " VS " + player  + " for " + amount );
                                            checkroom= (String)resdoucument.getString("room_Code");
                                            RCstatus=(String)resdoucument.getString("RCstatus");
                                            roomCode(checkroom,RCstatus,current_user,challenger_name,player_name);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

//        Toast.makeText(v.getContext(),"Accepted. You can Start the Match Now",Toast.LENGTH_SHORT).show();
        notice_text.setText("NOTE!!\n New Rule:If Dices of Both players are Open OR\n If 2 Dices of single Player is Open.Match will not be Cancelled.\n (Video Recording of Match is Recommened)\n\n" +
                "यदि दोनों खिलाड़ियों के पास खुला पासा है या यदि किसी एकल खिलाड़ी के पास 2 अंतर खुले हैं। मैच रद्द नहीं किया जायेगा");
        rule_text.setText("Rules:\nWait for Room code for at least 5 min.Room Code के लिए 5 मिनट तक प्रतीक्षा करें ।\n\n" +
                "1.If you WON the Match Click on 'I WON' and send Result.\n 2.If you LOST the Match Click on 'I LOST' and send Result.\n\n" +
                "Internet, Phone hang, or any personal issues will not be Entertained.\nIf you don't SEND Result for Send WRONG RESULT.You'll lose your Money ");
        img_notice.setText("Uploading Screenshot may take some time, so please Be Patient.");
        calcelview.setText("(It may take 15 minutes)\n (इसमें 15 मिनट का समय लग सकता है)");

        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Room_code=code.getText().toString();
                if(Room_code.isEmpty())
                {
                    Toast.makeText(StartmatchActivity.this,"Please Enter Room Code!",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(Room_code.length()<8)
                    {
                        Toast.makeText(StartmatchActivity.this,"Invalid Room Code!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        CollectionReference requestRef = fStore.collection("BetRequest");
                        requestRef.whereEqualTo("Challenger_Name", current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot DBdocument : task.getResult()) {
                                        Map<Object, String> map = new HashMap<>();
                                        map.put("room_Code", Room_code);
                                        requestRef.document(DBdocument.getId()).set(map, SetOptions.merge());
                                    }
                                    Toast.makeText(StartmatchActivity.this,"Room Code Sent",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
//                        entercode.setVisibility(View.GONE);
//                        msg_text.setText(Room_code);
//                        code_copy.setVisibility(View.VISIBLE);
                        CollectionReference responseRef = fStore.collection("BetRequest");
                        responseRef.whereEqualTo("room_Code",Room_code).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        player_name.add((String) document.getString("player_Name").toString());
                                        checkroom= (String) document.getString("room_Code");
                                        RCstatus=(String)document.getString("RCstatus");
                                        roomCode(checkroom,RCstatus,current_user,challenger_name,player_name);
                                    }
                                } else {
                                    System.out.println("NP");
                                }
                            }

                        });
                    }
                }
            }
        });

        code_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference requestRef = fStore.collection("BetRequest");
                requestRef.whereEqualTo("status", "ACCEPTED").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String room_code=(String)document.getString("room_Code").toString();
                                ClipboardManager clipboard=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip=ClipData.newPlainText("Code",room_code);
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(StartmatchActivity.this, "Room Code Copied!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(StartmatchActivity.this, "Not Copied", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        uploadimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioGroup.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(StartmatchActivity.this, "Please Select Result First", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    chooseImage();
                    uploadImage(current_user_id);
                }
            }
        });

        sendresbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancelbtn.isChecked()){
                    String complete=cancelbtn.getText().toString();
                    CollectionReference responseRef = fStore.collection("BetRequest");
                    responseRef.whereEqualTo("status", "ACCEPTED").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    challenger_name.add((String)document.getString("Challenger_Name").toString());
                                    player_name.add((String) document.getString("player_Name").toString());
                                    final ProgressDialog progressDialog = new ProgressDialog(StartmatchActivity.this);
                                    progressDialog.setTitle("Uploading Result,wait a few movement.Do Not press Back...");
                                    progressDialog.show();
                                    if(challenger_name.contains(current_user))
                                    {
                                        Map<Object, String> map = new HashMap<>();
                                        map.put("status", complete);
                                        responseRef.document(current_user_id).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(StartmatchActivity.this, "Your Balance will be Updated, Ater Opponent Result", Toast.LENGTH_SHORT).show();
                                                    StartmatchActivity.this.finish();
                                                }
                                            }
                                        });
                                    }
                                    if(player_name.contains(current_user));
                                    {
                                        Map<Object, String> res_map = new HashMap<>();
                                        res_map.put("status", complete);
                                        fStore.collection("BetRequest").document(document.getString("userID")).collection("BetResponse").document(current_user_id).set(res_map,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {    progressDialog.dismiss();
                                                    Toast.makeText(StartmatchActivity.this, "Balance Updated", Toast.LENGTH_SHORT).show();
                                                    StartmatchActivity.this.finish();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
                }
                else{
                    if(radioGroup.getCheckedRadioButtonId() == -1 && res_img.getDrawable() == null)
                    {
                        Toast.makeText(StartmatchActivity.this, "Please Select Result and Upload Screen Short", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        int radioId = radioGroup.getCheckedRadioButtonId();
                        radioButton = findViewById(radioId);
                        String complete=radioButton.getText().toString();
                        CollectionReference responseRef = fStore.collection("BetRequest");
                        responseRef.whereEqualTo("status", "ACCEPTED").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        challenger_name.add((String)document.getString("Challenger_Name").toString());
                                        player_name.add((String) document.getString("player_Name").toString());
                                        final ProgressDialog progressDialog = new ProgressDialog(StartmatchActivity.this);
                                        progressDialog.setTitle("Uploading Result,wait a few movement.Do Not press Back...");
                                        progressDialog.show();
                                        if(challenger_name.contains(current_user))
                                        {
                                            Map<Object, String> map = new HashMap<>();
                                            map.put("status", complete);
                                            responseRef.document(document.getId()).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(StartmatchActivity.this, "Your Balance will be Updated, Ater Opponent Result", Toast.LENGTH_SHORT).show();
                                                        StartmatchActivity.this.finish();
                                                    }
                                                }
                                            });
                                        }
                                        if(player_name.contains(current_user));
                                        {
                                            Map<Object, String> res_map = new HashMap<>();
                                            res_map.put("status", complete);
                                            fStore.collection("BetRequest").document(document.getString("userID")).collection("BetResponse").document(current_user_id).set(res_map,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {    progressDialog.dismiss();
                                                        Toast.makeText(StartmatchActivity.this, "Balance Updated", Toast.LENGTH_SHORT).show();
                                                        StartmatchActivity.this.finish();
                                                    }
                                                    else {
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollectionReference responseRC = fStore.collection("BetRequest");
                responseRC.whereEqualTo("player_Name", current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot insdocument : task.getResult()) {
                                String challengeid=insdocument.getString("userID");
                                Map<Object, String> map = new HashMap<>();
                                map.put("RCstatus", "ACCEPTED");
                                responseRC.document(challengeid).set(map, SetOptions.merge());
                            }
                        }
                    }

                });

                CollectionReference responseRef = fStore.collection("BetRequest");
                responseRef.whereEqualTo("RCstatus", "ACCEPTED").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot RCdocument : task.getResult()) {
                                checkroom=(String)RCdocument.getString("room_Code").toString();
                                challenger_name.add((String)RCdocument.getString("Challenger_Name").toString());
                                player_name.add((String) RCdocument.getString("player_Name").toString());
                                if(challenger_name.contains(current_user))
                                {
                                    code.setVisibility(View.GONE);
                                    setbtn.setVisibility(View.GONE);
                                    msg_text.setText(checkroom+"\n\nStart the Match, RoomCode has been Accepted.\n\n"+" मैच शरू करे\n");
                                    msg_text.setBackgroundResource(R.color.blue);
                                }
                                else if(player_name.contains(current_user))
                                {
                                    code.setVisibility(View.GONE);
                                    setbtn.setVisibility(View.GONE);
                                    yesbtn.setVisibility(View.GONE);
                                    nobtn.setVisibility(View.GONE);
                                    msg_text.setText(checkroom+"\n\nStart the Match, RoomCode has been Accepted.\n\n"+" मैच शरू करे\n");
                                    msg_text.setBackgroundResource(R.color.blue);
                                }
                            }
                        }
                    }
                });

            }
        });
        Cyesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg_text.setText("ENTER LUDOKING ROOMCODE");
                code.setVisibility(View.VISIBLE);
                setbtn.setVisibility(View.VISIBLE);
                Cyesbtn.setVisibility(View.GONE);
                Cnobtn.setVisibility(View.GONE);
            }
        });
        Cnobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CollectionReference responseRC = fStore.collection("BetRequest");
                responseRC.whereEqualTo("Challenger_Name", current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot insdocument : task.getResult()) {
                                String challengeid=insdocument.getString("userID");
                                Map<Object, String> map = new HashMap<>();
                                map.put("RCstatus", "REJECTED");
                                responseRC.document(challengeid).set(map, SetOptions.merge());
                            }
                        }
                    }

                });

                CollectionReference responseRef = fStore.collection("BetRequest");
                responseRef.whereEqualTo("status", "ACCEPTED").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                challenger_name.add((String)document.getString("Challenger_Name").toString());
                                player_name.add((String) document.getString("player_Name").toString());
                                price = (String) document.getString("amount").toString();
                                if(challenger_name.contains(current_user))
                                {
                                    CollectionReference userlabel=fStore.collection("BetRequest");
                                    userlabel.whereEqualTo("Challenger_Name",current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot curdoucument:task.getResult()){
                                                    checkroom= (String)curdoucument.getString("room_Code");
                                                    RCstatus=(String)curdoucument.getString("RCstatus").toString();
                                                    roomCode(checkroom,RCstatus,current_user,challenger_name,player_name);
                                                }
                                            }
                                        }
                                    });

                                }
                                if(player_name.contains(current_user))
                                {
                                    CollectionReference userlabel=fStore.collection("BetRequest");
                                    userlabel.whereEqualTo("player_Name",current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot resdoucument:task.getResult()){
                                                    checkroom= (String)resdoucument.getString("room_Code");
                                                    RCstatus=(String)resdoucument.getString("RCstatus").toString();
                                                    roomCode(checkroom,RCstatus,current_user,challenger_name,player_name);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });

            }
        });
        nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartmatchActivity.this);
                builder.setTitle("Change Room Code?");
                builder.setMessage("Are you Sure, You want to Change the Room Code?")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                CollectionReference responseRC = fStore.collection("BetRequest");
                                responseRC.whereEqualTo("player_Name", current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot insdocument : task.getResult()) {
                                                String challengeid=insdocument.getString("userID");
                                                Map<Object, String> map = new HashMap<>();
                                                map.put("RCstatus", "REJECTED");
                                                responseRC.document(challengeid).set(map, SetOptions.merge());
                                            }
                                        }
                                    }

                                });

                                CollectionReference responseRef = fStore.collection("BetRequest");
                                responseRef.whereEqualTo("RCstatus", "REJECTED").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot RCdocument : task.getResult()) {
                                                checkroom=(String)RCdocument.getString("room_Code").toString();
                                                challenger_name.add((String)RCdocument.getString("Challenger_Name").toString());
                                                player_name.add((String) RCdocument.getString("player_Name").toString());
                                                if(challenger_name.contains(current_user))
                                                {
                                                    code.setVisibility(View.GONE);
                                                    setbtn.setVisibility(View.GONE);
                                                    msg_text.setText("Old Room Code: "+checkroom+"\nNew Room Code Requested by"+player_name+"\n Can you change the Room Code?\n");
                                                    msg_text.setBackgroundResource(R.color.maroon);
                                                    Cyesbtn.setVisibility(View.VISIBLE);
                                                    Cnobtn.setVisibility(View.VISIBLE);
                                                }
                                                else if(player_name.contains(current_user))
                                                {
                                                    code.setVisibility(View.GONE);
                                                    setbtn.setVisibility(View.GONE);
                                                    yesbtn.setVisibility(View.GONE);
                                                    nobtn.setVisibility(View.GONE);
                                                    msg_text.setText(" Waiting for Room Code..\nOld Room Code: "+checkroom+"\n");
                                                    msg_text.setBackgroundResource(R.color.red);
                                                }
                                            }
                                        }
                                    }
                                });

                            }
                        })
                        .setNegativeButton("CANCEL",new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                dialog.cancel();
                            }
                        });
                AlertDialog  alertDialog = builder.create();
                alertDialog.show();
            }
        });
        msgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                                if(challenger_name.contains(current_user))
                                {
                                    CollectionReference userlabel=fStore.collection("BetRequest");
                                    userlabel.whereEqualTo("Challenger_Name",current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot phdoucument:task.getResult()){
                                                    String player=(String)phdoucument.getString("player_Name").toString();
                                                    CollectionReference msgref=fStore.collection("users");
                                                    msgref.whereEqualTo("Name",player).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                for(QueryDocumentSnapshot phonedocument:task.getResult()){
                                                                    String phoneNo="91"+phonedocument.getString("phone");
//                                                                    System.out.println("Phone:"+phoneNo);
                                                                    openWhatsApp(phoneNo);
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });

                                }
                                else if(player_name.contains(current_user))
                                {
                                    CollectionReference userlabel=fStore.collection("BetRequest");
                                    userlabel.whereEqualTo("player_Name",current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot pdoucument:task.getResult()){
                                                    String chalenger=(String)pdoucument.getString("Challenger_Name").toString();
                                                    CollectionReference msgref=fStore.collection("users");
                                                    msgref.whereEqualTo("Name",chalenger).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                for(QueryDocumentSnapshot phondocument:task.getResult()){
                                                                    String phoneNo="91"+phondocument.getString("phone");
//                                                                    System.out.println("Phone:"+phoneNo);
                                                                    openWhatsApp(phoneNo);
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }
            }
        });

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference responseRef = fStore.collection("BetRequest");
                responseRef.whereEqualTo("status", "ACCEPTED").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot pndocument : task.getResult()) {
                                challenger_name.add((String)pndocument.getString("Challenger_Name").toString());
                                player_name.add((String) pndocument.getString("player_Name").toString());
                                if(challenger_name.contains(current_user))
                                {
                                    CollectionReference userlabel=fStore.collection("BetRequest");
                                    userlabel.whereEqualTo("Challenger_Name",current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot lidoucument:task.getResult()){
                                                    String player=(String)lidoucument.getString("player_Name").toString();
                                                    CollectionReference msgref=fStore.collection("users");
                                                    msgref.whereEqualTo("Name",player).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                for(QueryDocumentSnapshot phonedocument:task.getResult()){
                                                                    String phoneNo="91"+phonedocument.getString("phone");
                                                                    openCall(phoneNo);
                                                                }
                                                            }
                                                        }
                                                    });

                                                }
                                            }
                                        }
                                    });
                                }
                                else if(player_name.contains(current_user))
                                {
                                    CollectionReference userlabel=fStore.collection("BetRequest");
                                    userlabel.whereEqualTo("player_Name",current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot lidoucument:task.getResult()){
                                                    String chalenger=(String)lidoucument.getString("Challenger_Name").toString();
                                                    CollectionReference msgref=fStore.collection("users");
                                                    msgref.whereEqualTo("Name",chalenger).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                for(QueryDocumentSnapshot phonedocument:task.getResult()){
                                                                    String phoneNo="91"+phonedocument.getString("phone");
                                                                    openCall(phoneNo);
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    private void roomCode(String checkroom,String RCstatus, String current_user, List<String> challenger_name, List<String>  player_name) {
        if(checkroom!= null)
        {
            if(player_name.contains(current_user))
            {
                if(RCstatus=="ACCEPTED")
                {
                    code.setVisibility(View.GONE);
                    setbtn.setVisibility(View.GONE);
                    yesbtn.setVisibility(View.GONE);
                    nobtn.setVisibility(View.GONE);
                    msg_text.setText(checkroom+"\n\nStart the Match, RoomCode has been Accepted.\n\n"+" मैच शरू करे\n");
                    msg_text.setBackgroundResource(R.color.blue);
                }
                else if(RCstatus=="REJECTED"){
                    code.setVisibility(View.GONE);
                    setbtn.setVisibility(View.GONE);
                    yesbtn.setVisibility(View.GONE);
                    nobtn.setVisibility(View.GONE);
                    msg_text.setText(" Waiting for Room Code..\nOld Room Code: "+checkroom+"\n");
                    msg_text.setBackgroundResource(R.color.red);
                }
                else {
                    msg_text.setText(checkroom + "\nStep 1.Join the Room Code in Ludoking Game.\nStep 2.If Room Code is correct,press Yes.If Room Code is invalid,press No.\n\n" +
                            "## WITHOUT THIS,MATCH WILL BE CONSIDERED FRAUD.\n\nStep 1.Ludoking game में Room Code join हो.\nStep 2.यदि Room Code सही हे,तो Yes पर दबाये" +
                            "\n\n## इसके बिना, मैच स्वीकार नहीं किया जाएगा\n");
                    msg_text.setBackgroundResource(R.color.maroon);
                    msg_text.setTextColor(Color.WHITE);
                    code_copy.setVisibility(View.VISIBLE);
                    yesbtn.setVisibility(View.VISIBLE);
                    nobtn.setVisibility(View.VISIBLE);
                }
            }
            else if(challenger_name.contains(current_user))
            {
                if(RCstatus=="ACCEPTED"){
                    code.setVisibility(View.GONE);
                    setbtn.setVisibility(View.GONE);
                    msg_text.setText(checkroom+"\n\nStart the Match, RoomCode has been Accepted.\n\n"+" मैच शरू करे\n");
                    msg_text.setBackgroundResource(R.color.blue);
                }
                else if(RCstatus=="REJECTED"){
                    code.setVisibility(View.GONE);
                    setbtn.setVisibility(View.GONE);
                    msg_text.setText("Old Room Code: "+checkroom+"\nNew Room Code Requested by"+player_name+"\n Can you change the Room Code?\n");
                    msg_text.setBackgroundResource(R.color.maroon);
                    Cyesbtn.setVisibility(View.VISIBLE);
                    Cnobtn.setVisibility(View.VISIBLE);
                }
                else{
                    code.setVisibility(View.GONE);
                    setbtn.setVisibility(View.GONE);
                    msg_text.setText(checkroom+"\n\nWaiting for Admin to check this Room Code Do not Start Match in Ludoking Game.\n\n"+" Room Code देखने के लिए Admin की प्रतीक्षा करे LudoKing Game में मैच अभी शुरू न करे\n");
                    msg_text.setBackgroundResource(R.color.maroon);
                }
            }
        }
        else if(checkroom==null && RCstatus==null)
        {
            if(challenger_name.contains(current_user))
            {
                    msg_text.setText("ENTER LUDOKING ROOMCODE");
                    code.setVisibility(View.VISIBLE);
                    setbtn.setVisibility(View.VISIBLE);
            }
            else if(player_name.contains(current_user))
            {
                    msg_text.setText("Waiting for Room Code...");
            }

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Screen Short"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                res_img.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String current_user_id ){

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(StartmatchActivity.this);
            progressDialog.setTitle("Uploading Screenshot Image.Do Not press Back...");
            progressDialog.show();

            StorageReference ref = storageReference.child(current_user_id+"/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(StartmatchActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(StartmatchActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        Toast.makeText(this, "Selected Button" + radioButton.getText(), Toast.LENGTH_SHORT).show();
    }

    public void openWhatsApp(String phoneNo){
        try {
            String text = "This is a test";// Replace with your message.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+phoneNo +"&text="+text));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openCall(String number)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        startActivity(intent);
    }

}
