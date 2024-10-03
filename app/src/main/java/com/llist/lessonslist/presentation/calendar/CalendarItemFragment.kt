package com.llist.lessonslist.presentation.calendar


import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.llist.lessonslist.R
import com.llist.lessonslist.databinding.FragmentCalndarBinding
import com.llist.lessonslist.presentation.helpers.NavigationOptions
import com.llist.lessonslist.presentation.helpers.StringHelpers
import com.llist.lessonslist.presentation.lessons.LessonsItemAddFragment
import com.llist.lessonslist.presentation.lessons.LessonsItemListFragment
import com.llist.lessonslist.presentation.lessons.LessonsListViewModel
import com.llist.lessonslist.presentation.payment.PaymentItemListFragment
import com.llist.lessonslist.presentation.payment.PaymentListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.extension.getColorInt
import ru.cleverpumpkin.calendar.sample.events.EventItem
import ru.cleverpumpkin.calendar.sample.events.EventItemsList
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.TimeZone


class CalendarItemFragment : Fragment() {


    private lateinit var onEditingFinishedListener: OnEditingFinishedListener
    private var _binding: FragmentCalndarBinding? = null
    private val binding: FragmentCalndarBinding
        get() = _binding ?: throw RuntimeException("FragmentCalendarBinding == null")


    val arrayList: ArrayList<String> = ArrayList()
    private val calendarList: ArrayList<CalendarDate> = ArrayList()

    private val viewModel by lazy {
        ViewModelProvider(this)[LessonsListViewModel::class.java]
    }
    private val viewModelPaymentList by lazy {
        ViewModelProvider(this)[PaymentListViewModel::class.java]
    }

    private val navController by lazy {
        (activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment).navController
    }

    private val dateTitleMutableMap: MutableMap<String, String> =
        mutableMapOf()


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
        _binding = FragmentCalndarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Календарь уроков"

        getDate()

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem1).isChecked = true

        showToolbarAndBottomNavigation()
      //  goCalendarFragmentBackPressed()
        goExitBackPressed()


    }

    private fun goExitBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            activity?.finishAffinity()
        }
    }


    private fun showToolbarAndBottomNavigation() {
        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.nav_view_bottom).visibility = View.VISIBLE
        (activity as AppCompatActivity).findViewById<NavigationView>(R.id.navView).visibility = View.VISIBLE
        (activity as AppCompatActivity).findViewById<View>(R.id.payment).visibility = View.VISIBLE
        (activity as AppCompatActivity).findViewById<View>(R.id.backup).visibility = View.VISIBLE
    }

    private fun getCurrentDate(): LocalDateTime {
        return LocalDateTime.now()
    }


    private fun getDate() {
        val calendarView = binding.calendarView
        val calendar = Calendar.getInstance()

        // The first day of week
        val firstDayOfWeek = Calendar.MONDAY

        // Set up calendar with all available parameters


        val calendarPicList = mutableListOf<EventItemsList>()
        val calendarShowMessageList = mutableListOf<EventItemsList>()


        viewModel.lessonsList.observe(viewLifecycleOwner) {

            val arrayListLessons: ArrayList<CalendarDate> = ArrayList()
            val arrayListPayments: ArrayList<CalendarDate> = ArrayList()



            if(calendarShowMessageList.size > 0) {
                calendarShowMessageList.clear()
                calendarPicList.clear()
            }

            for (item in it) {
                val nameLessons = item.title
                val dateEndLessons = CalendarDate(StringHelpers.calendarCreate(item.dateEnd))
                arrayListLessons.add(dateEndLessons)
                calendarPicList += EventItemsList(dateEndLessons, "lessons", nameLessons)
                calendarShowMessageList += EventItemsList(dateEndLessons, "lessons", nameLessons)
                dateTitleMutableMap[dateEndLessons.toString()] = nameLessons
            }


            viewModelPaymentList.paymentList.observe(viewLifecycleOwner) { paymentList ->
                for(item in paymentList) {

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


               var minDateNumberLessons: CalendarDate
                minDateNumberLessons = if(arrayListLessons.size == 0){
                    calendar.set(getCurrentDate().year, getCurrentDate().month.value, getCurrentDate().dayOfMonth)
                    CalendarDate(calendar.time)
                } else {
                    arrayListLessons[0]
                }

                for (dates in arrayListLessons) {
                    if(minDateNumberLessons > dates)
                        minDateNumberLessons = dates
                }

                var minDateNumberPayments: CalendarDate
                minDateNumberPayments = if (arrayListPayments.size == 0) {
                    calendar.set(getCurrentDate().year, getCurrentDate().month.value, getCurrentDate().dayOfMonth)
                    CalendarDate(calendar.time)
                } else {
                    arrayListPayments[0]
                }

                for (dates in arrayListPayments) {
                    if(minDateNumberPayments > dates)
                        minDateNumberPayments = dates
                }


                val minDateForSetMinDateTime: CalendarDate = if(minDateNumberLessons > minDateNumberPayments) {
                    minDateNumberPayments
                } else {
                    minDateNumberLessons
                }

                val calendarTimeZone: Calendar = Calendar.getInstance(TimeZone.getDefault())
                val currentYear = calendarTimeZone[Calendar.YEAR]
                val currentMonth = calendarTimeZone[Calendar.MONTH]
                val currentDay = calendarTimeZone[Calendar.DAY_OF_MONTH]

                calendar.set(currentYear, currentMonth, 1)
                val initialDate = CalendarDate(calendar.time)


                val minDate: CalendarDate
                calendar.set(getCurrentDate().year, getCurrentDate().month.value, getCurrentDate().dayOfMonth)
                val checkDate =  CalendarDate(calendar.time)



                minDate = if(minDateForSetMinDateTime == CalendarDate(StringHelpers.calendarCreate("2000/01/01 00:00").time)) {
                    minDateForSetMinDateTime
                } else if((minDateNumberLessons > minDateNumberPayments || minDateNumberLessons < minDateNumberPayments || minDateNumberLessons == minDateNumberPayments) && minDateForSetMinDateTime != checkDate) {
                    minDateForSetMinDateTime
                } else if(minDateForSetMinDateTime == checkDate) {
                    initialDate
                } else {
                    initialDate
                }

                calendar.set(getCurrentDate().year + 10, Calendar.DECEMBER, 31)
                val maxDate = CalendarDate(calendar.time)


                val calendarNewPaymentPicList = mutableListOf<EventItemsList>()
                //сортировать платежи долги

                for (index in calendarPicList.indices) {
                   calendarNewPaymentPicList += when (calendarPicList[index].color) {
                        "paymentyes" -> {
                            EventItemsList(calendarPicList[index].date, calendarPicList[index].color, "успешный платеж")

                        }
                        "payment" -> {

                            EventItemsList(calendarPicList[index].date, calendarPicList[index].color, "долг")

                        }
                        else -> {
                            EventItemsList(calendarPicList[index].date, calendarPicList[index].color, "урок")

                        }
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
                //val indicators: List<CalendarView.DateIndicator> = setDatesIndicators(calendarPicList) work
                calendarView.datesIndicators = indicators

                calendar.set(currentYear, currentMonth, currentDay)
                val today = calendar.time
                if (calendarList.size > 0) {
                    calendarList.clear()
                    calendarList.add(CalendarDate(today))
                } else if (calendarList.isEmpty()) {
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

        calendarView.onDateLongClickListener = { date ->
            val localDate = LocalDate.now()
            val dateClick = LocalDate.of(date.year, date.month+1, date.dayOfMonth)
            if(localDate > dateClick) {
                Toast.makeText(activity, "Урок не может быть добавлен задним числом.", Toast.LENGTH_SHORT).show()
            } else if (localDate <= dateClick) {
                launchLessonsAddFragment(date.toString())
            }
        }

   }





    private fun launchLessonsAddFragment(date: String) {
        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemAddFragment.SCREEN_MODE, LessonsItemAddFragment.MODE_ADD)
            putString(LessonsItemAddFragment.DATE_ADD, date)
        }
        navController.navigate(R.id.lessonsItemAddFragment, btnArgsLessons, NavigationOptions().invoke())
    }

    private fun launchLessonsListFragment(date: String) {
         val btnArgsLessons = Bundle().apply {
            putString(LessonsItemListFragment.DATE_ID, date)
            putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.DATE_ID_LIST)
        }

        navController.navigate(R.id.lessonsItemListFragment, btnArgsLessons, NavigationOptions().invoke())
    }

    private fun launchPaymentListFragment(date: String) {
        val btnArgsPayments = Bundle().apply {
            putString(PaymentItemListFragment.DATE_ID, date)
            putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.DATE_ID_LIST)
        }
        navController.navigate(R.id.paymentItemListFragment, btnArgsPayments, NavigationOptions().invoke())
    }


    @SuppressLint("ResourceAsColor")
    private fun dialogWindow(date: CalendarDate, dataDate: MutableList<EventItemsList>) {
        var flagNightMode = false
        var alert = AlertDialog.Builder(requireContext())

        val uiModeManager = requireContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val mode = uiModeManager.nightMode
        if (mode == UiModeManager.MODE_NIGHT_YES) {
            alert = AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            flagNightMode = true
            // System is in Night mode
        }


        if(dataDate.size == 0) {
            // Toast.makeText(getActivity(),"На сегодня ничего нет", Toast.LENGTH_SHORT).show()

            alert.setTitle("$date")
            alert.setMessage("Запланированных занятий нет.")

            val localDate = LocalDate.now()
            val dateClick = LocalDate.of(date.year, date.month+1, date.dayOfMonth)
            if (localDate <= dateClick) {
                alert.setPositiveButton("Добавить урок") { _, _ ->
                    launchLessonsAddFragment(date.toString())
                }
            }

            alert.setNeutralButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }

            alert.setCancelable(true)
            alert.show()
        } else {
            //  val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("$date")

            val layout = LinearLayout(requireContext())
            layout.orientation = LinearLayout.VERTICAL

            val lessonsLabel = TextView(requireContext())
            lessonsLabel.setSingleLine()
            lessonsLabel.text = "Уроки:"
            if (flagNightMode) {
                lessonsLabel.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            layout.addView(lessonsLabel)


            for (index in dataDate.indices) {
                if(dataDate[index].color == "lessons"){
                    val nameEvent = dataDate[index].eventName
                    layout.addView(TextView(requireContext()).apply {
                        setSingleLine()
                        this.text = nameEvent
                        if (flagNightMode) {
                            this.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                        }
                        this.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    })
                }

            }




            val paymentsLabel = TextView(requireContext())
            paymentsLabel.setSingleLine()
            paymentsLabel.text = "Оплаченные платежи:"
            if (flagNightMode) {
                paymentsLabel.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            //paymentsLabel.setTextColor(R.color.white)
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

            if (flagNightMode) {
                paymentYes.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }

            layout.addView(paymentYes)

            val paymentsNoLabel = TextView(requireContext())
            paymentsNoLabel.setSingleLine()
            paymentsNoLabel.text = "Долги :"
            paymentsNoLabel.top = 15
            if (flagNightMode) {
                paymentsNoLabel.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
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
            if (flagNightMode) {
                payNo.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            layout.addView(payNo)

            layout.setPadding(50, 40, 50, 10)

            alert.setView(layout)

            if(countPayYes != 0 || countPayNo != 0) {
                alert.setPositiveButton("Платежи") { _, _ ->
                    launchPaymentListFragment(date.toString())
                }
            }



            alert.setNegativeButton("Уроки") { _, _ ->
                launchLessonsListFragment(date.toString())
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
            when (calendarPicList[event].color) {
                "lessons" -> {
                    eventItems += EventItem(
                        date = date,
                        color = context.getColorInt(R.color.event_2_color)
                    )
                }
                "payment" -> {
                    eventItems += EventItem(
                        date = date,
                        color = context.getColorInt(R.color.event_1_color)
                    )
                }
                "paymentyes" -> {
                    eventItems += EventItem(
                        date = date,
                        color = context.getColorInt(R.color.event_3_color)
                    )
                }
            }

        }


        return eventItems
    }




   interface OnEditingFinishedListener {
            fun onEditingFinished()
   }

}