package com.example.lessonslist.domain.sale

class EditSaleItemUseCase(private val salesListRepository: SalesListRepository) {
    suspend fun editSaleItem(saleItem: SaleItem) {
        salesListRepository.editSaleItem(saleItem)
    }
}
