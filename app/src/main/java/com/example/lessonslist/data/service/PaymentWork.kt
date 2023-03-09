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
import androidx.work.WorkerParameters
import com.example.lessonslist.R
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.domain.sale.SaleItem
import com.example.lessonslist.presentation.MainActivity
import com.example.lessonslist.presentation.lessons.sale.DataSalePaymentModel
import com.example.lessonslist.presentation.payment.PaymentItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class PaymentWork(
    appContext: Context, params: WorkerParameters

) : CoroutineWorker(
    appContext,
    params
) {

    private val appDatabase = AppDatabase

    fun getStudentIds(dataString: String): List<Int> {
        var dataStr = dataString.replace("]", "")
        dataStr = dataStr.replace("[", "")
        return dataStr.split(",").map { it.trim().toInt() }
    }




   // @OptIn(DelicateCoroutinesApi::class)
   override suspend fun doWork(): Result {

       withContext(Dispatchers.Default) {

       // withContext(newSingleThreadContext("paymentWork")) {

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
          //  log("arrlist"+listIdsLessons.toString())
            dbPayment.let {
                for (payItem in it) {
                    listIdsPayment.add(payItem.lessonsId)
                }
            }

          //  log("arrlistPayment"+listIdsPayment.toString())

            sleep(2000)
            if (listIdsLessons.size > 0) {
                for (idLessons in listIdsLessons) {
                    if(listIdsPayment.contains(idLessons)) {
                        log("нет необходимости что либо создавать.")
                    } else {
                        //dbLessonGet()
                        //в противном случае на каждого ученика необходимо создать платеж
                        log("в противном случае на каждого ученика необходимо создать платеж" + idLessons)
                        /*dt*/
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
                        var formatted = current.format(formatter)
                        val currentTime = LocalDateTime.parse(formatted, formatter)
                        log("время текущее" + currentTime)
                        val lessonsItem = dbLessonGet.getLessonsItem(idLessons)
                        val formattedLess = (lessonsItem.dateEnd.format(formatter)).split(":")
                        val newFormatLess = (formattedLess[0] + ":" + formattedLess[1]).format(formatter)
                       // val formatterLess = DateTimeFormatter.ofPattern("yyyy/M/dd HH:mm")
                        val timeStartLessons = LocalDateTime.parse(newFormatLess, formatter)

                        //val fLess = newFormatLess.format(formatter)
                        log("время урока $timeStartLessons")
                        //delay(100)
                        if(timeStartLessons >= currentTime) {
                            log("время начала урока больше текущего текущее время:" + currentTime + " время начала урока:" + timeStartLessons)
                        } else if(currentTime >= timeStartLessons) {
                            log("время начала урока меньше текущего те урок окончен текущее время:" + currentTime + " время начала урока:" + timeStartLessons)
                            //log("время начала урока меньше текущего те урок окончен")
                          //  log("поле студенты " + lessonsItem.student.toString() + lessonsItem.title)
                            val stIds = getStudentIds(lessonsItem.student)
                            log("в противном случае на каждого ученика необходимо создать платеж" + stIds)

                            val namesStudentArrayList: ArrayList<String> = ArrayList()
                            var okPay = 0
                            var noPay = 0
                         //   sleep(1000)
                            if(stIds.isNotEmpty()) {
                                for (id in stIds.indices){
                                    val threadId = Thread.currentThread().id
                                    log("id потока" + threadId)
                                    log("id студента" + stIds[id])
                                    val student = dbStudent.getStudentItem(stIds[id])
                                    log(student.name + student.lastname + student.paymentBalance)
                                    val studentData = student.name + " " + student.lastname

                                    //тут необходимо на каждого студента создать платеж и
                                    //(inputTitle: String, inputDescription: String, inputLessonsId: Int, inputStudentId: Int, inputStudent: String, inputPrice: String)
                                    val newBalanceStudent = calculatePaymentPriceAdd(student.paymentBalance, lessonsItem.price)
                                    namesStudentArrayList.add(studentData + ' ' + newBalanceStudent.toString())
                                    log(newBalanceStudent.toString())
                                    val curPayment = viewModelPayment.checkExistsPaymentItem(student.id, idLessons)
                                    sleep(2000)
                                    //пробуем проверить наличие скидки
                                    val saleTest = getSale(lessonsItem.id, student.id)
                                    var saleValue = 0
                                    if(saleTest.size > 0) {
                                        log("наличие скидок" + saleTest[0].price.toString() + lessonsItem.title)
                                        saleValue = saleTest[0].price
                                    }
                                    //пробуем проверить наличие скидки


                                    if(!curPayment) {
                                        if(newBalanceStudent > 0) {
                                            if(lessonsItem.price > student.paymentBalance) {
                                                if(saleValue > 0) {
                                                    val price = calculatePaymentPriceAddPlus(
                                                        student.paymentBalance,
                                                        (lessonsItem.price - saleValue)
                                                    )
                                                    viewModelPayment.addPaymentItem(
                                                        lessonsItem.title,
                                                        lessonsItem.description,
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
                                                    val price = calculatePaymentPriceAddPlus(
                                                        student.paymentBalance,
                                                        lessonsItem.price
                                                    )
                                                    viewModelPayment.addPaymentItem(
                                                        lessonsItem.title,
                                                        lessonsItem.description,
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
                                                    viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description,
                                                        idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, (lessonsItem.price - saleValue).toString(), (lessonsItem.price - saleValue),true)
                                                    dbStudent.editStudentItemPaymentBalance(student.id, (student.paymentBalance - (lessonsItem.price - saleValue)))
                                                    okPay++
                                                } else if (saleValue == 0){
                                                    viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description,
                                                        idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, lessonsItem.price.toString(), lessonsItem.price,true)
                                                    dbStudent.editStudentItemPaymentBalance(student.id, (student.paymentBalance - lessonsItem.price))
                                                    okPay++
                                                }

                                            }
                                        } else if (newBalanceStudent == 0) {
                                            //val price = calculatePaymentPriceAddPlus(student.paymentBalance, lessonsItem.price)
                                            if(saleValue > 0) {
                                                viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description,
                                                    idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, (lessonsItem.price - saleValue).toString(), (lessonsItem.price - saleValue), true)
                                                dbStudent.editStudentItemPaymentBalance(student.id, (0).toInt())
                                                okPay++
                                            } else if (saleValue == 0){
                                                viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description,
                                                    idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, lessonsItem.price.toString(), lessonsItem.price, true)
                                                dbStudent.editStudentItemPaymentBalance(student.id, (0).toInt())
                                                okPay++
                                            }

                                        } else if (newBalanceStudent < 0) {
                                            //} else if (student.paymentBalance < 0) {
                                            if(saleValue > 0) {
                                                val pricePayment = calculatePaymentPriceAddPlus(student.paymentBalance, (lessonsItem.price - saleValue))
                                                viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description,
                                                    idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, (pricePayment).toString(), (lessonsItem.price - saleValue) ,false)
                                                dbStudent.editStudentItemPaymentBalance(student.id, (0).toInt())
                                                noPay++
                                            } else if (saleValue == 0){
                                                val pricePayment = calculatePaymentPriceAddPlus(student.paymentBalance, lessonsItem.price)
                                                viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description,
                                                    idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, (pricePayment).toString(), lessonsItem.price ,false)
                                                dbStudent.editStudentItemPaymentBalance(student.id, (0).toInt())
                                                noPay++
                                            }

                                        }
                                    }
                                }
                            }
                            lateinit var notificationString: String
                            if (noPay == 0) {
                                notificationString = "Выставлены счета " + namesStudentArrayList.size + " ученикам и все оплаты прошли успешно."
                            } else {
                                notificationString = "Выставлены счета " + namesStudentArrayList.size + " ученикам, " + okPay + " оплат успешных и " + noPay + " остались неоплаченными."
                            }

                            createNotification("Список уроков", notificationString, lessonsItem.id)
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

        return paymentBalance - priceLessons

    }

    private fun getSale(idLessons: Int, idStudent: Int): ArrayList<SaleItem> {
      //  val viewModelSale = SaleItemViewModel(applicationContext as Application)
     //   val viewModelSaleList = SalesItemListViewModel(applicationContext as Application)
        val dbSales = appDatabase.getInstance(applicationContext as Application).SaleListDao().getSalesIdLessons(idLessons)
        val valueExistsSale = ArrayList<SaleItem>()

        if(dbSales !== null) {
            dbSales.let {
                for (item in it) {
                    if(idStudent == item.idStudent) {
                        valueExistsSale.add(SaleItem(item.idStudent, item.idLessons, item.price, item.id))
                    }

                    //log("exists lessons sale" + item.id + " " + item.price + " " + item.idLessons + " " + item.idStudent)
                }
            }
        }

        /*
        * проверить наличие скидки на урок
        * проверить наличие скидки на студента
        * */

        return valueExistsSale

    }


    private fun calculatePaymentPriceAdd(paymentBalance: Int, priceLessons: Int): Int {
        val calculatePaymentPrice: Int = if(paymentBalance > 0){
            paymentBalance - priceLessons
        } else {
            paymentBalance + priceLessons
        }

        return calculatePaymentPrice
   
    }


    private fun log(message: String) {
        Log.d("SERVICE_PAYMENT", "PaymentService: $message")
    }


    private fun createNotification(title: String, description: String, lessonsId: Int) {


        // Create PendingIntent
        val resultIntent = Intent(applicationContext, MainActivity::class.java).putExtra("extra", lessonsId.toString())
        val resultPendingIntent = PendingIntent.getActivity(
            applicationContext, 0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel =
            NotificationChannel("101", "channel", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(notificationChannel)

        val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)


        val notificationBuilder = NotificationCompat.Builder(applicationContext, "101")
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(description))
            //.setContentText(description)
            .setSound(ringtoneManager)
            .addAction(R.drawable.ic_baseline_phone_forwarded_24, "Подробнее", resultPendingIntent)
            .setSmallIcon(R.drawable.ic_baseline_menu_book_24)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true) // закрыть по нажатию

        notificationManager.notify(1, notificationBuilder.build())

    }




}
