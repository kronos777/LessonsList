package com.example.lessonslist.domain.lessons

class EditLessonsItemUseCase(private val lessonsListRepository: LessonsListRepository) {
    suspend fun editLessonsItem(lessonsItem: LessonsItem) {
        lessonsListRepository.editLessonsItem(lessonsItem)
    }
}

