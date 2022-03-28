package com.example.lessonslist.domain.payment

import androidx.lifecycle.LiveData

class GetPaymentListItemUseCase(private val paymentListRepository: PaymentListRepository) {
    fun getPaymentList(): LiveData<List<PaymentItem>> {
        return paymentListRepository.getPaymentList()
    }
}
