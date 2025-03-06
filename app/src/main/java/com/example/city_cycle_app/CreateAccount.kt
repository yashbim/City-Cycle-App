package com.example.city_cycle_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CreateAccount : AppCompatActivity() {

    private var pwStatus = "rejected"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_acc_page)
        
        val proceedToLogin_button: Button = findViewById(R.id.proceed_to_login_button)
        proceedToLogin_button.setOnClickListener {

            passwordSimilarityCheck();

            if (pwStatus == "approved"){
                Toast.makeText(
                    applicationContext,
                    "Log in with your new credentials",
                    Toast.LENGTH_SHORT
                ).show()

                val intent_proceedToLogin_button = Intent(this, LoginPage::class.java)
                startActivity(intent_proceedToLogin_button)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }

    private fun passwordSimilarityCheck() {
        val pw1: EditText = findViewById(R.id.editTextTextPassword)
        val password1 = pw1.text.toString()
        print(password1)

        val pw2: EditText = findViewById(R.id.editTextreneterpw)
        val password2 = pw2.text.toString()
        print(password2)

        pwStatus = if (password1 == password2) "approved" else "rejected"

    }
}