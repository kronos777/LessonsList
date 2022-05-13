package com.example.lessonslist.presentation.lessons

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.group.GroupListRepositoryImpl
import com.example.lessonslist.data.lessons.LessonsListRepositoryImpl
import com.example.lessonslist.domain.group.*
import com.example.lessonslist.domain.lessons.DeleteLessonsItemUseCase
import com.example.lessonslist.domain.lessons.EditLessonsItemUseCase
import com.example.lessonslist.domain.lessons.GetLessonsListItemUseCase
import com.example.lessonslist.domain.lessons.LessonsItem
import kotlinx.coroutines.launch

class LessonsListViewModel(application: Application) : AndroidViewModel(application),
    LifecycleOwner {

    private val repository = LessonsListRepositoryImpl(application)

    private val getLessonsItemListUseCase = GetLessonsListItemUseCase(repository)
    private val deleteLessonsItemUseCase = DeleteLessonsItemUseCase(repository)
    private val editLessonsItemUseCase = EditLessonsItemUseCase(repository)

    val lessonsList = getLessonsItemListUseCase.getLessonsList()
    fun deleteLessonsItem(lessonsItem: LessonsItem) {
        viewModelScope.launch {
            deleteLessonsItemUseCase.deleteLessonsItem(lessonsItem)
        }
    }

    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }

}