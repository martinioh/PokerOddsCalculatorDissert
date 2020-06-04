package com.example.pokeroddscalculator;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CalculatedResultActivity extends AppCompatActivity {
    private String bigBlinds, tablePosition, lctablePosition, constopenRaise;
    private TextView introductorymessageField, outputField;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button homeBtn, backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_calculated_result);

        Toolbar toolbar = findViewById(R.id.calculatedresulttoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Calculated Result");
        toolbar.setTitleTextColor(Color.WHITE);

        //initialising fields into variables
        introductorymessageField = findViewById(R.id.introductorymessage);
        outputField = findViewById(R.id.output);
        homeBtn = findViewById(R.id.home);
        backBtn = findViewById(R.id.backtocalculator);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //goes back to homepage
                Intent intent = new Intent(CalculatedResultActivity.this,HomePageActivity.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //goes back to calculator page
                Intent intent = new Intent(CalculatedResultActivity.this,CalculatorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //no of bb and position is sent to this page from calculatoractivity.java through a bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            bigBlinds = bundle.getString("bigBlinds");
            tablePosition = bundle.getString("tablePosition");
            lctablePosition = tablePosition.toLowerCase(); //position is stored as lowercase in database so needs to be made as such here
            lctablePosition = lctablePosition.replaceAll("\\s+",""); //takes whitespace out
                if (Integer.parseInt(bigBlinds) <=15){ //gets hands for shoving
                introductorymessageField.setText("You have " + bigBlinds + "bb in the position '" + tablePosition + "'. This means you should be shoving:");

                    DocumentReference docRef = db.collection("hands").document(lctablePosition); //searches database for hands
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    if (Integer.parseInt(bigBlinds) !=1){
                                        outputField.setText(document.get(bigBlinds).toString() + " of hands."); //outputs these hands
                                    } else {
                                        outputField.setText(document.get(bigBlinds).toString()); //outputs these hands
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error, please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Database Error, please try again later.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

            } else {
                    constopenRaise = "100"; //all raising hands are stored as 100bb in the database
                    introductorymessageField.setText("You have " + bigBlinds + "bb in the position '" + tablePosition + "'. This means you should be open raising:");

                    DocumentReference docRef = db.collection("hands").document(lctablePosition); //gets hands
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    outputField.setText(document.get(constopenRaise).toString() + " of hands."); //outputs these
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error, please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Database Error, please try again later.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
            }


        }

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
