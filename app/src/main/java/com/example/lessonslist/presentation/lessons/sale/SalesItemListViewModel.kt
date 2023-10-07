package com.example.lessonslist.presentation.lessons.sale

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lessonslist.data.sale.SaleListRepositoryImpl
import com.example.lessonslist.domain.sale.DeleteSaleItemUseCase
import com.example.lessonslist.domain.sale.GetSaleItemUseCase
import com.example.lessonslist.domain.sale.GetSaleListItemUseCase
import com.example.lessonslist.domain.sale.SaleItem
import kotlinx.coroutines.launch

class SalesItemListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SaleListRepositoryImpl(application)
    private val salesListUseCase = GetSaleListItemUseCase(repository)
    private val deleteSaleItemUseCase = DeleteSaleItemUseCase(repository)
    private val getSaleItemUseCase = GetSaleItemUseCase(repository)

   val salesList = salesListUseCase.getSalesList()

   fun deleteSaleItem(saleId: Int) {
       viewModelScope.launch {
           val item = getSaleItemUseCase.getSaleItem(saleId)
           deleteSaleItemUseCase.deleteSaleItem(item)
       }
  }
}