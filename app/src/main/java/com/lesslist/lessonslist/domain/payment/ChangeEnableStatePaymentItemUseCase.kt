package com.lesslist.lessonslist.domain.payment

class ChangeEnableStatePaymentItemUseCase(private val paymentListRepository: PaymentListRepository) {
    suspend fun changeEnableStatePaymentItem(price: Int, id: Int) {
        paymentListRepository.changeEnableStatePaymentItem(price, id)
    }
}
