package com.lesslist.lessonslist.domain.group

class GetGroupItemUseCase(private val groupListRepository: GroupListRepository) {

    suspend fun getGroupItem(groupItemId: Int) : GroupItem {
        return groupListRepository.getGroupItem(groupItemId)
    }

}
