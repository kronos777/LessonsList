package com.example.lessonslist.data.payment

import com.example.lessonslist.domain.payment.PaymentItem

class PaymentListMapper {

    fun mapEntityToDbModel(paymentItem: PaymentItem) = PaymentItemDbModel(
        title = paymentItem.title,
        description = paymentItem.description,
        student = paymentItem.student,
        studentId = paymentItem.studentId,
        lessonsId = paymentItem.lessonsId,
        datePayment = paymentItem.datePayment,
        price = paymentItem.price,
        enabled = paymentItem.enabled,
        id = paymentItem.id
    )

    fun mapDbModelToEntity(paymentItemDbModel: PaymentItemDbModel) = PaymentItem(
        title = paymentItemDbModel.title,
        description = paymentItemDbModel.description,
        student = paymentItemDbModel.student,
        studentId = paymentItemDbModel.studentId,
        lessonsId = paymentItemDbModel.lessonsId,
        datePayment = paymentItemDbModel.datePayment,
        price = paymentItemDbModel.price,
        enabled = paymentItemDbModel.enabled,
        id = paymentItemDbModel.id

    )

    fun mapListDbModelToListEntity(list: List<PaymentItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }

}
