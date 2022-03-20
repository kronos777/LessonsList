package com.example.lessonslist.presentation.student


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.student.StudentListRepositoryImpl
import com.example.lessonslist.domain.student.AddStudentItemUseCase
import com.example.lessonslist.domain.student.EditStudentItemUseCase
import com.example.lessonslist.domain.student.GetStudentItemUseCase
import com.example.lessonslist.domain.student.StudentItem


import kotlinx.coroutines.launch

class StudentItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentListRepositoryImpl(application)

    private val getStudentItemUseCase = GetStudentItemUseCase(repository)
    private val addStudentItemUseCase = AddStudentItemUseCase(repository)
    private val editStudentItemUseCase = EditStudentItemUseCase(repository)

    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    private val _studentItem = MutableLiveData<StudentItem>()
    val studentItem: LiveData<StudentItem>
        get() = _studentItem

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    fun getStudentItem(studentItemId: Int) {
        viewModelScope.launch {
            val item = getStudentItemUseCase.getStudentItem(studentItemId)
            _studentItem.value = item
        }
    }

    fun addStudentItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)

        /*new testing var*/
        /*val group = ArrayList<Int>()
        group.addAll(listOf(1,3,5))

        val notes = ArrayList<String>()//Creating an empty arraylist
        notes.add("Ajay")//Adding object in arraylist
        notes.add("Vijay")
        notes.add("Prakash")
        notes.add("Rohan")
        notes.add("Vijay")

        new testing var*/



        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            viewModelScope.launch {
                val studentItem = StudentItem(1222f, "Vasy", "Kumov", "group", "notes", true)
                addStudentItemUseCase.addStudentItem(studentItem)
                finishWork()
            }
        }
    }

    fun editStudentItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            _studentItem.value?.let {
                viewModelScope.launch {
                    val item = it.copy(name = name)
                    //val item add parametrs StudentItems
                    editStudentItemUseCase.editStudentItem(item)
                    finishWork()
                }
            }
        }
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }
        if (count <= 0) {
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputCount() {
        _errorInputCount.value = false
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}
