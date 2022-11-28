package com.example.lessonslist.data.payment

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lessonslist.domain.payment.PaymentItem


@Dao
interface PaymentListDao {
    @Query("SELECT * FROM payment_items")
    fun  getPaymentList(): LiveData<List<PaymentItemDbModel>>

    @Query("SELECT * FROM payment_items")
    fun  getPaymentAllList(): List<PaymentItemDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPaymentItem(paymentItemDbModel: PaymentItemDbModel)

    @Query("DELETE FROM payment_items WHERE id=:paymentItemId")
    suspend fun deletePaymentItem(paymentItemId: Int)

    @Query("SELECT * FROM payment_items WHERE id=:paymentItemId LIMIT 1")
    suspend fun getPaymentItem(paymentItemId: Int): PaymentItemDbModel
//@Query("UPDATE student_items SET paymentBalance =:paymentBalance WHERE id=:studentItemId")

    @Query("SELECT * FROM payment_items WHERE studentId=:studentId AND lessonsId=:lessonsId LIMIT 1")
    fun getPaymentItemExists(studentId: Int, lessonsId: Int): Boolean

    @Query("UPDATE payment_items SET price =:price, enabled =:enabled WHERE id=:paymentItemId")
    suspend fun changeEnableStatePaymentItem(price: Int, paymentItemId: Int, enabled: Boolean = true)


}
