package com.lesslist.lessonslist.data


import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lesslist.lessonslist.data.date.DateItemDbModel
import com.lesslist.lessonslist.data.date.DateListDao
import com.lesslist.lessonslist.data.group.GroupItemDbModel
import com.lesslist.lessonslist.data.group.GroupListDao
import com.lesslist.lessonslist.data.lessons.LessonsItemDbModel
import com.lesslist.lessonslist.data.lessons.LessonsListDao
import com.lesslist.lessonslist.data.notes.NotesItemDbModel
import com.lesslist.lessonslist.data.notes.NotesListDao
import com.lesslist.lessonslist.data.parent.ParentItemDbModel
import com.lesslist.lessonslist.data.parent.ParentListDao
import com.lesslist.lessonslist.data.payment.PaymentItemDbModel
import com.lesslist.lessonslist.data.payment.PaymentListDao
import com.lesslist.lessonslist.data.sale.SaleItemDbModel
import com.lesslist.lessonslist.data.sale.SaleListDao
import com.lesslist.lessonslist.data.student.StudentItemDbModel
import com.lesslist.lessonslist.data.student.StudentListDao

@Database(entities = [StudentItemDbModel::class, PaymentItemDbModel::class, GroupItemDbModel::class, LessonsItemDbModel::class, ParentItemDbModel::class, NotesItemDbModel::class, SaleItemDbModel::class, DateItemDbModel::class], version = 19, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun StudentListDao(): StudentListDao
    abstract fun GroupListDao(): GroupListDao
    abstract fun DateListDao(): DateListDao
    abstract fun PaymentListDao(): PaymentListDao
    abstract fun LessonsListDao(): LessonsListDao
    abstract fun ParentListDao(): ParentListDao
    abstract fun NotesListDao(): NotesListDao
    abstract fun SaleListDao(): SaleListDao


    companion object {

        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()
        const val DB_NAME = "student.db"

        fun getInstance(application: Application): AppDatabase {
            INSTANCE?.let {
                return it
            }
            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }
                val db = Room.databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = db
                return db
            }
        }


    }
}
