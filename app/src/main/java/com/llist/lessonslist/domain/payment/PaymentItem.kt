package com.llist.lessonslist.domain.payment

data class PaymentItem (
    val title: String,
    val description: String,
    val studentId: Int,
    val lessonsId: Int,
    val datePayment: String,
    val student: String,
    val price: Int,
    val allPrice: Int,
    val enabled: Boolean,
    val id: Int = UNDEFINED_ID
){

    companion object {
        const val UNDEFINED_ID = 0
    }
}