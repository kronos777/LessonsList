package com.example.lessonslist.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.student.StudentListRepositoryImpl
import com.example.lessonslist.domain.student.DeleteStudentItemUseCase
import com.example.lessonslist.domain.student.GetStudentItemListUseCase
import com.example.lessonslist.domain.student.StudentItem
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentListRepositoryImpl(application)

    private val getStudentListUseCase = GetStudentItemListUseCase(repository)
    private val deleteStudentItemUseCase = DeleteStudentItemUseCase(repository)

    val studentList = getStudentListUseCase.getStudentList()

    fun deleteStudentItem(studentItem: StudentItem) {
        viewModelScope.launch {
            deleteStudentItemUseCase.deleteStudentItem(studentItem)
        }
    }
}
