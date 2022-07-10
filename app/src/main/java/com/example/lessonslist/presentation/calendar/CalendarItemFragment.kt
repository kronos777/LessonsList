package com.example.lessonslist.presentation.calendar


import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentCalndarBinding
import com.example.lessonslist.presentation.lessons.LessonsItemFragment
import com.example.lessonslist.presentation.lessons.LessonsItemListFragment
import com.example.lessonslist.presentation.lessons.LessonsListViewModel
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.extension.getColorInt
import ru.cleverpumpkin.calendar.sample.events.EventItem
import ru.cleverpumpkin.calendar.sample.events.EventItemsList
import java.util.*


class CalendarItemFragment() : Fragment() {


    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentCalndarBinding? = null
    private val binding: FragmentCalndarBinding
        get() = _binding ?: throw RuntimeException("FragmentCalndarBinding == null")


    val arrayList: ArrayList<String> = ArrayList()
    val calendarList: ArrayList<CalendarDate> = ArrayList()

    lateinit var viewModel: LessonsListViewModel
    lateinit var viewModelPaymentList: PaymentListViewModel
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

        (activity as AppCompatActivity).supportActionBar?.title = "Календарь уроков"

        /*binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            binding.calendarText.text = "$dayOfMonth.${month + 1}.$year"
        }*/
        getDate()

    }
    private fun log(message: String) {
        Log.d("SERVICE_TAG", "DateCalendar: $message")
    }

/*
fun testData (): List<LessonsItem>? {
    viewModel = ViewModelProvider(this)[LessonsListViewModel::class.java]

     viewModel.lessonsList.observe(this) {
         return@observe it
    }
}*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Toast.makeText(getActivity(),"Фрагмент снова на связи!", Toast.LENGTH_SHORT).show();
    }
    private fun getDate() {
        val calendarView = binding.calendarView
        val calendar = Calendar.getInstance()

// List of preselected dates that will be initially selected

       // val preselectedDates: List<CalendarDate> = getPreselectedDates()


// The first day of week
        val firstDayOfWeek = java.util.Calendar.MONDAY

// Set up calendar with all available parameters


        val calendarPicList = mutableListOf<EventItemsList>()
        viewModel = ViewModelProvider(this)[LessonsListViewModel::class.java]
        viewModelPaymentList = ViewModelProvider(this)[PaymentListViewModel::class.java]
        viewModel.lessonsList.observe(viewLifecycleOwner) {

            var arrayListLessons: ArrayList<CalendarDate> = ArrayList()
            var arrayListPayments: ArrayList<CalendarDate> = ArrayList()
            for (item in it) {

                val date = item.dateEnd.split(" ")
                val nameLessons = item.title
                val  dd = CalendarDate(Date(date[0]))
                arrayListLessons.add(dd)
             //   calendarList.add(dd)
                calendarPicList += EventItemsList(dd, "lessons", nameLessons)
                dateTitleMutableMap.put(dd.toString(), nameLessons)
            }



            viewModelPaymentList.paymentList.observe(viewLifecycleOwner) {
                for(item in it) {

                    val date = item.datePayment.split(" ")
                    if(date[0].length >= 8) {

                        val  dd = CalendarDate(Date(date[0]))


                        arrayListPayments.add(dd)

                        if(!item.enabled) {
                            calendarPicList += EventItemsList(dd, "payment", item.student)
                        } else {
                            calendarPicList += EventItemsList(dd, "paymentyes", item.student)
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
// Initial date
                calendar.set(currentYear, currentMonth, 1)
                val initialDate = CalendarDate(calendar.time)

// Minimum available date
                // get first date lessons
              //  calendar.set(2022, Calendar.FEBRUARY, 1)
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
                    var ixpr = 0
                    for (index in calendarPicList.indices) {


                        if(calendarPicList[index].color == "paymentyes") {
                            val dateLessLast: CalendarDate
                            if(index != 0) {
                                dateLessLast = calendarPicList[index - 1].date
                            } else {
                                dateLessLast = calendarPicList[index].date
                            }



                            if(dateLessLast == calendarPicList[index].date) {

                                ixpr = ixpr + 1


                            } else {
                                var addOne = true
                                if(ixpr != 0) {
                                  //  Toast.makeText(activity, ixpr.toString() + " успешных платежей" + (dateLessLast.toString() + " " +  calendarPicList[index].date.toString()).toString(), Toast.LENGTH_SHORT).show()
                                    calendarNewPaymentPicList += EventItemsList(dateLessLast, calendarPicList[index - 1].color, "Платежей успешных" + (ixpr + 1))
                                    addOne = false
                                }

                               ixpr = 0
                               if(ixpr == 0 && addOne != false) {
                                        //  Toast.makeText(activity, ixpr.toString() + " успешных платежей" + (dateLessLast.toString() + " " +  calendarPicList[index].date.toString()).toString(), Toast.LENGTH_SHORT).show()
                                        calendarNewPaymentPicList += EventItemsList(dateLessLast, calendarPicList[index].color, "Платежей успешных" + (ixpr + 1))
                               }



                               // Toast.makeText(activity, "настоящая дата не равна с предыдущей" + (dateLessLast.toString() + " " +  calendarPicList[index].date.toString()).toString(), Toast.LENGTH_SHORT).show()
                            }


                        } else if (calendarPicList[index].color == "payment") {

                            calendarNewPaymentPicList += EventItemsList(calendarPicList[index].date, calendarPicList[index].color, "долг")
  /*                          var ixpr = 0
                            val dateLessLast: CalendarDate
                            if(index != 0) {
                                dateLessLast = calendarPicList[index - 1].date
                            } else {
                                dateLessLast = calendarPicList[index].date
                            }



                            if(dateLessLast == calendarPicList[index].date) {

                                ixpr += 1


                            } else {

                                var addOne = true
                                if(ixpr != 0) {
                                    Toast.makeText(activity, ixpr.toString() + " успешных платежей" + (dateLessLast.toString() + " " +  calendarPicList[index].date.toString()).toString(), Toast.LENGTH_SHORT).show()
                                    calendarNewPaymentPicList += EventItemsList(dateLessLast, calendarPicList[index - 1].color, "долгов " + (ixpr + 1))
                                    addOne = false
                                }

                                ixpr = 0

                                if(ixpr == 0 && addOne != false) {
                                    Toast.makeText(activity, ixpr.toString() + " долгов" + calendarPicList[index].date.toString() + "" + ixpr.toString(), Toast.LENGTH_SHORT).show()
                                    calendarNewPaymentPicList += EventItemsList(calendarPicList[index].date, calendarPicList[index].color, "долгов" + (ixpr + 1))
                                }



                                // Toast.makeText(activity, "настоящая дата не равна с предыдущей" + (dateLessLast.toString() + " " +  calendarPicList[index].date.toString()).toString(), Toast.LENGTH_SHORT).show()
                            }
*/
                        } else {
                            calendarNewPaymentPicList += calendarPicList[index]
                        }
                    }


                    val indicators: List<CalendarView.DateIndicator> = setDatesIndicators(calendarNewPaymentPicList)
                    //val indicators: List<CalendarView.DateIndicator> = setDatesIndicators(calendarPicList) work
                    calendarView.datesIndicators = indicators
                }

                calendar.set(currentYear, currentMonth, currentDay)
                val today = calendar.time
                // val today =
                calendarList.add(CalendarDate(today))
                calendarView.setupCalendar(
                    initialDate = initialDate,
                    minDate = minDate,
                    maxDate = maxDate,
                    selectionMode = CalendarView.SelectionMode.MULTIPLE,
                    selectedDates = calendarList,
                    firstDayOfWeek = firstDayOfWeek,
                    showYearSelectionView = true
                )

            }



         //   log(calendarList.toString())
          //  log(dateTitleMutableMap.toString())


            //val indicators: List<CalendarView.DateIndicator> = setDatesIndicators()
            //val indicators: List<CalendarView.DateIndicator> = getDatesIndicators()

// Set List of indicators that will be displayed on the calendar
            //calendarView.datesIndicators = indicators


            calendarView.onDateClickListener = { date ->

                showDialogWithEventsForSpecificDate(date)

                /*

                val indicatorsForDate = calendarView.getDateIndicators(date)

                for (item in indicatorsForDate) {
                    log(item.date.toString())
                    log(item.color.toString())
                }


                val dialogBuilder = AlertDialog.Builder(requireActivity())
                dialogBuilder.setMessage(indicatorsForDate.toString())
                    // if the dialog is cancelable
                    .setCancelable(false)
                    .setPositiveButton("Ok", DialogInterface.OnClickListener {
                            dialog, id ->
                        dialog.dismiss()

                    })

                val alert = dialogBuilder.create()
                alert.setTitle("Уроки на день:")
                alert.show()
                */


/*

                if(getScreenOrientation() == true) {
                    val fragmentTransaction = fragmentManager?.beginTransaction()
                        ?.replace(R.id.shop_item_container, LessonsItemListFragment.newInstanceDateId(date.toString()))
                        ?.addToBackStack(null)
                        ?.commit()
                } else {
                    var curLes: ArrayList<String> = ArrayList()
                    for (item in it) {
                        val curdate = item.dateEnd.split(" ")
                        val  dd = CalendarDate(Date(curdate[0]))
                        //      log(dd.toString())
                        log(date.toString())
                        if(dd.toString() == date.toString()) {
                            curLes.add(item.dateEnd + " " + item.title + " " + item.price)
                        }
                    }

                    if(curLes.size == 0) {
                        curLes.add("На эту дату уроков нет.")
                    }


                    val selectedDates = calendarView.selectedDates
                    log("selectarr" + selectedDates.toString())

                    val dialogBuilder = AlertDialog.Builder(requireActivity())
                    dialogBuilder.setMessage(curLes.toString())
                        // if the dialog is cancelable
                        .setCancelable(false)
                        .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                dialog, id ->
                            dialog.dismiss()

                        })

                    val alert = dialogBuilder.create()
                    alert.setTitle("Уроки на день:")
                    alert.show()

                    log(date.toString())
                }
*/
                }




        }



// Set date long click callback
        calendarView.onDateLongClickListener = { date ->
            log("arrlistLong"+date.toString() + getScreenOrientation())
            if(getScreenOrientation() == true){
                val fragmentTransaction = fragmentManager?.beginTransaction()
                    ?.replace(R.id.shop_item_container, LessonsItemFragment.newInstanceAddItem(date.toString()))
                    ?.addToBackStack(null)
                    ?.commit()
            } else {
                val fragmentTransaction = fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_item_container, LessonsItemFragment.newInstanceAddItem(date.toString()))
                    ?.addToBackStack(null)
                    ?.commit()
            }


        }

    }


    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(com.example.lessonslist.R.id.fragment_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setDatesIndicators(calendarPicList: List<EventItemsList>): List<EventItem> {
        val context = requireContext()
        val eventItems = mutableListOf<EventItem>()
        var payNoCount = 0
        var payYesCount = 0

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
                //log(title)
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


    private fun showDialogWithEventsForSpecificDate(date: CalendarDate) {
        val eventItems = binding.calendarView.getDateIndicators(date)
            .filterIsInstance<EventItem>()
            .toTypedArray()

        if (eventItems.isNotEmpty()) {
            val adapter = EventDialogAdapter(requireContext(), eventItems)

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("$date")
                .setAdapter(adapter, null)
                .setCancelable(false)
                .setPositiveButton("Платежи", DialogInterface.OnClickListener {
                        dialog, id ->
                    launchFragment(PaymentItemListFragment.newInstanceDateId(date.toString()))
                })
                .setNegativeButton("Уроки", DialogInterface.OnClickListener {
                        dialog, id ->
                    //    log(date.toString())
                    launchFragment(LessonsItemListFragment.newInstanceDateId(date.toString()))
                })
                .setNeutralButton("Закрыть", DialogInterface.OnClickListener {
                        dialog, id ->
                    dialog.dismiss()
                })

            val dialog = builder.create()
            dialog.show()
        }
    }



    private fun getDatesIndicators(): List<EventItem> {
        val context = requireContext()
        val calendar = Calendar.getInstance()

        val eventItems = mutableListOf<EventItem>()
      //  log(CalendarDate(calendar.time).toString());

        repeat(10) {
         /*   eventItems += EventItem(
                eventName = "Event #1",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_1_color)
            )

            eventItems += EventItem(
                eventName = "Event #2",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_2_color)
            )*/

            eventItems += EventItem(
                eventName = "Event #3",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_3_color)
            )

            eventItems += EventItem(
                eventName = "Event #4",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_4_color)
            )

            eventItems += EventItem(
                eventName = "Event #5",
                date = CalendarDate(calendar.time),
                color = context.getColorInt(R.color.event_1_color)
            )

            calendar.add(Calendar.DAY_OF_MONTH, 5)
        }

        return eventItems
    }

    private fun getScreenOrientation(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> false
            Configuration.ORIENTATION_LANDSCAPE -> true
            else -> false
        }
    }

    private fun getPreselectedDates(): List<CalendarDate> {

        val cal = Calendar.getInstance()
        /**/
        cal.set(2022, Calendar.MAY, 3)
    //    log(cal.time.toString())
        val initOne = CalendarDate(cal.time)
        cal.set(2022, Calendar.MAY, 13)
        val initTwo = CalendarDate(cal.time)
        cal.set(2022, Calendar.MAY, 23)
        val initFree = CalendarDate(cal.time)
        cal.set(2022, Calendar.MAY, 27)
        val initFour = CalendarDate(cal.time)

        val current = Date("2022/5/14")
       // val formatter = DateTimeFormatter.ofPattern("yyyy/M/dd HH:mm")
        //val formatted = current.format(formatter)
        val initFive = CalendarDate(current)
        //val formatter = DateTimeFormatter.ofPattern("yyyy/M/dd HH:mm")
        //val formatted = current.format(formatter)
     //   log(current.toString())
        return listOf(initOne, initTwo, initFree, initFour, initFive)
        //return listOf(cal.set(2022, Calendar.MONTH.3, 16), cal.set(2022, 6, 12))
    }




    interface OnEditingFinishedListener {

        fun onEditingFinished()
    }



}

