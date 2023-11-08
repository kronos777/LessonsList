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


    private val addParentContactUseCase = AddParentItemUseCase(repository)
    val parentContactList = GetParentContactListItemUseCase(repository)


    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    fun addParentContact(inputName: String, inputPhone: String, inputStudentId: Int) {
        val name = parseName(inputName)

        val fieldsValid = validateInput(name, inputPhone)
        if (fieldsValid) {
            viewModelScope.launch {
                val parentItem = ParentContact(name, inputPhone, inputStudentId)
                addParentContactUseCase.addParentContact(parentItem)
                finishWork()
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

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}
