package com.leslist.lessonslist.domain.parent

class AddParentItemUseCase(private val parentListRepository: ParentListRepository) {
    suspend fun  addParentContact(parentContact: ParentContact) {
        parentListRepository.addParentContact(parentContact)
    }
}
