package com.example.lessonslist.domain.lessons

class DeleteLessonsItemUseCase(private val lessonsListRepository: LessonsListRepository) {
    suspend fun deleteLessonsItem(lessonsItem: LessonsItem) {
        lessonsListRepository.deleteLessonsItem(lessonsItem)
    }
}
