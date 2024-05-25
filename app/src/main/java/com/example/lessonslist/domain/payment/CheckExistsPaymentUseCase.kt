package com.example.lessonslist.domain.payment

class CheckExistsPaymentUseCase(private val paymentListRepository: PaymentListRepository) {
    fun getPaymentItemExists(studentId: Int, lessonsId: Int): Boolean {
        return paymentListRepository.getPaymentItemExists(studentId, lessonsId)
    }
}
