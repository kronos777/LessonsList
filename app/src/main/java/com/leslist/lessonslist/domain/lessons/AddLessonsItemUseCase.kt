package com.leslist.lessonslist.domain.lessons

class AddLessonsItemUseCase(private val lessonsListRepository: LessonsListRepository) {
    suspend fun addLessonsItem(lessonsItem: LessonsItem){
        lessonsListRepository.addLessonsItem(lessonsItem)
    }
}

