package com.leslist.lessonslist.data.student


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StudentListDao {

    @Query("SELECT * FROM student_items")
    fun getStudentList(): LiveData<List<StudentItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStudentItem(studentItemDbModel: StudentItemDbModel)

    @Query("DELETE FROM student_items WHERE id=:studentItemId")
    suspend fun deleteStudentItem(studentItemId: Int)

    @Query("UPDATE student_items SET paymentBalance =:paymentBalance WHERE id=:studentItemId")
    suspend fun editStudentItemPaymentBalance(studentItemId: Int, paymentBalance: Int)

    @Query("UPDATE student_items SET telephone =:phoneNumber WHERE id=:studentItemId")
    suspend fun editStudentItemPhoneNumber(studentItemId: Int, phoneNumber: String)

    @Query("SELECT * FROM student_items WHERE id=:studentItemId LIMIT 1")
    suspend fun getStudentItem(studentItemId: Int): StudentItemDbModel


    @Query("SELECT * FROM student_items WHERE name=:studentName AND lastname=:studentLastName LIMIT 1")
    suspend fun checkExistsStudentItem(studentName: String, studentLastName: String): StudentItemDbModel?

}
