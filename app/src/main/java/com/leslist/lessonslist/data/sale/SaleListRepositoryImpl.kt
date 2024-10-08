package com.leslist.lessonslist.data.sale

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.leslist.lessonslist.data.AppDatabase
import com.leslist.lessonslist.domain.sale.SaleItem
import com.leslist.lessonslist.domain.sale.SalesListRepository

class SaleListRepositoryImpl(
    application: Application,
): SalesListRepository {

    private val salesListDao = AppDatabase.getInstance(application).SaleListDao()
    private val mapper = SaleListMapper()


    override suspend fun addSaleItem(saleItem: SaleItem) {
        salesListDao.addASaleItem(mapper.mapEntityToDbModel(saleItem))
    }

    override suspend fun deleteSaleItem(saleItem: SaleItem) {
        salesListDao.deleteSaleItem(saleItem.id)
    }

    override suspend fun editSaleItem(saleItem: SaleItem) {
        salesListDao.addASaleItem(mapper.mapEntityToDbModel(saleItem))
    }

    override suspend fun getSaleItem(saleItemId: Int): SaleItem {
        val dbModel = salesListDao.getSaleItem(saleItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override fun getListSaleItems(): LiveData<List<SaleItem>> = Transformations.map(
        salesListDao.getSaleList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }

//getSalesSalesIdLessons
    override fun getSalesSalesIdLessons(idLessons: Int): LiveData<List<SaleItem>> = Transformations.map(
        salesListDao.getSalesSalesIdLessons(idLessons)
    ) {
        mapper.mapListDbModelToListEntity(it)
    }

}
