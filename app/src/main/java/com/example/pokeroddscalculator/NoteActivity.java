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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {
    private Button btnlogNote, btnBack;
    private ListView listView;
    private FirebaseDatabase firebaseDatabase;
    private Query databaseReference;
    private FirebaseUser firebaseUser;
    private ArrayList<String> arraylist, keys;
    private ArrayAdapter<String> adapter;
    private Note note;
    private String outputtitle, outputnote, outputdate;
    private TextView numberofNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_note);

        Toolbar toolbar = findViewById(R.id.notetoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes");
        toolbar.setTitleTextColor(Color.WHITE);


        //initialise fields and get current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        listView = (ListView) findViewById(R.id.loggednotes);
        numberofNotes = findViewById(R.id.numberofnotes);
        //reference gets the whole list of notes for current user logged in
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Notes").child(firebaseUser.getUid());
        arraylist = new ArrayList<>(); //stores the info about notes
        keys = new ArrayList<>(); //stores info of notes unique keys in case they are clicked on for editing
        adapter = new ArrayAdapter<String>(this,R.layout.notes,R.id.notesinfo, arraylist); //a layout is used that was written by me, creates individual lines in listview for each note
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){ //for every note
                    if (dataSnapshot.exists()){
                        note=ds.getValue(Note.class); //store in a variable and use this to store info in variables
                        outputtitle = String.valueOf(note.getTitle());
                        outputnote = String.valueOf(note.getNote());
                        outputdate = String.valueOf(note.getDate());
                        arraylist.add(outputtitle + " - " + outputdate); //add the title and date to the arraylist
                        keys.add(ds.getKey()); //store the key
                    }
                }

                if (arraylist.size() == 0){
                    numberofNotes.setText("No notes logged."); //if no notes exist then show this
                } else {
                    numberofNotes.setText("Number of notes: " + arraylist.size()); //else lis the number of notes
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //when a note is clicked
                        Intent intent = new Intent(NoteActivity.this,EditNoteActivity.class);
                        intent.putExtra("noteindex",keys.get(position)); //pass the key to the editnoteactivity.java class
                        startActivity(intent);
                        finish();
                    }
                });
                listView.setAdapter(adapter); //add all notes to the listview
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnlogNote = findViewById(R.id.openlognote);
        btnBack = findViewById(R.id.backtohome2);

        btnlogNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //opens page to log a note
                Intent intent = new Intent(NoteActivity.this, LogNoteActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //back to home
                Intent intent = new Intent(NoteActivity.this,HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
