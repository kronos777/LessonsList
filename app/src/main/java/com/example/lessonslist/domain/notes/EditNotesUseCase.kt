package com.example.lessonslist.domain.notes

class EditNotesUseCase(private val notesListRepository: NotesListRepository) {
    suspend fun editParentContact(notesItem: NotesItem) {
        notesListRepository.editParentContact(notesItem)
    }
}
