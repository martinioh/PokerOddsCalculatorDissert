package com.example.pokeroddscalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private Button btnSubmit, btnBack, btnDelete;
    private EditText editTitle, editNote;
    private Bundle bundle;
    private String noteClicked, title, retrievednote, date;
    private Query databaserecallReference;
    private Note note;
    private int day, month, year;
    private Date currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = findViewById(R.id.editnotetoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Note");
        toolbar.setTitleTextColor(Color.WHITE);


        //initialise firebase connectivity
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //initialise fields
        editTitle = findViewById(R.id.edittitle);
        editNote = findViewById(R.id.editnote);
        btnSubmit = findViewById(R.id.editnotebutton);
        btnBack = findViewById(R.id.backtonotes2);
        btnDelete = findViewById(R.id.deletenote);
        editTitle.requestFocus();

        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        bundle = getIntent().getExtras(); //stores the note key passed from noteactivity.java

        if (bundle != null) {
            noteClicked = bundle.getString("noteindex"); //used to identify the note in question
            databaserecallReference = firebaseDatabase.getReference("Notes").child(firebaseUser.getUid()).child(noteClicked);

            //re-searches database for the session and its data and then populates the fields
            databaserecallReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    note = dataSnapshot.getValue(Note.class);
                    if (dataSnapshot.exists()) {
                        title = String.valueOf(note.getTitle());
                        retrievednote = String.valueOf(note.getNote());
                        date = String.valueOf(note.getDate());

                        editTitle.setText(title); //populate fields
                        editNote.setText(retrievednote);

                        EditText editText = (EditText)findViewById(R.id.edittitle); //checks length of current text in title box and places cursor to end of that text
                        editText.setSelection(editText.getText().length());

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processInput();
            }
        }); //runs process input method

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //back to editnoteactivity
                Intent intent = new Intent(EditNoteActivity.this, NoteActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               deleteNote();
            }
        }); //runs deletenote method
    }

    private void processInput(){
        //basic validation
        if (editTitle.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a title!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editNote.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a note!", Toast.LENGTH_SHORT).show();
            return;
        }

        title = editTitle.getText().toString();
        retrievednote = editNote.getText().toString();

        try { //get current date
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            date = day+"/"+month+"/"+year;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            currentDate = sdf.parse(day+"-"+(month+1)+"-"+year);
        } catch (java.text.ParseException e) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            return;
        }
        Note note = new Note(firebaseUser.getUid(), title, retrievednote, date); //add to database
        databaseReference.child("Notes").child(firebaseUser.getUid()).child(noteClicked).setValue(note);
        Toast.makeText(getApplicationContext(), "Note Saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditNoteActivity.this, NoteActivity.class);
        startActivity(intent);
        finish();
    }

    private void deleteNote(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_NEGATIVE: //if no is clicked then do nothing
                        //No button clicked
                        break;

                    case DialogInterface.BUTTON_POSITIVE: //if yes is clicked then delete note from database and close page
                        databaseReference.child("Notes").child(firebaseUser.getUid()).child(noteClicked).removeValue();  //Yes button clicked delete session
                        Intent intent = new Intent(EditNoteActivity.this, NoteActivity.class);
                        startActivity(intent);
                        finish();
                }
            }
        };
        //alert box confirming yes or no
        AlertDialog.Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
        builder.setMessage("Are you sure you want to delete this note?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
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
