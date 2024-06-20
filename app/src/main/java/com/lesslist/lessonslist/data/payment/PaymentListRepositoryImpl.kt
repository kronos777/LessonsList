package com.lesslist.lessonslist.data.payment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lesslist.lessonslist.data.AppDatabase
import com.lesslist.lessonslist.domain.payment.PaymentItem
import com.lesslist.lessonslist.domain.payment.PaymentListRepository

class PaymentListRepositoryImpl(
    application: Application
) : PaymentListRepository {

    private val paymentListDao = AppDatabase.getInstance(application).PaymentListDao()
    private val mapper = PaymentListMapper()


    override suspend fun addPaymentItem(paymentItem: PaymentItem) {
        paymentListDao.addPaymentItem(mapper.mapEntityToDbModel(paymentItem))
    }

    override suspend fun deletePaymentItem(paymentItem: PaymentItem) {
        paymentListDao.deletePaymentItem(paymentItem.id)
    }

    override suspend fun editPaymentItem(paymentItem: PaymentItem) {
        paymentListDao.addPaymentItem(mapper.mapEntityToDbModel(paymentItem))
    }

    override suspend fun changeEnableStatePaymentItem(price: Int, id: Int) {
        paymentListDao.changeEnableStatePaymentItem(price, id)
    }

    override suspend fun getPaymentItem(paymentItemId: Int): PaymentItem {
         val dbModel = paymentListDao.getPaymentItem(paymentItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override fun getPaymentItemExists(studentId: Int, lessonsId: Int): Boolean {
        return paymentListDao.getPaymentItemExists(studentId, lessonsId)
    }

    override fun getPaymentList(): LiveData<List<PaymentItem>> = Transformations.map(
        paymentListDao.getPaymentList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }

}
