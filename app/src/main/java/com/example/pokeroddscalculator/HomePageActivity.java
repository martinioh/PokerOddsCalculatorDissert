package com.example.pokeroddscalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class HomePageActivity extends AppCompatActivity {

    private Button btnCalculate, btnNotes, btnlogSession, btnlogOut, btnhandAnalyser, btnsetLimits;
    private FirebaseUser user;
    private TextView gamblingHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_homepage);
        //gets user logged in and if not logged in then redirect
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        //initialising fields
        btnCalculate = findViewById(R.id.calculator);
        btnNotes = findViewById(R.id.notes);
        btnlogSession = findViewById(R.id.logsession);
        btnlogOut = findViewById(R.id.logout);
        btnhandAnalyser = findViewById(R.id.handanalyser);
        btnsetLimits = findViewById(R.id.setLimits);
        gamblingHelp = findViewById(R.id.gamblingTips);


        String[] gamblingInfo = getResources().getStringArray(R.array.gambling_tips);

        gamblingHelp.setText(gamblingInfo[new Random().nextInt((gamblingInfo.length))]);

        //each button goes to corresponding pages
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, CalculatorActivity.class);
                startActivity(intent);
            }
        });

        btnNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });

        btnlogSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, SessionActivity.class);
                startActivity(intent);
            }
        });

        btnhandAnalyser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, HandCalculatorActivity.class);
                startActivity(intent);
            }
        });

        btnsetLimits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, SetLimitsActivity.class);
                startActivity(intent);
            }
        });



        btnlogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomePageActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed() {
        // empty so nothing happens
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    //code taken from stackexchange to hide nav bar after using keyboard
    public void updateUI() {
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
