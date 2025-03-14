package com.example.city_cycle_app

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPassword : AppCompatActivity() {

    private var confirmPW = false

    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        setContentView(R.layout.activity_reset_password)

        val proceedToLogin_button: Button = findViewById(R.id.proceed_to_login_button)
        proceedToLogin_button.setOnClickListener {

            passwordSimilarityCheck();
            proceedRegistration()

        }
    }

    private fun proceedRegistration(){
        if(confirmPW == false){
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
        val pw1: EditText = findViewById(R.id.ResetPW1)
        val password1 = pw1.text.toString()
        print(password1)

        val pw2: EditText = findViewById(R.id.ResetPW2)
        val password2 = pw2.text.toString()
        print(password2)

        confirmPW = if (password1 == password2) true else false

    }

    private fun proceedToLogin(){

        updatePassword()

        Toast.makeText(
            applicationContext,
            "Log in with your new credentials",
            Toast.LENGTH_SHORT
        ).show()

        val intent_proceedToLogin_button = Intent(this, LoginPage::class.java)
        startActivity(intent_proceedToLogin_button)
    }


    private fun updatePassword() {


        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val emailInput = sharedPreferences.getString("user_email", "No email found")
        val newPasswordInput = findViewById<EditText>(R.id.ResetPW2)

        val email = emailInput.toString().trim()
        val newPassword = newPasswordInput.text.toString().trim()


        if (dbHelper.updatePassword(email, newPassword)) {
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
            // Optional: Navigate to another screen
            startActivity(Intent(this, LoginPage::class.java))
            finish()
        } else {
            Toast.makeText(this, "Error updating password - user not found", Toast.LENGTH_SHORT).show()
        }
    }
}