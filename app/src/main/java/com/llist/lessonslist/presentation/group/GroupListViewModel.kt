package com.llist.lessonslist.presentation.group

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.llist.lessonslist.data.group.GroupListRepositoryImpl
import com.llist.lessonslist.domain.group.DeleteGroupItemUseCase
import com.llist.lessonslist.domain.group.GetGroupItemUseCase
import com.llist.lessonslist.domain.group.GetGroupListItemUseCase
import com.llist.lessonslist.domain.group.GroupItem
import kotlinx.coroutines.launch

class GroupListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GroupListRepositoryImpl(application)
    private val getGroupItemUseCase = GetGroupItemUseCase(repository)
    private val getGroupItemListUseCase = GetGroupListItemUseCase(repository)
    private val deleteGroupItemUseCase = DeleteGroupItemUseCase(repository)

    val groupList = getGroupItemListUseCase.getGroupList()

    private val _groupItem = MutableLiveData<GroupItem>()
    val groupItem: LiveData<GroupItem>
        get() = _groupItem

    fun deleteGroupItemId(groupItemId: Int) {
        viewModelScope.launch {
            val item = getGroupItemUseCase.getGroupItem(groupItemId)
            deleteGroupItemUseCase.deleteGroupItem(item)
        }
    }


}