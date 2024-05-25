package com.example.lessonslist.domain.group

class AddGroupItemUseCase(private val groupListRepository: GroupListRepository) {
    suspend fun  addGroupItem(groupItem: GroupItem) {
        groupListRepository.addGroupItem(groupItem)
    }
}
