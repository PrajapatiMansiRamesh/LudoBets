package com.game.ludobets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button Adminbtn,creatorbtn;
    TextView Admin,creator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Admin=findViewById(R.id.Admin);
        creator=findViewById(R.id.creator);
        Adminbtn=findViewById(R.id.Adminbtn);
        creatorbtn=findViewById(R.id.creatorbtn);
        TextView helpInfo=findViewById(R.id.helpinfo);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("Ludo Bets");
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        Admin.setText("   Contact Us Via WhatsApp\n\n"+
                "               Admin- 7027624123\n");
        creator.setText("Creator- 9518606273\n");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Adminbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String text = "Hello\n I have Query in Ludo Bets";// Replace with your message.

                    String toNumber = "917027624123"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
                    //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        creatorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String text = "Hello\n I have Query in Ludo Bets";// Replace with your message.

                    String toNumber = "919518606273"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
                    //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        helpInfo.setText("OR YOU CAN GO TO 'HOW TO USE' SECTION IF YOU ARE HAVING ANY ISSUES RELATED TO GAMEPLAY");
    }
}


