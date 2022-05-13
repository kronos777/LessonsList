package com.example.lessonslist.data.payment

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "payment_items")
data class PaymentItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description: String,
    val student: String,
    val price: Int,
    val enabled: Boolean
)
