package com.example.lessonslist.presentation.student


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.notes.NotesListRepositoryImpl
import com.example.lessonslist.data.student.StudentListRepositoryImpl
import com.example.lessonslist.domain.notes.*
import com.example.lessonslist.domain.student.*


import kotlinx.coroutines.launch

class NotesItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotesListRepositoryImpl(application)

    private val getNotesItemUseCase = GetNotesUseCase(repository)
    private val addNotesItemUseCase = AddNotesItemUseCase(repository)
    private val editNotesItemUseCase = EditNotesUseCase(repository)
    val notesList = GetNotesListItemUseCase(repository)


    private val _notesItem = MutableLiveData<NotesItem>()
    val notesItem: LiveData<NotesItem>
        get() = _notesItem

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    fun getNotesItem(notesItemId: Int) {
        viewModelScope.launch {
            val item = getNotesItemUseCase.getNotesItem(notesItemId)
            _notesItem.value = item
        }
    }

    fun addNotesItem(inputName: String, inputDate: String, inputStudentid: Int): Boolean {
        val name = parseName(inputName)
        val date = inputDate
        val idStudent = inputStudentid

        val fieldsValid = validateInput(name, date)
        if (fieldsValid) {
            viewModelScope.launch {
                val notesItem = NotesItem(name, date, idStudent)
                addNotesItemUseCase.addNotesItem(notesItem)
                finishWork()

            }
            return true
        } else {
            return false
        }
    }

    fun editNotesItem(inputName: String, inputDate: String, inputStudentid: Int) {
        val name = parseName(inputName)
        val date = inputDate
        val idStudent = inputStudentid

        val fieldsValid = validateInput(name, date)
        if (fieldsValid) {
            _notesItem.value?.let {
                viewModelScope.launch {
                    val item = it.copy(text = name, date = date, student = idStudent)
                    editNotesItemUseCase.editNotesItem(item)
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
