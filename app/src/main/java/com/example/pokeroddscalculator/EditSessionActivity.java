package com.example.pokeroddscalculator;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class EditSessionActivity extends AppCompatActivity {
    private String sessionClicked, location, description, profit, date, profitorloss;
    private Button btnSubmit, btnBack, btnDelete;
    private EditText editProfit, editDescription, editLocation;
    private int day, month, year;
    private TextView editDate;
    private ToggleButton editbtnprofitorLoss;
    private FirebaseDatabase firebaseDatabase;
    private Query databaserecallReference;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private PokerSession session;
    private BigDecimal convertedProfit, bdprofit;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Date enteredDate, currentDate;
    private Bundle bundle;
    private String[] dateArray;
    private Calendar cal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_edit_session);

        Toolbar toolbar = findViewById(R.id.editsessiontoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Session");
        toolbar.setTitleTextColor(Color.WHITE);

        //initialise firebase connectivity
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //initilaise fields
        editbtnprofitorLoss = findViewById(R.id.editprofitlosstoggle);
        editDate = findViewById(R.id.editdate);
        editDescription = findViewById(R.id.editdescription);
        editLocation = findViewById(R.id.editlocation);
        editProfit = findViewById(R.id.editprofit);
        btnSubmit = findViewById(R.id.editsession);
        btnBack = findViewById(R.id.backtosessions2);
        btnDelete = findViewById(R.id.deletesession);
        //bundle is passed from sessionactivity.java
        bundle = getIntent().getExtras();

        if(bundle!=null) {
                sessionClicked = bundle.getString("sessionindex"); //used to identify the session in question
                databaserecallReference = firebaseDatabase.getReference("Sessions").child(firebaseUser.getUid()).child(sessionClicked);

                //re-searches database for the session and its data and then populates the fields
                databaserecallReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        session = dataSnapshot.getValue(PokerSession.class);
                        if (dataSnapshot.exists()){
                            location = String.valueOf(session.getLocation());
                            description = String.valueOf(session.getDescription());
                            profit = String.valueOf(session.getProfit());
                            date = String.valueOf(session.getDate());
                            convertedProfit = new BigDecimal(profit);

                            editLocation.setText(location);
                            editDescription.setText(description);

                            editDate.setText(date);
                            try { //this takes the string of date e.g 12/05/2019 and then splits up with '/' as the borderlines and passes to an array
                                cal = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                dateArray = date.split("\\/"); //splits date into day month and year seperated by the slash
                                day = Integer.parseInt(dateArray[0]);
                                month = Integer.parseInt(dateArray[1]);
                                year = Integer.parseInt(dateArray[2]);
                                enteredDate = sdf.parse(dateArray[0] + "-" + dateArray[1] + "-" + dateArray[2]); //variables used for validation later
                                currentDate = sdf.parse(cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR));
                            } catch (java.text.ParseException e) {
                                Toast.makeText(getApplicationContext(), "Error retrieving date from session.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (convertedProfit.compareTo(BigDecimal.ZERO) > 0) { //if profit if greater than zero
                                editbtnprofitorLoss.setChecked(false); //set toggle button to be unchecked (false), which shows that it is profit
                                editProfit.setText(profit);
                                profitorloss = "Profit";
                            } else { //else it must be loss so check the button
                                editbtnprofitorLoss.setChecked(true);
                                editProfit.setText(profit.substring(1)); //removes the minus sign from the string to add into field
                                profitorloss = "Loss";
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        }

        editbtnprofitorLoss.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //code which handles user clicking toggle button
                if (isChecked) {                                                           //already instantiated above from database
                    profitorloss = "Loss";
                } else {
                    profitorloss = "Profit";
                }
            }
        });

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog( //sets the default load for date picker which is the
                        EditSessionActivity.this,       //day, month and year from the session being edited
                        android.R.style.Theme_DeviceDefault,
                        mDateSetListener,
                        year,(month-1),day); //month was displaying one out, may was being shown as june so need to -1
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) { //when a date is picked
                month = month + 1; //add 1 to month as the index starts at zero so will be a month behind
                date = day + "/" + month + "/" + year;
                editDate.setText(date); //whenever a new date is picked it displays in text box

                try {
                    cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    enteredDate = sdf.parse(day + "-" + month + "-" + year); //stores the new data in a variable
                } catch (java.text.ParseException e) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processInput();
            }
        }); //runs processinput method

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //back to sessionactivity
                Intent intent = new Intent(EditSessionActivity.this, SessionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSession();
            }
        }); //runs deletesession method

    }

        private void processInput(){
        //all simple validation from original logsessionactivity.java
            if (editLocation.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Please enter a location!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editDescription.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Please enter a description!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editProfit.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Please enter a profit/loss amount!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editProfit.getText().length() > 6){
                Toast.makeText(getApplicationContext(), "Please enter a profit/loss amount less than £999,999!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (day == 0 || month == 0 || year == 0){
                Toast.makeText(getApplicationContext(), "Please enter a correct date!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (enteredDate.after(currentDate)){
                Toast.makeText(getApplicationContext(), "Please enter a date from the past!", Toast.LENGTH_SHORT).show();
                return;
            }

            profit = editProfit.getText().toString();

            //if profit doesnt meet the requirement for money, display error (regex taken from stackexchange)
            if (!(profit.matches("^\\£?([0-9]{1,3},([0-9]{3},)*[0-9]{3}|[0-9]+)(.[0-9][0-9])?$"))){
                Toast.makeText(getApplicationContext(), "Please enter a correct profit/loss value!", Toast.LENGTH_SHORT).show();
                return;
            }
            //converts profit to a big decimal to negate if necessary
            bdprofit = new BigDecimal(profit);

            if (profitorloss.equals("Loss")){
                bdprofit = bdprofit.negate();
            }

            location = editLocation.getText().toString();
            description = editDescription.getText().toString();
            //creating a new object to pass to firebase
            PokerSession session = new PokerSession(firebaseUser.getUid(), location, description, bdprofit.toString(), date);
            //editing the current session instead of adding a new one
            databaseReference.child("Sessions").child(firebaseUser.getUid()).child(sessionClicked).setValue(session);

            Toast.makeText(getApplicationContext(), "Session Edited", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditSessionActivity.this, SessionActivity.class);
            startActivity(intent);
            finish();
        }


        public void onBackPressed() { //if nav bar is used then back to sessionactivity
            Intent intent = new Intent(EditSessionActivity.this, SessionActivity.class);
            startActivity(intent);
            finish();
        }

        private void deleteSession(){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_NEGATIVE: //if no do nothing
                            //No button clicked
                            break;

                        case DialogInterface.BUTTON_POSITIVE: //if yes delete session and close page
                            databaseReference.child("Sessions").child(firebaseUser.getUid()).child(sessionClicked).removeValue();  //Yes button clicked delete session
                            Intent intent = new Intent(EditSessionActivity.this, SessionActivity.class);
                            startActivity(intent);
                            finish();
                    }
                }
            };
            //alert box with yes or no answer before deleting
            AlertDialog.Builder builder = new AlertDialog.Builder(EditSessionActivity.this);
            builder.setMessage("Are you sure you want to delete this session?").setPositiveButton("Yes", dialogClickListener)
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



