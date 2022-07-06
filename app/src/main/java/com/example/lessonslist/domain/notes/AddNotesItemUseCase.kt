package com.example.lessonslist.domain.notes

class AddNotesItemUseCase(private val notesListRepository: NotesListRepository) {
    suspend fun  addParentContact(notesItem: NotesItem) {
        notesListRepository.addParentContact(notesItem)
    }
}
