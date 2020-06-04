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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogSessionActivity extends AppCompatActivity {
    private Button btnSubmit, btnBack;
    private EditText enterProfit, enterDescription, enterLocation;
    private String location, profit, description, date, profitorloss;
    private TextView selectDate;
    private int day, month, year;
    private BigDecimal bdprofit;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Date enteredDate, currentDate;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private ToggleButton btnprofitorLoss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_log_session);

        Toolbar toolbar = findViewById(R.id.logsessiontoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Log Session");
        toolbar.setTitleTextColor(Color.WHITE);

        //initialising database variables
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //initialise fields
        btnprofitorLoss = findViewById(R.id.profitlosstoggle);
        selectDate = findViewById(R.id.selectdate);
        enterDescription = findViewById(R.id.enterdescription);
        enterLocation = findViewById(R.id.enterlocation);
        enterProfit = findViewById(R.id.enterprofit);
        btnSubmit = findViewById(R.id.submitsession);
        btnBack = findViewById(R.id.backtosessions);

        profitorloss = "Profit"; //toggle button is initially set to Profit so initialise variable as such

        btnprofitorLoss.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //handles when button is changed to profit or loss
                if(isChecked) {
                    profitorloss = "Loss";
                }
                else {
                    profitorloss = "Profit";
                }

            }
        });


        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //when textfield is clicked, calendar opens with year month and day stored
                Calendar cal = Calendar.getInstance();
                 year = cal.get(Calendar.YEAR);
                 month = cal.get(Calendar.MONTH);
                 day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        LogSessionActivity.this,
                        android.R.style.Theme_DeviceDefault,
                        mDateSetListener,
                        year,month,day);
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) { //when a date is clicked
                month = month + 1; //index starts at 0 for months so needs to be accounted for
                date = day+"/"+month+"/"+year;
                selectDate.setText(date);
                try {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    enteredDate = sdf.parse(day+"-"+month+"-"+year); //stores date clicked and current date for use in validation
                    currentDate = sdf.parse(cal.get(Calendar.DAY_OF_MONTH)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR));
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
        }); //loads processinput method

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //back to session page
                Intent intent = new Intent(LogSessionActivity.this,SessionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void processInput(){
        //basic validation
        if (enterLocation.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a location!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enterDescription.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a description!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enterProfit.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a profit/loss amount!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enterProfit.getText().length() > 6){
            Toast.makeText(getApplicationContext(), "Please enter a profit/loss amount less than £999,999!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (day == 0 || month == 0 || year == 0){
            Toast.makeText(getApplicationContext(), "Please enter a correct date!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredDate.after(currentDate)){ //checks whether date entered isnt after current date as it wont have happened yet
            Toast.makeText(getApplicationContext(), "Please enter a date from the past!", Toast.LENGTH_SHORT).show();
            return;
        }

        profit = enterProfit.getText().toString();

        //regex checks whether strings match the format for money.
        if (!(profit.matches("^\\£?([0-9]{1,3},([0-9]{3},)*[0-9]{3}|[0-9]+)(.[0-9][0-9])?$"))){
            Toast.makeText(getApplicationContext(), "Please enter a correct profit/loss value!", Toast.LENGTH_SHORT).show();
            return;
        }

        bdprofit = new BigDecimal(profit); //converts the profit to a bigdecimal

        if (profitorloss.equals("Loss")){ //if the toggle button is set to loss then make the profit negative
            bdprofit = bdprofit.negate();
        }

        location = enterLocation.getText().toString();
        description = enterDescription.getText().toString();
        //create a pokersession for the validated data and pass to database
        PokerSession session = new PokerSession(firebaseUser.getUid(), location, description, bdprofit.toString(), date);
        databaseReference.child("Sessions").child(firebaseUser.getUid()).push().setValue(session);
        Toast.makeText(getApplicationContext(), "Session Logged", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LogSessionActivity.this, SessionActivity.class);
        startActivity(intent);
        finish();

    }


    public void onBackPressed() { //back to session page
        Intent intent = new Intent(LogSessionActivity.this, SessionActivity.class);
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
