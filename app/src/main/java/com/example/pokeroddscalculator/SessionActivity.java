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
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;

public class SessionActivity extends AppCompatActivity {
    private Button btnlogSession, btnBack;
    private TextView profitlossDisplay;
    private ListView listView;
    private FirebaseDatabase firebaseDatabase;
    private Query databaseReference, getLimit;
    private FirebaseUser firebaseUser;
    private ArrayList<String> arraylist, keys;
    private ArrayList <BigDecimal> profitlossarraylist;
    private ArrayAdapter<String> adapter;
    private PokerSession session;
    private Bundle bundle;
    private String location, description, profit, date, limit;
    private BigDecimal convertedProfit;
    private BigDecimal totalProfit;
    private PokerLimit pokerlimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_session);

        Toolbar toolbar = findViewById(R.id.sessiontoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sessions");
        toolbar.setTitleTextColor(Color.WHITE);

        //initialise fields and current user
        profitlossDisplay = findViewById(R.id.profitloss);
        listView = (ListView) findViewById(R.id.loggedsessions);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //database reference is all the sessions for the current user
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Sessions").child(firebaseUser.getUid());

        arraylist = new ArrayList<>(); //stores info about sessions
        profitlossarraylist = new ArrayList<>(); //stores a running calculation of profit/loss
        keys = new ArrayList<>(); //stores the unique key of session if needed for editing

        getLimit = firebaseDatabase.getReference("Limit").child(firebaseUser.getUid());

        getLimit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    limit = String.valueOf(dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        totalProfit = new BigDecimal(0); //initialise profit to 0
        adapter = new ArrayAdapter<String>(this,R.layout.session,R.id.sessioninfo, arraylist); //a layout is used that was written by me, creates individual lines in listview for each session

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren( )){ //for each session
                    session=ds.getValue(PokerSession.class); //store for use
                    location = String.valueOf(session.getLocation()); //use to populate variables
                    description = String.valueOf(session.getDescription());
                    profit = String.valueOf(session.getProfit());
                    convertedProfit = new BigDecimal(profit); //convert profit from database (string) to bigdecimal
                    date = String.valueOf(session.getDate());

                    totalProfit = totalProfit.add(convertedProfit); //add profit current value to current profit value
                    arraylist.add("Location: " + location + "\n" + "Description: " + description + "\n"  + "Date: " + date + "\n" + "Profit: £" + profit); //store info
                    keys.add(ds.getKey()); //store key
                }


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //if session is clicked then pass info to class and load
                        Intent intent = new Intent(SessionActivity.this,EditSessionActivity.class);
                        intent.putExtra("sessionindex",keys.get(position));
                        startActivity(intent);
                        finish();
                    }
                });
                listView.setAdapter(adapter);

                if (arraylist.size() == 0){ //if there are no sessions output profit and session number as 0
                    profitlossDisplay.setText("Profit: £" + totalProfit.toString() +  "\n" + "Number of sessions: 0");
                } else {
                    if (totalProfit.compareTo(BigDecimal.ZERO) > 0){ //if the profit if greater than 0 show Profit: and number of sessions
                        profitlossDisplay.setText("Profit: £" + totalProfit.toString() + "\n" + "Number of sessions: " + arraylist.size());
                    } else { //if the profit if less than 0 show Loss: and number of sessions
                        profitlossDisplay.setText("Loss: £" + totalProfit.toString().substring(1) + "\n" + "Number of sessions: " + arraylist.size());

                        if (limit != null){
                            int noLimit = Integer.parseInt(limit);
                            if (Integer.parseInt(totalProfit.toString().substring(1)) > noLimit){
                                Toast.makeText(getApplicationContext(), "You are over your loss limit. This can be damaging. Please seek help at begambleaware.org", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        btnlogSession = findViewById(R.id.openlogsession);
        btnBack = findViewById(R.id.backtohome3);


        btnlogSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //load page to log a session
                Intent intent = new Intent(SessionActivity.this, LogSessionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //back to home
                Intent intent = new Intent(SessionActivity.this, HomePageActivity.class);
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
