package com.leslist.lessonslist.domain.notes

class AddNotesItemUseCase(private val notesListRepository: NotesListRepository) {
    suspend fun  addNotesItem(notesItem: NotesItem) {
        notesListRepository.addNotesItem(notesItem)
    }
}
