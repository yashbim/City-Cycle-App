package com.example.city_cycle_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.edit

class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        // Retrieve the logged-in email from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("user_email", "No email found")

        // Update the TextView with the email
        val emailTextView = findViewById<TextView>(R.id.emailText)
        emailTextView.text = savedEmail


        val login_button: Button = findViewById(R.id.resetPasswordBtn)
        login_button.setOnClickListener{

            val intent = Intent(this, ResetPassword::class.java)
            startActivity(intent)

        }
    }
}