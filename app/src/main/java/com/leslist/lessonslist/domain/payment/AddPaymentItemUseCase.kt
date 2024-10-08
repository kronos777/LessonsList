package com.leslist.lessonslist.domain.payment

class AddPaymentItemUseCase(private val paymentListRepository: PaymentListRepository) {
    suspend fun addPaymentItem(paymentItem: PaymentItem) {
        paymentListRepository.addPaymentItem(paymentItem)
    }
}
