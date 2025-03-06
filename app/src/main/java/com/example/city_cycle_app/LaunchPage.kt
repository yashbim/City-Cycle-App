package com.example.city_cycle_app

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat

class LaunchPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_page)

        val loginpage_button: Button = findViewById(R.id.sign_in_button)
        loginpage_button.setOnClickListener{
            val intent_loginpage_button = Intent(this, LoginPage::class.java)
            startActivity(intent_loginpage_button)
        }

        val createaccount_button : Button = findViewById(R.id.create_acc_button)
        createaccount_button.setOnClickListener{
            val intent_createaccount_button = Intent(this, CreateAccount::class.java)
            startActivity(intent_createaccount_button)
        }

    }
}