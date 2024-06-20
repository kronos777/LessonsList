package com.lesslist.lessonslist.data.sale

import com.lesslist.lessonslist.domain.sale.SaleItem

class SaleListMapper {
    fun mapEntityToDbModel(saleItem: SaleItem) = SaleItemDbModel(
        idStudent = saleItem.idStudent,
        idLessons = saleItem.idLessons,
        price = saleItem.price,
        id = saleItem.id
    )
    fun mapDbModelToEntity(saleItemDbModel: SaleItemDbModel) = SaleItem(
        idStudent = saleItemDbModel.idStudent,
        idLessons = saleItemDbModel.idLessons,
        price = saleItemDbModel.price,
        id = saleItemDbModel.id
    )

    fun mapListDbModelToListEntity(list: List<SaleItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}
