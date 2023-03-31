package com.example.lessonslist.domain.lessons

import androidx.lifecycle.LiveData

class GetLessonsListItemDateUseCase(private val lessonsListRepository: LessonsListRepository) {
    fun getLessonsListDate(date: String): LiveData<List<LessonsItem>> {
        return lessonsListRepository.getLessonsListDate(date)
    }

}
