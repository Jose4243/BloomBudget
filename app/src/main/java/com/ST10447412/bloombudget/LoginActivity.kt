package com.ST10447412.bloombudget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val tvSignUpLink = findViewById<TextView>(R.id.logInLink)
        val tvForgotPassword = findViewById<TextView>(R.id.txtForgotPassword)

        // 1. SIGN UP LINK
        tvSignUpLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 2. FORGET PASSWORD LINK
        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Reset link sent to your email (Simulated)", Toast.LENGTH_LONG).show()
        }

        btnLogin.setOnClickListener {
            val emailInput = edtEmail.text.toString().trim()
            val passInput = edtPassword.text.toString().trim()

            val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            sharedPref.edit().putString("current_user_email",emailInput).apply()
            val storedPassword = sharedPref.getString(emailInput, null)

            if (emailInput.isEmpty() || passInput.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (passInput == storedPassword) {

                // SAVE THE LOGGED IN EMAIL BEFORE MOVING TO DASHBOARD
                sharedPref.edit().putString("current_user_email", emailInput).apply()

                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}