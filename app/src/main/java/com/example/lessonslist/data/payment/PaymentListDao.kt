package com.example.lessonslist.data.payment

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PaymentListDao {
    @Query("SELECT * FROM payment_items")
    fun  getPaymentList(): LiveData<List<PaymentItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPaymentItem(paymentItemDbModel: PaymentItemDbModel)

    @Query("DELETE FROM payment_items WHERE id=:paymentItemId")
    suspend fun deletePaymentItem(paymentItemId: Int)

    @Query("SELECT * FROM payment_items WHERE id=:paymentItemId LIMIT 1")
    suspend fun getPaymentItem(paymentItemId: Int): PaymentItemDbModel

}
