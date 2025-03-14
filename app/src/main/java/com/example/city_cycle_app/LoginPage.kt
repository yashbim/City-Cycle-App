package com.example.city_cycle_app

import DatabaseHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlin.math.log

class LoginPage : AppCompatActivity() {

    private var login_success = false //change
    private var credentials_ok = false
    private var no_empty_fields = false


    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        setContentView(R.layout.activity_login_page)

        val login_button: Button = findViewById(R.id.proceed_to_login_button)
        login_button.setOnClickListener{

            emptyFields()
            verifyCredentials()
            loginSuccess()

        }
    }

    private fun loginSuccess(){

        if(login_success == true && no_empty_fields == true && credentials_ok == true){
            val email_address = findViewById<EditText>(R.id.editTextTextEmailAddress)
            val email = email_address.text.toString().trim()


            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("user_email", email) // Store email after login
            editor.apply()


            val intent_login = Intent(this, LandingPage::class.java)
            startActivity(intent_login)
        }

    }

    private fun verifyCredentials(){
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val pw = findViewById<EditText>(R.id.editTextTextPassword)

        val email_value = email.text.toString().trim()
        val pw_value = pw.text.toString().trim()

        credentials_ok = dbHelper.checkUser(email_value, pw_value)

        if (credentials_ok == false) {
            Toast.makeText(applicationContext, "Invalid email or password", Toast.LENGTH_SHORT).show()
        } else if (credentials_ok == true){
            login_success = true
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