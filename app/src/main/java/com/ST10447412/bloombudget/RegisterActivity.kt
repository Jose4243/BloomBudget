package com.ST10447412.bloombudget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Find views by the IDs shown in your code
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtName = findViewById<EditText>(R.id.txtname)
        val edtPassword = findViewById<EditText>(R.id.PasswordTxt)
        val btnSignUp = findViewById<AppCompatButton>(R.id.btnSignUp)
        val btnLoginLink = findViewById<TextView>(R.id.logInLink)

        //if user already has account this link takes them to the login page
        btnLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val name = edtName.text.toString().trim()
            val pass = edtPassword.text.toString().trim()

            // Regex Explanation:
            // (?=.*[A-Z]) -> At least one uppercase letter
            // (?=.*[0-9]) -> At least one digit
            // .{6,}       -> At least 6 characters long
            val passwordPattern = "^(?=.*[A-Z])(?=.*[0-9]).{6,}$".toRegex()

            if (email.isEmpty()) {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (!pass.matches(passwordPattern)) {
                // Specifically tells the user what is missing
                Toast.makeText(this,
                    "Password must be 6+ characters with a Capital letter and a Number",
                    Toast.LENGTH_LONG).show()
            } else {
                // Proceed with saving the account
                val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString(email, pass)
                editor.putString("${email}_name", name)
                editor.apply()

                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }


        }
    }
}