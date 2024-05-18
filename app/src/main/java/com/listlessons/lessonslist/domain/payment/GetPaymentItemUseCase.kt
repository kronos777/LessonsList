package com.listlessons.lessonslist.domain.payment

class GetPaymentItemUseCase(private val paymentListRepository: PaymentListRepository) {
    suspend fun getPaymentItem(paymentItemId: Int): PaymentItem {
        return paymentListRepository.getPaymentItem(paymentItemId)
    }
}
