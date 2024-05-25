package com.example.lessonslist.presentation.student

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.student.StudentListRepositoryImpl
import com.example.lessonslist.domain.student.DeleteStudentItemUseCase
import com.example.lessonslist.domain.student.GetStudentItemListUseCase
import com.example.lessonslist.domain.student.GetStudentItemUseCase
import com.example.lessonslist.domain.student.StudentItem
import kotlinx.coroutines.launch

class StudentListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentListRepositoryImpl(application)

    private val getStudentItemUseCase = GetStudentItemUseCase(repository)
    private val getStudentListUseCase = GetStudentItemListUseCase(repository)
    private val deleteStudentItemUseCase = DeleteStudentItemUseCase(repository)

    private val _studentItem = MutableLiveData<StudentItem>()
    val studentItem: LiveData<StudentItem>
        get() = _studentItem

    val studentList = getStudentListUseCase.getStudentList()

    fun deleteStudentItem(studentItemId: Int) {
        viewModelScope.launch {
            val item = getStudentItemUseCase.getStudentItem(studentItemId)
           // _studentItem.value = item
            deleteStudentItemUseCase.deleteStudentItem(item)

        }
    }

}
