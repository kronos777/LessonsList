package com.listlessons.lessonslist.domain.parent

class GetParentContactUseCase(private val parentListRepository: ParentListRepository) {

    suspend fun getParentContact(parentContactId: Int) : ParentContact {
        return parentListRepository.getParentContact(parentContactId)
    }

}
