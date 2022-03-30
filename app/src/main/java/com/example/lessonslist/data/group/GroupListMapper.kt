package com.example.lessonslist.data.group

import com.example.lessonslist.domain.group.GroupItem


class GroupListMapper {
    fun mapEntityToDbModel(groupItem: GroupItem) = GroupItemDbModel(
        title = groupItem.title,
        description = groupItem.description,
        student = groupItem.student,
        id = groupItem.id
    )

    fun mapDbModelToEntity(groupItemDbModel: GroupItemDbModel) = GroupItem(
        title = groupItemDbModel.title,
        description = groupItemDbModel.description,
        student = groupItemDbModel.student,
        id = groupItemDbModel.id

    )

    fun mapListDbModelToListEntity(list: List<GroupItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}