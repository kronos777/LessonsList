package com.example.lessonslist.domain.notes

class DeleteNotesItemUseCase(private val notesListRepository: NotesListRepository) {
    suspend fun deleteParent(notesItem: NotesItem) {
        notesListRepository.deleteParentContact(notesItem)
    }
}
