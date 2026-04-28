package com.ST10447412.bloombudget

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddExpenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val spCategory = findViewById<Spinner>(R.id.spCategory)
        val btnLogGrow = findViewById<Button>(R.id.btnLogGrow)

        val categories = arrayOf("Select Category", "Salary", "Transport", "Food & Dining", "App Subscriptions")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapter

        etDate.setOnClickListener {
            val c = java.util.Calendar.getInstance()
            val year = c.get(java.util.Calendar.YEAR)
            val month = c.get(java.util.Calendar.MONTH)
            val day = c.get(java.util.Calendar.DAY_OF_MONTH)

            val dpd = android.app.DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format: DD/MM/YYYY
                etDate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day)

            dpd.show()
        }

        btnLogGrow.setOnClickListener {
            val amountStr = etAmount.text.toString()
            val date = etDate.text.toString()
            val description = etDescription.text.toString()
            val selectedCategory = spCategory.selectedItem.toString()

            // 1. Validation Logic
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            } else if (date.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            } else if (selectedCategory == "Select Category") {
                Toast.makeText(this, "Please pick a category", Toast.LENGTH_SHORT).show()
            } else {
                val amount = amountStr.toDoubleOrNull() ?: 0.0

                // 2. Map Category to Icon

                val iconResId = when (selectedCategory) {
                    "Salary" -> R.drawable.salary
                    "Transport" -> R.drawable.transport
                    "Food & Dining" -> R.drawable.ic_food
                    "App Subscriptions" -> R.drawable.subscription
                    else -> R.drawable.ic_launcher_foreground
                }

                // 3. Create the expense object with all 5 parameters
                val newExpense = Expense(
                    amount = amount,
                    date = date,
                    categoryName = selectedCategory,
                    description = description,
                    iconResId = iconResId
                )

                // 4. Save to SharedPreferences
                saveExpenseToNotebook(newExpense)

                Toast.makeText(this, "Expense Logged & Growing!", Toast.LENGTH_SHORT).show()
                finish() // Go back to Dashboard
            }
        }
    }

    private fun saveExpenseToNotebook(expense: Expense) {
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val gson = Gson()

        // Fetch existing list
        val json = sharedPref.getString("expenses_list", null)
        val type = object : TypeToken<MutableList<Expense>>() {}.type
        val expenseList: MutableList<Expense> = if (json == null) {
            mutableListOf()
        } else {
            gson.fromJson(json, type)
        }

        // Add new expense and save back to JSON
        expenseList.add(expense)
        val updatedJson = gson.toJson(expenseList)
        sharedPref.edit().putString("expenses_list", updatedJson).apply()
    }
}