package com.example.lessonslist.data.date
import com.example.lessonslist.domain.date.DateItem
import com.example.lessonslist.domain.group.GroupItem


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