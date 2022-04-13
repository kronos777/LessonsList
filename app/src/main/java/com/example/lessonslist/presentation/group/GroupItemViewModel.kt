package com.example.lessonslist.presentation.group

import android.app.Application
import android.icu.text.CaseMap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.group.GroupListRepositoryImpl
import com.example.lessonslist.domain.group.*
import com.example.lessonslist.domain.student.StudentItem
import kotlinx.coroutines.launch

class GroupItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GroupListRepositoryImpl(application)
    private val getGroupItemUseCase = GetGroupItemUseCase(repository)
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


    fun getGroupItem(groupItemId: Int) {
        viewModelScope.launch {
            val item = getGroupItemUseCase.getGroupItem(groupItemId)

            _groupItem.value = item
        }
    }


    fun addGroupItem(inputTitle: String, inputDescription: String, inputStudent: String) {
        val title = inputTitle
        val description = inputDescription
        val student = inputStudent

        // add validation fun
        val fieldsValid = validateInput(title, student)

        if(fieldsValid) {
            viewModelScope.launch {
                val groupItem = GroupItem(title, description, student)
                addGroupItemUseCase.addGroupItem(groupItem)
                finishWork()
            }
        } else {
            Log.d("errorinput", "error in add group")
        }

    }

    fun editGroupItem(inputTitle: String, inputDescription: String, inputStudent: String) {
        val title = inputTitle
        val description = inputDescription
        val student = inputStudent

        // add validation fun
        val fieldsValid = validateInput(title, student)
        if (fieldsValid) {
            _groupItem.value?.let {
                viewModelScope.launch {
                 //   val groupItem = GroupItem(title, description, student)
                    val groupItem = it.copy(title = title, description = description, student = student)
                    editGroupItemUseCase.editGroupItem(groupItem)
                    finishWork()
                }
            }
        } else {
            Log.d("errorinput", "error in edit group")
         }

    }

    private fun validateInput(title: String, inputStudent: String): Boolean {
        var result = true
        if (title.isBlank()) {
            //_errorInputName.value = true
            result = false
        }
        if (inputStudent.isBlank()) {
            //_errorInputLastName.value = true
            result = false
        }

        return result
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
