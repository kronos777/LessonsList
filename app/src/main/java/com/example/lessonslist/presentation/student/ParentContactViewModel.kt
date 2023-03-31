package com.example.lessonslist.presentation.student


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.parent.ParentListRepositoryImpl
import com.example.lessonslist.domain.parent.*
import kotlinx.coroutines.launch

class ParentContactViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ParentListRepositoryImpl(application)

    private val getParentContactUseCase = GetParentContactUseCase(repository)
    private val addParentContactUseCase = AddParentItemUseCase(repository)
    private val editParentContactUseCase = EditParentContactUseCase(repository)
    val parentContactList = GetParentContactListItemUseCase(repository)


    private val _parentItem = MutableLiveData<ParentContact>()
    val parentItem: LiveData<ParentContact>
        get() = _parentItem

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    fun getParentContact(parentContactId: Int) {
        viewModelScope.launch {
            val item = getParentContactUseCase.getParentContact(parentContactId)
            _parentItem.value = item
        }
    }

    fun addParentContact(inputName: String, inputPhone: String, inputStudentid: Int) {
        val name = parseName(inputName)
        val phone = inputPhone
        val idStudent = inputStudentid

        val fieldsValid = validateInput(name, phone)
        if (fieldsValid) {
            viewModelScope.launch {
                val parentItem = ParentContact(name, phone, idStudent)
                addParentContactUseCase.addParentContact(parentItem)
                finishWork()
            }
        }
    }

    fun editParentContact(inputName: String, inputPhone: String, inputStudentid: Int) {
        val name = parseName(inputName)
        val phone = inputPhone
        val idStudent = inputStudentid

        val fieldsValid = validateInput(name, phone)
        if (fieldsValid) {
            _parentItem.value?.let {
                viewModelScope.launch {
                    val item = it.copy(name = name, number = phone, student = idStudent)
                    editParentContactUseCase.editParentContact(item)
                    finishWork()
                }
            }
        }
    }


    private fun validateInput(name: String, date: String): Boolean {
        var result = true
        if (name.isBlank()) {
            result = false
        }
        if (date.isBlank()) {
            result = false
        }

        return result
    }


    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parsePaymentBalance(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }


    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}
