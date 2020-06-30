package com.game.ludobets;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class MainheaderActivity extends AppCompatActivity {

    TextView username,useremail;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainheader);
        useremail=findViewById(R.id.currentuser);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        useremail.setText(firebaseUser.getEmail());
        Toast.makeText(MainheaderActivity.this,"Your Email."+useremail,Toast.LENGTH_SHORT).show();

//        fStore=FirebaseFirestore.getInstance();
//        userID=fAuth.getCurrentUser().getUid();
//        DocumentReference documentReference=fStore.collection("users").document(userID);
//        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                //phone.setText(documentSnapshot.getString("phone"));
//                name.setText(documentSnapshot.getString("Name"));
//                Toast.makeText(MainheaderActivity.this,"Name:"+name,Toast.LENGTH_SHORT).show();
//                email.setText(documentSnapshot.getString("Email"));
//                Toast.makeText(MainheaderActivity.this,"Email:"+email,Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
