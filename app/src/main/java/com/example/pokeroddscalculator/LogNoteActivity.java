package com.example.pokeroddscalculator;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogNoteActivity extends AppCompatActivity {
    private Button btnSubmit, btnBack;
    private EditText enterTitle, enterNote;
    private String titleEntered, noteEntered, date;
    private int day, month, year;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Date currentDate;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_log_note);

        Toolbar toolbar = findViewById(R.id.lognotetoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Log Note");
        toolbar.setTitleTextColor(Color.WHITE);

        //initialising database variables
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //initialising fields
        enterTitle = findViewById(R.id.entertitle);
        enterNote = findViewById(R.id.enternote);
        btnSubmit = findViewById(R.id.submitnote);
        btnBack = findViewById(R.id.backtonotes);
        //calculates current date and stores in currentDate variable for use in logging date note was created
        try {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            date = day+"/"+month+"/"+year;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            currentDate = sdf.parse(day+"-"+(month+1)+"-"+year); //month always starts at index 0, so add 1 to account for this
        } catch (java.text.ParseException e) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processInput();
            }
        }); //loads processinput method

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //back to note page
                Intent intent = new Intent(LogNoteActivity.this,NoteActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }



    private void processInput(){
        titleEntered = enterTitle.getText().toString();
        noteEntered = enterNote.getText().toString();
        //basic validation
        if (titleEntered.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a title!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (noteEntered.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a note!", Toast.LENGTH_SHORT).show();
            return;
        }//creates note object and adds details to firebase database
        Note note = new Note(firebaseUser.getUid(), titleEntered, noteEntered, date);
        databaseReference.child("Notes").child(firebaseUser.getUid()).push().setValue(note);
        Toast.makeText(getApplicationContext(), "Note Logged", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LogNoteActivity.this, NoteActivity.class);
        startActivity(intent);
        finish();
    }

    public void onBackPressed() { //back to note page
        Intent intent = new Intent(LogNoteActivity.this, NoteActivity.class);
        startActivity(intent);
        finish();
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
