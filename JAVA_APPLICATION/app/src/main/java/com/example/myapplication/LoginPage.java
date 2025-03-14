package com.example.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {
    private boolean login_success = false; //change
    private boolean credentials_ok = false;
    private boolean no_empty_fields = false;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this);
        setContentView(R.layout.activity_login_page);

        Button login_button = findViewById(R.id.proceed_to_login_button);
        login_button.setOnClickListener(v -> {

            emptyFields();
            verifyCredentials();
            loginSuccess();
        });
    }

    private void loginSuccess() {
        if (login_success == true && no_empty_fields == true && credentials_ok == true) {
            EditText email_address = findViewById(R.id.editTextTextEmailAddress);
            String email = email_address.getText().toString().trim();

            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_email", email);
            editor.apply();

            Intent intent_login = new Intent(this, LandingPage.class);
            startActivity(intent_login);
        }
    }

    private void verifyCredentials() {
        EditText email = findViewById(R.id.editTextTextEmailAddress);
        EditText pw = findViewById(R.id.editTextTextPassword);
        String email_value = email.getText().toString().trim();
        String pw_value = pw.getText().toString().trim();

        credentials_ok = dbHelper.checkUser(email_value, pw_value);

        if (credentials_ok == false) {
            Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
        } else if (credentials_ok == true) {
            login_success = true;
        }
    }

    private void emptyFields() {
        EditText email = findViewById(R.id.editTextTextEmailAddress);
        EditText pw = findViewById(R.id.editTextTextPassword);
        String email_value = email.getText().toString();
        String pw_value = pw.getText().toString();

        no_empty_fields = !(email_value.isEmpty() || pw_value.isEmpty());

        if (email_value.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
        } else if (pw_value.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
        }
    }
}