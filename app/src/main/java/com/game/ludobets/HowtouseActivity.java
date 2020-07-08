package com.game.ludobets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HowtouseActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button watchVideo;
    TextView helpDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howtouse);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        helpDesc=findViewById(R.id.helpdesc);
        watchVideo=findViewById(R.id.watch);
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

        helpDesc.setText(" Download and Install LudoKing Game App from Play Store if you haven't already.\n" +
                "        you can't use this App without LudoKing Game.\n\n" +
                "1.If your phone's diamensions sometimes don't fit and you can't see person's Challenge clearly.Rotate your Mobile.\n" +
                " 2.Add Balance if your Balance is 0 by going to balance section in Menu.\n" +
                " 3. To Set the Challenge,Add the Ammont and Click on SET.you can only set for amount in multiples of 5, and less then 20,000 Rs.\n" +
                "4.If someone Accept your challenge,you'll get a Notification,Accept it and START Playing,and decline It if you don't want to play with  that person.\n" +
                "5.If the Challenge was yours,Add The ROOM CODE,If not then WAIT for the Opponent to SET the ROOM CODE.\n" +
                "6.If you WON the game.Click on I WON and UPLOAD the winning screenshot.If you LOST click on I LOST and go submit result(please give the correct result otherwise 50 Rs will be deducted from your balance).\n" +
                "7. Withdraw or Add Balance.whenever you want by going to Balance Section.\n" +
                " 8.For E-Commision Charger,go to Terms and Condition section.\n\n");

        watchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=VExoJ0NkR1w&feature=youtu.be")));
                Log.i("Video", "Video Playing....");

            }
        });
    }
}
