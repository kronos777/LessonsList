package com.llist.lessonslist.domain.payment

class DeletePaymentItemUseCase(private val paymentListRepository: PaymentListRepository) {
    suspend fun deletePaymentItem(paymentItem: PaymentItem) {
        paymentListRepository.deletePaymentItem(paymentItem)
    }
}
