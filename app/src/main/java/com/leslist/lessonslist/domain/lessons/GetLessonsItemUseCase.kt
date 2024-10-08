package com.leslist.lessonslist.domain.lessons

class GetLessonsItemUseCase(private val lessonsListRepository: LessonsListRepository) {
    suspend fun getLessonsItem(lessonsItemId: Int): LessonsItem {
        return lessonsListRepository.getLessonsItem(lessonsItemId)
    }
}
