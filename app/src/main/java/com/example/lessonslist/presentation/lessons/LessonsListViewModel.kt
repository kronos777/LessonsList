package com.example.lessonslist.presentation.lessons

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.lessons.LessonsListRepositoryImpl
import com.example.lessonslist.domain.lessons.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LessonsListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LessonsListRepositoryImpl(application)

    private val getLessonsItemUseCase = GetLessonsItemUseCase(repository)
    private val getLessonsItemListUseCase = GetLessonsListItemUseCase(repository)
    private val deleteLessonsItemUseCase = DeleteLessonsItemUseCase(repository)

    val lessonsList = getLessonsItemListUseCase.getLessonsList()

    private val _lessonsItem = MutableLiveData<LessonsItem>()
    val lessonsItem: LiveData<LessonsItem>
        get() = _lessonsItem


    fun deleteLessonsItem(lessonsItem: LessonsItem) {
        viewModelScope.launch {
            deleteLessonsItemUseCase.deleteLessonsItem(lessonsItem)
        }
    }


    fun getLessonsItem(lessonsItemId: Int) {
        viewModelScope.launch {
            val item = getLessonsItemUseCase.getLessonsItem(lessonsItemId)
            _lessonsItem.value = item
        }
    }


}
