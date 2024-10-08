package com.leslist.lessonslist.presentation.lessons.sale

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.leslist.lessonslist.data.sale.SaleListRepositoryImpl
import com.leslist.lessonslist.domain.sale.*
import kotlinx.coroutines.launch

class SaleItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SaleListRepositoryImpl(application)
    private val getSaleItemUseCase = GetSaleItemUseCase(repository)
    private val addSaleItemUseCase = AddSaleItemUseCase(repository)
    private val editSaleItemUseCase = EditSaleItemUseCase(repository)
    private val deleteSaleItemUseCase = DeleteSaleItemUseCase(repository)

    private val _saleItem = MutableLiveData<SaleItem>()
        val saleItem: LiveData<SaleItem>
        get() = _saleItem

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen


    fun getSaleItem(saleId: Int) {
        viewModelScope.launch {
            val item = getSaleItemUseCase.getSaleItem(saleId)
            _saleItem.value = item
        }
    }

    fun addSaleItem(inputIdStudent: Int, inputIdLessons: Int, inputPrice: Int) {
        viewModelScope.launch {
            val saleItem = SaleItem(inputIdStudent, inputIdLessons, inputPrice)
            addSaleItemUseCase.addSaleItem(saleItem)
           // finishWork()
        }
    }

    fun editSaleItem(saleItem: SaleItem, inputPrice: Int) {
        viewModelScope.launch {
            val item = getSaleItemUseCase.getSaleItem(saleItem.id)
            val saleItemLocal = item.copy(price = inputPrice)
            editSaleItemUseCase.editSaleItem(saleItemLocal)
            finishWork()
        }
    }


    fun deleteSaleItem(saleId: Int) {
        viewModelScope.launch {
            val item = getSaleItemUseCase.getSaleItem(saleId)
            deleteSaleItemUseCase.deleteSaleItem(item)
        }
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }


}