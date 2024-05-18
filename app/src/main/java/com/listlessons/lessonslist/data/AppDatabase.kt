package com.listlessons.lessonslist.data


import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.listlessons.lessonslist.data.date.DateItemDbModel
import com.listlessons.lessonslist.data.date.DateListDao
import com.listlessons.lessonslist.data.group.GroupItemDbModel
import com.listlessons.lessonslist.data.group.GroupListDao
import com.listlessons.lessonslist.data.lessons.LessonsItemDbModel
import com.listlessons.lessonslist.data.lessons.LessonsListDao
import com.listlessons.lessonslist.data.notes.NotesItemDbModel
import com.listlessons.lessonslist.data.notes.NotesListDao
import com.listlessons.lessonslist.data.parent.ParentItemDbModel
import com.listlessons.lessonslist.data.parent.ParentListDao
import com.listlessons.lessonslist.data.payment.PaymentItemDbModel
import com.listlessons.lessonslist.data.payment.PaymentListDao
import com.listlessons.lessonslist.data.sale.SaleItemDbModel
import com.listlessons.lessonslist.data.sale.SaleListDao
import com.listlessons.lessonslist.data.student.StudentItemDbModel
import com.listlessons.lessonslist.data.student.StudentListDao

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
