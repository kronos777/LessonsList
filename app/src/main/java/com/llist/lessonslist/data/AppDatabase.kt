package com.llist.lessonslist.data


import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.llist.lessonslist.data.date.DateItemDbModel
import com.llist.lessonslist.data.date.DateListDao
import com.llist.lessonslist.data.group.GroupItemDbModel
import com.llist.lessonslist.data.group.GroupListDao
import com.llist.lessonslist.data.lessons.LessonsItemDbModel
import com.llist.lessonslist.data.lessons.LessonsListDao
import com.llist.lessonslist.data.notes.NotesItemDbModel
import com.llist.lessonslist.data.notes.NotesListDao
import com.llist.lessonslist.data.parent.ParentItemDbModel
import com.llist.lessonslist.data.parent.ParentListDao
import com.llist.lessonslist.data.payment.PaymentItemDbModel
import com.llist.lessonslist.data.payment.PaymentListDao
import com.llist.lessonslist.data.sale.SaleItemDbModel
import com.llist.lessonslist.data.sale.SaleListDao
import com.llist.lessonslist.data.student.StudentItemDbModel
import com.llist.lessonslist.data.student.StudentListDao

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
