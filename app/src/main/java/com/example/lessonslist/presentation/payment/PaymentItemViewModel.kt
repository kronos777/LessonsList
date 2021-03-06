package com.example.lessonslist.presentation.payment

import android.app.Application
import android.icu.text.CaseMap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.group.GroupListRepositoryImpl
import com.example.lessonslist.data.payment.PaymentListRepositoryImpl
import com.example.lessonslist.domain.group.*
import com.example.lessonslist.domain.payment.*
import com.example.lessonslist.domain.student.StudentItem
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.launch

class PaymentItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PaymentListRepositoryImpl(application)
    private val getPaymentItemUseCase = GetPaymentItemUseCase(repository)
    private val addPaymentItemUseCase = AddPaymentItemUseCase(repository)
    private val editPaymentItemUseCase = EditPaymentItemUseCase(repository)
    private val deletePaymentItemUseCase = DeletePaymentItemUseCase(repository)
    private val ChangeEnableStatePaymentItemUseCase = ChangeEnableStatePaymentItemUseCase(repository)

    private val _paymentItem = MutableLiveData<PaymentItem>()
    val paymentItem: LiveData<PaymentItem>
        get() = _paymentItem

    private val _errorInputTitle = MutableLiveData<Boolean>()
    val errorInputTitle: LiveData<Boolean>
        get() = _errorInputTitle

    private val _errorInputDescription = MutableLiveData<Boolean>()
    val errorInputDescription: LiveData<Boolean>
        get() = _errorInputDescription

    private val _errorInputStudent = MutableLiveData<Boolean>()
    val errorInputStudent: LiveData<Boolean>
        get() = _errorInputStudent

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen


    fun getPaymentItem(paymentItemId: Int) {
        viewModelScope.launch {
            val item = getPaymentItemUseCase.getPaymentItem(paymentItemId)

            _paymentItem.value = item
        }
    }


    fun addPaymentItem(inputTitle: String, inputDescription: String, inputLessonsId: String, inputStudentId: String, inputDatePayment: String, inputStudent: String, inputPrice: String, enabled: Boolean) {
        val title = inputTitle
        val description = inputDescription
        val student = inputStudent
        val price = inputPrice.toInt()
        val studentId = inputStudentId
        val datePayment = inputDatePayment
        val lessonsId = inputLessonsId
        // add validation fun
        val fieldsValid = validateInput(title, student)

        if(fieldsValid) {
            viewModelScope.launch {
               // val paymentItem = PaymentItem(title, description, student, studentId.toInt(), lessonsId.toInt(), datePayment, price, true)
                val paymentItem = PaymentItem(title, description, studentId.toInt(), lessonsId.toInt(), datePayment, student, price, enabled)
                addPaymentItemUseCase.addPaymentItem(paymentItem)
                finishWork()
            }
        } else {
            Log.d("errorinput", "error in add group")
        }

    }

    fun editPaymentItem(inputTitle: String, inputDescription: String, inputLessonsId: String, inputStudentId: String, inputDatePayment: String, inputStudent: String, inputPrice: String, enabledPayment: Boolean) {
        val title = inputTitle
        val description = inputDescription
        val student = inputStudent
        val price = inputPrice.toInt()
        val studentId = inputStudentId.toInt()
        val datePayment = inputDatePayment
        val lessonsId = inputLessonsId.toInt()

        // add validation fun
        val fieldsValid = validateInput(title, student)
        if (fieldsValid) {
            _paymentItem.value?.let {
                viewModelScope.launch {
                    val paymentItem = it.copy(title = title, description = description, student = student, studentId = studentId, datePayment = datePayment.toString(), lessonsId = lessonsId, price = price, enabled = enabledPayment)
                    editPaymentItemUseCase.editPaymentItem(paymentItem)
                    //finishWork()

                }
            }
        } else {
            Log.d("errorinput", "error in edit group")
         }

    }


    fun editPaymentItemDolg(idPaymnet: Int, inputTitle: String, inputDescription: String, inputLessonsId: String, inputStudentId: String, inputDatePayment: String, inputStudent: String, inputPrice: String, enabledPayment: Boolean) {
        val title = inputTitle
        val description = inputDescription
        val student = inputStudent
        val price = inputPrice.toInt()
        val studentId = inputStudentId.toInt()
        val datePayment = inputDatePayment
        val lessonsId = inputLessonsId.toInt()

        // add validation fun
        val fieldsValid = validateInput(title, student)
        if (fieldsValid) {
            _paymentItem.value?.let {
                viewModelScope.launch {
                    val paymentItem = it.copy(id = idPaymnet, title = title, description = description, student = student, studentId = studentId, datePayment = datePayment.toString(), lessonsId = lessonsId, price = price, enabled = enabledPayment)
                    editPaymentItemUseCase.editPaymentItem(paymentItem)
                    finishWork()
                }
            }
        } else {
            Log.d("errorinput", "error in edit group")
        }

    }



    private fun validateInput(title: String, inputStudent: String): Boolean {
        var result = true
        if (title.isBlank()) {
            //_errorInputName.value = true
            result = false
        }
        if (inputStudent.isBlank()) {
            //_errorInputLastName.value = true
            result = false
        }

        return result
    }

    fun changeEnableState(price: Int, id: Int) {
        viewModelScope.launch {
            /*val newItem = paymentItem.copy(price = price, enabled = !paymentItem.enabled)
            editPaymentItemUseCase.editPaymentItem(newItem)*/
            ChangeEnableStatePaymentItemUseCase.changeEnableStatePaymentItem(price, id)
        }
    }

    fun deleteGroupItem(paymentItem: PaymentItem) {
        viewModelScope.launch {
            deletePaymentItemUseCase.deletePaymentItem(paymentItem)
        }
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}
