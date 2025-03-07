package com.example.city_cycle_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlin.math.log

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val login_button: Button = findViewById(R.id.proceed_to_login_button)
        login_button.setOnClickListener{
            val intent_login = Intent(this, LandingPage::class.java)
            startActivity(intent_login)
        }

        //login page now working
    }




}