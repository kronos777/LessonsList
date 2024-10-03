package com.llist.lessonslist.domain.date

class CheckExistsDateItemUseCase(private val dateListRepository: DateListRepository) {

    suspend fun checkExistsDateItem(date: String): DateItem? {
        return dateListRepository.checkExistsDateItem(date)
    }

}
