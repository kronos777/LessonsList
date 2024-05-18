package com.listlessons.lessonslist.domain.date

class AddDateItemUseCase(private val dateListRepository: DateListRepository) {
    suspend fun  addDateItem(dateItem: DateItem) {
        dateListRepository.addDateItem(dateItem)
    }
}
