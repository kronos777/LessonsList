package com.lesslist.lessonslist.domain.notes

class GetNotesUseCase(private val notesListRepository: NotesListRepository) {

    suspend fun getNotesItem(notesItemId: Int) : NotesItem {
        return notesListRepository.getNotesItem(notesItemId)
    }

}
