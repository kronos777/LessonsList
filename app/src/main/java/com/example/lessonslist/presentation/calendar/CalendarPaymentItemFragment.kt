package com.example.lessonslist.presentation.calendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentCalendarPaymentBinding
import com.example.lessonslist.presentation.helpers.NavigationOptions
import com.example.lessonslist.presentation.helpers.StringHelpers
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.extension.getColorInt
import ru.cleverpumpkin.calendar.sample.events.EventItem
import ru.cleverpumpkin.calendar.sample.events.EventItemsList
import java.util.Calendar
import java.util.TimeZone

class CalendarPaymentItemFragment : Fragment() {

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentCalendarPaymentBinding? = null
    private val binding: FragmentCalendarPaymentBinding
        get() = _binding ?: throw RuntimeException("FragmentCalendarPaymentBinding == null")


    val arrayList: ArrayList<String> = ArrayList()
    private val calendarList: ArrayList<CalendarDate> = ArrayList()
    private val viewModel by lazy {
        ViewModelProvider(this)[PaymentListViewModel::class.java]
    }

    private val navController by lazy {
        (activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment).navController
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Календарь платежей"
        getDate()
        switchViewPayments()
    }

    private fun switchViewPayments() {
        binding.buttonSwithPayment.setOnClickListener {
            val btnArgsPayment = Bundle().apply {
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.CUSTOM_LIST)
            }

            navController.navigate(R.id.paymentItemListFragment, btnArgsPayment)
        }
    }

    private fun getDate() {
        val calendarView = binding.calendarPaymentView
        val calendar = Calendar.getInstance()
        val firstDayOfWeek = Calendar.MONDAY

        val calendarPicList = mutableListOf<EventItemsList>()
        val calendarShowMessageList = mutableListOf<EventItemsList>()

        viewModel.paymentList.observe(viewLifecycleOwner) {
            val arrayListPayments: java.util.ArrayList<CalendarDate> = java.util.ArrayList()
                for(item in it) {
                    val date = item.datePayment.split(" ")
                    if(date[0].length >= 8) {
                        val  dd = CalendarDate(StringHelpers.calendarCreate(item.datePayment))
                        arrayListPayments.add(dd)
                        if(!item.enabled) {
                            calendarPicList += EventItemsList(dd, "payment", item.student)
                            calendarShowMessageList += EventItemsList(dd, "dolg", item.student)

                        } else {

                            calendarPicList += EventItemsList(dd, "paymentyes", item.student)
                            calendarShowMessageList += EventItemsList(dd, "paymentyes", item.student)
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


            val minDateForSetMinDateTime: CalendarDate = minDateNumberPayments


                val calendarTimeZone: Calendar = Calendar.getInstance(TimeZone.getDefault())
                val currentYear = calendarTimeZone[Calendar.YEAR]
                val currentMonth = calendarTimeZone[Calendar.MONTH]
                val currentDay = calendarTimeZone[Calendar.DAY_OF_MONTH]

                calendar.set(currentYear, currentMonth, 1)
                val initialDate = CalendarDate(calendar.time)


                val minDate: CalendarDate
                calendar.set(2000, Calendar.JANUARY, 1)
                val checkDate =  CalendarDate(calendar.time)



            minDate = if(minDateForSetMinDateTime == CalendarDate(StringHelpers.calendarCreate("2000/01/01 00:00").time)) {
                minDateForSetMinDateTime
            } else if(minDateForSetMinDateTime != checkDate) {
                minDateForSetMinDateTime
            } else if(minDateForSetMinDateTime == checkDate) {
                initialDate
            } else {
                initialDate
            }


                //  Toast.makeText(activity, "min date here !!!" + minDate.toString(), Toast.LENGTH_SHORT).show()
// Maximum available date
                calendar.set(2032, Calendar.DECEMBER, 31)
                val maxDate = CalendarDate(calendar.time)


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


            calendarView.datesIndicators = indicators

            calendar.set(currentYear, currentMonth, currentDay)
                val today = calendar.time
                if (calendarList.size > 0) {
                    calendarList.clear()
                    calendarList.add(CalendarDate(today))
                } else {
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
                for (index in calendarShowMessageList.indices) {

                    if(calendarShowMessageList[index].date == date) {
                        calendarDateList += calendarShowMessageList[index]
                    }
                }

                dialogWindow(date, calendarDateList)
            }




    }


    private fun dialogWindow(date: CalendarDate, dataDate: MutableList<EventItemsList>) {
        if(dataDate.size == 0) {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("$date")
            alert.setMessage("Платежей нет.")

            alert.setNeutralButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }

            alert.setCancelable(true)
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
                alert.setPositiveButton("Все платежи") { _, _ ->
                    launchPaymentListFragment(date.toString())
                }
            }



            alert.setNegativeButton("Долги") { _, _ ->
                launchPaymentListFragment("$date&debts")
            }
            alert.setNeutralButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }

            alert.setCancelable(true)
            alert.show()

        }



    }


    private fun setDatesIndicators(calendarPicList: List<EventItemsList>): List<EventItem> {

        val context = requireContext()
        val eventItems = mutableListOf<EventItem>()

        for (event in calendarPicList.indices) {
            val date = calendarPicList[event].date

            if (calendarPicList[event].color == "paymentyes") {
                eventItems += EventItem(
                    date = date,
                    color = context.getColorInt(R.color.event_3_color)
                )
            } else if (calendarPicList[event].color == "payment") {
                eventItems += EventItem(
                    date = date,
                    color = context.getColorInt(R.color.event_1_color)
                )
            }

        }

        return eventItems
    }

    private fun launchPaymentListFragment(date: String) {

        val btnArgsPayments = Bundle().apply {
            putString(PaymentItemListFragment.DATE_ID, date)
            putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.DATE_ID_LIST)
        }
        navController.navigate(R.id.paymentItemListFragment, btnArgsPayments, NavigationOptions().invoke())
    }


    interface OnEditingFinishedListener {
        fun onEditingFinished()
    }

}