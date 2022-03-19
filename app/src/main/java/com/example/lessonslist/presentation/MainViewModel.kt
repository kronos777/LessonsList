package com.example.lessonslist.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.buylistapplication.domain.StudentItem
import com.example.lessonslist.data.student.StudentListRepositoryImpl
import com.example.lessonslist.domain.student.DeleteStudentItemUseCase
import com.example.lessonslist.domain.student.EditStudentItemUseCase
import com.example.lessonslist.domain.student.GetStudentItemListUseCase
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentListRepositoryImpl(application)

    private val getStudentListUseCase = GetStudentItemListUseCase(repository)
    private val deleteStudentItemUseCase = DeleteStudentItemUseCase(repository)
    private val editStudentItemUseCase = EditStudentItemUseCase(repository)

    val studentList = getStudentListUseCase.getStudentList()

    fun deleteStudentItem(shopItem: StudentItem) {
        viewModelScope.launch {
            deleteStudentItemUseCase.deleteStudentItem(studentItem)
        }
    }

    fun changeEnableState(studentItem: StudentItem) {
        viewModelScope.launch {
            val newItem = studentItem.copy(enabled = !studentItem.enabled)
            editStudentItemUseCase.editStudentItem(newItem)
        }
    }
}
