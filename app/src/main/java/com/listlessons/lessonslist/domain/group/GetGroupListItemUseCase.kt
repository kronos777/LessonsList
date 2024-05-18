package com.listlessons.lessonslist.domain.group

import androidx.lifecycle.LiveData

class GetGroupListItemUseCase(private val groupListRepository: GroupListRepository) {
    fun getGroupList(): LiveData<List<GroupItem>> {
        return groupListRepository.getGroupList()
    }
}
