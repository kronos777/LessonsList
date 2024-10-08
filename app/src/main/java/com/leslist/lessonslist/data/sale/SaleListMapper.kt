package com.leslist.lessonslist.data.sale

import com.leslist.lessonslist.domain.sale.SaleItem

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
