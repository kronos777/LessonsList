package com.leslist.lessonslist.data.sale

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface SaleListDao {
    @Query("SELECT * FROM sale_item ORDER BY id DESC")
    fun getSaleList(): LiveData<List<SaleItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addASaleItem(saleItemDbModel: SaleItemDbModel)

    @Query("DELETE FROM sale_item WHERE id=:saleId")
    suspend fun deleteSaleItem(saleId: Int)

    @Query("SELECT * FROM sale_item WHERE id=:saleId LIMIT 1")
    suspend fun getSaleItem(saleId: Int): SaleItemDbModel

    @Query("SELECT * FROM sale_item WHERE idLessons LIKE :idLess")
    fun getSalesIdLessons(idLess: Int) : List<SaleItemDbModel>

    @Query("SELECT * FROM sale_item WHERE idLessons LIKE :idLess")
    fun getSalesSalesIdLessons(idLess: Int) : LiveData<List<SaleItemDbModel>>


}