package com.example.lessonslist.domain.notes

class EditNotesUseCase(private val notesListRepository: NotesListRepository) {
    suspend fun editNotesItem(notesItem: NotesItem) {
        notesListRepository.editNotesItem(notesItem)
    }
}
