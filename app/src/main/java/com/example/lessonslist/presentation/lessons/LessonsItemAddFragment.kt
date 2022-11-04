package com.example.lessonslist.presentation.lessons

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.databinding.FragmentLessonsItemAddBinding
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.presentation.MainViewModel
import com.example.lessonslist.presentation.group.DataStudentGroupModel
import com.example.lessonslist.presentation.group.GroupListViewModel
import com.example.lessonslist.presentation.group.ListStudentAdapter
import com.example.lessonslist.presentation.student.StudentItemListFragment
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class LessonsItemAddFragment : Fragment()  {

    private lateinit var viewModel: LessonsItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentLessonsItemAddBinding? = null
    private val binding: FragmentLessonsItemAddBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")


    private var screenMode: String = MODE_UNKNOWN
    private var lessonsItemId: Int = LessonsItem.UNDEFINED_ID

    private var selectionStatesStudent: Boolean = false


    private lateinit var adapter: ListStudentAdapter
    private lateinit var listView: ListView
    private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null
    private lateinit var dataStudentlList: MainViewModel


    private lateinit var adapterGroup: ListGroupAdapter
    private lateinit var listViewGroup: ListView
    private var dataGroupLessonsModel: ArrayList<DataGroupLessonsModel>? = null
    private lateinit var dataGroupList: GroupListViewModel
    private var dataGroupListString: Boolean = true



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
    ): View? {
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
       // launchRightMode()
        launchAddMode()
        observeViewModel()



        binding.tilStudent!!.setVisibility (View.GONE)
        binding.listViewGroup.setVisibility(View.GONE)




        listView = binding.listView

        dataStudentlList = ViewModelProvider(this)[MainViewModel::class.java]
        dataStudentGroupModel = ArrayList<DataStudentGroupModel>()
        var studentName: Array<String> = emptyArray()
        var groupName: Array<String> = emptyArray()


        dataStudentlList.studentList.observe(viewLifecycleOwner) {
            if(it.size > 0) {
                for(student in it){
                    val name = student.name + " " + student.lastname
                    val id = student.id
                    studentName += name
                    if(viewModel.lessonsItem.value != null) {
                        viewModel.lessonsItem.observe(viewLifecycleOwner) {
                            var dataString = it.student
                            dataString = dataString.replace("]", "")
                            dataString = dataString.replace("[", "")
                            val lstValues: List<Int> = dataString.split(",").map { it -> it.trim().toInt() }
                            if(lstValues.contains(id)) {
                                dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,true))
                                // ListStudentAdapter.arrayList.add(id)
                            } else {
                                dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                            }
                        }
                    } else {
                        dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                    }



                }


                adapter = ListStudentAdapter(dataStudentGroupModel!!, requireContext().applicationContext)
                //  openDialog(dataStudentGroupModel)
                listView.adapter = adapter

                listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    val dataStudent: DataStudentGroupModel = dataStudentGroupModel!![position] as DataStudentGroupModel
                    dataStudent.checked = !dataStudent.checked
                    Log.d("checkstate", dataStudent.checked.toString())
                    adapter.notifyDataSetChanged()
                }

            } else {
                //log("в учениках пока нет значений")
                //studentName += "в учениках пока нет значений"
                Toast.makeText(getActivity(), "В приложении нет учеников, добавьте учеников.", Toast.LENGTH_LONG).show()
                sleep(1_500)
                launchFragment(StudentItemListFragment())
            }


        }


/**/
        listViewGroup = binding.listViewGroup

        dataGroupList = ViewModelProvider(this)[GroupListViewModel::class.java]
        dataGroupLessonsModel = ArrayList<DataGroupLessonsModel>()

        dataGroupList.groupList.observe(viewLifecycleOwner) {
            if(it.size > 0) {
                for(group in it){
                    val students = group.student
                    val name = group.title
                    val id = group.id
                    groupName += name
                    Log.d("groupId", name)
                    dataGroupLessonsModel!!.add(DataGroupLessonsModel(name, students, id,false))
                    if(viewModel.lessonsItem.value != null) {
                        listViewGroup.isInvisible
                    }
                    /* if(viewModel.groupItem.value != null) {
                         viewModel.lessonsItem.observe(viewLifecycleOwner) {
                             var dataString = it.student
                             dataString = dataString.replace("]", "")
                             dataString = dataString.replace("[", "")
                             val lstValues: List<Int> = dataString.split(",").map { it -> it.trim().toInt() }
                             if(lstValues.contains(id)) {
                                 dataGroupLessonsModel!!.add(DataGroupLessonsModel(name, id,true))
                                 // ListStudentAdapter.arrayList.add(id)
                             } else {
                                 dataGroupLessonsModel!!.add(DataGroupLessonsModel(name, id,false))
                             }
                         }
                     } else {
                         dataGroupLessonsModel!!.add(DataGroupLessonsModel(name, id,false))
                     }
                 }*/


                    adapterGroup = ListGroupAdapter(dataGroupLessonsModel!!, requireContext().applicationContext)
                    listViewGroup.adapter = adapterGroup

                }
            } else {
                dataGroupListString = false
                log("в группе пока значений нет.")
                groupName += "в группе пока значений нет."
            }


        }




        binding.textViewChangeStateCheckbox.setOnClickListener {
            if(!selectionStatesStudent) {
                binding.listViewGroup.setVisibility(View.VISIBLE)
                binding.listView.setVisibility(View.GONE)
                binding.textViewChangeStateCheckbox.text = "Выберите учеников из списка."
                selectionStatesStudent = true
            } else {
                binding.listViewGroup.setVisibility(View.GONE)
                binding.listView.setVisibility(View.VISIBLE)
                binding.textViewChangeStateCheckbox.text = "Выбрать студентов из групп."
                selectionStatesStudent = false
            }

        }


        /*string list*/
        // dataGroupListString = adapterGroup.arrayList.toString()
        /*string list*/

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
                    getActivity()?.let {
                        DatePickerDialog(it, DatePickerDialog.OnDateSetListener { view, yearcur, monthOfYear, dayOfMonth ->
                            //  val monthOfYear = monthOfYear - 1
                            // Display Selected date in textbox
                            Toast.makeText(activity, "need date lessons" + "You Selected: $dayOfMonth/$monthOfYear/$yearcur", Toast.LENGTH_SHORT).show()

                            cal.set(yearcur, monthOfYear, dayOfMonth)

                            year = cal[Calendar.YEAR]
                            month = cal[Calendar.MONTH]
                            day = cal[Calendar.DAY_OF_MONTH]


                        }, year1, month1, day1)

                    }

                dpd!!.show()


                //year = mcurrentTime.get(Calendar.YEAR)
                //month = mcurrentTime.get(Calendar.MONTH)
                //day = mcurrentTime.get(Calendar.DAY_OF_MONTH)

            } else {
                log(dateAdd.toString())
                val dateTime = dateAdd!!.split("/")
                //val dateTime = Date(dateAdd)
                val cal = Calendar.getInstance()
                /*log(dateTime[0].toString())
                log(dateTime[1].toString())
                log(dateTime[2].toString())*/
                cal.set(dateTime[2].toInt(), dateTime[1].toInt()-1, dateTime[0].toInt())
                year = cal[Calendar.YEAR]
                month = cal[Calendar.MONTH]
                day = cal[Calendar.DAY_OF_MONTH]
                /* log(year.toString())
                 log(month.toString())
                 log(day.toString())*/
            }
        } else {
            year = mcurrentTime.get(Calendar.YEAR)
            month = mcurrentTime.get(Calendar.MONTH)
            day = mcurrentTime.get(Calendar.DAY_OF_MONTH)

        }


        var timePicker1 = ""
        var timePicker2 = ""

        /*    val str = "2022-7-19 16:4"
            val formatter = DateTimeFormatter.ofPattern("yyyy-M-dd HH:m")
            val dateTime: LocalDateTime = LocalDateTime.parse(str, formatter)
            Toast.makeText(activity, "Время начала " + dateTime, Toast.LENGTH_SHORT).show()
    */



        mTimePicker = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.etDatestart.setText(String.format("%d/%d/%d %d : %d", year, month + 1, day, hourOfDay, minute))
                timePicker1 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + hourOfDay.toString() + ":" + minute.toString()
                if (timePicker1.length > 0 && timePicker2.length > 0) {
                    checkAddDateTime(timePicker1, timePicker2)
                }
            }
        }, hour, minute, true)

        mTimePickerEnd = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.etDateend.setText(String.format("%d/%d/%d %d : %d", year, month + 1, day, hourOfDay, minute))
                timePicker2 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + hourOfDay.toString() + ":" + minute.toString()
              /*  val timePicker2Date = LocalDateTime.parse((year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + hourOfDay.toString() + ":" + minute.toString()),
                    DateTimeFormatter.ofPattern("yyyy-M-d H:m"))
                Toast.makeText(activity, "Время начала " + timePicker2Date, Toast.LENGTH_SHORT).show()*/
                if (timePicker1.length > 0 && timePicker2.length > 0) {
                    checkAddDateTime(timePicker1, timePicker2)
                }
            }
        }, hour, minute, true)

        binding.etDatestart.setOnClickListener{
            mTimePicker.show()
        }

        binding.etDateend.setOnClickListener{
            mTimePickerEnd.show()
        }
    }

    fun withMultiChoiceList(listData: Array<String>): ArrayList<String> {

        //val items = arrayOf("Microsoft", "Apple", "Amazon", "Google")
        val items = listData
        val selectedList = ArrayList<Int>()
        val builder = AlertDialog.Builder(getContext())
        val selectedStrings = ArrayList<String>()
        builder.setTitle("Выберите студентов")
        builder.setMultiChoiceItems(items, null
        ) { dialog, which, isChecked ->
            if (isChecked) {
                selectedList.add(which)
            } else if (selectedList.contains(which)) {
                selectedList.remove(Integer.valueOf(which))
            }
        }

        builder.setPositiveButton("ок") { dialogInterface, i ->


            for (j in selectedList.indices) {
                selectedStrings.add(items[selectedList[j]])
            }

            Toast.makeText(getContext(), "Items selected are: " + Arrays.toString(selectedStrings.toTypedArray()), Toast.LENGTH_SHORT).show()


        }

        builder.show()

        return selectedStrings

    }

    private fun checkAddDateTime(valueCheck1: String, valueCheck2: String): Boolean {

        if (valueCheck1.length > 0 && valueCheck2.length > 0) {

            val formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m")
            val dt: LocalDateTime = LocalDateTime.parse(valueCheck1, formatter)
            val dt2: LocalDateTime = LocalDateTime.parse(valueCheck2, formatter)

  /*


            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val dt: LocalDateTime = LocalDateTime.parse(dtTemp.toString(), formatter)
            val dt2: LocalDateTime = LocalDateTime.parse(dt2Temp.toString(), formatter)

                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val dt = format.parse(valueCheck1)
            val dt2 = format.parse(valueCheck2)

            */


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
                if(minutes < 30) {
                    Toast.makeText(activity, "урок не может быть менее 30 минут",
                        Toast.LENGTH_SHORT).show()
                    return false
                } else if (minutes > 180) {
                    Toast.makeText(activity, "урок не может быть больше  3 часов",
                        Toast.LENGTH_SHORT).show()
                    return false
                } else {
                    Toast.makeText(activity, "разница минут" + minutes.toString(),
                        Toast.LENGTH_SHORT).show()
                    return true
                }

            }
        } else {
            Toast.makeText(activity, "Не все поля с датами были заполнены.",
                Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun getStudentsOfString(student: String) : List<Int> {
        //val ss = student.joinToString()
        val ss = student
        ss.replace("]", "")
        ss.replace("[", "")
        val lstValues: List<Int> = ss.split(",").map { it -> it.trim().toInt() }
        return lstValues.distinct()
    }

    fun convertDate(dateString: String): String {
//        return SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
        return dateString.format(formatter)
    }

    private fun addTextChangeListeners() {
        TODO("Not yet implemented")
    }


    private fun launchRightMode() {
        Log.d("screenMode", screenMode)
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            // else -> launchEditMode()
        }
    }




    private fun launchAddMode() {
        binding.tilStudent?.setVisibility(View.GONE)
            // binding.etPrice?.setVisibility(View.GONE)
        binding.etStudent.setVisibility(View.GONE)
        binding.saveButton.setOnClickListener{
            Toast.makeText(getActivity(), "btn click", Toast.LENGTH_SHORT).show()

            var studentIds: String = adapter.arrayList.toString()
            /* var groupStudentIds: String
             if(adapterGroup.arrayList.toString().length > 0) {
                 groupStudentIds = adapterGroup.arrayList.toString()
             }*/
            var groupStudentIds: String
            var allStudent: String
            if(dataGroupListString) {
                groupStudentIds = adapterGroup.arrayList.toString()
                allStudent = studentIds + groupStudentIds
            } else {
                allStudent = studentIds
            }

            var lstValues: ArrayList<Int> = ArrayList()

            allStudent.forEach {
                if(it.isDigit()) {
                    var str = it.toString()
                    lstValues.add(str.toInt())
                    Log.d("allStudent", it.toString())
                }
            }
            var noD = HashSet(lstValues)
            Toast.makeText(getActivity(), "value student string"+noD.toString(), Toast.LENGTH_SHORT).show()
           /* if(stdlistName.size > 0) {


                for (index in dataStudentGroupModel!!.indices) {
                    if(stdlistName.contains(dataStudentGroupModel!!.get(index).name)) {
                        Toast.makeText(getActivity(), dataStudentGroupModel!!.get(index).name, Toast.LENGTH_SHORT).show()
                        Toast.makeText(getActivity(), dataStudentGroupModel!!.get(index).id.toString(), Toast.LENGTH_SHORT).show()
                    }
                }


                return@setOnClickListener
            }*/

            if(noD.size <= 0) {
                Toast.makeText(activity, "Без учеников урок не может быть создан.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            /*   Log.d("allStudent", noD.toString())
             var allStudent: ArrayList<String> = ArrayList()
              allStudent.add(studentIds)
              allStudent.add(groupStudentIds)*/
            //  var resultStudent = getStudentsOfString(studentIds)

            viewModel.addLessonsItem(
                binding.etTitle.text.toString(),
                "",
                noD.toString(),
                //binding.etStudent.text.toString(),
                binding.etPrice.text.toString(),
                binding.etDatestart.text.toString(),
                binding.etDateend.text.toString()
            )
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

    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(com.example.lessonslist.R.id.fragment_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = args.getString(SCREEN_MODE)

    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "DateCalendar: $message")
    }




    companion object {

        private const val SCREEN_MODE = "extra_mode"

        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""
        private const val DATE_ADD = "date_add"

        fun addInstance(date: String): LessonsItemAddFragment {
            return LessonsItemAddFragment().apply {
                arguments = Bundle().apply {
                    putString(LessonsItemAddFragment.SCREEN_MODE, LessonsItemAddFragment.MODE_ADD)
                    putString(LessonsItemAddFragment.DATE_ADD, date)
                }
            }
        }


    }
}

