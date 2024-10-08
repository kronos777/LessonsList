package com.leslist.lessonslist.data.date
import com.leslist.lessonslist.domain.date.DateItem


class DateListMapper {
    fun mapEntityToDbModel(dateItem: DateItem) = DateItemDbModel(
        date = dateItem.date,
        id = dateItem.id
    )

    fun mapDbModelToEntity(dateItemDbModel: DateItemDbModel) = DateItem(
        date = dateItemDbModel.date,
        id = dateItemDbModel.id

    )

    fun mapListDbModelToListEntity(list: List<DateItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}