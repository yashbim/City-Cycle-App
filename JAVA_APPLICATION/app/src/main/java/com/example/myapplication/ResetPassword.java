package com.example.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class ResetPassword extends AppCompatActivity {

    private boolean confirmPW = false;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this);
        setContentView(R.layout.activity_reset_password);

        Button proceedToLogin_button = (Button) findViewById(R.id.proceed_to_login_button);
        proceedToLogin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordSimilarityCheck();
                proceedRegistration();
            }
        });
    }

    private void proceedRegistration() {
        if (confirmPW == false) {
            Toast.makeText(
                    getApplicationContext(),
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
            ).show();
        } else if (confirmPW == true) {
            proceedToLogin();
        }
    }

    private void passwordSimilarityCheck() {
        EditText pw1 = (EditText) findViewById(R.id.ResetPW1);
        String password1 = pw1.getText().toString();
        System.out.print(password1);

        EditText pw2 = (EditText) findViewById(R.id.ResetPW2);
        String password2 = pw2.getText().toString();
        System.out.print(password2);

        confirmPW = password1.equals(password2) ? true : false;
    }

    private void proceedToLogin() {
        updatePassword();

        Toast.makeText(
                getApplicationContext(),
                "Log in with your new credentials",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent_proceedToLogin_button = new Intent(this, LoginPage.class);
        startActivity(intent_proceedToLogin_button);
    }

    private void updatePassword() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String emailInput = sharedPreferences.getString("user_email", "No email found");
        EditText newPasswordInput = (EditText) findViewById(R.id.ResetPW2);

        String email = emailInput.toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();

        if (dbHelper.updatePassword(email, newPassword)) {
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginPage.class));
            finish();
        } else {
            Toast.makeText(this, "Error updating password - user not found", Toast.LENGTH_SHORT).show();
        }
    }
}
