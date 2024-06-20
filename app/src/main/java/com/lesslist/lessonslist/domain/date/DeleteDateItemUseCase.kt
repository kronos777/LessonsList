package com.lesslist.lessonslist.domain.date

class DeleteDateItemUseCase(private val dateListRepository: DateListRepository) {
    suspend fun deleteDateItem(dateItem: DateItem) {
        dateListRepository.deleteDateItem(dateItem)
    }
}
