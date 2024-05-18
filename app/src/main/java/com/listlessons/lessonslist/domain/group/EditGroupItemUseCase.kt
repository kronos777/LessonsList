package com.listlessons.lessonslist.domain.group

class EditGroupItemUseCase(private val groupListRepository: GroupListRepository) {
    suspend fun editGroupItem(groupItem: GroupItem) {
        groupListRepository.editGroupItem(groupItem)
    }
}
