package com.example.lessonslist.domain.date

class EditDateItemUseCase(private val dateListRepository: DateListRepository) {
    suspend fun editDateItem(dateItem: DateItem) {
        dateListRepository.editDateItem(dateItem)
    }
}
