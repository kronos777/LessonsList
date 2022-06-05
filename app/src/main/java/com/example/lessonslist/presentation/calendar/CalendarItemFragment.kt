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
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
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
        Toast.makeText(getActivity(),"Фрагмент снова на связи!", Toast.LENGTH_SHORT).show();
    }
    private fun getDate() {
        val calendarView = binding.calendarView
        val calendar = Calendar.getInstance()

// Initial date
        calendar.set(2022, Calendar.JUNE, 3)
        val initialDate = CalendarDate(calendar.time)

// Minimum available date
        calendar.set(2022, Calendar.JANUARY, 1)
        val minDate = CalendarDate(calendar.time)

// Maximum available date
        calendar.set(2023, Calendar.JULY, 15)
        val maxDate = CalendarDate(calendar.time)

// List of preselected dates that will be initially selected

       // val preselectedDates: List<CalendarDate> = getPreselectedDates()


// The first day of week
        val firstDayOfWeek = java.util.Calendar.MONDAY

// Set up calendar with all available parameters



        viewModel = ViewModelProvider(this)[LessonsListViewModel::class.java]
        viewModel.lessonsList.observe(viewLifecycleOwner) {
            for (item in it) {
                val date = item.dateEnd.split(" ")
                val nameLessons = item.title
                val  dd = CalendarDate(Date(date[0]))
                calendarList.add(dd)
                dateTitleMutableMap.put(dd.toString(), nameLessons)
            }

         //   log(calendarList.toString())
          //  log(dateTitleMutableMap.toString())
            calendarView.setupCalendar(
                initialDate = initialDate,
                minDate = minDate,
                maxDate = maxDate,
                selectionMode = CalendarView.SelectionMode.MULTIPLE,
                selectedDates = calendarList,
                firstDayOfWeek = firstDayOfWeek,
                showYearSelectionView = true
            )



            calendarView.onDateClickListener = { date ->

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

                    /*for(item in dateTitleMutableMap) {
                        if(item.key == date.toString()) {
                            curLes.add(item.value)
                        }
                    }*/

                    // Do something ...
                    // for example get list of selected dates
                    val selectedDates = calendarView.selectedDates
                    log("selectarr" + selectedDates.toString())
                   // calendarView.selectedDates.last().remove()

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

