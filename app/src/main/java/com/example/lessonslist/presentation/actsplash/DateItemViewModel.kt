package com.example.lessonslist.presentation.actsplash

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.date.DateListRepositoryImpl
import com.example.lessonslist.domain.date.AddDateItemUseCase
import com.example.lessonslist.domain.date.CheckExistsDateItemUseCase
import com.example.lessonslist.domain.date.DateItem
import com.example.lessonslist.domain.date.DeleteDateItemUseCase
import kotlinx.coroutines.launch

class DateItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DateListRepositoryImpl(application)
    private val checkExistsDateItemUseCase = CheckExistsDateItemUseCase(repository)
    private val addDateItemUseCase = AddDateItemUseCase(repository)
    private val deleteDateItemUseCase = DeleteDateItemUseCase(repository)



    private val _dateItem = MutableLiveData<DateItem>()
    val dateItem: LiveData<DateItem>
        get() = _dateItem


    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen


    @SuppressLint("NullSafeMutableLiveData")
    fun checkExistsDateItem(dateItem: String) {
        viewModelScope.launch {
            val item = checkExistsDateItemUseCase.checkExistsDateItem(dateItem)
            _dateItem.value = item
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun addDateItem(inputDate: String) {
        viewModelScope.launch {
                val dateItem = DateItem(inputDate)
                addDateItemUseCase.addDateItem(dateItem)
            }


    }


    fun deleteDateItem(dateValue: String) {
        viewModelScope.launch {
            val dateItem = checkExistsDateItemUseCase.checkExistsDateItem(dateValue)
            if(dateItem != null) {
                deleteDateItemUseCase.deleteDateItem(dateItem)
            }
        }
    }

}
