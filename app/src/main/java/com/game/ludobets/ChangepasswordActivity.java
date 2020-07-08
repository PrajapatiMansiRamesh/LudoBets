package com.game.ludobets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangepasswordActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button change_password;
    EditText current_pass,new_pass,confirm_pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        change_password=findViewById(R.id.btn_change_password);
        current_pass=findViewById(R.id.current_password);
        new_pass=findViewById(R.id.new_password);
        confirm_pass=findViewById(R.id.confirm_password);
        FirebaseAuth fAuth=FirebaseAuth.getInstance();
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
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String cur_pass=current_pass.getText().toString();
               String new_p=new_pass.getText().toString();
               String con_pass=confirm_pass.getText().toString();
               if(cur_pass.isEmpty() && new_p.isEmpty() && con_pass.isEmpty()){
                   Toast.makeText(ChangepasswordActivity.this, "Please Enter All the Fields.", Toast.LENGTH_SHORT).show();
               }
               else{
                   if(new_p.equals(con_pass))
                   {
                       FirebaseUser firebaseUser=fAuth.getCurrentUser();
                       if(firebaseUser !=null && firebaseUser.getEmail()!=null){
                           AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail().toString(), cur_pass);
                           firebaseUser.reauthenticate(credential)
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful()){
                                               Toast.makeText(ChangepasswordActivity.this, "Reauthontication sucess.", Toast.LENGTH_SHORT).show();
                                               firebaseUser.updatePassword(con_pass)
                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task) {
                                                               if (task.isSuccessful()) {
                                                                   Toast.makeText(ChangepasswordActivity.this, "Password Updated Successfully.", Toast.LENGTH_SHORT).show();
                                                                   fAuth.signOut();
                                                                   startActivity(new Intent(ChangepasswordActivity.this, LoginActivity.class));
                                                                   finish();
                                                               }
                                                           }
                                                       });
                                           }
                                           else
                                           {
                                               Toast.makeText(ChangepasswordActivity.this, "Reauthontication faild.", Toast.LENGTH_SHORT).show();
                                           }

                                       }
                                   });
                       }
                       else {
                           startActivity(new Intent(ChangepasswordActivity.this, LoginActivity.class));
                           finish();
                       }

                   }
                   else
                   {
                       Toast.makeText(ChangepasswordActivity.this, "Password Mismatching.", Toast.LENGTH_SHORT).show();
                   }
               }
            }
        });

    }

}
