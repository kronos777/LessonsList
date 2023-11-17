package com.example.lessonslist.presentation.lessons

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentLessonsItemAddBinding
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.presentation.student.StudentListViewModel
import com.example.lessonslist.presentation.group.DataStudentGroupModel
import com.example.lessonslist.presentation.group.GroupListViewModel
import com.example.lessonslist.presentation.group.ListStudentAdapter
import com.example.lessonslist.presentation.helpers.PhoneTextFormatter
import com.example.lessonslist.presentation.helpers.StringHelpers
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


@Suppress("NAME_SHADOWING")
class LessonsItemAddFragment : Fragment()  {

    private lateinit var viewModel: LessonsItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentLessonsItemAddBinding? = null
    private val binding: FragmentLessonsItemAddBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")


    private lateinit var adapter: ListStudentAdapter

    private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null

    private lateinit var dataStudentlList: StudentListViewModel



    private lateinit var adapterGroup: ListGroupAdapter
    private lateinit var listViewGroup: ListView
    private var dataGroupLessonsModel: ArrayList<DataGroupLessonsModel>? = null
    private lateinit var dataGroupList: GroupListViewModel
    private var dataGroupListString: Boolean = true


    private lateinit var timePicker1RepeatDate: String
    private lateinit var timePicker2RepeatDate: String
    private lateinit var timePicker1RepeatStartHourMinuteDate: String
    private lateinit var timePicker2RepeatEndHourMinuteDate: String
    private val dateLessons: ArrayList<String> = ArrayList()
    private var notificationString: String = ""
    private var notificationBoolean: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         parseParams()
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
        _binding = FragmentLessonsItemAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Урок"

        viewModel = ViewModelProvider(this)[LessonsItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //addTextChangeListeners()
        //launchRightMode()

        launchAddMode()
        observeViewModel()
        lessonsTextChangeListeners()


       // binding.tilStudent.visibility = View.GONE
      //  binding.listViewGroup.visibility = View.GONE


        chooseDateLessons()

        setListViewStudent()
        setGroupViewStudent()
     //   listenSwitchGroup()
        goLessonsListFragmentBackPressed()

        repeatLessons()
        notificationsLessons()

    }


    private fun goLessonsListFragmentBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            val arguments = Bundle().apply {
                putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.CUSTOM_LIST)
            }
            navController.popBackStack(R.id.lessonsItemListFragment, true)
            val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
                .setExitAnim(R.anim.slide_in_right)
                .setPopEnterAnim(R.anim.slide_out_left)
                .setPopExitAnim(R.anim.slide_out_right).build()
            navController.navigate(R.id.lessonsItemListFragment, arguments, animationOptions)
        }
    }

   /* private fun listenSwitchGroup() {
        val switchChoose = binding.switch1
        switchChoose.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                binding.listViewGroup.visibility = View.VISIBLE
                binding.listView.visibility = View.GONE
                binding.textViewChangeStateCheckbox.text = "Список групп."
              //  Toast.makeText(activity, "isChecked", Toast.LENGTH_SHORT).show()
            } else {
                binding.listViewGroup.visibility = View.GONE
                binding.listView.visibility = View.VISIBLE
                binding.textViewChangeStateCheckbox.text = "Список учеников."
              //  Toast.makeText(activity, "unchecked", Toast.LENGTH_SHORT).show()
            }

        }
    }*/


    private fun repeatLessons() {
        val switchChoose = binding.etRepeat
        switchChoose.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                 //   Toast.makeText(activity, "checked", Toast.LENGTH_SHORT).show()
                    binding.cardRepeatLessons.visibility = View.VISIBLE
                  /*  binding.etDatestartRepeat.setOnClickListener {
                        chooseDateStartRepeat()
                    }*/
                    binding.etDateendRepeat.setOnClickListener {
                        chooseDateEndRepeat()
                    }
                } else {
                    binding.cardRepeatLessons.visibility = View.GONE
                    //Toast.makeText(activity, "unchecked", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun notificationsLessons() {
        val switchChoose = binding.etNotifications
        binding.etTimeNotifications.addTextChangedListener(PhoneTextFormatter(binding.etTimeNotifications, "##:##"))
        //textChangedListener(PhoneTextFormatter(binding.etTimeNotifications, "##:##"))
        switchChoose.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                //   Toast.makeText(activity, "checked", Toast.LENGTH_SHORT).show()
                binding.cardNotifications.visibility = View.VISIBLE
                binding.etTimeNotifications.setOnClickListener {
                    setNotifications()
                }
                /*binding.etDateendRepeat.setOnClickListener {
                    chooseDateEndRepeat()
                }*/
            } else {
                Toast.makeText(activity, "Вы убрали уведомление.", Toast.LENGTH_SHORT).show()
                binding.cardNotifications.visibility = View.GONE
                notificationString = ""
                binding.tilTimeNotifications.error = ""
                binding.etTimeNotifications.setText("")
                notificationBoolean = true
                //Toast.makeText(activity, "unchecked", Toast.LENGTH_SHORT).show()
            }
        }
    }
   /* private fun chooseDateStartRepeat() {
        val cal = Calendar.getInstance()
        val year1 = cal.get(Calendar.YEAR)
        val month1 = cal.get(Calendar.MONTH)
        val day1 = cal.get(Calendar.DAY_OF_MONTH)
        val mcurrentTime = Calendar.getInstance()
        var year = mcurrentTime.get(Calendar.YEAR)
        var month = mcurrentTime.get(Calendar.MONTH)
        var day = mcurrentTime.get(Calendar.DAY_OF_MONTH)


        val startRepeat =
            activity?.let {
                DatePickerDialog(it, { _, yearcur, monthOfYear, dayOfMonth ->
                    cal.set(yearcur, monthOfYear, dayOfMonth)
                    year = cal[Calendar.YEAR]
                    month = cal[Calendar.MONTH]
                    day = cal[Calendar.DAY_OF_MONTH]
                    binding.etDatestartRepeat.setText(String.format("%d/%d/%d", year, month + 1, day))
                    timePicker1RepeatDate = year.toString() + "-" + (month + 1).toString() + "-" + day.toString()
                }, year1, month1, day1)
            }
        startRepeat!!.show()
        //binding.tilDatestartRepeat.error = "Период повторения не может быть мене текущей даты."
    }*/
/*    private fun checkDateTimeNotifications() {
       val valueNotifications = binding.etTimeNotifications.text.toString()
       Log.d("countChars", valueNotifications.count().toString())
       if(valueNotifications.count() == 5) {
           val hh = valueNotifications.split(":")
           val h = hh[0]
           val m = hh[1]
           if((h.toInt() >= 24 || m.toInt() >= 60) || (h.toInt() >= 24 && m.toInt() >= 60)) {
               binding.tilTimeNotifications.error = "Формат даты не может быть более 23:59"
           } else {
               binding.tilTimeNotifications.error = ""

           }
       } else if (valueNotifications.count() < 5) {
           binding.tilTimeNotifications.error = "Введите время корректно"
       } else if (valueNotifications.count() == 0) {
           binding.tilTimeNotifications.error = ""
       }

    }
*/
    private fun validValueNotifications() {
        val notify = binding.etTimeNotifications.text.toString()
        if (notify.isNotBlank() && binding.etDatestart.text.toString().isNotBlank()) {
            val dateStart = binding.etDatestart.text.toString().split(" ")
            val timeDateStart = dateStart[1]

            /*time*/
            val hstart = timeDateStart.split(":")
            val hhstart = hstart[0].toInt()
            val mmstart = hstart[1].toInt()

            val hnotify = notify.split(":")
            val hhnotify = hnotify[0].toInt()
            val mmnotify = hnotify[1].toInt()
            /*time*/

            if (notify.isNotBlank() && timeDateStart.isNotBlank()) {
              //val formatter = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                //val formattedDatetimeDateNotifications = formatter.parse(timeDateStart)
                //LocalTime time = LocalTime.of(23, 59);
                val formattedDatetimeDateNotifications = LocalTime.of(hhnotify, mmnotify)
                val formattedDatetimeDateStart = LocalTime.of(hhstart, mmstart)
                //val formattedDatetimeDateStart = formatter.parse(timeDateStart)
                if(formattedDatetimeDateNotifications >= formattedDatetimeDateStart.minusMinutes(20)) {
                    Toast.makeText(activity, "Значение даты напоминания не может быть больше или равному дате начала урока за 20 минут.", Toast.LENGTH_SHORT).show()
                    binding.tilTimeNotifications.error = "Значение даты напоминания не может быть больше или равному дате начала урока за 20 минут."
                    notificationBoolean = false
                } else {
                    notificationString = formattedDatetimeDateNotifications.toString()
                    binding.tilTimeNotifications.error = ""
                    notificationBoolean = true

                }
                //Toast.makeText(activity, "this value start time" + formattedDatetimeDateStart.minusMinutes(20), Toast.LENGTH_SHORT).show()

            } else if(notify.isBlank() && timeDateStart.isNotBlank()) {
                notificationString = ""
                binding.tilTimeNotifications.error = ""
                notificationBoolean = true
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setNotifications() {
       /* binding.etTimeNotifications.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                checkDateTimeNotifications()
            }
        }*/
       /* binding.etTimeNotifications.addTextChangedListener(object : TextWatcher {
              //  var length_before = 0
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    //length_before = s.length
                }
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val valueNotifications = binding.etTimeNotifications.text.toString()
                    Log.d("notificationsValue", "on "+valueNotifications)
                }
                override fun afterTextChanged(s: Editable) {


                }
            })*/
        val c = Calendar.getInstance()
        val mHour = c[Calendar.HOUR_OF_DAY]
        val mMinute = c[Calendar.MINUTE]

        val timePickerDialog = TimePickerDialog(activity,

            { _, hourOfDay, minute ->

                val minH = if (hourOfDay < 10) "0" + hourOfDay else hourOfDay
                val minM = if (minute < 10) "0" + minute else minute

                binding.etTimeNotifications.setText("$minH:$minM")
               // binding.tilNotifications.setEndIconDrawable(R.drawable.ic_baseline_circle_24)
            },
            mHour,
            mMinute,
            true
        )
        timePickerDialog.show()
    }

    private fun chooseDateEndRepeat() {
        val cal = Calendar.getInstance()
        val year1 = cal.get(Calendar.YEAR)
        val month1 = cal.get(Calendar.MONTH)
        val day1 = cal.get(Calendar.DAY_OF_MONTH)
        Calendar.getInstance()
        var year: Int
        var month: Int
        var day: Int


        val endRepeat =
            activity?.let {
                DatePickerDialog(it, { _, yearcur, monthOfYear, dayOfMonth ->
                    cal.set(yearcur, monthOfYear, dayOfMonth)
                    year = cal[Calendar.YEAR]
                    month = cal[Calendar.MONTH]
                    day = cal[Calendar.DAY_OF_MONTH]
                    binding.etDateendRepeat.setText(String.format("%d/%d/%d", year, month + 1, day))
                    timePicker2RepeatDate = year.toString() + "-" + (month + 1).toString() + "-" + day.toString()
                    checkRepeatDate()
                }, year1, month1, day1)
            }
        endRepeat!!.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkRepeatDate() {
        val formatter = SimpleDateFormat("yyyy-M-d")
        //val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        //val startDate: LocalDateTime = LocalDateTime.parse(timePicker1RepeatDate, formatter)
        val tempStartDate = binding.etDatestart.text.toString().split(" ")
        val tempStartDate2 = tempStartDate[0].split("/")

        timePicker1RepeatDate = tempStartDate2[0] + "-" + tempStartDate2[1] + "-" + tempStartDate2[2]

        val startDate = formatter.parse(timePicker1RepeatDate)
        //val endDate: LocalDateTime = LocalDateTime.parse(timePicker2RepeatDate, formatter)
        val endDate = formatter.parse(timePicker2RepeatDate)

        if(startDate == endDate) {
            Toast.makeText(activity, "Время начала и конца урока не могут совпадать.",
                Toast.LENGTH_SHORT).show()
        } else if (startDate != null) {
            if (startDate > endDate) {
                Toast.makeText(activity, "Время начала урока не может превышать время конца урока.", Toast.LENGTH_SHORT).show()
            } else if(startDate < endDate) {
                val diffInMillies: Long = abs(endDate!!.time - startDate.time)
                val diff: Long = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 7
                val calendarLoc = GregorianCalendar.getInstance()
                val tempTime1 = timePicker1RepeatDate.split("-")
                val tempHourStartTime1 = timePicker1RepeatStartHourMinuteDate.split(":")
                val tempHourStartTime2 = timePicker2RepeatEndHourMinuteDate.split(":")


                calendarLoc.set(tempTime1[0].toInt(), tempTime1[1].toInt() - 1, tempTime1[2].toInt())


                dateLessons.add(binding.etDatestart.text.toString())
                dateLessons.add(binding.etDateend.text.toString())
                for (i in 0 until diff) {
                    calendarLoc.add(Calendar.DAY_OF_MONTH, 7)
                    val strAdd = calendarLoc.get(Calendar.YEAR).toString() + "/" + (calendarLoc.get(Calendar.MONTH) + 1).toString() + "/" + calendarLoc.get(Calendar.DAY_OF_MONTH).toString() + " " + tempHourStartTime1.get(0) + ":" + tempHourStartTime1.get(1)
                    val strAdd2 = calendarLoc.get(Calendar.YEAR).toString() + "/" + (calendarLoc.get(Calendar.MONTH) + 1).toString() + "/" + calendarLoc.get(Calendar.DAY_OF_MONTH).toString() + " " + tempHourStartTime2.get(0) + ":" + tempHourStartTime2.get(1)
                    dateLessons.add(strAdd)
                    dateLessons.add(strAdd2)
                }

            }
        }


    }


    private fun setListViewStudent() {

      //  listView = binding.listView
        dataStudentGroupModel = ArrayList()
        dataStudentlList = ViewModelProvider(this)[StudentListViewModel::class.java]
        var studentName: Array<String> = emptyArray()

        dataStudentlList.studentList.observe(viewLifecycleOwner) { it ->
            if(it.isNotEmpty()) {
                for(student in it){
                    val name = student.name + " " + student.lastname
                    val id = student.id
                    studentName += name
                    if(viewModel.lessonsItem.value != null) {
                        viewModel.lessonsItem.observe(viewLifecycleOwner) { item ->
                            var dataString = item.student
                            dataString = dataString.replace("]", "")
                            dataString = dataString.replace("[", "")
                            val lstValues: List<Int> = dataString.split(",").map { it.trim().toInt() }
                            if(lstValues.contains(id)) {
                                dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,true))
                            } else {
                                dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                            }
                        }
                    } else {
                        dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                    }



                }

                binding.cardStudents.setOnClickListener {
                    setStudentLessons()
                }

              //  adapter = ListStudentAdapter(dataStudentGroupModel!!, requireContext().applicationContext)
                /*  listView.adapter = adapter

                  listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                      val dataStudent: DataStudentGroupModel = dataStudentGroupModel!![position]
                      dataStudent.checked = !dataStudent.checked
                      adapter.notifyDataSetChanged()
                  }*/


            } else {

                Toast.makeText(activity, "В приложении нет учеников, добавьте учеников.", Toast.LENGTH_LONG).show()
                sleep(1_500)
                launchFragment()
            }


        }

    }

    private fun setStudentLessons() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Выберите студентов урока")

        val rowList = layoutInflater.inflate(R.layout.list_data, null)
        val listView = rowList.findViewById<ListView>(R.id.listViewData)
        adapter = ListStudentAdapter(dataStudentGroupModel!!, requireContext().applicationContext)
        listView.adapter = adapter
        builder.setView(rowList)

        /*Log.d("dataList", dataList.toString())
        // add a radio button list
        val animals = arrayOf("horse", "cow", "camel", "sheep", "goat", "horse1", "cow2", "camel3", "sheep4", "goat5", "horse2", "cow3", "camel4", "sheep5", "goat6")
        val checkedItem = booleanArrayOf(true, false, false, true, false, true, false, false, true, false, true, false, false, true, false)
        builder.setMultiChoiceItems(animals, checkedItem) { dialog, which, isChecked ->
            // user checked an item
        }
        */
        // add OK and Cancel buttons
        builder.setPositiveButton("Выбрать") { _, _ ->
            // user clicked OK
            //Log.d("dialogValue", checkValidStudentAlertDialog(adapter.arrayList).toString())
        }
        builder.setNegativeButton("Отмена", null)

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun setGroupLessons() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Выберите группы студентов")

        val rowList = layoutInflater.inflate(R.layout.list_data, null)
        val listView = rowList.findViewById<ListView>(R.id.listViewData)
        //adapterGroup = ListGroupAdapter(dataGroupLessonsModel!!, requireContext().applicationContext)
        listView.adapter =  adapterGroup
        builder.setView(rowList)

        builder.setPositiveButton("Выбрать") { _, _ ->
            // user clicked OK
           // Log.d("dialogValue", checkValidStudentAlertDialog(adapterGroup.arrayList).toString())
            Log.d("dialogValue", adapterGroup.arrayList.toString())
        }
        builder.setNegativeButton("Отмена", null)

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }


    private fun setGroupViewStudent() {
       // listViewGroup = binding.listViewGroup
        var groupName: Array<String> = emptyArray()
        dataGroupList = ViewModelProvider(this)[GroupListViewModel::class.java]
        dataGroupLessonsModel = ArrayList()

        dataGroupList.groupList.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                for(group in it){
                    val students = group.student
                    val name = group.title
                    val id = group.id
                    groupName += name
                    dataGroupLessonsModel!!.add(DataGroupLessonsModel(name, students, id,false))
                    if(viewModel.lessonsItem.value != null) {
                        listViewGroup.isInvisible
                    }
                    adapterGroup = ListGroupAdapter(dataGroupLessonsModel!!, requireContext().applicationContext)
                   // listViewGroup.adapter = adapterGroup
                    binding.cardGroupStudent.setOnClickListener {
                        setGroupLessons()
                    }
                }
            } else {
                dataGroupListString = false
                groupName += "в группе пока значений нет."
               // binding.switch1.visibility = View.GONE

            }


        }
    }



    private fun chooseDateLessons() {

        val mTimePicker: TimePickerDialog
        val mTimePickerEnd: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)


        var year: Int
        var month: Int
        var day: Int

        val args = requireArguments()
        val dateAdd = args.getString(DATE_ADD)
        val mode = args.getString(SCREEN_MODE)

        if (mode == MODE_ADD) {
            if (dateAdd == "") {
                val cal = Calendar.getInstance()
                val year1 = cal.get(Calendar.YEAR)
                val month1 = cal.get(Calendar.MONTH)
                val day1 = cal.get(Calendar.DAY_OF_MONTH)
                year = mcurrentTime.get(Calendar.YEAR)
                month = mcurrentTime.get(Calendar.MONTH)
                day = mcurrentTime.get(Calendar.DAY_OF_MONTH)


                val dpd =
                    activity?.let {
                            DatePickerDialog(it, { _, yearcur, monthOfYear, dayOfMonth ->
                                cal.set(yearcur, monthOfYear, dayOfMonth)
                                year = cal[Calendar.YEAR]
                                month = cal[Calendar.MONTH]
                                day = cal[Calendar.DAY_OF_MONTH]
                        }, year1, month1, day1)
                    }
                dpd!!.show()
            } else {
                //log(dateAdd.toString())
                val dateTime = dateAdd!!.split("/")
                val cal = Calendar.getInstance()
                cal.set(dateTime[2].toInt(), dateTime[1].toInt()-1, dateTime[0].toInt())
                year = cal[Calendar.YEAR]
                month = cal[Calendar.MONTH]
                day = cal[Calendar.DAY_OF_MONTH]

            }
        } else {
            year = mcurrentTime.get(Calendar.YEAR)
            month = mcurrentTime.get(Calendar.MONTH)
            day = mcurrentTime.get(Calendar.DAY_OF_MONTH)

        }

        var timePicker1 = ""
        var timePicker2 = ""

        mTimePicker = TimePickerDialog(context,
            { _, hourOfDay, minute ->
                val minH = StringHelpers.timeForLessons(hourOfDay)
                val minM = StringHelpers.timeForLessons(minute)
                binding.etDatestart.setText(String.format("%d/%d/%d %s:%s", year, month + 1, day, minH, minM))
                timePicker1 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + minH + ":" + minM
                timePicker1RepeatStartHourMinuteDate = "$minH:$minM"
                if (timePicker1.isNotEmpty() && timePicker2.isNotEmpty()) {
                    checkAddDateTime(timePicker1, timePicker2)
                }
            }, hour, minute, true)

        mTimePickerEnd = TimePickerDialog(context,
            { _, hourOfDay, minute ->
                val minH = StringHelpers.timeForLessons(hourOfDay)
                val minM = StringHelpers.timeForLessons(minute)
//                Toast.makeText(getActivity(), setValue.toString(), Toast.LENGTH_SHORT).show()
                binding.etDateend.setText(String.format("%d/%d/%d %s:%s", year, month + 1, day, minH, minM))
                timePicker2 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + minH + ":" + minM
                timePicker2RepeatEndHourMinuteDate = "$minH:$minM"
                if (timePicker1.isNotEmpty() && timePicker2.isNotEmpty()) {
                    checkAddDateTime(timePicker1, timePicker2)
                }
            }, hour, minute, true)

        binding.etDatestart.setOnClickListener{
            mTimePicker.show()
        }

        binding.etDateend.setOnClickListener{
            mTimePickerEnd.show()
        }
    }

    private fun checkAddDateTime(valueCheck1: String, valueCheck2: String): Boolean {

        if (valueCheck1.isNotEmpty() && valueCheck2.isNotEmpty()) {

            val formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m")
            val dt: LocalDateTime = LocalDateTime.parse(valueCheck1, formatter)
            val dt2: LocalDateTime = LocalDateTime.parse(valueCheck2, formatter)

            if(dt == dt2) {
                Toast.makeText(activity, "Время начала и конца урока не могут совпадать.",
                    Toast.LENGTH_SHORT).show()
                return false
            } else if (dt > dt2) {
                Toast.makeText(activity, "Время начала урока не может превышать время конца урока.", Toast.LENGTH_SHORT).show()
                return false
            } else if (dt < dt2) {
                val diff: Duration = Duration.between(dt, dt2)
                val minutes = diff.toMinutes()
                return if(minutes < 30) {
                    Toast.makeText(activity, "урок не может быть менее 30 минут",
                        Toast.LENGTH_SHORT).show()
                    false
                } else if (minutes > 180) {
                    Toast.makeText(activity, "урок не может быть больше  3 часов",
                        Toast.LENGTH_SHORT).show()
                    false
                } else {
                    //Toast.makeText(activity, "разница минут $minutes",
                       // Toast.LENGTH_SHORT).show()
                    true
                }

            }
        } else {
            Toast.makeText(activity, "Не все поля с датами были заполнены.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun launchAddMode() {
        binding.tilStudent.visibility = View.GONE
            // binding.etPrice?.setVisibility(View.GONE)
        binding.etStudent.visibility = View.GONE
        binding.saveButton.setOnClickListener{
            validValueNotifications()
            val valueStudent = checkValidStudent()
            /*Toast.makeText(activity, "Студенты урока.$valueStudent",
                Toast.LENGTH_SHORT).show()*/
            val checkField: Boolean
            if (valueStudent.size <= 0) {
                 Toast.makeText(activity, "Урок не может создан без учеников.", Toast.LENGTH_SHORT).show()
                 checkField = viewModel.validateInput(binding.etTitle.text.toString(), valueStudent, binding.etPrice.text.toString(),
                     binding.etDatestart.text.toString(), binding.etDateend.text.toString())
             } else {
                 checkField = viewModel.validateInput(binding.etTitle.text.toString(), valueStudent, binding.etPrice.text.toString(),
                     binding.etDatestart.text.toString(), binding.etDateend.text.toString())
             }

             if(checkField && valueStudent.size > 0 && notificationBoolean) {
                if(binding.etRepeat.isChecked && dateLessons.size >= 2) {
                    for (index in dateLessons.indices step 2) {
                        val lessonsItem = LessonsItem(
                            binding.etTitle.text.toString(),
                            notificationString,
                            valueStudent.toString(),
                            binding.etPrice.text.toString().toInt(),
                            dateLessons.get(index),
                            dateLessons.get(index + 1))
                        checkExistsLessonsCurrentDateTime(dateLessons.get(index), dateLessons.get(index + 1), lessonsItem)
                    }

                 } else {
                    val lessonsItem = LessonsItem(
                        binding.etTitle.text.toString(),
                        notificationString,
                        valueStudent.toString(),
                        binding.etPrice.text.toString().toInt(),
                        binding.etDatestart.text.toString(),
                        binding.etDateend.text.toString())
                    checkExistsLessonsCurrentDateTime(binding.etDatestart.text.toString(), binding.etDateend.text.toString(), lessonsItem)

                 }
             } else {
                 setHideError()
             }
        }
    }

    private fun checkExistsLessonsCurrentDateTime(
        dateTimeStart: String,
        dateTimeEnd: String,
        lessonsItem: LessonsItem
    ) {

        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
        val dateStart = LocalDateTime.parse(dateTimeStart, formatter)
        val dateEnd = LocalDateTime.parse(dateTimeEnd, formatter)
        val current = LocalDateTime.now()
        val formatted = current.format(formatter)
        val currentTime = LocalDateTime.parse(formatted, formatter)
        if(currentTime > dateStart) {
            Toast.makeText(activity, "Урок не может быть добавлен задним числом.", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.lessonsList.observe(viewLifecycleOwner) {
            var ret = true
            it.forEach {
                val dateStartLesson = LocalDateTime.parse(it.dateStart, formatter)
                val dateEndLesson = LocalDateTime.parse(it.dateEnd, formatter)
                if(dateStart in dateStartLesson..dateEndLesson || dateEnd in dateStartLesson..dateEndLesson) {
                        ret = false
                        return@forEach
                    }
            }

            if (ret) {
                binding.tilDatestart.error = ""
                binding.tilDateend.error = ""
                viewModel.addLessonsItem(lessonsItem.title, lessonsItem.notifications, lessonsItem.student,
                    lessonsItem.price.toString(), lessonsItem.dateStart, lessonsItem.dateEnd)
            } else {
                binding.etDatestart.setText("")
                binding.etDateend.setText("")
                binding.tilDatestart.error = "На это время урок существует, выберите другое время начала урока"
                binding.tilDateend.error = "На это время урок существует, выберите другое время конца урока"
                return@observe
            }

        }


    }


    private fun checkValidStudent(): HashSet<Int> {
        val lstValues: ArrayList<Int> = ArrayList()
        if ((::adapter.isInitialized && ::adapterGroup.isInitialized) || ::adapter.isInitialized) {


            if (!adapterGroup.isEmpty) {
                adapterGroup.arrayList.forEach {
                    StringHelpers.getStudentIds(it).forEach { studentId ->
                        lstValues.add(studentId)
                    }
                }
            }
            if (adapter.arrayList.isNotEmpty()) {
                adapter.arrayList.forEach {
                    lstValues.add(it)
                }
            }

            return HashSet(lstValues)
        } else if (::adapterGroup.isInitialized) {
            if (!adapterGroup.isEmpty) {
                adapterGroup.arrayList.forEach {
                    StringHelpers.getStudentIds(it).forEach { studentId ->
                        lstValues.add(studentId)
                    }
                }
            }
            return HashSet(lstValues)
        } else {
            return HashSet(lstValues)
        }
    }

    private fun lessonsTextChangeListeners() {
        binding.etTitle.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputTitle()
                setHideError()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.etPrice.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputPrice()
                setHideError()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.etDatestart.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputDateStart()
                setHideError()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.etDateend.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputDateEnd()
                setHideError()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }

    private fun setHideError() {
        if(viewModel.errorInputTitle.value == true) {
            binding.tilTitle.error = "Введите название урока"
        } else {
            binding.tilTitle.error = ""
        }
        if(viewModel.errorInputPrice.value == true) {
            binding.tilPrice.error = "Введите значение цены урока"
        } else {
            binding.tilPrice.error = ""
        }
        if(viewModel.errorInputStudent.value == true) {
            if(checkValidStudent().isNotEmpty()) {
                viewModel.resetErrorInputStudent()
                with(binding) { textViewChangeStateCheckbox.setTextColor(ContextCompat.getColor(requireContext().applicationContext,R.color.custom_calendar_days_bar_background)) }
            } else {
                with(binding) { textViewChangeStateCheckbox.setTextColor(ContextCompat.getColor(requireContext().applicationContext,R.color.custom_calendar_weekend_days_bar_text_color)) }
            }
        } else {
            with(binding) { textViewChangeStateCheckbox.setTextColor(ContextCompat.getColor(requireContext().applicationContext,R.color.custom_calendar_days_bar_background)) }
        }
        if(viewModel.errorInputDateStart.value == true) {
            binding.tilDatestart.error = "Выберите время начала урока"
        } else {
            binding.tilDatestart.error = ""
        }
        if(viewModel.errorInputDateEnd.value == true) {
            binding.tilDateend.error = "Выберите время конца урока"
        } else {
            binding.tilDateend.error = ""
        }
    }



    private fun observeViewModel() {
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
    }

    interface OnEditingFinishedListener {
        fun onEditingFinished()
    }

    private fun launchFragment() {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.studentItemListFragment, null, animationOptions)
    }


    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        args.getString(SCREEN_MODE)

    }



    companion object {

        const val SCREEN_MODE = "extra_mode"
        const val MODE_ADD = "mode_add"
        const val DATE_ADD = "date_add"

    }


}