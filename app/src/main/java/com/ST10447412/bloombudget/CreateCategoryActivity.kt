package com.ST10447412.bloombudget

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Data class to save category info
data class Category(val name: String, val limit: Double, val reminder: String, val iconResName: String)

class CreateCategoryActivity : AppCompatActivity() {

    private var selectedIconName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        val etName = findViewById<EditText>(R.id.etCategoryName)
        val etLimit = findViewById<EditText>(R.id.etSpendingLimit)
        val etReminder = findViewById<EditText>(R.id.etReminder)
        val rvIcons = findViewById<RecyclerView>(R.id.rvIcons)
        val btnPlant = findViewById<AppCompatButton>(R.id.btnPlant)

        // 1. List of custom icons
        val myIcons = listOf(
            R.drawable.burger,
            R.drawable.french_fries,
            R.drawable.ic_food,
            R.drawable.pizza,
            R.drawable.grocery,
            R.drawable.ic_groceries,
            R.drawable.shopping,
            R.drawable.transport,
            R.drawable.bus,
            R.drawable.public_transport,
            R.drawable.deal,
            R.drawable.insurance,
            R.drawable.lease,
            R.drawable.subscription,
            R.drawable.membership,
            R.drawable.salary,
            R.drawable.wages,

        )

        // 2. Set up the Grid
        rvIcons.layoutManager = GridLayoutManager(this, 3)
        rvIcons.adapter = IconAdapter(myIcons) { resId ->
            selectedIconName = resources.getResourceEntryName(resId)
        }

        btnPlant.setOnClickListener {
            val name = etName.text.toString().trim()
            val limit = etLimit.text.toString().toDoubleOrNull() ?: 0.0
            val reminder = etReminder.text.toString().trim()

            if (name.isEmpty() || selectedIconName.isEmpty()) {
                Toast.makeText(this, "Give it a name and pick an icon!", Toast.LENGTH_SHORT).show()
            } else {
                saveCategory(Category(name, limit, reminder, selectedIconName))
                Toast.makeText(this, "Category Planted!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun saveCategory(category: Category) {
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("categories_list", "[]")
        val type = object : TypeToken<MutableList<Category>>() {}.type
        val list: MutableList<Category> = gson.fromJson(json, type)
        list.add(category)
        sharedPref.edit().putString("categories_list", gson.toJson(list)).apply()
    }
}