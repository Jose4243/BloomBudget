package com.ST10447412.bloombudget

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var spinnerPeriod: Spinner
    private lateinit var graphContainer: LinearLayout
    private lateinit var tvStatusMessage: TextView
    private lateinit var statusVisualContainer: LinearLayout
    private lateinit var badgesContainer: LinearLayout

    private val MIN_SPENDING_GOAL = 2000.0
    private val MAX_SPENDING_GOAL = 7000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        spinnerPeriod = findViewById(R.id.spinnerPeriod)
        graphContainer = findViewById(R.id.graphContainer)
        tvStatusMessage = findViewById(R.id.tvStatusMessage)
        statusVisualContainer = findViewById(R.id.statusVisualContainer)
        badgesContainer = findViewById(R.id.badgesContainer)

        setupPeriodDropdown()
    }

    private fun setupPeriodDropdown() {
        val periods = arrayOf("Past Week", "Past Month", "Past Year")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPeriod.adapter = adapter

        spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                generateGraphData()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun generateGraphData() {
        graphContainer.removeAllViews()

        val spendingData = mutableMapOf<String, Float>()
        spendingData["Rent"] = 100f
        spendingData["Food"] = 450f
        spendingData["Transport"] = 250f

        val maxSpendInDataset = spendingData.values.maxOrNull() ?: 1f

        for ((category, amount) in spendingData) {
            val barLayout = LinearLayout(this)
            barLayout.orientation = LinearLayout.VERTICAL
            barLayout.gravity = Gravity.BOTTOM

            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            params.setMargins(14, 0, 14, 0)
            barLayout.layoutParams = params

            val barHeight = ((amount / maxSpendInDataset) * 400).toInt().coerceAtLeast(60)

            val barView = View(this)
            barView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, barHeight)
            barView.setBackgroundColor(Color.parseColor("#4CAF50"))

            val labelView = TextView(this)
            labelView.text = "$category\nR${amount.toInt()}"
            labelView.setTextColor(Color.WHITE)
            labelView.textSize = 12f
            labelView.gravity = Gravity.CENTER
            labelView.setPadding(0, 8, 0, 0)

            barLayout.addView(barView)
            barLayout.addView(labelView)
            graphContainer.addView(barLayout)
        }

        val totalSpent = spendingData.values.sum().toDouble()
        evaluateSpendingGoals(totalSpent)

        // Triggers gamification updates directly based on real calculated results
        updateGamificationBadges(totalSpent)
    }

    private fun evaluateSpendingGoals(totalSpent: Double) {
        when {
            totalSpent < MIN_SPENDING_GOAL -> {
                tvStatusMessage.text = "Below Minimum Budget Goal! (Spent: R$totalSpent)"
                statusVisualContainer.setBackgroundColor(Color.parseColor("#0099CC"))
            }
            totalSpent in MIN_SPENDING_GOAL..MAX_SPENDING_GOAL -> {
                tvStatusMessage.text = "Perfect Range Target! (Spent: R$totalSpent)"
                statusVisualContainer.setBackgroundColor(Color.parseColor("#669900"))
            }
            else -> {
                tvStatusMessage.text = "Maximum Limit Exceeded Warning! (Spent: R$totalSpent)"
                statusVisualContainer.setBackgroundColor(Color.parseColor("#CC0000"))
            }
        }
    }

    // ==========================================
    // GAMIFICATION LOGIC AREA
    // ==========================================
    private fun updateGamificationBadges(totalSpent: Double) {
        badgesContainer.removeAllViews()

        // Badge 1: Tracker Initialized (Always unlocked since they opened the view)
        addBadgeElement("🌱 First Step", "Started logging data", "#FF9800")

        // Badge 2: Budget Goal Condition Check
        if (totalSpent <= MAX_SPENDING_GOAL && totalSpent > 0) {
            addBadgeElement("🛡️ Budget Master", "Stayed under limits", "#E91E63")
        } else {
            addBadgeElement("🔒 Overspent", "Keep trying next week!", "#444444")
        }

        // Badge 3: High Roller Achievement
        if (totalSpent > 5000.0) {
            addBadgeElement("💎 Power Investor", "Large volume active tracking", "#9C27B0")
        }
    }


    private fun addBadgeElement(title: String, subtitle: String, colorHex: String) {
        val badgeCard = LinearLayout(this)
        badgeCard.orientation = LinearLayout.VERTICAL
        badgeCard.gravity = Gravity.CENTER
        badgeCard.setBackgroundColor(Color.parseColor("#1E1E1E"))
        badgeCard.setPadding(16, 16, 16, 16)

        val params = LinearLayout.LayoutParams(280, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 0, 16, 0)
        badgeCard.layoutParams = params


        val iconCircle = TextView(this)
        iconCircle.text = title.split(" ")[0]
        iconCircle.textSize = 24f
        iconCircle.gravity = Gravity.CENTER

        val iconParams = LinearLayout.LayoutParams(100, 100)
        iconParams.setMargins(0, 0, 0, 8)
        iconCircle.layoutParams = iconParams

        val tvTitle = TextView(this)
        tvTitle.text = title.replace(title.split(" ")[0], "").trim()
        tvTitle.setTextColor(Color.parseColor(colorHex))
        tvTitle.textSize = 14f
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD)
        tvTitle.gravity = Gravity.CENTER

        val tvSubtitle = TextView(this)
        tvSubtitle.text = subtitle
        tvSubtitle.setTextColor(Color.GRAY)
        tvSubtitle.textSize = 10f
        tvSubtitle.gravity = Gravity.CENTER
        tvSubtitle.setPadding(0, 4, 0, 0)

        badgeCard.addView(iconCircle)
        badgeCard.addView(tvTitle)
        badgeCard.addView(tvSubtitle)

        badgesContainer.addView(badgeCard)
    }
}