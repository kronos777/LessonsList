package com.lesslist.lessonslist.domain.notes

import androidx.lifecycle.LiveData


interface NotesListRepository {
    suspend fun addNotesItem(notesItem: NotesItem)

    suspend fun deleteNotesItem(notesItem: NotesItem)

    suspend fun editNotesItem(notesItem: NotesItem)

    suspend fun getNotesItem(parentContactId: Int): NotesItem

    fun getNotesList(): LiveData<List<NotesItem>>
}