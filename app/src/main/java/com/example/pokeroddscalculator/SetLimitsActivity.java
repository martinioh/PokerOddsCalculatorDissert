package com.example.pokeroddscalculator;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetLimitsActivity extends AppCompatActivity {

    private EditText limits;
    private Button btnSubmit, btnBack, btngetHelp;
    private String sLimit;
    private TextView currentlimit;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, databasereferenceCheck;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_set_limits);
        Toolbar toolbar = findViewById(R.id.setlimitstoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Set Limits");
        toolbar.setTitleTextColor(Color.WHITE);

        //initialising database variables
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //initialising fields
        limits = findViewById(R.id.limits);
        btnSubmit = findViewById(R.id.submitlimits);
        btnBack = findViewById(R.id.backtohome4);
        btngetHelp = findViewById(R.id.linktohelp);
        currentlimit = findViewById(R.id.currentlimit);


        databasereferenceCheck = firebaseDatabase.getReference("Limit").child(firebaseUser.getUid());
        databasereferenceCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentlimit.setText("£"+String.valueOf(dataSnapshot.getValue()));
                } else {
                    currentlimit.setText("No Limit Set.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processInput();
            }
        }); //submits limits

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //back to home
                Intent intent = new Intent(SetLimitsActivity.this,HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btngetHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //back to home
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.begambleaware.org"));
                startActivity(intent);
            }
        });

    }

    public void processInput(){
        sLimit = limits.getText().toString(); //general validation for fields

        if (sLimit.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a limit!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Integer.parseInt(sLimit) == 0){
            Toast.makeText(getApplicationContext(), "Please enter a value more than zero.", Toast.LENGTH_SHORT).show();
        }

        if (Integer.parseInt(sLimit) >= 2500){
            Toast.makeText(getApplicationContext(), "Please enter a value less than £2500.", Toast.LENGTH_SHORT).show();
            return;
        }

        databasereferenceCheck = firebaseDatabase.getReference("Limit").child(firebaseUser.getUid());
        databasereferenceCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        databaseReference.child("Limit").child(firebaseUser.getUid()).setValue(sLimit);
                        Toast.makeText(getApplicationContext(), "Limit changed to £" + sLimit, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SetLimitsActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                } else {
                        databaseReference.child("Limit").child(firebaseUser.getUid()).setValue(sLimit);
                        Toast.makeText(getApplicationContext(), "Limit set to £" + sLimit, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SetLimitsActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });




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
