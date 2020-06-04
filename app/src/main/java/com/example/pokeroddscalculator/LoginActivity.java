package com.example.pokeroddscalculator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin, btnRegistration;
    private EditText enterEmail, enterPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUI();
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth = FirebaseAuth.getInstance();

        enterEmail = findViewById(R.id.loginemailfield); //initialise page attributes as variables to manipulate
        enterPassword = findViewById(R.id.loginpasswordfield);
        btnLogin = findViewById(R.id.loginbutton);
        btnRegistration = findViewById(R.id.registerbutton);

        btnLogin.setOnClickListener(new View.OnClickListener() { //when login button is clicked
            @Override
            public void onClick(View v) {
                processLogin(); //run process login method
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() { //when login button is clicked
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void processLogin(){
         String email, password;

        email = enterEmail.getText().toString();
        password = enterPassword.getText().toString();
        //basic validation
        if (email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter an Email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a Password!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password) //firebase method which checks if user is in authentication database w correct credentials
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class); //log user in
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Login Unsuccessful. Please check your credentials and try again!", Toast.LENGTH_LONG).show();
                        }
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
