package com.leslist.lessonslist.presentation.payment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.leslist.lessonslist.data.payment.PaymentListRepositoryImpl
import com.leslist.lessonslist.domain.payment.DeletePaymentItemUseCase
import com.leslist.lessonslist.domain.payment.GetPaymentListItemUseCase
import com.leslist.lessonslist.domain.payment.PaymentItem
import kotlinx.coroutines.launch

class PaymentListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PaymentListRepositoryImpl(application)

    private val getPaymentItemListUseCase = GetPaymentListItemUseCase(repository)
    private val deletePaymentItemUseCase = DeletePaymentItemUseCase(repository)
    //private val editPaymentItemUseCase = EditPaymentItemUseCase(repository)
    val paymentList = getPaymentItemListUseCase.getPaymentList()




    fun deletePaymentItem(paymentItem: PaymentItem) {
        viewModelScope.launch {
            deletePaymentItemUseCase.deletePaymentItem(paymentItem)
        }
    }
    /*fun changeEnableState(paymentItem: PaymentItem) {
        viewModelScope.launch {
            val newItem = paymentItem.copy(enabled = !paymentItem.enabled)
            editPaymentItemUseCase.editPaymentItem(newItem)
        }
    }*/
}