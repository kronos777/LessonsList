package com.listlessons.lessonslist.domain.sale

import androidx.lifecycle.LiveData

interface SalesListRepository {
    suspend fun addSaleItem(saleItem: SaleItem)

    suspend fun deleteSaleItem(saleItem: SaleItem)

    suspend fun editSaleItem(saleItem: SaleItem)

    suspend fun getSaleItem(saleItemId: Int): SaleItem

    fun getListSaleItems(): LiveData<List<SaleItem>>

    fun getSalesSalesIdLessons(idLessons: Int): LiveData<List<SaleItem>>
}
