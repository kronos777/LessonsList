package com.example.lessonslist.data.payment

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "payment_items")
data class PaymentItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val title: String,
    val description: String,
    val student: String,
    val price: Int,
    val id: Int
)
