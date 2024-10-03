package com.llist.lessonslist.domain.sale

class AddSaleItemUseCase(private val salesListRepository: SalesListRepository) {
    suspend fun addSaleItem(saleItem: SaleItem) {
        salesListRepository.addSaleItem(saleItem)
    }
}

