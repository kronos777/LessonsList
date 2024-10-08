package com.leslist.lessonslist.data.notes

import com.leslist.lessonslist.domain.notes.NotesItem

class NotesListMapper {
    fun mapEntityToDbModel(notesItem: NotesItem) = NotesItemDbModel(
        text = notesItem.text,
        date = notesItem.date,
        student = notesItem.student,
        id = notesItem.id
    )

    fun mapDbModelToEntity(notesItemDbModel: NotesItemDbModel) = NotesItem(
        text = notesItemDbModel.text,
        date = notesItemDbModel.date,
        student = notesItemDbModel.student,
        id = notesItemDbModel.id

    )

    fun mapListDbModelToListEntity(list: List<NotesItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}