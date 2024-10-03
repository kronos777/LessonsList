package com.llist.lessonslist.data.parent
import com.llist.lessonslist.domain.parent.ParentContact


class ParentListMapper {
    fun mapEntityToDbModel(parentContact: ParentContact) = ParentItemDbModel(
        name = parentContact.name,
        number = parentContact.number,
        student = parentContact.student,
        id = parentContact.id
    )

    fun mapDbModelToEntity(parentItemDbModel: ParentItemDbModel) = ParentContact(
        name = parentItemDbModel.name,
        number = parentItemDbModel.number,
        student = parentItemDbModel.student,
        id = parentItemDbModel.id

    )

    fun mapListDbModelToListEntity(list: List<ParentItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}