package com.game.ludobets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class termsandconditionActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView description,creator,desca,descb,descc,descd,desce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsandcondition);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("Ludo Bets");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        description=findViewById(R.id.description);
        creator=findViewById(R.id.creator);
        desca=findViewById(R.id.desca);
        descb=findViewById(R.id.descb);
        descc=findViewById(R.id.descc);
        descd=findViewById(R.id.descd);
        desce=findViewById(R.id.desce);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        description.setText("These terms and conditions of use(\"Terms\") along with privacy policy(\"Privacy Policy\") forms a legally binding agreement(\"Agreement\") between You and Us.");
        creator.setText("- Creator: Suraj Verma.\n\n" +
                "Hence,We insist that You read these Terms and Privacy Policy and let Us kow if you have any questions regarding the same.We will try Our best to answer Your queries.");
        desca.setText("1. Users approve of and accept over Agreement by:\n" +
                "\n" +
                "a.Reading all terms and condition\n" +
                "b Reading all rules of this app\n" +
                "\n" +
                "2.Users may accept this Agreement only if:\n" +
                "\n" +
                "a.If such User is a natural Person,is of the legal age,eligibility and mental capacity to form a binding contract with us.\n" +
                "b.If such User is a not a resident od Telangana,Assam,Orissa,Kerala,Sikkim,Nagaland or Gujarat.\n" +
                "c.If such User is a juristic Person, is lawfully existing and has all the authorizations, permits and allowances to enter into this Agreement and form a binding contract.\n" +
                "d.Such User is not legally barred or restricted from using the App.\n" +
                "\n" +
                "3.You Understand that we want You to not use the App if You do not understand, approve of or accept all  the terms specified in this Agreement. Hence,You are requested to read these Terms and Privacy Policy carefully and understand the Agreement before You accept it and agree to be bound by it.\n" +
                "\n" +
                "4.The Agreement shall govern the relationship of each User with Us. However, we may also execute separate written agreements with its Users. In case of conflict between terms of such separate written agreement and this Agreement,the terms of the separate written agreement shall prevail.\n");

        descb.setText("1.Section 12 of the Public Gambling Act,1867 provides that games of mere skill are exempt from the application of the Act.The Supreme Court of India and various High Courts in india have opened that a game of mere skill is a game “in which, although the element of chance necessarily cannot be entirely eliminated,success depends principally upon the superior knowledge,training,attention,experience and adroitness of the player.A game of skill is one in which the element of skill predominates over the element of chance.” No penalty can be imposed upon a person for playing such games of skill.\n" +
                "2.Users must note that Ludo' game available for challenge in our platform is Games of Skill', under Indian law,and that we does not support.endorse or offer to Users games of chance'for money.While Games of Skill' Do not have a comprehensive definition. they are those games where the impact of a player's effort and skill on the outcome of a game is higher than the impact of luck and chance.\n" +
                "3.It may be noted that States are permitted,by the Indian Constitution,to enact laws regulating betting and gambling in their respective jurisdictions.In furtherance of these powers,various States have enacted anti-gambling legislations. Such legislations are largely in concert with the Public Gambling Act of 1867 (and include the exception of \"games of skill\" ). Where a State legislation\n" +
                "on gambling exists, it prevails over the Public Gambling Act of 1867. In this regard, the Assam Game and Betting Act, 1970 and Orissa (Prevention of) Gambling\n" +
                "Act, 1955 and Telangana State Gaming (Amendment) Ordinance and High Court Judgment in Gujarat,2017 prohibits games with money stakes and also does not create an exception for games of skill. Therefore, currently, residents of Assam, Odisha, Telangana and Gujarat are not permitted to play on our App/site.\n" +
                "4. The games rules are clearly defined on this platform and Users are encouraged to read, understand and follow these rules to be successful in these games.\n" +
                "5. The games on our platform are 'Games of Skills' such that the outcome / success in the games is directly dependent on the User' s effort, performance and skill. By choosing how to play, the actions of Users shall have direct impact on the game.\n" +
                "6. Every game will have some elements of chance, but in the form of challenges / obstacles that a User would be able to overcome using his/her skills and knowledge of the game. Elements of luck are present in every game to add and excitement, but no two attempts at a game are identical so Users must use their skills in order to be successful\n" +
                "7. Since the games available on our platform can be won through Users ' skills and such skills may be enhanced with practice and experience, the performance of a User may improve with time and practice.\n" +
                "8. Certain games may have pre-determined outcomes (Ludo), and these outcomes are achievable by Users using their skills.\n");

        descc.setText("1. Player who sets a challenge will share a room idlroom code with his/her opponent.\n" +
                "2. On winning both players have to update there results in following manner:\n" +
                "(a) If you have won, select Won' option and upload winning screenshot of the game.\n" +
                "(b) If you have lost, select Lost' option.\n" +
                "(c) If match is not started and both parties doesn't want to play, select'Cancel' option.\n" +
                "3. Posting a wrong result after game, will charge you a penality  of Rs 50.\n" +
                "4. User must have to record every game, and if any player is hacking or cheating a game, contact support.\n" +
                "5. If game is not started, if you haven't played a single move yourself, please show the recording\n" +
                "of game to support. Game will be cancelled only if you have recording.\n" +
                "6. If you don't have any proof against player cheating and error in game, you will be considered as lost.\n");

        descd.setText("1. Player can deposit there balance in Add Balance section.\n" +
                "2. Player can take withdrawal by setting a withdrawal request in Withdraw Balance section.\n" +
                "3. Deposit and withdrawal request completed by support at any time.\n" +
                "4. Any wrong payment detail given by you, will not be considered in refund.\n" +
                "5. Once withdrawal is done, you don't have any authority to raise any query.\n" +
                "6. If withdrawal request go on pending, user must have to wait for 2days.\n");

        desce.setText("Flat 5% commission will be charged on Total Winning Amount.");

    }
}
