package com.ST10447412.bloombudget
    data class Expense(
        val amount: Double,
        val date: String,
        val categoryName: String,
        val description: String,
        val iconResId: Int
    )