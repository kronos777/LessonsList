package com.example.lessonslist.presentation.lessons

import android.app.Application
import androidx.lifecycle.*
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.data.lessons.LessonsListDao
import com.example.lessonslist.data.lessons.LessonsListRepositoryImpl
import com.example.lessonslist.domain.group.*
import com.example.lessonslist.domain.lessons.*
import kotlinx.coroutines.launch
import java.util.stream.Collectors


class LessonsListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LessonsListRepositoryImpl(application)

    private val getLessonsItemUseCase = GetLessonsItemUseCase(repository)
    private val getLessonsItemListUseCase = GetLessonsListItemUseCase(repository)
    private val getLessonsListItemDateUseCase = GetLessonsListItemDateUseCase(repository)
    private val deleteLessonsItemUseCase = DeleteLessonsItemUseCase(repository)
    private val editLessonsItemUseCase = EditLessonsItemUseCase(repository)

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
