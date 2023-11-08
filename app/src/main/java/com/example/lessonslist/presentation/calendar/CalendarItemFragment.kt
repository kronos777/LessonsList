package com.example.lessonslist.presentation.calendar


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentCalndarBinding
import com.example.lessonslist.presentation.helpers.StringHelpers
import com.example.lessonslist.presentation.lessons.LessonsItemAddFragment
import com.example.lessonslist.presentation.lessons.LessonsItemListFragment
import com.example.lessonslist.presentation.lessons.LessonsListViewModel
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.extension.getColorInt
import ru.cleverpumpkin.calendar.sample.events.EventItem
import ru.cleverpumpkin.calendar.sample.events.EventItemsList
import java.util.Calendar
import java.util.TimeZone


class CalendarItemFragment : Fragment() {


    private lateinit var onEditingFinishedListener: OnEditingFinishedListener
    private var _binding: FragmentCalndarBinding? = null
    private val binding: FragmentCalndarBinding
        get() = _binding ?: throw RuntimeException("FragmentCalndarBinding == null")


    val arrayList: ArrayList<String> = ArrayList()
    val calendarList: ArrayList<CalendarDate> = ArrayList()

    lateinit var viewModel: LessonsListViewModel
    lateinit var viewModelPaymentList: PaymentListViewModel
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
        //(activity as AppCompatActivity).findViewById<MaterialToolbar>(R.id.tool_bar).setNavigationIcon(R.drawable.ic_baseline_menu_24)
    }

    private fun getDate() {
        val calendarView = binding.calendarView
        val calendar = Calendar.getInstance()

        // The first day of week
        val firstDayOfWeek = Calendar.MONDAY

        // Set up calendar with all available parameters


        val calendarPicList = mutableListOf<EventItemsList>()
        val calendarShowMessageList = mutableListOf<EventItemsList>()

        viewModel = ViewModelProvider(this)[LessonsListViewModel::class.java]
        viewModelPaymentList = ViewModelProvider(this)[PaymentListViewModel::class.java]
        viewModel.lessonsList.observe(viewLifecycleOwner) {

            val arrayListLessons: ArrayList<CalendarDate> = ArrayList()
            val arrayListPayments: ArrayList<CalendarDate> = ArrayList()



            if(calendarShowMessageList.size > 0) {
               // Toast.makeText(getActivity(),"Размер больше 0 ", Toast.LENGTH_SHORT).show()
                calendarShowMessageList.clear()
                calendarPicList.clear()
            }

            for (item in it) {

                //val date = item.dateEnd.split(" ")
                val nameLessons = item.title
                /*Log.d("calendarDate", testCreateDt(date[0]).toString())
                Log.d("calendarDate 2", Date(date[0]).toString())
                val  dd = CalendarDate(Date(date[0]))*/
                //val tv = calendarCreate(item.dateEnd)
                val  dd = CalendarDate(StringHelpers.calendarCreate(item.dateEnd))
                arrayListLessons.add(dd)
             //   calendarList.add(dd)
                calendarPicList += EventItemsList(dd, "lessons", nameLessons)
                calendarShowMessageList += EventItemsList(dd, "lessons", nameLessons)
                dateTitleMutableMap[dd.toString()] = nameLessons
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
               if(arrayListLessons.size == 0){
                   calendar.set(2000, Calendar.JANUARY, 1)
                   minDateNumberLessons = CalendarDate(calendar.time)
               } else {
                   minDateNumberLessons = arrayListLessons[0]
               }

                for (dates in arrayListLessons) {
                    if(minDateNumberLessons > dates)
                        minDateNumberLessons = dates
                }

                var minDateNumberPayments: CalendarDate
                if (arrayListPayments.size == 0) {
                    calendar.set(2000, Calendar.JANUARY, 1)
                    minDateNumberPayments = CalendarDate(calendar.time)
                } else {
                    minDateNumberPayments = arrayListPayments[0]
                }

                for (dates in arrayListPayments) {
                    if(minDateNumberPayments > dates)
                        minDateNumberPayments = dates
                }



                val minDateForSetMinDateTime: CalendarDate


                if(minDateNumberLessons > minDateNumberPayments) {
                    minDateForSetMinDateTime = minDateNumberPayments
                } else {
                    minDateForSetMinDateTime = minDateNumberLessons
                }

                val calendarTimeZone: Calendar = Calendar.getInstance(TimeZone.getDefault())
                val currentYear = calendarTimeZone[Calendar.YEAR]
                val currentMonth = calendarTimeZone[Calendar.MONTH]
                val currentDay = calendarTimeZone[Calendar.DAY_OF_MONTH]

                calendar.set(currentYear, currentMonth, 1)
                val initialDate = CalendarDate(calendar.time)


                val minDate: CalendarDate
                calendar.set(2000, Calendar.JANUARY, 1)
                val checkDate =  CalendarDate(calendar.time)



                if(minDateForSetMinDateTime == CalendarDate(StringHelpers.calendarCreate("2000/01/01 00:00").time)) {
                    Log.d("calendarDate", minDateForSetMinDateTime.toString())
                //if(minDateForSetMindateTime == CalendarDate(Date(2000, 1, 1).time)) {
                    minDate = minDateForSetMinDateTime
                } else if((minDateNumberLessons > minDateNumberPayments || minDateNumberLessons < minDateNumberPayments || minDateNumberLessons == minDateNumberPayments) && minDateForSetMinDateTime != checkDate) {
                    minDate = minDateForSetMinDateTime

                } else if(minDateForSetMinDateTime == checkDate) {
                    minDate = initialDate
                } else {
                    minDate = initialDate

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
            launchLessonsAddFragment(date.toString())
        }

   }





    private fun launchLessonsAddFragment(date: String) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemAddFragment.SCREEN_MODE, LessonsItemAddFragment.MODE_ADD)
            putString(LessonsItemAddFragment.DATE_ADD, date)
        }

        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navController.navigate(R.id.lessonsItemAddFragment, btnArgsLessons, animationOptions)
    }

    private fun launchLessonsListFragment(date: String) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemListFragment.DATE_ID, date)
            putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.DATE_ID_LIST)
        }

        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navController.navigate(R.id.lessonsItemListFragment, btnArgsLessons, animationOptions)
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

    private fun dialogWindow(date: CalendarDate, dataDate: MutableList<EventItemsList>) {

        if(dataDate.size == 0) {
           // Toast.makeText(getActivity(),"На сегодня ничего нет", Toast.LENGTH_SHORT).show()
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("$date")
            alert.setMessage("Запланированных занятий нет.")

            alert.setPositiveButton("Добавить урок") { _, _ ->
                launchLessonsAddFragment(date.toString())
            }
            alert.setNeutralButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }

            alert.setCancelable(false)
            alert.show()
        } else {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("$date")

            val layout = LinearLayout(requireContext())
            layout.orientation = LinearLayout.VERTICAL

            val lessonsLabel = TextView(requireContext())
            lessonsLabel.setSingleLine()
            lessonsLabel.text = "Уроки:"
            layout.addView(lessonsLabel)


            for (index in dataDate.indices) {
                if(dataDate[index].color == "lessons"){
                   val nameEvent = dataDate[index].eventName
                    /*val index = TextView(requireContext()).apply {
                        setSingleLine()
                        //this.text = nameEvent
                        this.text = nameEvent
                        this.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    }*/
                    layout.addView(TextView(requireContext()).apply {
                        setSingleLine()
                        this.text = nameEvent
                        this.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    })
                }

            }




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
                alert.setPositiveButton("Платежи") { _, _ ->
                    // launchFragment(PaymentItemListFragment.newInstanceDateId(date.toString()))
                    launchPaymentListFragment(date.toString())
                }
            }



            alert.setNegativeButton("Уроки") { _, _ ->
                //  launchFragment(LessonsItemListFragment.newInstanceDateId(date.toString()))
                launchLessonsListFragment(date.toString())
            }
            alert.setNeutralButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }

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

               if (calendarPicList[event].color == "lessons") {
                     eventItems += EventItem(
                         date = date,
                         color = context.getColorInt(R.color.event_2_color)
                     )
                } else if (calendarPicList[event].color == "payment") {
                    eventItems += EventItem(
                        date = date,
                        color = context.getColorInt(R.color.event_1_color)
                    )
                } else if (calendarPicList[event].color == "paymentyes") {

                    eventItems += EventItem(
                        date = date,
                        color = context.getColorInt(R.color.event_3_color)
                    )
                }

        }


        return eventItems
    }




   interface OnEditingFinishedListener {
            fun onEditingFinished()
   }



}