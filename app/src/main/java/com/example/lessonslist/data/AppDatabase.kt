package com.example.lessonslist.data


import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lessonslist.data.group.GroupItemDbModel
import com.example.lessonslist.data.group.GroupListDao
import com.example.lessonslist.data.lessons.LessonsItemDbModel
import com.example.lessonslist.data.lessons.LessonsListDao
import com.example.lessonslist.data.payment.PaymentItemDbModel
import com.example.lessonslist.data.payment.PaymentListDao
import com.example.lessonslist.data.student.StudentItemDbModel
import com.example.lessonslist.data.student.StudentListDao

@Database(entities = [StudentItemDbModel::class, PaymentItemDbModel::class, GroupItemDbModel::class, LessonsItemDbModel::class], version = 7, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun StudentListDao(): StudentListDao
    abstract fun GroupListDao(): GroupListDao
    abstract fun PaymentListDao(): PaymentListDao
    abstract fun LessonsListDao(): LessonsListDao


    companion object {

        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = "student.db"

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
