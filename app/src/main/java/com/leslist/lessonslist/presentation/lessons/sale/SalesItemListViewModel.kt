package com.leslist.lessonslist.presentation.lessons.sale

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.leslist.lessonslist.data.sale.SaleListRepositoryImpl
import com.leslist.lessonslist.domain.sale.*
import kotlinx.coroutines.launch

class SalesItemListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SaleListRepositoryImpl(application)
    private val getSaleListIdLessonsItemUseCase = GetSaleListIdLessonsItemUseCase(repository)
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

  fun currentLessonHaveSale(idLessons: Int): LiveData<List<SaleItem>> {
      return getSaleListIdLessonsItemUseCase.getSalesListIdLessons(idLessons)
  }




}