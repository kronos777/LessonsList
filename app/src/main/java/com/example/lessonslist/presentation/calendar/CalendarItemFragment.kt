package com.example.lessonslist.presentation.calendar


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentCalndarBinding
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
import java.util.*
import kotlin.collections.ArrayList


class CalendarItemFragment() : Fragment() {


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
    ): View? {
        _binding = FragmentCalndarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //(activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Календарь уроков"
        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Календарь уроков"

        getDate()

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem1).isChecked = true

        showToolbarAndBottomNavigation()

    }

    fun goToStList() {
      //  val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
      //  val navController = navHostFragment.navController
        findNavController().navigate(R.id.action_calendarItemFragment_to_studentItemListFragment)
    }

    private fun showToolbarAndBottomNavigation() {
        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.nav_view_bottom).visibility = View.VISIBLE
        (activity as AppCompatActivity).findViewById<NavigationView>(R.id.navView).visibility = View.VISIBLE
        (activity as AppCompatActivity).findViewById<View>(R.id.payment).visibility = View.VISIBLE
        (activity as AppCompatActivity).findViewById<View>(R.id.backup).visibility = View.VISIBLE
    }

    private fun getDate() {
        val calendarView = binding.calendarView
        val calendar = Calendar.getInstance()

        // The first day of week
        val firstDayOfWeek = java.util.Calendar.MONDAY

        // Set up calendar with all available parameters


        val calendarPicList = mutableListOf<EventItemsList>()
        val calendarShowMessgeList = mutableListOf<EventItemsList>()

        viewModel = ViewModelProvider(this)[LessonsListViewModel::class.java]
        viewModelPaymentList = ViewModelProvider(this)[PaymentListViewModel::class.java]
        viewModel.lessonsList.observe(viewLifecycleOwner) {

            var arrayListLessons: ArrayList<CalendarDate> = ArrayList()
            var arrayListPayments: ArrayList<CalendarDate> = ArrayList()



            if(calendarShowMessgeList.size > 0) {
                Toast.makeText(getActivity(),"Размер больше 0 ", Toast.LENGTH_SHORT).show()
                calendarShowMessgeList.clear()
                calendarPicList.clear()
            }

            for (item in it) {

                val date = item.dateEnd.split(" ")
                val nameLessons = item.title
                val  dd = CalendarDate(Date(date[0]))
                arrayListLessons.add(dd)
             //   calendarList.add(dd)
                calendarPicList += EventItemsList(dd, "lessons", nameLessons)
                calendarShowMessgeList += EventItemsList(dd, "lessons", nameLessons)
                dateTitleMutableMap[dd.toString()] = nameLessons
            }


            viewModelPaymentList.paymentList.observe(viewLifecycleOwner) {
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



                val minDateForSetMindateTime: CalendarDate


                if(minDateNumberLessons > minDateNumberPayments) {
                    minDateForSetMindateTime = minDateNumberPayments
                } else {
                    minDateForSetMindateTime = minDateNumberLessons
                }

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
                } else if((minDateNumberLessons > minDateNumberPayments || minDateNumberLessons < minDateNumberPayments || minDateNumberLessons == minDateNumberPayments) && minDateForSetMindateTime != checkDate) {
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
                 //showDialogWithEventsForSpecificDate(date)
                val calendarDateList = mutableListOf<EventItemsList>()
                for (index in calendarShowMessgeList.indices) {

                    if(calendarShowMessgeList[index].date == date) {
                    //   Log.d("current date:", calendarShowMessgeList[index].date.toString() + calendarShowMessgeList[index].eventName.toString())
                   //    Toast.makeText(getActivity(),"данные на дату!" + calendarShowMessgeList[index].date.toString() + calendarShowMessgeList[index].eventName.toString(), Toast.LENGTH_SHORT).show();
                        calendarDateList += calendarShowMessgeList[index]
                    }
                }



                dialogWindow(date, calendarDateList)
            }




        }



        // Set date long click callback
        calendarView.onDateLongClickListener = { date ->
               /* if(getScreenOrientation()){
                    val fragmentTransaction = fragmentManager?.beginTransaction()
                     ?.replace(R.id.shop_item_container, LessonsItemAddFragment.addInstance(date.toString()))
                     ?.addToBackStack(null)
                     ?.commit()
                } else {
                    val fragmentTransaction = fragmentManager?.beginTransaction()
                     ?.replace(R.id.fragment_item_container, LessonsItemAddFragment.addInstance(date.toString()))
                     ?.addToBackStack(null)
                     ?.commit()
                }*/
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

        navController.navigate(R.id.lessonsItemAddFragment, btnArgsLessons)
    }

    private fun launchLessonsListFragment(date: String) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemListFragment.DATE_ID, date)
            putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.DATE_ID_LIST)
        }

        navController.navigate(R.id.lessonsItemListFragment, btnArgsLessons)
    }

    private fun launchPaymentListFragment(date: String) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsPayments = Bundle().apply {
            putString(PaymentItemListFragment.DATE_ID, date)
            putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.DATE_ID_LIST)
        }

        navController.navigate(R.id.paymentItemListFragment, btnArgsPayments)
    }

    private fun dialogWindow(date: CalendarDate, dataDate: MutableList<EventItemsList>) {

        if(dataDate.size == 0) {
            Toast.makeText(getActivity(),"На сегодня ничего нет", Toast.LENGTH_SHORT).show()
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("$date")
            alert.setMessage("Запланированных занятий нет.")

            alert.setPositiveButton("Добавить урок", DialogInterface.OnClickListener {
                    dialog, id ->
                launchLessonsAddFragment(date.toString())
            })
            alert.setNeutralButton("Закрыть", DialogInterface.OnClickListener {
                    dialog, id ->
                dialog.dismiss()
            })

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

                    val index = TextView(requireContext())
                    index.setSingleLine()
                    index.text = nameEvent
                    index.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    layout.addView(index)
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
                alert.setPositiveButton("Платежи", DialogInterface.OnClickListener {
                        dialog, id ->
                   // launchFragment(PaymentItemListFragment.newInstanceDateId(date.toString()))
                    launchPaymentListFragment(date.toString())
                })
            }



            alert.setNegativeButton("Уроки", DialogInterface.OnClickListener {
                    dialog, id ->
                 //  launchFragment(LessonsItemListFragment.newInstanceDateId(date.toString()))
                launchLessonsListFragment(date.toString())
            })
            alert.setNeutralButton("Закрыть", DialogInterface.OnClickListener {
                    dialog, id ->
                dialog.dismiss()
            })

            alert.setCancelable(false)
            alert.show()

        }



    }


        private fun launchFragment(fragment: Fragment, name: String? = "other") {
            requireActivity().supportFragmentManager.beginTransaction()
            .replace(com.example.lessonslist.R.id.fragment_item_container, fragment)
            .addToBackStack(name)
            .commit()
        }

        private fun setDatesIndicators(calendarPicList: List<EventItemsList>): List<EventItem> {

        val context = requireContext()
        val eventItems = mutableListOf<EventItem>()

        for (event in calendarPicList.indices) {
                val title = calendarPicList[event].eventName
                val date = calendarPicList[event].date

               if (calendarPicList[event].color == "lessons") {
                     eventItems += EventItem(
                         eventName = title,
                         date = date,
                         color = context.getColorInt(R.color.event_2_color)
                    )
                } else if (calendarPicList[event].color == "payment") {
                    eventItems += EventItem(
                         eventName = title,
                         date = date,
                         color = context.getColorInt(R.color.event_1_color)
                    )
                } else if (calendarPicList[event].color == "paymentyes") {

                    eventItems += EventItem(
                            eventName = title,
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