package com.leslist.lessonslist.presentation.group

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.leslist.lessonslist.data.group.GroupListRepositoryImpl
import com.leslist.lessonslist.domain.group.DeleteGroupItemUseCase
import com.leslist.lessonslist.domain.group.GetGroupItemUseCase
import com.leslist.lessonslist.domain.group.GetGroupListItemUseCase
import com.leslist.lessonslist.domain.group.GroupItem
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