package com.example.lessonslist.domain.payment

data class PaymentItem (
    val title: String,
    val description: String,
    val student: String,
    val price: Int,
    val enabled: Boolean,
    val id: Int = UNDEFINED_ID
){

    companion object {
        const val UNDEFINED_ID = 0
    }
}