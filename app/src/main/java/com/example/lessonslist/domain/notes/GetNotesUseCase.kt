package com.example.lessonslist.domain.notes

class GetNotesUseCase(private val notesListRepository: NotesListRepository) {

    suspend fun getParentContact(parentContactId: Int) : NotesItem {
        return notesListRepository.getParentContact(parentContactId)
    }

}
