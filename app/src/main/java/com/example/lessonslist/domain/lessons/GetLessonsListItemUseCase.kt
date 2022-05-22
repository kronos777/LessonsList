package com.example.lessonslist.domain.lessons

import androidx.lifecycle.LiveData
import com.example.lessonslist.data.lessons.LessonsItemDbModel

class GetLessonsListItemUseCase(private val lessonsListRepository: LessonsListRepository) {
    fun getLessonsList(): LiveData<List<LessonsItem>> {
        return lessonsListRepository.getLessonsList()
    }

}
