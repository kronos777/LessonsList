package com.example.lessonslist.data.lessons

import com.example.lessonslist.domain.lessons.LessonsItem

class LessonsListMapper {

    fun mapEntityToDbModel(lessonsItem: LessonsItem) = LessonsItemDbModel (
        title = lessonsItem.title,
        description = lessonsItem.description,
        student = lessonsItem.student,
        price = lessonsItem.price,
        dateStart  = lessonsItem.dateStart,
        dateEnd = lessonsItem.dateEnd,
        id = lessonsItem.id
    )
    fun mapDbModelToEntity(lessonsItemDbModel: LessonsItemDbModel) = LessonsItem(
        title = lessonsItemDbModel.title,
        description = lessonsItemDbModel.description,
        student = lessonsItemDbModel.student,
        price = lessonsItemDbModel.price,
        dateStart = lessonsItemDbModel.dateStart,
        dateEnd = lessonsItemDbModel.dateEnd,
        id = lessonsItemDbModel.id
    )

    fun mapListDbModelToListEntity(list: List<LessonsItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}


/*
*
class GroupListMapper {
    fun mapListDbModelToListEntity(list: List<GroupItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}
* */