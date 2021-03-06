package com.example.lessonslist.domain.parent

class DeleteParentItemUseCase(private val parentListRepository: ParentListRepository) {
    suspend fun deleteParent(parentContact: ParentContact) {
        parentListRepository.deleteParentContact(parentContact)
    }
}
