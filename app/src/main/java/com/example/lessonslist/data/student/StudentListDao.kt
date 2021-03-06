package com.example.lessonslist.data.student


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lessonslist.domain.student.StudentItem

@Dao
interface StudentListDao {

    @Query("SELECT * FROM student_items")
    fun getStudentList(): LiveData<List<StudentItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStudentItem(studentItemDbModel: StudentItemDbModel)

    @Query("DELETE FROM student_items WHERE id=:studentItemId")
    suspend fun deleteStudentItem(studentItemId: Int)

    @Query("UPDATE student_items SET paymentBalance =:paymentBalance WHERE id=:studentItemId")
    suspend fun editStudentItemPaymentBalance(studentItemId: Int, paymentBalance: Float)

    @Query("SELECT * FROM student_items WHERE id=:studentItemId LIMIT 1")
    suspend fun getStudentItem(studentItemId: Int): StudentItemDbModel
}
