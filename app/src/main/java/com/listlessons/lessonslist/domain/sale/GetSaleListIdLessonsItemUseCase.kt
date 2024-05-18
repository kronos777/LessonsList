package com.listlessons.lessonslist.domain.sale

import androidx.lifecycle.LiveData

class GetSaleListIdLessonsItemUseCase(private val salesListRepository: SalesListRepository) {
    fun getSalesListIdLessons(idLessons: Int): LiveData<List<SaleItem>> {
        return salesListRepository.getSalesSalesIdLessons(idLessons)
    }
}
