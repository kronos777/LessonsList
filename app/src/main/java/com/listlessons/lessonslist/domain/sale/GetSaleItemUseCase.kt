package com.listlessons.lessonslist.domain.sale

class GetSaleItemUseCase(private val salesListRepository: SalesListRepository) {
     suspend fun getSaleItem(saleItemId: Int): SaleItem {
         return salesListRepository.getSaleItem(saleItemId)
     }
}
