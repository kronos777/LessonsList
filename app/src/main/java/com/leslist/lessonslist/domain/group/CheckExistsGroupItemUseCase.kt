package com.leslist.lessonslist.domain.group

class CheckExistsGroupItemUseCase(private val groupListRepository: GroupListRepository) {

    suspend fun checkExistsGroupItem(groupName: String): GroupItem? {
        return groupListRepository.checkExistsGroupItem(groupName)
    }

}
