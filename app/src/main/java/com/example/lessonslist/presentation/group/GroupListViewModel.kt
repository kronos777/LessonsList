package com.example.lessonslist.presentation.group

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.group.GroupListRepositoryImpl
import com.example.lessonslist.domain.group.DeleteGroupItemUseCase
import com.example.lessonslist.domain.group.EditGroupItemUseCase
import com.example.lessonslist.domain.group.GetGroupItemUseCase
import com.example.lessonslist.domain.group.GetGroupListItemUseCase
import com.example.lessonslist.domain.group.GroupItem
import kotlinx.coroutines.launch

class GroupListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GroupListRepositoryImpl(application)
    private val getGroupItemUseCase = GetGroupItemUseCase(repository)
    private val getGroupItemListUseCase = GetGroupListItemUseCase(repository)
    private val deleteGroupItemUseCase = DeleteGroupItemUseCase(repository)
    private val editGroupItemUseCase = EditGroupItemUseCase(repository)

    val groupList = getGroupItemListUseCase.getGroupList()

    private val _groupItem = MutableLiveData<GroupItem>()
    val groupItem: LiveData<GroupItem>
        get() = _groupItem

    fun deleteGroupItem(groupItem: GroupItem) {
        viewModelScope.launch {
            deleteGroupItemUseCase.deleteGroupItem(groupItem)
        }
    }
    fun deleteGroupItemId(groupItemId: Int) {
        viewModelScope.launch {
            val item = getGroupItemUseCase.getGroupItem(groupItemId)
            deleteGroupItemUseCase.deleteGroupItem(item)
        }
    }


}