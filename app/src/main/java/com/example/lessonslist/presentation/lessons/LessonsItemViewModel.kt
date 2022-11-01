package com.example.lessonslist.presentation.lessons

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.lessons.LessonsListRepositoryImpl
import com.example.lessonslist.domain.lessons.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LessonsItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LessonsListRepositoryImpl(application)
    private val getLessonsItemUseCase = GetLessonsItemUseCase(repository)
    private val addLessonsItemUseCase = AddLessonsItemUseCase(repository)
    private val editLessonsItemUseCase = EditLessonsItemUseCase(repository)
    private val deleteLessonsItemUseCase = DeleteLessonsItemUseCase(repository)
    private val _lessonsItem = MutableLiveData<LessonsItem>()
    val lessonsItem: LiveData<LessonsItem>
        get() = _lessonsItem

    private val _lessonsItemMain = LessonsItem
    val lessonsItemMain: LessonsItem.Companion
        get() = _lessonsItemMain

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
        val fieldsValid = validateInput(title, student, price, dateStart, dateEnd)

        if(fieldsValid) {
            viewModelScope.launch {
                val lessonsItem = LessonsItem(title, description, student, price.toInt(), dateStart, dateEnd)
                addLessonsItemUseCase.addLessonsItem(lessonsItem)
                finishWork()
            }
        } else {
            Log.d("errorinput", "error in edit lessons")
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
        val fieldsValid = validateInput(title, student, price, dateStart, dateEnd)
        if (fieldsValid) {
            _lessonsItem.value?.let {
                viewModelScope.launch {
                    val lessonsItem = it.copy(title = title, description = description, student = student, price = price.toInt(), dateStart = dateStart, dateEnd = dateEnd)
                    editLessonsItemUseCase.editLessonsItem(lessonsItem)
                    finishWork()
                }
            }
        } else {
            Log.d("errorinput", "error in edit lessons")
        }

    }


    private fun validateInput(title: String, student: String, price: String, dateStart: String, dateEnd: String): Boolean {
        var result = true
        if (title.isBlank()) {
            //_errorInputName.value = true
            result = false
        }
        if (student.isBlank()) {
            //_errorInputLastName.value = true
            result = false
        }
        if (price.isBlank()) {
            //_errorInputLastName.value = true
            result = false
        }
        if (dateStart.isBlank()) {
            //_errorInputLastName.value = true
            result = false
        }
        if (dateEnd.isBlank()) {
            //_errorInputLastName.value = true
            result = false
        }
        return result
    }

    fun deleteLessonsItem(lessonsItem: LessonsItem) {
        viewModelScope.launch {
            deleteLessonsItemUseCase.deleteLessonsItem(lessonsItem)
        }
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}
