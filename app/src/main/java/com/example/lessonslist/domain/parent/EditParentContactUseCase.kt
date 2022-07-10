package com.example.lessonslist.domain.parent

class EditParentContactUseCase(private val parentListRepository: ParentListRepository) {
    suspend fun editParentContact(parentContact: ParentContact) {
        parentListRepository.editParentContact(parentContact)
    }
}
