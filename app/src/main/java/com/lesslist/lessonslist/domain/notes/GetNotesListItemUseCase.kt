package com.lesslist.lessonslist.domain.notes

import androidx.lifecycle.LiveData

class GetNotesListItemUseCase(private val notesListRepository: NotesListRepository) {
    fun getNotesList(): LiveData<List<NotesItem>> {
        return notesListRepository.getNotesList()
    }
}
