package com.example.city_cycle_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CreateAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_acc_page)

        val proceedToLogin_button: Button = findViewById(R.id.proceed_to_login_button)
        proceedToLogin_button.setOnClickListener{
            val intent_proceedToLogin_button = Intent(this, LoginPage::class.java)
            startActivity(intent_proceedToLogin_button)
        }

    }

}