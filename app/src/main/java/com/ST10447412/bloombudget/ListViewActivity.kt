package com.ST10447412.bloombudget

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListViewActivity : AppCompatActivity() {

    private lateinit var transactionList: MutableList<Expense>
    private lateinit var adapter: TransactionAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvBalance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        recyclerView = findViewById(R.id.lvExpenses)
        tvBalance = findViewById(R.id.tvTotalBalance)

        loadTransactions()
        setupRecyclerView()
        updateBalance()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(transactionList) { position ->
            showTransactionDetails(position)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                transactionList.removeAt(position)
                saveData()
                updateBalance()
                adapter.notifyItemRemoved(position)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }

    private fun showTransactionDetails(position: Int) {
        val item = transactionList[position]
        AlertDialog.Builder(this)
            .setTitle("Transaction Details")
            .setMessage("Date: ${item.date}\nCategory: ${item.categoryName}\nAmount: R${item.amount}\n\nDescription: ${item.description}")
            .setPositiveButton("Close", null)
            .show()
    }

    private fun updateBalance() {
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val income = sharedPref.getFloat("user_income", 0f)
        val totalExpenses = transactionList.sumOf { it.amount.toDouble() }.toFloat()
        tvBalance.text = "R${String.format("%.2f", income - totalExpenses)}"
    }

    private fun loadTransactions() {
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val json = sharedPref.getString("expenses_list", null)
        val type = object : TypeToken<MutableList<Expense>>() {}.type
        transactionList = if (json == null) mutableListOf() else Gson().fromJson(json, type)
    }

    private fun saveData() {
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        sharedPref.edit().putString("expenses_list", Gson().toJson(transactionList)).apply()
    }
}