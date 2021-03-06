package com.example.lessonslist.data.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.lessonslist.R
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.presentation.MainActivity
import com.example.lessonslist.presentation.payment.PaymentItemViewModel
import com.example.lessonslist.presentation.student.StudentItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class PaymentWork(
    appContext: Context, params: WorkerParameters

) : CoroutineWorker(
    appContext,
    params
) {

    private val appDatabase = AppDatabase

    fun getStudentIds(dataString: String) : List<Int> {
        var dataStr = dataString.replace("]", "")
        dataStr = dataStr.replace("[", "")
        var lstValues: List<Int> = dataStr.split(",").map { it -> it.trim().toInt() }
        return lstValues
    }




    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            val dbLessons = appDatabase.getInstance(applicationContext as Application).LessonsListDao().getAllLessonsList()
            val dbLessonGet = appDatabase.getInstance(applicationContext as Application).LessonsListDao()
            val dbStudent = appDatabase.getInstance(applicationContext as Application).StudentListDao()
            val dbPayment = appDatabase.getInstance(applicationContext as Application).PaymentListDao().getPaymentAllList()
            val viewModelPayment: PaymentItemViewModel = PaymentItemViewModel(applicationContext as Application)
            val viewModelStudent: StudentItemViewModel = StudentItemViewModel(applicationContext as Application)
            var listIdsLessons: ArrayList<Int> = ArrayList()
            var listIdsPayment: ArrayList<Int> = ArrayList()


            dbLessons.let {
                for (item in it){
                    listIdsLessons.add(item.id)


                }
               // log(it.get(0).title)
            }
            log("arrlist"+listIdsLessons.toString())
            dbPayment.let {
                for (payItem in it) {
                    listIdsPayment.add(payItem.lessonsId)
                }
            }

            log("arrlistPayment"+listIdsPayment.toString())


            if (listIdsLessons.size > 0) {
                for (idLessons in listIdsLessons) {
                    if(listIdsPayment.contains(idLessons)) {
                        log("?????? ?????????????????????????? ?????? ???????? ??????????????????.")
                    } else {
                        //dbLessonGet()
                        //?? ?????????????????? ???????????? ???? ?????????????? ?????????????? ???????????????????? ?????????????? ????????????
                        log("?? ?????????????????? ???????????? ???? ?????????????? ?????????????? ???????????????????? ?????????????? ????????????" + idLessons)
                        /*dt*/
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m")
                        val formatted = current.format(formatter)
                        log("?????????? ??????????????" + formatted)
                        val lessonsItem = dbLessonGet.getLessonsItem(idLessons)
                        val formattedLess = lessonsItem.dateEnd.format(formatter)
                       // val formatterLess = DateTimeFormatter.ofPattern("yyyy/M/dd HH:mm")
                       // val fLess = formattedLess.format(formattedLess)
                        log("?????????? ??????????" + formattedLess)
                        if(formattedLess >= formatted) {
                            log("?????????? ???????????? ?????????? ???????????? ????????????????")
                        } else if(formatted >= formattedLess) {
                            log("?????????? ???????????? ?????????? ???????????? ???????????????? ???? ???????? ??????????????")
                          //  log("???????? ???????????????? " + lessonsItem.student.toString() + lessonsItem.title)
                            val stIds = getStudentIds(lessonsItem.student)
                            log("?? ?????????????????? ???????????? ???? ?????????????? ?????????????? ???????????????????? ?????????????? ????????????" + stIds)
                            var namesStudentArrayList: ArrayList<String> = ArrayList()
                            if(stIds.size > 0) {
                                for (ids in stIds){
                                    var student = dbStudent.getStudentItem(ids)
                                    log(student.name + student.lastname + student.paymentBalance)
                                    val studentData = student.name + " " + student.lastname

                                    //?????? ???????????????????? ???? ?????????????? ???????????????? ?????????????? ???????????? ??
                                    //(inputTitle: String, inputDescription: String, inputLessonsId: Int, inputStudentId: Int, inputStudent: String, inputPrice: String)
                                    val newBalanceStudent = calculatePaymentPriceAdd(student.paymentBalance.toInt(), lessonsItem.price.toInt())
                                    namesStudentArrayList.add(studentData + ' ' + newBalanceStudent.toString())
                                    log(newBalanceStudent.toString())

                                    if(newBalanceStudent > 0) {
                                        if(lessonsItem.price > student.paymentBalance) {
                                            val price = calculatePaymentPriceAddPlus(student.paymentBalance, lessonsItem.price)
                                            viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description, idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, price.toString(), false)
                                            dbStudent.editStudentItemPaymentBalance(student.id, (0).toFloat())
                                        } else {
                                            viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description, idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, lessonsItem.price.toString(), true)
                                            dbStudent.editStudentItemPaymentBalance(student.id, (student.paymentBalance - lessonsItem.price).toFloat())
                                        }
                                     } else if (newBalanceStudent <= 0) {
                                        val price = calculatePaymentPriceAddPlus(student.paymentBalance, lessonsItem.price)
                                        viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description, idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, price.toString(), false)
                                        dbStudent.editStudentItemPaymentBalance(student.id, (0).toFloat())
                                        log("???????????? ?????????????????????????? ????????????" + studentData)
                                    } else if (student.paymentBalance <= 0) {
                                        viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description, idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, (- lessonsItem.price).toString(), false)
                                        dbStudent.editStudentItemPaymentBalance(student.id, (0).toFloat())
                                    }


                                }
                            }
                            createNotification("???????????? ????????????", "???????????????????? ?????????? ????????????????:" + namesStudentArrayList.toString())
                        }

                        /*dt*/

                    }
                }


            }


        }

        return Result.success()
      //  Log.d("lesList", less.toString())

    }


    private fun calculatePaymentPriceAddPlus(paymentBalance: Int, priceLessons: Int): Int {
        val calculatePaymentPrice: Int = paymentBalance - priceLessons

        /*if(priceLessons > paymentBalance) {
            for (it in 0..paymentBalance) {
            //priceLessons?.let {
                if (it < priceLessons) {
                    log(it.toString() + "menee")
                } else {
                    log(it.toString() + "bolee")
                }

            }
        }*/
        return calculatePaymentPrice
        //   return l
    }




    private fun calculatePaymentPriceAdd(paymentBalance: Int, priceLessons: Int): Int {
        val calculatePaymentPrice: Int
        if(paymentBalance > 0){
            calculatePaymentPrice = paymentBalance - priceLessons
        } else {
            calculatePaymentPrice = paymentBalance + priceLessons
        }
        /*if(priceLessons > paymentBalance) {
            for (it in 0..paymentBalance) {
            //priceLessons?.let {
                if (it < priceLessons) {
                    log(it.toString() + "menee")
                } else {
                    log(it.toString() + "bolee")
                }

            }
        }*/
        return calculatePaymentPrice
    //   return l
    }


    private fun log(message: String) {
        Log.d("SERVICE_PAYMENT", "PaymentService: $message")
    }


    private fun createNotification(title: String, description: String) {


        // Create PendingIntent
        val resultIntent = Intent(applicationContext, MainActivity::class.java).putExtra("extra", "PARAMS_EXTRA")
        val resultPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        var notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("101", "channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)


        val notificationBuilder = NotificationCompat.Builder(applicationContext, "101")
            .setContentTitle(title)
            .setContentText(description)
            .setSound(ringtoneManager)
            .setSmallIcon(R.drawable.ic_baseline_menu_book_24)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true) // ?????????????? ???? ??????????????

        notificationManager.notify(1, notificationBuilder.build())

    }




}
