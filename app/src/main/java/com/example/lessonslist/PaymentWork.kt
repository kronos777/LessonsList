package com.example.lessonslist

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.data.lessons.LessonsItemDbModel
import com.example.lessonslist.data.lessons.LessonsListMapper
import com.example.lessonslist.domain.lessons.GetLessonsItemUseCase
import com.example.lessonslist.domain.lessons.GetLessonsListItemUseCase
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.presentation.MainViewModel
import com.example.lessonslist.presentation.lessons.LessonsItemListFragment
import com.example.lessonslist.presentation.lessons.LessonsItemViewModel
import com.example.lessonslist.presentation.lessons.LessonsListViewModel
import com.example.lessonslist.presentation.payment.PaymentItemViewModel
import com.example.lessonslist.presentation.student.StudentItemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class PaymentWork(
    appContext: Context, params: WorkerParameters

) : CoroutineWorker(
    appContext,
    params
) {
  //  private lateinit var viewLifecycleOwner: LifecycleOwner
   // private var dataLessonsList: LessonsListViewModel = LessonsListViewModel(applicationContext as Application)
    // private var dataLessonsListFragment: LessonsItemListFragment = LessonsItemListFragment()
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
         //   listIdsPayment.add(1)
            log("arrlistPayment"+listIdsPayment.toString())

            //val stIds = getStudentIds("[1, 2, 3]")
            /*for (ids in stIds){
                var student = dbStudent.getStudentItem(ids)
                log(student.name + student.lastname + student.paymentBalance)
            }
*/


            if (listIdsLessons.size > 0) {
              /*  listIdsLessons.removeIf {
                    it.equals(id)
                }*/
                for (idLessons in listIdsLessons) {
                    if(listIdsPayment.contains(idLessons)) {
                        log("нет необходимости что либо создавать.")
                    } else {
                        //dbLessonGet()
                        //в противном случае на каждого ученика необходимо создать платеж
                        log("в противном случае на каждого ученика необходимо создать платеж" + idLessons)
                        /*dt*/
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m")
                        val formatted = current.format(formatter)
                        log("время текущее" + formatted)
                        val lessonsItem = dbLessonGet.getLessonsItem(idLessons)
                        val formattedLess = lessonsItem.dateEnd.format(formatter)
                       // val formatterLess = DateTimeFormatter.ofPattern("yyyy/M/dd HH:mm")
                       // val fLess = formattedLess.format(formattedLess)
                        log("время урока" + formattedLess)
                        if(formattedLess >= formatted) {
                            log("время начала урока больше текущего")
                        } else if(formatted >= formattedLess) {
                            log("время начала урока меньше текущего те урок окончен")
                            val stIds = getStudentIds(lessonsItem.student)
                            log("в противном случае на каждого ученика необходимо создать платеж" + stIds)
                            var namesStudentArrayList: ArrayList<String> = ArrayList()
                            if(stIds.size > 0) {
                                for (ids in stIds){
                                    var student = dbStudent.getStudentItem(ids)
                                    log(student.name + student.lastname + student.paymentBalance)
                                    val studentData = student.name + " " + student.lastname

                                    //тут необходимо на каждого студента создать платеж и
                                    //(inputTitle: String, inputDescription: String, inputLessonsId: Int, inputStudentId: Int, inputStudent: String, inputPrice: String)
                                    val newBalanceStudent = calculatePaymentPriceAdd(student.paymentBalance.toInt(), lessonsItem.price.toInt())
                                    namesStudentArrayList.add(studentData + ' ' + newBalanceStudent.toString())
                                    log(newBalanceStudent.toString())

                                    if(newBalanceStudent > 0) {
                                        if(lessonsItem.price > student.paymentBalance){
                                            val price = calculatePaymentPriceAddPlus(student.paymentBalance, lessonsItem.price)
                                            viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description, idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, price.toString(), false)
                                        } else {
                                            viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description, idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, lessonsItem.price.toString(), true)
                                       }
                                     } else if (newBalanceStudent < 0){
                                        viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description, idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, newBalanceStudent.toString(), false)
                                        log("создан отрицательный платеж" + studentData)
                                    } else if (student.paymentBalance < 0){
                                        viewModelPayment.addPaymentItem(lessonsItem.title, lessonsItem.description, idLessons.toString(), student.id.toString(), lessonsItem.dateEnd, studentData, (- lessonsItem.price.toInt()).toString(), false)

                                    }


                                    dbStudent.editStudentItemPaymentBalance(student.id, (student.paymentBalance - lessonsItem.price).toFloat())
                                    // вычесть значение с платежного баланса

                                    //inputName: String?, inputLastName: String?, inputPaymentBalance: String, inputNotes: String, inputGroup: String
                                        //viewModelStudent.getStudentItem(student.id)
                                    //student.editStudentItem(student.name, student.lastname, newBalanceStudent.toString(), student.notes, student.group)

                                }
                            }
                            createNotification("Список уроков", "выставлены счета ученикам:" + namesStudentArrayList.toString())
                        }

                        /*dt*/

                    }
                }


            }


            /*val db = appDatabase.getInstance(applicationContext as Application).LessonsListDao().getLessonsList()
            log(db.toString())
            db.observe(applicationContext) {
                log(it.get(0).title)
            }*/
        }
     //   val db = appDatabase.getInstance(applicationContext as Application).LessonsListDao().getLessonsList()
     //   log(db.value.toString())
       /* dataLessonsList.lessonsList.observe(applicationContext) {
           for (item in it) {
               log(item.title)
           }
        }*/
        /*dataLessonsList.lessonsList.observe(this) {
            for(lessons in it){
                Log.d("lesList", lessons.title)
            }
        }*/
        /*val db = AppDatabase.getInstance(applicationContext as Application)
        var less = db.LessonsListDao().getLessonsList()
        less.observe(viewLifecycleOwner) {


        }*/
//Log.d("lesList", it.toString())
    //    viewLifecycleOwner = ViewModelProvider(this)[LessonsListViewModel::class.java]
      /*  dataLessonsList.lessonsList.observe(viewLifecycleOwner) {
            for(lessons in it){
                Log.d("lesList", lessons.title)
            }
        }
*/
        //Log.d("lesList", dataLessonsList.lessonsList.value.toString())

       // createNotification("Background Task", "This notification is generated by workManager")

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
            calculatePaymentPrice = 0 - priceLessons
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
        Log.d("SERVICE_TAG", "PaymentService: $message")
    }


    private fun createNotification(title: String, description: String) {

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
            .setSmallIcon(R.drawable.ic_add)

        notificationManager.notify(1, notificationBuilder.build())

    }




}
