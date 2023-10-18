package com.example.lessonslist.presentation.calendar

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentCalendarPaymentBinding
import com.example.lessonslist.presentation.lessons.LessonsItemEditFragment
import com.example.lessonslist.presentation.lessons.LessonsListViewModel
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.extension.getColorInt
import ru.cleverpumpkin.calendar.sample.events.EventItem
import ru.cleverpumpkin.calendar.sample.events.EventItemsList
import java.util.*
import kotlin.collections.ArrayList

class CalendarPaymentItemFragment : Fragment() {

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentCalendarPaymentBinding? = null
    private val binding: FragmentCalendarPaymentBinding
        get() = _binding ?: throw RuntimeException("FragmentCalendarPaymentBinding == null")


    val arrayList: ArrayList<String> = ArrayList()
    val calendarList: ArrayList<CalendarDate> = ArrayList()
    lateinit var viewModel: PaymentListViewModel
    val dateTitleMutableMap: MutableMap<String, String> =
        mutableMapOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  Toast.makeText(getActivity(),"Фрагмент снова на связи!", Toast.LENGTH_SHORT).show();
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun getScreenOrientationLandscape(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> false
            Configuration.ORIENTATION_LANDSCAPE -> true
            else -> false
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Календарь платежей"

        /*binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            binding.calendarText.text = "$dayOfMonth.${month + 1}.$year"
        }*/
        getDate()
        switchViewPayments()
    }

    private fun switchViewPayments() {
        binding.buttonSwithPayment.setOnClickListener {
            val btnArgsPayment = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.CUSTOM_LIST)
            }
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.paymentItemListFragment, btnArgsPayment)
        }
    }

    /*
    fun testData (): List<LessonsItem>? {
        viewModel = ViewModelProvider(this)[LessonsListViewModel::class.java]

         viewModel.lessonsList.observe(this) {
             return@observe it
        }
    }*/
    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(com.example.lessonslist.R.id.fragment_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun getDate() {
        val calendarView = binding.calendarPaymentView
        val calendar = Calendar.getInstance()
        // The first day of week
        val firstDayOfWeek = Calendar.MONDAY


        val calendarPicList = mutableListOf<EventItemsList>()
        val calendarShowMessgeList = mutableListOf<EventItemsList>()

        viewModel = ViewModelProvider(this)[PaymentListViewModel::class.java]

        viewModel.paymentList.observe(viewLifecycleOwner) {
            val arrayListPayments: java.util.ArrayList<CalendarDate> = java.util.ArrayList()
                for(item in it) {
                    val date = item.datePayment.split(" ")
                    if(date[0].length >= 8) {
                        val  dd = CalendarDate(Date(date[0]))
                        arrayListPayments.add(dd)
                        if(!item.enabled) {
                            calendarPicList += EventItemsList(dd, "payment", item.student)
                            calendarShowMessgeList += EventItemsList(dd, "dolg", item.student)

                        } else {

                            calendarPicList += EventItemsList(dd, "paymentyes", item.student)
                            calendarShowMessgeList += EventItemsList(dd, "paymentyes", item.student)
                        }
                    } else {
                        continue
                    }

                }


                var minDateNumberPayments: CalendarDate

                if (arrayListPayments.size == 0) {
                    calendar.set(Calendar.YEAR, Calendar.JANUARY, 1)
                    minDateNumberPayments = CalendarDate(calendar.time)
                } else {
                    minDateNumberPayments = arrayListPayments[0]
                }

                for (dates in arrayListPayments) {
                    if(minDateNumberPayments > dates)
                        minDateNumberPayments = dates
                }



                val minDateForSetMindateTime: CalendarDate
                minDateForSetMindateTime = minDateNumberPayments


                val calendarTimeZone: Calendar = Calendar.getInstance(TimeZone.getDefault())
                val currentYear = calendarTimeZone[Calendar.YEAR]
                val currentMonth = calendarTimeZone[Calendar.MONTH]
                val currentDay = calendarTimeZone[Calendar.DAY_OF_MONTH]

                calendar.set(currentYear, currentMonth, 1)
                val initialDate = CalendarDate(calendar.time)


                val minDate: CalendarDate
                calendar.set(2000, Calendar.JANUARY, 1)
                val checkDate: CalendarDate =  CalendarDate(calendar.time)



                if(minDateForSetMindateTime == CalendarDate(Date(2000, 1, 1).time)) {
                    minDate = minDateForSetMindateTime
                } else if(minDateForSetMindateTime != checkDate) {
                    minDate = minDateForSetMindateTime
                } else if(minDateForSetMindateTime == checkDate) {
                    minDate = initialDate
                } else {
                    minDate = initialDate
                }


                //  Toast.makeText(activity, "min date here !!!" + minDate.toString(), Toast.LENGTH_SHORT).show()
// Maximum available date
                calendar.set(2032, Calendar.DECEMBER, 31)
                val maxDate = CalendarDate(calendar.time)



                if(calendarPicList != null) {
                    val calendarNewPaymentPicList = mutableListOf<EventItemsList>()
                    //сортировать платежи долги

                    for (index in calendarPicList.indices) {


                        if(calendarPicList[index].color == "paymentyes") {
                            calendarNewPaymentPicList += EventItemsList(calendarPicList[index].date, calendarPicList[index].color, "успешный платеж")

                        } else if (calendarPicList[index].color == "payment") {

                            calendarNewPaymentPicList += EventItemsList(calendarPicList[index].date, calendarPicList[index].color, "долг")

                        } else {
                            calendarNewPaymentPicList += EventItemsList(calendarPicList[index].date, calendarPicList[index].color, "урок")

                        }
                    }

                    val newCalendarNewPaymentPicList = mutableListOf<EventItemsList>()
                    val map: HashMap<String, String> = HashMap()

                    for(index in calendarNewPaymentPicList.indices) {
                        if(newCalendarNewPaymentPicList.size > 0) {
                            if(map.containsKey(calendarNewPaymentPicList[index].date.toString() + calendarNewPaymentPicList[index].eventName) &&
                                !map.containsValue(calendarNewPaymentPicList[index].color)){
                                map[calendarNewPaymentPicList[index].date.toString() + calendarNewPaymentPicList[index].eventName] =
                                    calendarNewPaymentPicList[index].color
                                newCalendarNewPaymentPicList += calendarNewPaymentPicList[index]
                            }
                            if(!map.containsKey(calendarNewPaymentPicList[index].date.toString() + calendarNewPaymentPicList[index].eventName) &&
                                map.containsValue(calendarNewPaymentPicList[index].color)){
                                map[calendarNewPaymentPicList[index].date.toString() + calendarNewPaymentPicList[index].eventName] =
                                    calendarNewPaymentPicList[index].color
                                newCalendarNewPaymentPicList += calendarNewPaymentPicList[index]
                            }
                            if(!map.containsKey(calendarNewPaymentPicList[index].date.toString() + calendarNewPaymentPicList[index].eventName) &&
                                !map.containsValue(calendarNewPaymentPicList[index].color)){
                                map[calendarNewPaymentPicList[index].date.toString() + calendarNewPaymentPicList[index].eventName] =
                                    calendarNewPaymentPicList[index].color
                                newCalendarNewPaymentPicList += calendarNewPaymentPicList[index]
                            }
                        } else {
                            map[calendarNewPaymentPicList[index].date.toString() + calendarNewPaymentPicList[index].eventName] =
                                calendarNewPaymentPicList[index].color
                            newCalendarNewPaymentPicList += calendarNewPaymentPicList[index]
                        }


                    }




                    val indicators: List<CalendarView.DateIndicator> = setDatesIndicators(newCalendarNewPaymentPicList)
                    Log.d("calendarDta", newCalendarNewPaymentPicList.toString())
                    //val indicators: List<CalendarView.DateIndicator> = setDatesIndicators(calendarPicList) work
                    calendarView.datesIndicators = indicators
                }

                calendar.set(currentYear, currentMonth, currentDay)
                val today = calendar.time
                if (calendarList.size > 0) {
                    calendarList.clear()
                    calendarList.add(CalendarDate(today))
                } else if (calendarList.size == 0) {
                    calendarList.add(CalendarDate(today))
                }

                calendarView.setupCalendar(
                    initialDate = initialDate,
                    minDate = minDate,
                    maxDate = maxDate,
                    selectionMode = CalendarView.SelectionMode.SINGLE,
                    selectedDates = calendarList,
                    firstDayOfWeek = firstDayOfWeek,
                    showYearSelectionView = true
                )





            }


            calendarView.onDateClickListener = { date ->
                val calendarDateList = mutableListOf<EventItemsList>()
                for (index in calendarShowMessgeList.indices) {

                    if(calendarShowMessgeList[index].date == date) {
                        calendarDateList += calendarShowMessgeList[index]
                    }
                }

                dialogWindow(date, calendarDateList)
            }




        }

        /*calendarView.onDateLongClickListener = { date ->
           // launchLessonsAddFragment(date.toString())
        }*/



    private fun dialogWindow(date: CalendarDate, dataDate: MutableList<EventItemsList>) {
        if(dataDate.size == 0) {
            // Toast.makeText(getActivity(),"На сегодня ничего нет", Toast.LENGTH_SHORT).show()
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("$date")
            alert.setMessage("Платежей нет.")

            alert.setNeutralButton("Закрыть", DialogInterface.OnClickListener {
                    dialog, id ->
                dialog.dismiss()
            })

            alert.setCancelable(false)
            alert.show()
        } else {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("$date Платежи:")

            val layout = LinearLayout(requireContext())
            layout.orientation = LinearLayout.VERTICAL


            val paymentsLabel = TextView(requireContext())
            paymentsLabel.setSingleLine()
            paymentsLabel.text = "Оплаченные платежи:"
            paymentsLabel.top = 15
            layout.addView(paymentsLabel)


            var countPayYes = 0
            for (index in dataDate.indices) {
                if(dataDate[index].color == "paymentyes"){
                    countPayYes++
                }

            }
            val paymentYes = TextView(requireContext())
            paymentYes.setSingleLine()
            paymentYes.text = countPayYes.toString()
            paymentYes.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            layout.addView(paymentYes)

            val paymentsNoLabel = TextView(requireContext())
            paymentsNoLabel.setSingleLine()
            paymentsNoLabel.text = "Долги :"
            paymentsNoLabel.top = 15
            layout.addView(paymentsNoLabel)


            var countPayNo = 0
            for (index in dataDate.indices) {
                if(dataDate[index].color == "dolg"){
                    countPayNo++

                }

            }

            val payNo = TextView(requireContext())
            payNo.setSingleLine()
            payNo.text = countPayNo.toString()
            payNo.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            layout.addView(payNo)

            layout.setPadding(50, 40, 50, 10)

            alert.setView(layout)

            if(countPayYes != 0 || countPayNo != 0) {
                alert.setPositiveButton("Все платежи", DialogInterface.OnClickListener {
                        dialog, id ->
                    launchPaymentListFragment(date.toString())
                })
            }



            alert.setNegativeButton("Долги", DialogInterface.OnClickListener {
                    dialog, id ->
                launchPaymentListFragment(date.toString()+"&debts")
            })
            alert.setNeutralButton("Закрыть", DialogInterface.OnClickListener {
                    dialog, id ->
                dialog.dismiss()
            })

            alert.setCancelable(false)
            alert.show()

        }



    }


    private fun setDatesIndicators(calendarPicList: List<EventItemsList>): List<EventItem> {

        val context = requireContext()
        val eventItems = mutableListOf<EventItem>()

        for (event in calendarPicList.indices) {
            val title = calendarPicList[event].eventName
            val date = calendarPicList[event].date

            if (calendarPicList[event].color == "paymentyes") {
                eventItems += EventItem(
                    eventName = title,
                    date = date,
                    color = context.getColorInt(R.color.event_3_color)
                )
            } else if (calendarPicList[event].color == "payment") {
                eventItems += EventItem(
                    eventName = title,
                    date = date,
                    color = context.getColorInt(R.color.event_1_color)
                )
            }

        }

        return eventItems
    }

    private fun launchPaymentListFragment(date: String) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsPayments = Bundle().apply {
            putString(PaymentItemListFragment.DATE_ID, date)
            putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.DATE_ID_LIST)
        }

        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navController.navigate(R.id.paymentItemListFragment, btnArgsPayments, animationOptions)
    }


    interface OnEditingFinishedListener {
        fun onEditingFinished()
    }

}