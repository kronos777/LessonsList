package com.leslist.lessonslist.domain.sale

import androidx.lifecycle.LiveData

class GetSaleListItemUseCase(private val salesListRepository: SalesListRepository) {
    fun getSalesList(): LiveData<List<SaleItem>> {
        return salesListRepository.getListSaleItems()
    }
}
