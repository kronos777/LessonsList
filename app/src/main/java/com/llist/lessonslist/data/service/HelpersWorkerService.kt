package com.llist.lessonslist.data.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.llist.lessonslist.R
import com.llist.lessonslist.data.AppDatabase
import com.llist.lessonslist.data.lessons.LessonsItemDbModel
import com.llist.lessonslist.domain.sale.SaleItem
import com.llist.lessonslist.presentation.MainActivity
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HelpersWorkerService {
    private val appDatabase = AppDatabase
    suspend fun sendNotifications(currentTime: LocalDateTime, startLessonsTime: LocalDateTime, lessonsItem: LessonsItemDbModel, context: Context) {
        val notification = lessonsItem.notifications

        val formatterCurentDay = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDay = currentTime.format(formatterCurentDay)
        val dayLessons = startLessonsTime.format(formatterCurentDay)

        if(dayLessons == currentDay) {
            val hstart = currentTime.toString().split("T")
            val shstart = hstart[1].split(":")
            val hhstart = shstart[0].toInt()
            val mmstart = shstart[1].toInt()

            val hnotify = notification.split(":")
            val hhnotify = hnotify[0].toInt()
            val mmnotify = hnotify[1].toInt()
            val formattedDatetimeDateNotifications = LocalTime.of(hhnotify, mmnotify)
            val formattedDatetimeDateStart = LocalTime.of(hhstart, mmstart)

            val minValueTime = formattedDatetimeDateStart.minusMinutes(1)
            val maxValueTime = formattedDatetimeDateStart.plusMinutes(1)


            if(formattedDatetimeDateNotifications in minValueTime..maxValueTime) {

                val dateTimeNotify = LocalTime.now().toString().split(".")
                val idNotification = dateTimeNotify[1].toInt()
                createNotification(idNotification,"Напоминание об уроке "+lessonsItem.title, "Сегодня в " + lessonsItem.dateStart + " состоится занятие.", lessonsItem.id, context)

                val viewModelLessonsItem = appDatabase.getInstance(context as Application).LessonsListDao()
                val editLessItem = lessonsItem.copy(lessonsItem.id, lessonsItem.title, "", lessonsItem.student, lessonsItem.price, lessonsItem.dateStart, lessonsItem.dateEnd)
                viewModelLessonsItem.addLessonsItem(editLessItem)


            }


        }



    }


    fun calculatePaymentPriceAddPlus(paymentBalance: Int, priceLessons: Int): Int {
        return paymentBalance - priceLessons

    }

    fun getSale(idLessons: Int, idStudent: Int, context: Context): ArrayList<SaleItem> {
        val dbSales = appDatabase.getInstance(context as Application).SaleListDao().getSalesIdLessons(idLessons)
        val valueExistsSale = ArrayList<SaleItem>()

        if(dbSales !== null) {
            dbSales.let {
                for (item in it) {
                    if(idStudent == item.idStudent) {
                        valueExistsSale.add(SaleItem(item.idStudent, item.idLessons, item.price, item.id))
                    }
                }
            }
        }

        /*
        * проверить наличие скидки на урок
        * проверить наличие скидки на студента
        * */

        return valueExistsSale

    }


   fun calculatePaymentPriceAdd(paymentBalance: Int, priceLessons: Int): Int {
        val calculatePaymentPrice: Int = if(paymentBalance > 0){
            paymentBalance - priceLessons
        } else {
            paymentBalance + priceLessons
        }

        return calculatePaymentPrice

    }


    fun log(message: String) {
        Log.d("SERVICE_PAYMENT", "PaymentService: $message")
    }


    fun createNotification(notificationId: Int, title: String, description: String, lessonsId: Int, context: Context) {


        // Create PendingIntent
        val resultIntent = Intent(context, MainActivity::class.java).putExtra("extra", lessonsId.toString())
        val resultPendingIntent = PendingIntent.getActivity(
            context, 0, resultIntent,
            //PendingIntent.FLAG_MUTABLE
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel =
            NotificationChannel("101", "channel", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(notificationChannel)

        val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val notificationBuilder = NotificationCompat.Builder(context, "101")
            .setContentTitle(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(description))
            //.setContentText(description)
            .setSound(ringtoneManager)
            .addAction(R.drawable.ic_baseline_phone_forwarded_24, "Подробнее", resultPendingIntent)
            .setSmallIcon(R.drawable.ic_baseline_menu_book_24)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true) // закрыть по нажатию

        notificationManager.notify(notificationId, notificationBuilder.build())

    }

}