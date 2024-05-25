package com.example.lessonslist.domain.group

import androidx.lifecycle.LiveData


interface GroupListRepository {
    suspend fun addGroupItem(groupItem: GroupItem)

    suspend fun deleteGroupItem(groupItem: GroupItem)

    suspend fun editGroupItem(groupItem: GroupItem)

    suspend fun getGroupItem(groupItemId: Int): GroupItem
    suspend fun checkExistsGroupItem(groupName: String): GroupItem?

    fun getGroupList(): LiveData<List<GroupItem>>
}