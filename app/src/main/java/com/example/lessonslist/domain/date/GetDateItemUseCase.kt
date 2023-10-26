package com.example.lessonslist.domain.date

class GetDateItemUseCase(private val dateListRepository: DateListRepository) {

    suspend fun getDateItem(dateItemId: Int) : DateItem {
        return dateListRepository.getDateItem(dateItemId)
    }

}
