package com.example.lessonslist.domain.date

import androidx.lifecycle.LiveData

class GetDateListItemUseCase(private val dateListRepository: DateListRepository) {
    fun getDateList(): LiveData<List<DateItem>> {
        return dateListRepository.getDateList()
    }
}
