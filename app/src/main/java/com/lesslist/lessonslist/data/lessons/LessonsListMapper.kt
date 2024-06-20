package com.lesslist.lessonslist.data.lessons

import com.lesslist.lessonslist.domain.lessons.LessonsItem

class LessonsListMapper {

    fun mapEntityToDbModel(lessonsItem: LessonsItem) = LessonsItemDbModel (
        title = lessonsItem.title,
        notifications = lessonsItem.notifications,
        student = lessonsItem.student,
        price = lessonsItem.price,
        dateStart  = lessonsItem.dateStart,
        dateEnd = lessonsItem.dateEnd,
        id = lessonsItem.id
    )
    fun mapDbModelToEntity(lessonsItemDbModel: LessonsItemDbModel) = LessonsItem(
        title = lessonsItemDbModel.title,
        notifications = lessonsItemDbModel.notifications,
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