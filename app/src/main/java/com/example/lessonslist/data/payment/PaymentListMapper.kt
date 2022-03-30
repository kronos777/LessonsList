package com.example.lessonslist.data.payment

import com.example.lessonslist.domain.payment.PaymentItem

class PaymentListMapper {

    fun mapEntityToDbModel(paymentItem: PaymentItem) = PaymentItemDbModel(
        title = paymentItem.title,
        description = paymentItem.description,
        student = paymentItem.student,
        price = paymentItem.price,
        id = paymentItem.id
    )

    fun mapDbModelToEntity(paymentItemDbModel: PaymentItemDbModel) = PaymentItem(
        title = paymentItemDbModel.title,
        description = paymentItemDbModel.description,
        student = paymentItemDbModel.student,
        price = paymentItemDbModel.price,
        id = paymentItemDbModel.id
    )

    fun mapListDbModelToListEntity(list: List<PaymentItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }

}
