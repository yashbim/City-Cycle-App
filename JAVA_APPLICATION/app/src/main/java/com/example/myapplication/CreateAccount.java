package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;


        import android.content.Intent;
        import android.os.Bundle;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;
        import androidx.appcompat.app.AppCompatActivity;

public class CreateAccount extends AppCompatActivity {
    private boolean confirmPW = false;
    private boolean emptyFields = true;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this);
        setContentView(R.layout.activity_create_account);

        Button proceedToLogin_button = findViewById(R.id.proceed_to_login_button);
        proceedToLogin_button.setOnClickListener(v -> {
            emptyFieldsCheck();
            passwordSimilarityCheck();
            proceedRegistration();
        });
    }

    private void proceedRegistration() {
        if (emptyFields == true) {
            Toast.makeText(getApplicationContext(), "Fill out all fields", Toast.LENGTH_SHORT).show();
        } else if (confirmPW == false) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else if (confirmPW == true) {
            proceedToLogin();
        }
    }

    private void passwordSimilarityCheck() {
        EditText pw1 = findViewById(R.id.editTextTextPassword);
        String password1 = pw1.getText().toString();
        System.out.print(password1);

        EditText pw2 = findViewById(R.id.editTextreneterpw);
        String password2 = pw2.getText().toString();
        System.out.print(password2);

        confirmPW = password1.equals(password2);
    }

    private void emptyFieldsCheck() {
        EditText inputEmail = findViewById(R.id.editTextTextEmailAddress);
        String inputEmail_value = inputEmail.getText().toString().trim();

        EditText inputPW = findViewById(R.id.editTextTextPassword);
        String inputPW_value = inputPW.getText().toString().trim();

        EditText inputConfirmPW = findViewById(R.id.editTextreneterpw);
        String inputConfirmPW_value = inputConfirmPW.getText().toString().trim();

        if (inputEmail_value.isEmpty()) {
            emptyFields = true;
        } else if (inputPW_value.isEmpty()) {
            emptyFields = true;
        } else if (inputConfirmPW_value.isEmpty()) {
            emptyFields = true;
        } else {
            emptyFields = false;
        }
    }

    private void proceedToLogin() {
        registerUser();
        Toast.makeText(getApplicationContext(), "Log in with your new credentials", Toast.LENGTH_SHORT).show();
        Intent intent_proceedToLogin_button = new Intent(this, LoginPage.class);
        startActivity(intent_proceedToLogin_button);
    }

    private void registerUser() {
        EditText userEmail = findViewById(R.id.editTextTextEmailAddress);
        EditText userPW = findViewById(R.id.editTextTextPassword);
        String userEmail_value = userEmail.getText().toString().trim();
        String userPW_value = userPW.getText().toString().trim();

        if (dbHelper.registerUser(userEmail_value, userPW_value)) {
            Toast.makeText(getApplicationContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginPage.class));
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Error Creating Account", Toast.LENGTH_SHORT).show();
        }
    }
}