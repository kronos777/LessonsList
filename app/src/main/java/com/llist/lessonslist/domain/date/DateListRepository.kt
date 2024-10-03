package com.llist.lessonslist.domain.date

import androidx.lifecycle.LiveData


interface DateListRepository {
    suspend fun addDateItem(dateItem: DateItem)

    suspend fun deleteDateItem(dateItem: DateItem)

    suspend fun editDateItem(dateItem: DateItem)

    suspend fun getDateItem(dateItemId: Int): DateItem
    suspend fun checkExistsDateItem(dateName: String): DateItem?

    fun getDateList(): LiveData<List<DateItem>>
}