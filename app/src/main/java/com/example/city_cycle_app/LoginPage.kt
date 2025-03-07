package com.example.city_cycle_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlin.math.log

class LoginPage : AppCompatActivity() {

    private var login_success = true //change
    private var no_empty_fields = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val login_button: Button = findViewById(R.id.proceed_to_login_button)
        login_button.setOnClickListener{

            emptyFields()
            loginSuccess()

        }
    }

    private fun loginSuccess(){

        if(login_success == true && no_empty_fields == true){
            val intent_login = Intent(this, LandingPage::class.java)
            startActivity(intent_login)
        }

    }

    private fun emptyFields(){
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val pw = findViewById<EditText>(R.id.editTextTextPassword)

        val email_value = email.text.toString()
        val pw_value = pw.text.toString()

        no_empty_fields = if (email_value.isEmpty() || pw_value.isEmpty()) false else true

        if (email_value.isEmpty()){
            Toast.makeText(
                applicationContext,
                "Enter email",
                Toast.LENGTH_SHORT
            ).show()
        } else if (pw_value.isEmpty()){
            Toast.makeText(
                applicationContext,
                "Enter Password",
                Toast.LENGTH_SHORT
            ).show()
        }

    }






}