package com.leslist.lessonslist.data.payment

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "payment_items")
data class PaymentItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description: String,
    val student: String,
    val studentId: Int,
    val lessonsId: Int,
    val datePayment: String,
    val price: Int,
    val allprice: Int,
    val enabled: Boolean
)
