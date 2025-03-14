package com.example.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Retrieve the logged-in email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("user_email", "No email found");

        // Update the TextView with the email
        TextView emailTextView = findViewById(R.id.emailText);
        emailTextView.setText(savedEmail);

        Button resetpw_button = findViewById(R.id.resetPasswordBtn);
        resetpw_button.setOnClickListener(v -> {
            Intent intent = new Intent(this, ResetPassword.class);
            startActivity(intent);
        });
    }
}