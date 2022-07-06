package com.example.lessonslist.domain.notes

import androidx.lifecycle.LiveData

class GetNotesListItemUseCase(private val groupListRepository: GroupListRepository) {
    fun getGroupList(): LiveData<List<GroupItem>> {
        return groupListRepository.getGroupList()
    }
}
