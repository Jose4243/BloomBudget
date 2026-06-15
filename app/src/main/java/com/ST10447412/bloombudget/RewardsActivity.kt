package com.ST10447412.bloombudget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class RewardsActivity : AppCompatActivity() {

    private lateinit var rewardsBadgesContainer: LinearLayout
    private lateinit var ivRewardsProfilePic: ImageView
    private lateinit var fabChangePfp: FloatingActionButton
    private lateinit var btnScanReceipt: Button

    // System Picker Contract for Changing Profile Picture
    private val pickProfilePictureImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {

                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("PfpDebug", "Failed to secure permanent URI permissions: ${e.message}")
            }

            ivRewardsProfilePic.setImageURI(it)


            val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putString("profile_pfp_uri", it.toString()).apply()
            Toast.makeText(this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // System Picker Contract for Scanning Receipt Photos
    private val scanReceiptImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imageUri ->
            processReceiptText(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)

        rewardsBadgesContainer = findViewById(R.id.rewardsBadgesContainer)
        ivRewardsProfilePic = findViewById(R.id.ivRewardsProfilePic)
        fabChangePfp = findViewById(R.id.fabChangePfp)
        btnScanReceipt = findViewById(R.id.btnScanReceipt)

        // Load Persistent PFP image path if it exists
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedUriStr = sharedPrefs.getString("profile_pfp_uri", null)
        if (!savedUriStr.isNullOrEmpty()) {
            ivRewardsProfilePic.setImageURI(Uri.parse(savedUriStr))
        }

        // Click listeners
        fabChangePfp.setOnClickListener { pickProfilePictureImage.launch("image/*") }
        btnScanReceipt.setOnClickListener { scanReceiptImage.launch("image/*") }

        buildBadges(800.0)
    }
    private fun processReceiptText(imageUri: Uri) {
        Toast.makeText(this, "Reading receipt details...", Toast.LENGTH_SHORT).show()

        try {
            val image = InputImage.fromFilePath(this, imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    var detectedAmount = 0.0
                    val priceRegex = Regex("""(total|balance|due|amount|sum)[\s:]*[rR$]?\s*([\d,]+\.\d{2})""", RegexOption.IGNORE_CASE)

                    for (block in visionText.textBlocks) {
                        val textLine = block.text
                        val match = priceRegex.find(textLine)
                        if (match != null) {
                            val parsedVal = match.groupValues[2].replace(",", "").toDoubleOrNull()
                            if (parsedVal != null && parsedVal > detectedAmount) {
                                detectedAmount = parsedVal
                            }
                        }
                    }

                    if (detectedAmount > 0.0) {
                        Toast.makeText(this, "Detected Expense: R$detectedAmount Added!", Toast.LENGTH_LONG).show()
                        // Optional extension hook: append directly to your list storage here if needed
                    } else {
                        Toast.makeText(this, "Could not find explicit Total amount. Try scanning again.", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "OCR Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed loading image file matrix.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildBadges(totalSpent: Double) {
        rewardsBadgesContainer.removeAllViews()
        addBadgeRow("🌱 First Step", "Started logging dashboard data", "#FF9800")
        if (totalSpent <= 7000.0) {
            addBadgeRow("🛡️ Budget Master", "Stayed completely within your spending limit target!", "#E91E63")
        } else {
            addBadgeRow("🔒 Locked Badge", "Spend stays below maximum thresholds", "#444444")
        }
        addBadgeRow("🔥 On Streak", "Logged multiple item adjustments this week", "#0099CC")
    }

    private fun addBadgeRow(title: String, subtitle: String, colorHex: String) {
        val rowCard = LinearLayout(this)
        rowCard.orientation = LinearLayout.HORIZONTAL
        rowCard.gravity = Gravity.CENTER_VERTICAL
        rowCard.setBackgroundColor(Color.parseColor("#1E1E1E"))
        rowCard.setPadding(24, 24, 24, 24)

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 0, 0, 20)
        rowCard.layoutParams = params

        val badgeIcon = TextView(this)
        badgeIcon.text = title.split(" ")[0]
        badgeIcon.textSize = 28f
        badgeIcon.setPadding(0, 0, 24, 0)

        val textLayout = LinearLayout(this)
        textLayout.orientation = LinearLayout.VERTICAL

        val tvTitle = TextView(this)
        tvTitle.text = title.replace(title.split(" ")[0], "").trim()
        tvTitle.setTextColor(Color.parseColor(colorHex))
        tvTitle.textSize = 16f
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD)

        val tvSubtitle = TextView(this)
        tvSubtitle.text = subtitle
        tvSubtitle.setTextColor(Color.GRAY)
        tvSubtitle.textSize = 12f
        tvSubtitle.setPadding(0, 4, 0, 0)

        textLayout.addView(tvTitle)
        textLayout.addView(tvSubtitle)
        rowCard.addView(badgeIcon)
        rowCard.addView(textLayout)
        rewardsBadgesContainer.addView(rowCard)
    }
}