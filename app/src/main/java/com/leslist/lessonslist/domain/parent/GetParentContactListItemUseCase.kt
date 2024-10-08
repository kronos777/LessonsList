package com.leslist.lessonslist.domain.parent

import androidx.lifecycle.LiveData

class GetParentContactListItemUseCase(private val parentListRepository: ParentListRepository) {
    fun getParentList(): LiveData<List<ParentContact>> {
        return parentListRepository.getParentList()
    }
}
