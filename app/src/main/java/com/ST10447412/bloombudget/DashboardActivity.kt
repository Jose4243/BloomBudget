package com.ST10447412.bloombudget

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Navigation bar
        findViewById<ImageView>(R.id.wallet_page).setOnClickListener {
            startActivity(Intent(this, ListViewActivity::class.java))
        }

        findViewById<ImageView>(R.id.addExpense_page).setOnClickListener {
            startActivity(Intent(this, CreateCategoryActivity::class.java))
        }

        findViewById<Button>(R.id.btnTopUp).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        findViewById<ImageView>(R.id.btnPlus).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        // 2. Click Listener to Set Savings Goal
        val tvGoalText = findViewById<TextView>(R.id.btnSetGoal)
        tvGoalText.setOnClickListener {
            showSetGoalDialog()
        }


        val profileIcon = findViewById<ImageView>(R.id.profile_settings)

        profileIcon.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            startActivity(intent)
        }


        val imgProfileTop = findViewById<ImageView>(R.id.userProfile_pic)

        imgProfileTop.setOnClickListener {
            val intent = Intent(this, RewardsActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        updateDashboardUI()
        super.onResume()

            val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val savedUriStr = sharedPrefs.getString("profile_pfp_uri", null)
            if (!savedUriStr.isNullOrEmpty()) {
                try {
                    val imgProfileTop = findViewById<ImageView>(R.id.userProfile_pic)
                    imgProfileTop.setImageURI(Uri.parse(savedUriStr))
                } catch (e: Exception) {
                    Log.e("DashboardDebug", "Safe-catch triggered: could not parse profile picture URI.")
                }
            }
        }



    private fun updateDashboardUI() {
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val gson = Gson()


        // 1. Get the current logged-in user's email
        val loggedInEmail = sharedPref.getString("current_user_email", "")

        // 2. Use that email to find the specific name saved during registration
        val userName = sharedPref.getString("${loggedInEmail}_name", "User")

        // 3. Determine time of day greeting
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val timeGreeting = when (currentHour) {
            in 0..11 -> "Morning"
            in 12..16 -> "Afternoon"
            else -> "Evening"
        }

        // 4. Update the Greeting TextView
        val tvGreeting = findViewById<TextView>(R.id.greeting_text)
        tvGreeting.text = "$timeGreeting, $userName"


        // Fetch Expenses
        val json = sharedPref.getString("expenses_list", null)
        val type = object : TypeToken<MutableList<Expense>>() {}.type
        val expenses: MutableList<Expense> = if (json == null) mutableListOf() else gson.fromJson(json, type)

        // Fetch Views
        val tvDashboardTotal = findViewById<TextView>(R.id.tvDashboardTotal)
        val tvStatus = findViewById<TextView>(R.id.tvTreeStatus)
        val tvGoalText = findViewById<TextView>(R.id.tvGoalText)
        val pbSavingsGoal = findViewById<ProgressBar>(R.id.pbSavingsGoal)

        // Calculate Total Balance
        var totalBalance = 0.0
        for (expense in expenses) {
            if (expense.categoryName.equals("Salary", ignoreCase = true)) {
                totalBalance += expense.amount
            } else {
                totalBalance -= expense.amount
            }
        }

        // Retrieve Saved Goal (Defaults to 10,000)
        val savingsGoal = sharedPref.getFloat("savings_goal", 10000f).toDouble()

        // Calculate Percentage (Added check for negative balance)
        val progressPercent = if (savingsGoal > 0) {
            val calcBalance = if (totalBalance < 0) 0.0 else totalBalance
            ((calcBalance / savingsGoal) * 100).toInt()
        } else 0

        // Update UI
        tvDashboardTotal.text = "R${String.format("%.2f", totalBalance)}"
        tvGoalText.text = "Goal: R${String.format("%.2f", savingsGoal)} ($progressPercent%)"
        pbSavingsGoal.progress = progressPercent.coerceIn(0, 100)

        tvStatus.setTypeface(null, Typeface.BOLD)
        updateTreeVisuals(progressPercent, tvStatus)
    }
    private fun updateTreeVisuals(percent: Int, tvStatus: TextView) {
        val imgTree = findViewById<ImageView>(R.id.imgTreeDisplay)
        val density = resources.displayMetrics.density

        // Dynamic Scaling (40dp to 300dp)
        val startSize = 40
        val endSize = 300
        val dynamicSize = startSize + ((endSize - startSize) * (percent.coerceIn(0, 100) / 100.0)).toInt()

        val params = imgTree.layoutParams
        params.width = (dynamicSize * density).toInt()
        params.height = (dynamicSize * density).toInt()
        imgTree.layoutParams = params

        // Update Image & Status based on milestones
        when {
            percent <= 0 -> {
                imgTree.setImageResource(R.drawable.seed)
                tvStatus.text = "Your garden is empty... plant a seed!"
            }
            percent < 40 -> {
                imgTree.setImageResource(R.drawable.sprout)
                tvStatus.text = "A sprout is appearing! Keep going."
            }
            percent < 80 -> {
                imgTree.setImageResource(R.drawable.plant)
                tvStatus.text = "Your tree is growing strong!"
            }
            percent < 100 -> {
                imgTree.setImageResource(R.drawable.sapling)
                tvStatus.text = "Almost at full bloom!"
            }
            else -> {
                imgTree.setImageResource(R.drawable.money_tree)
                tvStatus.text = "GOAL REACHED! YOUR GARDEN IS LUXURIOUS!"
            }
        }
    }

    private fun showSetGoalDialog() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Enter amount (e.g. 5000)"

        AlertDialog.Builder(this)
            .setTitle("Set Savings Goal")
            .setMessage("How much do you want to save this month?")
            .setView(input)
            .setPositiveButton("Set") { _, _ ->
                val goalValue = input.text.toString().toFloatOrNull() ?: 10000f
                getSharedPreferences("UserData", Context.MODE_PRIVATE)
                    .edit()
                    .putFloat("savings_goal", goalValue)
                    .apply()
                updateDashboardUI() // Refresh everything
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}