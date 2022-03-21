package com.example.lessonslist.presentation.student


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.student.StudentListRepositoryImpl
import com.example.lessonslist.domain.student.AddStudentItemUseCase
import com.example.lessonslist.domain.student.EditStudentItemUseCase
import com.example.lessonslist.domain.student.GetStudentItemUseCase
import com.example.lessonslist.domain.student.StudentItem


import kotlinx.coroutines.launch

class StudentItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentListRepositoryImpl(application)

    private val getStudentItemUseCase = GetStudentItemUseCase(repository)
    private val addStudentItemUseCase = AddStudentItemUseCase(repository)
    private val editStudentItemUseCase = EditStudentItemUseCase(repository)

    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    private val _studentItem = MutableLiveData<StudentItem>()
    val studentItem: LiveData<StudentItem>
        get() = _studentItem

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    fun getStudentItem(studentItemId: Int) {
        viewModelScope.launch {
            val item = getStudentItemUseCase.getStudentItem(studentItemId)
            _studentItem.value = item
        }
    }

    fun addStudentItem(inputName: String?, inputLastName: String?, inputPaymentBalance: String, inputNotes: String, inputGroup: String) {
        val name = parseName(inputName)
        val lastName = parseName(inputLastName)
        val paymentBalance = inputPaymentBalance?.toFloat()
        val group = inputGroup
        val notes = inputNotes
        //val count = parseCount(inputCount)

        val fieldsValid = validateInput(name, lastName)
        if (fieldsValid) {
            viewModelScope.launch {
                val studentItem = StudentItem(paymentBalance, name, lastName, group, notes, true)
                addStudentItemUseCase.addStudentItem(studentItem)
                finishWork()
            }
        }
    }

    fun editStudentItem(inputName: String?, inputLastName: String?, inputPaymentBalance: String, inputNotes: String, inputGroup: String) {
        val name = parseName(inputName)
        val lastName = parseName(inputLastName)
        val paymentBalance = inputPaymentBalance?.toFloat()
        val group = inputGroup
        val notes = inputNotes
        //val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, lastName)
        if (fieldsValid) {
            _studentItem.value?.let {
                viewModelScope.launch {
                    val item = it.copy(name = name, lastname = lastName, paymentBalance = paymentBalance, group = group, notes = notes, enabled = true)
                    //val item add parametrs StudentItems
                    editStudentItemUseCase.editStudentItem(item)
                    finishWork()
                }
            }
        }
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun validateInput(name: String, lastName: String): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }
        if (lastName.isBlank()) {
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputCount() {
        _errorInputCount.value = false
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}
