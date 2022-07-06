package com.example.lessonslist.domain.parent

import androidx.lifecycle.LiveData

class GetParentContactListItemUseCase(private val groupListRepository: GroupListRepository) {
    fun getGroupList(): LiveData<List<GroupItem>> {
        return groupListRepository.getGroupList()
    }
}
