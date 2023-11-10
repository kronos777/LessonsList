package com.example.lessonslist.presentation.lessons

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.lessons.LessonsListRepositoryImpl
import com.example.lessonslist.domain.lessons.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.log

class LessonsItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LessonsListRepositoryImpl(application)
    private val getLessonsItemUseCase = GetLessonsItemUseCase(repository)
    private val addLessonsItemUseCase = AddLessonsItemUseCase(repository)
    private val editLessonsItemUseCase = EditLessonsItemUseCase(repository)
    private val deleteLessonsItemUseCase = DeleteLessonsItemUseCase(repository)
    private val getLessonsItemListUseCase = GetLessonsListItemUseCase(repository)

    val lessonsList = getLessonsItemListUseCase.getLessonsList()

    private val _lessonsItem = MutableLiveData<LessonsItem>()
    val lessonsItem: LiveData<LessonsItem>
        get() = _lessonsItem

    private val _lessonsItemMain = LessonsItem
    val lessonsItemMain: LessonsItem.Companion
        get() = _lessonsItemMain

    private val _errorInputTitle = MutableLiveData<Boolean>()
    val errorInputTitle: LiveData<Boolean>
        get() = _errorInputTitle

    private val _errorInputPrice = MutableLiveData<Boolean>()
    val errorInputPrice: LiveData<Boolean>
        get() = _errorInputPrice

    private val _errorInputStudent = MutableLiveData<Boolean>()
    val errorInputStudent: LiveData<Boolean>
        get() = _errorInputStudent

    private val _errorInputDateStart = MutableLiveData<Boolean>()
    val errorInputDateStart: LiveData<Boolean>
        get() = _errorInputDateStart

    private val _errorInputDateEnd = MutableLiveData<Boolean>()
    val errorInputDateEnd: LiveData<Boolean>
        get() = _errorInputDateEnd


    private val _errorInputRepeat = MutableLiveData<Boolean>()
    val errorInputRepeat: LiveData<Boolean>
        get() = _errorInputRepeat

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen


    fun getLessonsItem(lessonsItemId: Int) {
        viewModelScope.launch {
            val item = getLessonsItemUseCase.getLessonsItem(lessonsItemId)
            _lessonsItem.value = item
        }
    }

    fun addLessonsItemTemp(lessonsItem: LessonsItem){
        viewModelScope.launch {
            addLessonsItemUseCase.addLessonsItem(lessonsItem)
            finishWork()
        }
    }

    fun addLessonsItem(inputTitle: String, inputNotifications: String, inputStudent: String, inputPrice: String, inputDateStart: String, inputDateEnd: String) {
        val title = inputTitle
        val notifications = inputNotifications
        val student = inputStudent
        val price = inputPrice
        val dateStart = inputDateStart
        val dateEnd = inputDateEnd

            viewModelScope.launch {
                val lessonsItem = LessonsItem(title, notifications, student, price.toInt(), dateStart, dateEnd)
                //Log.d("viewModelLessonItem", lessonsItem.toString())
                addLessonsItemUseCase.addLessonsItem(lessonsItem)
                finishWork()
            }


    }

    fun editLessonsItem(inputTitle: String, inputNotifications: String, inputStudent: String, inputPrice: String, inputDateStart: String, inputDateEnd: String) {
    val title = inputTitle
    val notifications = inputNotifications
    val student = inputStudent
    val price = inputPrice
    val dateStart = inputDateStart
    val dateEnd = inputDateEnd
        // add validation fun
            _lessonsItem.value?.let {
                viewModelScope.launch {
                    val lessonsItem = it.copy(title = title, notifications = notifications, student = student, price = price.toInt(), dateStart = dateStart, dateEnd = dateEnd)
                    editLessonsItemUseCase.editLessonsItem(lessonsItem)
                    finishWork()
                }
            }

    }

    fun editLessonsItemNotfication(idLessons: Int, inputNotifications: String) {
        val notifications = inputNotifications
        getLessonsItem(idLessons)
        _lessonsItem.value?.let {
            viewModelScope.launch {
                val lessonsItem = it.copy(notifications = notifications)
                editLessonsItemUseCase.editLessonsItem(lessonsItem)
                //finishWork()
                Log.d("editLessonsItemNotfication", idLessons.toString())
            }
        }

    }


    fun validateInput(title: String, student: HashSet<Int>, price: String, dateStart: String, dateEnd: String): Boolean {
        var result = true
        if (title.isBlank()) {
            _errorInputTitle.value = true
            result = false
        }
        if (student.isEmpty()) {
            _errorInputStudent.value = true
            result = false
        }
        if (price.isBlank()) {
            _errorInputPrice.value = true
            result = false
        }
        if (dateStart.isBlank()) {
            _errorInputDateStart.value = true
            result = false
        }
        if (dateEnd.isBlank()) {
            _errorInputDateEnd.value = true
            result = false
        }
        return result
    }

    fun resetErrorInputTitle() {
        _errorInputTitle.value = false
    }

    fun resetErrorInputPrice() {
    _errorInputPrice.value = false
    }

    fun resetErrorInputStudent() {
        _errorInputStudent.value = false
    }

    fun resetErrorInputDateStart() {
        _errorInputDateStart.value = false
    }

    fun resetErrorInputDateEnd() {
        _errorInputDateEnd.value = false
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
