package com.example.lessonslist.domain.notes

class DeleteNotesItemUseCase(private val notesListRepository: NotesListRepository) {
    suspend fun deleteNotesItem(notesItem: NotesItem) {
        notesListRepository.deleteNotesItem(notesItem)
    }
}
