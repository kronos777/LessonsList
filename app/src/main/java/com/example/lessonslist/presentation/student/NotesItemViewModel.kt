package com.example.lessonslist.presentation.student


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.notes.NotesListRepositoryImpl
import com.example.lessonslist.domain.notes.*
import kotlinx.coroutines.launch

class NotesItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotesListRepositoryImpl(application)


    private val addNotesItemUseCase = AddNotesItemUseCase(repository)
    private val deleteNotesItemUseCase = DeleteNotesItemUseCase(repository)
    private val getNotesUseCase = GetNotesUseCase(repository)
    val notesList = GetNotesListItemUseCase(repository)


    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen




    fun deleteNotesItem(notesId: Int) {
        viewModelScope.launch {
            val notesItem = getNotesUseCase.getNotesItem(notesId)
            deleteNotesItemUseCase.deleteNotesItem(notesItem)
        }
    }

    fun addNotesItem(inputName: String, inputDate: String, inputStudentId: Int): Boolean {
        val name = parseName(inputName)

        val fieldsValid = validateInput(name, inputDate)
        return if (fieldsValid) {
            viewModelScope.launch {
                val notesItem = NotesItem(name, inputDate, inputStudentId)
                addNotesItemUseCase.addNotesItem(notesItem)
                finishWork()

            }
            true
        } else {
            false
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
