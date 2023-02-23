package com.example.lessonslist.domain.sale

import com.example.lessonslist.domain.notes.NotesItem
import com.example.lessonslist.domain.notes.NotesListRepository

class GetSaleItemUseCase(private val salesListRepository: SalesListRepository) {
     suspend fun getSaleItem(saleItemId: Int): SaleItem {
         return salesListRepository.getSaleItem(saleItemId)
     }
}
