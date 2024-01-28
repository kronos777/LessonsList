package com.example.lessonslist.data.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.example.lessonslist.R
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.data.lessons.LessonsItemDbModel
import com.example.lessonslist.domain.sale.SaleItem
import com.example.lessonslist.presentation.MainActivity
import com.example.lessonslist.presentation.helpers.StringHelpers
import com.example.lessonslist.presentation.payment.PaymentItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class PaymentMinuteWork(
    appContext: Context, params: WorkerParameters
) : CoroutineWorker(
    appContext,
    params
) {

    private val appDatabase = AppDatabase



   override suspend fun doWork(): Result {
       /* while (true)
        {*/
            try {
                withContext(Dispatchers.Default) {
                    val dbLessons = appDatabase.getInstance(applicationContext as Application).LessonsListDao().getAllLessonsList()
                    val dbLessonGet = appDatabase.getInstance(applicationContext as Application).LessonsListDao()
                    val dbStudent = appDatabase.getInstance(applicationContext as Application).StudentListDao()
                    val dbPayment = appDatabase.getInstance(applicationContext as Application).PaymentListDao().getPaymentAllList()

                    val viewModelPayment = PaymentItemViewModel(applicationContext as Application)
                    val listIdsLessons: ArrayList<Int> = ArrayList()
                    val listIdsPayment: ArrayList<Int> = ArrayList()


                    dbLessons.let {
                        for (item in it){
                            listIdsLessons.add(item.id)
                        }

                    }

                    dbPayment.let {
                        for (payItem in it) {
                            listIdsPayment.add(payItem.lessonsId)
                        }
                    }

                    if (listIdsLessons.size > 0) {
                        for (idLessons in listIdsLessons) {
                            if(!listIdsPayment.contains(idLessons)) {
                                val current = LocalDateTime.now()
                                val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
                                val formatted = current.format(formatter)
                                val currentTime = LocalDateTime.parse(formatted, formatter)
                                val lessonsItem = dbLessonGet.getLessonsItem(idLessons)
                                val formattedLess = (lessonsItem.dateEnd.format(formatter)).split(":")
                                val newFormatLess = (formattedLess[0] + ":" + formattedLess[1]).format(formatter)
                                val timeStartLessons = LocalDateTime.parse(newFormatLess, formatter)
                                if(timeStartLessons >= currentTime) {
                                    if(lessonsItem.notifications != ""){
                                        HelpersWorkerService().sendNotifications(currentTime, timeStartLessons, lessonsItem, applicationContext)
                                    }
                                } else if(currentTime >= timeStartLessons) {
                                    val stIds = StringHelpers.getStudentIds(lessonsItem.student)
                                    val namesStudentArrayList: ArrayList<String> = ArrayList()
                                    var okPay = 0
                                    var noPay = 0
                                    if(stIds.isNotEmpty()) {
                                        for (id in stIds.indices){
                                            val student = dbStudent.getStudentItem(stIds[id])
                                            HelpersWorkerService().log(student.name + student.lastname + student.paymentBalance)
                                            val studentData = student.name + " " + student.lastname
                                            val newBalanceStudent = HelpersWorkerService().calculatePaymentPriceAdd(student.paymentBalance, lessonsItem.price)
                                            namesStudentArrayList.add(studentData + ' ' + newBalanceStudent.toString())
                                            HelpersWorkerService().log(newBalanceStudent.toString())
                                            val curPayment = viewModelPayment.checkExistsPaymentItem(student.id, idLessons)
                                            val saleTest = HelpersWorkerService().getSale(lessonsItem.id, student.id, applicationContext)
                                            var saleValue = 0
                                            if(saleTest.size > 0) {
                                                saleValue = saleTest[0].price
                                            }
                                            if(!curPayment) {
                                                if(newBalanceStudent > 0) {
                                                    if(lessonsItem.price > student.paymentBalance) {
                                                        if(saleValue > 0) {
                                                            val price = HelpersWorkerService().calculatePaymentPriceAddPlus(
                                                                student.paymentBalance,
                                                                (lessonsItem.price - saleValue)
                                                            )
                                                            viewModelPayment.addPaymentItem(
                                                                lessonsItem.title,
                                                                lessonsItem.notifications,
                                                                idLessons.toString(),
                                                                student.id.toString(),
                                                                lessonsItem.dateEnd,
                                                                studentData,
                                                                price.toString(),
                                                                (lessonsItem.price - saleValue),
                                                                false
                                                            )
                                                            dbStudent.editStudentItemPaymentBalance(
                                                                student.id,
                                                                (0).toInt()
                                                            )
                                                            noPay++
                                                        } else if (saleValue == 0) {
                                                            val price = HelpersWorkerService().calculatePaymentPriceAddPlus(
                                                                student.paymentBalance,
                                                                lessonsItem.price
                                                            )
                                                            viewModelPayment.addPaymentItem(
                                                                lessonsItem.title,
                                                                lessonsItem.notifications,
                                                                idLessons.toString(),
                                                                student.id.toString(),
                                                                lessonsItem.dateEnd,
                                                                studentData,
                                                                price.toString(),
                                                                lessonsItem.price,
                                                                false
                                                            )
                                                            dbStudent.editStudentItemPaymentBalance(
                                                                student.id,
                                                                (0).toInt()
                                                            )
                                                            noPay++
                                                        }
                                                    } else {
                                                        if(saleValue > 0) {
                                                            viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.notifications,
                                                                idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, (lessonsItem.price - saleValue).toString(), (lessonsItem.price - saleValue),true)
                                                            dbStudent.editStudentItemPaymentBalance(student.id, (student.paymentBalance - (lessonsItem.price - saleValue)))
                                                            okPay++
                                                        } else if (saleValue == 0){
                                                            viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.notifications,
                                                                idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, lessonsItem.price.toString(), lessonsItem.price,true)
                                                            dbStudent.editStudentItemPaymentBalance(student.id, (student.paymentBalance - lessonsItem.price))
                                                            okPay++
                                                        }

                                                    }
                                                } else if (newBalanceStudent == 0) {
                                                    if(saleValue > 0) {
                                                        viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.notifications,
                                                            idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, (lessonsItem.price - saleValue).toString(), (lessonsItem.price - saleValue), true)
                                                        dbStudent.editStudentItemPaymentBalance(student.id, (0).toInt())
                                                        okPay++
                                                    } else if (saleValue == 0){
                                                        viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.notifications,
                                                            idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, lessonsItem.price.toString(), lessonsItem.price, true)
                                                        dbStudent.editStudentItemPaymentBalance(student.id, (0).toInt())
                                                        okPay++
                                                    }

                                                } else {
                                                    if(saleValue > 0) {
                                                        val pricePayment = HelpersWorkerService().calculatePaymentPriceAddPlus(student.paymentBalance, (lessonsItem.price - saleValue))
                                                        viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.notifications,
                                                            idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, (pricePayment).toString(), (lessonsItem.price - saleValue) ,false)
                                                        dbStudent.editStudentItemPaymentBalance(student.id, (0).toInt())
                                                        noPay++
                                                    } else if (saleValue == 0){
                                                        val pricePayment = HelpersWorkerService().calculatePaymentPriceAddPlus(student.paymentBalance, lessonsItem.price)
                                                        viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.notifications,
                                                            idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, (pricePayment).toString(), lessonsItem.price ,false)
                                                        dbStudent.editStudentItemPaymentBalance(student.id, (0).toInt())
                                                        noPay++
                                                    }

                                                }
                                            }

                                        }
                                    }
                                    lateinit var notificationString: String
                                    if(noPay == 0) {
                                        notificationString = "Выставлены счета " + namesStudentArrayList.size + " ученикам и все оплаты прошли успешно."
                                    } else {
                                        notificationString = "Выставлены счета " + namesStudentArrayList.size + " ученикам, " + okPay + " оплат успешных и " + noPay + " остались неоплаченными."
                                    }
                                    HelpersWorkerService().createNotification(lessonsItem.id, "Прошел урок: " + lessonsItem.title, notificationString, lessonsItem.id, applicationContext)
                                }


                            }
                        }


                    }


                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
       return Result.success()

    }

    companion object {

        const val NAME = "PaymentMinuteWork"

        fun makeRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<PaymentMinuteWork>().build()
        }
    }

}
