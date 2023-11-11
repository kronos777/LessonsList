package com.example.lessonslist.presentation.group

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.group.GroupListRepositoryImpl
import com.example.lessonslist.domain.group.*
import kotlinx.coroutines.launch

class GroupItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GroupListRepositoryImpl(application)
    private val getGroupItemUseCase = GetGroupItemUseCase(repository)
    private val checkExistsGroupItemUseCase = CheckExistsGroupItemUseCase(repository)
    private val addGroupItemUseCase = AddGroupItemUseCase(repository)
    private val editGroupItemUseCase = EditGroupItemUseCase(repository)
    private val deleteGroupItemUseCase = DeleteGroupItemUseCase(repository)

    private val _groupItem = MutableLiveData<GroupItem>()
    val groupItem: LiveData<GroupItem>
        get() = _groupItem

    private val _errorInputTitle = MutableLiveData<Boolean>()
    val errorInputTitle: LiveData<Boolean>
        get() = _errorInputTitle

    private val _errorInputDescription = MutableLiveData<Boolean>()
    val errorInputDescription: LiveData<Boolean>
        get() = _errorInputDescription

    private val _errorInputStudent = MutableLiveData<Boolean>()
    val errorInputStudent: LiveData<Boolean>
        get() = _errorInputStudent

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen


    private val _checkExistsgroupItem = MutableLiveData<GroupItem?>()
    val checkExistsGroupItem: LiveData<GroupItem?>
        get() = _checkExistsgroupItem

    fun checkExistsGroupItem(groupName: String) {
        viewModelScope.launch {
            val item = checkExistsGroupItemUseCase.checkExistsGroupItem(groupName)
            if(item != null) {
                _checkExistsgroupItem.value = item
            } else {
                _checkExistsgroupItem.value = null
            }

        }
    }

    fun getGroupItem(groupItemId: Int) {
        viewModelScope.launch {
            val item = getGroupItemUseCase.getGroupItem(groupItemId)

            _groupItem.value = item
        }
    }


    fun addGroupItem(inputTitle: String, inputDescription: String, inputStudent: String) {

        viewModelScope.launch {
                val groupItem = GroupItem(inputTitle, inputDescription, inputStudent)
                addGroupItemUseCase.addGroupItem(groupItem)
                finishWork()
            }

    }

    fun editGroupItem(inputTitle: String, inputDescription: String, inputStudent: String) {

        _groupItem.value?.let {
                viewModelScope.launch {
                 //   val groupItem = GroupItem(title, description, student)
                    val groupItem = it.copy(
                        title = inputTitle,
                        description = inputDescription,
                        student = inputStudent
                    )
                    editGroupItemUseCase.editGroupItem(groupItem)
                    finishWork()
                }
        }


    }

    fun validateInput(title: String): Boolean {
        var result = true
        if (title.isBlank()) {
            _errorInputTitle.value = true
            result = false
        }

        return result
    }

    fun resetErrorInputTitle() {
        _errorInputTitle.value = false
    }

    fun deleteGroupItem(groupItem: GroupItem) {
        viewModelScope.launch {
            deleteGroupItemUseCase.deleteGroupItem(groupItem)
        }
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}
