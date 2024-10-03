package com.llist.lessonslist.domain.lessons

import androidx.lifecycle.LiveData

class GetLessonsListItemUseCase(private val lessonsListRepository: LessonsListRepository) {
    fun getLessonsList(): LiveData<List<LessonsItem>> {
        return lessonsListRepository.getLessonsList()
    }

}
