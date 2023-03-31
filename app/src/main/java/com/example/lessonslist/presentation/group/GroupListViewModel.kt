package com.example.lessonslist.presentation.group

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.group.GroupListRepositoryImpl
import com.example.lessonslist.domain.group.DeleteGroupItemUseCase
import com.example.lessonslist.domain.group.EditGroupItemUseCase
import com.example.lessonslist.domain.group.GetGroupListItemUseCase
import com.example.lessonslist.domain.group.GroupItem
import kotlinx.coroutines.launch

class GroupListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GroupListRepositoryImpl(application)

    private val getGroupItemListUseCase = GetGroupListItemUseCase(repository)
    private val deleteGroupItemUseCase = DeleteGroupItemUseCase(repository)
    private val editGroupItemUseCase = EditGroupItemUseCase(repository)

    val groupList = getGroupItemListUseCase.getGroupList()
    fun deleteGroupItem(groupItem: GroupItem) {
        viewModelScope.launch {
            deleteGroupItemUseCase.deleteGroupItem(groupItem)
        }
    }

}