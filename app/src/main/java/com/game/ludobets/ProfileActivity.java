package com.game.ludobets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView name,email,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("Ludo Bets");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String current_user_id = intent.getStringExtra("current_user_id");
        ImageView roundedimag = (ImageView) findViewById(R.id.profileimag);
        name=findViewById(R.id.Name);
        email=findViewById(R.id.email);
        phone=findViewById(R.id.phone);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Load an image using Picasso library
        Picasso.with(getApplicationContext())
                .load("https://img.pngio.com/profile-icon-png-image-free-download-searchpngcom-profile-icon-png-673_673.png")
                .into(roundedimag);

        // Load an image using Glide library
        Glide.with(getApplicationContext())
                .load("https://img.pngio.com/profile-icon-png-image-free-download-searchpngcom-profile-icon-png-673_673.png")
                .into(roundedimag);

        DocumentReference documentReference=fStore.collection("users").document(current_user_id);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                name.setText("Name: "+(documentSnapshot.getString("Name")).toString());
                email.setText("Email ID: "+(documentSnapshot.getString("Email")).toString());
                phone.setText("Phone Number: "+(documentSnapshot.getString("phone")).toString());
            }
        });
    }
}
