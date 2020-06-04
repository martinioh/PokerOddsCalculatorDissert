package com.example.pokeroddscalculator;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import android.widget.Toast;


public class CalculatorActivity extends AppCompatActivity {
private Spinner tablepositionSpinner;
private Button btnSubmit, btnBack;
private EditText bigblindsET;
private String sbigBlinds, selectedPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();

        setContentView(R.layout.activity_calculator);

        Toolbar toolbar = findViewById(R.id.calculatortoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Raise & All In Calculator");
        toolbar.setTitleTextColor(Color.WHITE);


        //initialising fields
        bigblindsET = findViewById(R.id.bigblinds);
        initialiseSpinner();
        btnSubmit = findViewById(R.id.submitcalculator);
        btnBack = findViewById(R.id.backtohome);
        //setting variable whenever a table position is clicked
        tablepositionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedPosition = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedPosition = "";
            }

        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processInput();
            }
        }); //loads result page

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //back to home
                Intent intent = new Intent(CalculatorActivity.this,HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    public void initialiseSpinner(){
        tablepositionSpinner = (Spinner) findViewById(R.id.tableposition);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.table_positions, android.R.layout.simple_spinner_item); //list of positions is stored in table_positions in res/values folder
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tablepositionSpinner.setAdapter(adapter);
    }


    public void processInput(){
        sbigBlinds = bigblindsET.getText().toString(); //general validation for fields

        if (sbigBlinds.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter an amount of big blinds!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Integer.parseInt(sbigBlinds) == 0){
            Toast.makeText(getApplicationContext(), "Please enter a value more than one big blind.", Toast.LENGTH_SHORT).show();

        }

        if (sbigBlinds.length() > 4) {
            Toast.makeText(getApplicationContext(), "Please enter a correct value of big blinds.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Integer.parseInt(sbigBlinds) >= 300){
            Toast.makeText(getApplicationContext(), "Please enter a value less than 300 big blinds.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPosition.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter a table position.", Toast.LENGTH_SHORT).show();
            return;
        }

        //passes validated data to calculatedresult.java
        Intent intent = new Intent(CalculatorActivity.this, CalculatedResultActivity.class);
        intent.putExtra("bigBlinds",sbigBlinds);
        intent.putExtra("tablePosition",selectedPosition);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
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
