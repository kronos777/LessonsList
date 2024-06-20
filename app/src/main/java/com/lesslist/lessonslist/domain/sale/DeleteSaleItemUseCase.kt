package com.lesslist.lessonslist.domain.sale

class DeleteSaleItemUseCase(private val salesListRepository: SalesListRepository) {
    suspend fun deleteSaleItem(saleItem: SaleItem) {
        salesListRepository.deleteSaleItem(saleItem)
    }
}
