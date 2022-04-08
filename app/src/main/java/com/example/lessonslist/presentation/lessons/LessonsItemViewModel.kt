package com.example.lessonslist.presentation.lessons

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.group.GroupListRepositoryImpl
import com.example.lessonslist.data.lessons.LessonsListRepositoryImpl
import com.example.lessonslist.domain.group.*
import com.example.lessonslist.domain.lessons.AddLessonsItemUseCase
import com.example.lessonslist.domain.lessons.EditLessonsItemUseCase
import com.example.lessonslist.domain.lessons.GetLessonsItemUseCase
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.domain.student.StudentItem
import kotlinx.coroutines.launch

class LessonsItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LessonsListRepositoryImpl(application)
    private val getLessonsItemUseCase = GetLessonsItemUseCase(repository)
    private val addLessonsItemUseCase = AddLessonsItemUseCase(repository)
    private val editLessonsItemUseCase = EditLessonsItemUseCase(repository)
    private val _lessonsItem = MutableLiveData<LessonsItem>()
    val lessonsItem: LiveData<LessonsItem>
        get() = _lessonsItem

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


    fun getLessonsItem(lessonsItemId: Int) {
        viewModelScope.launch {
            val item = getLessonsItemUseCase.getLessonsItem(lessonsItemId)

            _lessonsItem.value = item
        }
    }


    fun addLessonsItem(inputTitle: String, inputDescription: String, inputStudent: String, inputPrice: String, inputDateStart: String, inputDateEnd: String) {
        val title = inputTitle
        val description = inputDescription
        val student = inputStudent
        val price = inputPrice
        val dateStart = inputDateStart
        val dateEnd = inputDateEnd

        // add validation fun
        val fieldsValid = true

        if(fieldsValid) {
            viewModelScope.launch {
                val lessonsItem = LessonsItem(title, description, student, price.toInt(), dateStart, dateEnd)
                addLessonsItemUseCase.addLessonsItem(lessonsItem)
                finishWork()
            }
        }

    }

    fun editLessonsItem(inputTitle: String, inputDescription: String, inputStudent: String, inputPrice: String, inputDateStart: String, inputDateEnd: String) {
    val title = inputTitle
    val description = inputDescription
    val student = inputStudent
    val price = inputPrice
    val dateStart = inputDateStart
    val dateEnd = inputDateEnd
        // add validation fun
        val fieldsValid = true
        if (fieldsValid) {
            _lessonsItem.value?.let {
                viewModelScope.launch {
                    val lessonsItem = it.copy(title = title, description = description, student = student, price = price.toInt(), dateStart = dateStart, dateEnd = dateEnd)
                    editLessonsItemUseCase.editLessonsItem(lessonsItem)
                    finishWork()
                }
            }
        }

    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}
