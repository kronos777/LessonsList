package com.example.lessonslist.domain.payment

import androidx.lifecycle.LiveData

interface PaymentListRepository {

    suspend fun addPaymentItem(paymentItem: PaymentItem)

    suspend fun  deletePaymentItem(paymentItem: PaymentItem)

    suspend fun editPaymentItem(paymentItem: PaymentItem)

    suspend fun getPaymentItem(paymentItemId: Int): PaymentItem

    fun getPaymentList(): LiveData<List<PaymentItem>>
}
