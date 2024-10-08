package com.leslist.lessonslist.domain.group

class DeleteGroupItemUseCase(private val groupListRepository: GroupListRepository) {
    suspend fun deleteGroupItem(groupItem: GroupItem) {
        groupListRepository.deleteGroupItem(groupItem)
    }
}
