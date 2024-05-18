package com.listlessons.lessonslist.domain.payment

class EditPaymentItemUseCase(private val paymentListRepository: PaymentListRepository) {
    suspend fun editPaymentItem(paymentItem: PaymentItem) {
        paymentListRepository.editPaymentItem(paymentItem)
    }
}

