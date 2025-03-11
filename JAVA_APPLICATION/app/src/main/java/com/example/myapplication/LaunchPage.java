package com.example.myapplication;



        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Color;
        import androidx.appcompat.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.Button;
        import android.view.View;
        import androidx.core.content.ContextCompat;

public class LaunchPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_page);

        Button loginpage_button = findViewById(R.id.sign_in_button);
        loginpage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_loginpage_button = new Intent(LaunchPage.this, LoginPage.class);
                startActivity(intent_loginpage_button);
            }
        });

        Button createaccount_button = findViewById(R.id.create_acc_button);
        createaccount_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_createaccount_button = new Intent(LaunchPage.this, CreateAccount.class);
                startActivity(intent_createaccount_button);
            }
        });
    }
}