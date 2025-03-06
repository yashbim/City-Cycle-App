package com.example.city_cycle_app

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreateAccount : AppCompatActivity() {

    private var confirmPW = false
    private var emptyFields = true

    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        setContentView(R.layout.activity_create_acc_page)

        val proceedToLogin_button: Button = findViewById(R.id.proceed_to_login_button)
        proceedToLogin_button.setOnClickListener {

            emptyFieldsCheck()
            passwordSimilarityCheck();
            proceedRegistration()

        }
    }

    private fun proceedRegistration(){
        if (emptyFields == true){
            Toast.makeText(
                applicationContext,
                "Fill out all fields",
                Toast.LENGTH_SHORT
            ).show()
        } else if(confirmPW == false){
            Toast.makeText(
                applicationContext,
                "Passwords do not match",
                Toast.LENGTH_SHORT
            ).show()
        }else if (confirmPW == true){

            proceedToLogin()

        }
    }

    private fun passwordSimilarityCheck() {
        val pw1: EditText = findViewById(R.id.editTextTextPassword)
        val password1 = pw1.text.toString()
        print(password1)

        val pw2: EditText = findViewById(R.id.editTextreneterpw)
        val password2 = pw2.text.toString()
        print(password2)

        confirmPW = if (password1 == password2) true else false

    }

    private fun emptyFieldsCheck(){
        val inputEmail = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val inputEmail_value = inputEmail.text.toString().trim()
        val inputPW = findViewById<EditText>(R.id.editTextTextPassword)
        val inputPW_value = inputPW.text.toString().trim()
        val inputConfirmPW = findViewById<EditText>(R.id.editTextreneterpw)
        val inputConfirmPW_value = inputConfirmPW.text.toString().trim()

        if (inputEmail_value.isEmpty()){
            emptyFields = true
        } else if (inputPW_value.isEmpty()){
            emptyFields = true
        } else if (inputConfirmPW_value.isEmpty()){
            emptyFields = true
        } else {
            emptyFields = false
        }
    }

    private fun proceedToLogin(){

        registerUser()

        Toast.makeText(
            applicationContext,
            "Log in with your new credentials",
            Toast.LENGTH_SHORT
        ).show()

        val intent_proceedToLogin_button = Intent(this, LoginPage::class.java)
        startActivity(intent_proceedToLogin_button)
    }

    private fun registerUser(){
        val userEmail: EditText = findViewById(R.id.editTextTextEmailAddress)
        val userPW: EditText = findViewById(R.id.editTextTextPassword)

        val userEmail_value = userEmail.text.toString().trim()
        val userPW_value = userPW.text.toString().trim()

        if (dbHelper.registerUser(userEmail_value, userPW_value)) {
            Toast.makeText(applicationContext, "Account Created Successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginPage::class.java))
            finish()
        } else {
            Toast.makeText(applicationContext, "Error Creating Account", Toast.LENGTH_SHORT).show()
        }
    }
}