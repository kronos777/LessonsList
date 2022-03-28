package com.example.lessonslist.domain.lessons

import androidx.lifecycle.LiveData

interface LessonsListRepository {

    suspend fun addLessonsItem(lessonsItem: LessonsItem)

    suspend fun deleteLessonsItem(lessonsItem: LessonsItem)

    suspend fun editLessonsItem(lessonsItem: LessonsItem)

    suspend fun getLessonsItem(lessonsItemId: Int): LessonsItem

    fun getLessonsList(): LiveData<List<LessonsItem>>


}

