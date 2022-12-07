package com.example.lessonslist.presentation.lessons

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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentLessonsItemAddBinding
import com.example.lessonslist.presentation.MainViewModel
import com.example.lessonslist.presentation.group.DataStudentGroupModel
import com.example.lessonslist.presentation.group.GroupListViewModel
import com.example.lessonslist.presentation.group.ListStudentAdapter
import com.example.lessonslist.presentation.student.StudentItemListFragment
import java.lang.Thread.sleep
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class LessonsItemAddFragment : Fragment()  {

    private lateinit var viewModel: LessonsItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentLessonsItemAddBinding? = null
    private val binding: FragmentLessonsItemAddBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")


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
       // launchRightMode()
        launchAddMode()
        observeViewModel()
        lessonsTextChangeListeners()


        binding.tilStudent.visibility = View.GONE
        binding.listViewGroup.visibility = View.GONE

        chooseDateLessons()

        setListViewStudent()
        setGroupViewStudent()
        listenSwitchGroup()

    }

    private fun listenSwitchGroup() {
        val switchChoose = binding.switch1
        switchChoose.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                binding.listViewGroup.visibility = View.VISIBLE
                binding.listView.visibility = View.GONE
                binding.textViewChangeStateCheckbox.text = "Список групп."
                Toast.makeText(activity, "isChecked", Toast.LENGTH_SHORT).show()
            } else {
                binding.listViewGroup.visibility = View.GONE
                binding.listView.visibility = View.VISIBLE
                binding.textViewChangeStateCheckbox.text = "Список учеников."
                Toast.makeText(activity, "unchecked", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setListViewStudent() {

        listView = binding.listView
        dataStudentGroupModel = ArrayList()
        dataStudentlList = ViewModelProvider(this)[MainViewModel::class.java]
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
                    val dataStudent: DataStudentGroupModel = dataStudentGroupModel!![position]
                    dataStudent.checked = !dataStudent.checked
                    Log.d("checkstate", dataStudent.checked.toString())
                    adapter.notifyDataSetChanged()
                }

            } else {
                //log("в учениках пока нет значений")
                //studentName += "в учениках пока нет значений"
                Toast.makeText(activity, "В приложении нет учеников, добавьте учеников.", Toast.LENGTH_LONG).show()
                sleep(1_500)
                launchFragment(StudentItemListFragment())
            }


        }

    }

    private fun setGroupViewStudent() {
        listViewGroup = binding.listViewGroup
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
                    listViewGroup.adapter = adapterGroup

                }
            } else {
                dataGroupListString = false
                groupName += "в группе пока значений нет."
                binding.switch1.visibility = View.GONE

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
                val minH = if (hourOfDay < 10) "0" + hourOfDay else hourOfDay
                val minM = if (minute < 10) "0" + minute else minute
                binding.etDatestart.setText(String.format("%d/%d/%d %s : %s", year, month + 1, day, minH, minM))
                timePicker1 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + minH.toString() + ":" + minM.toString()
                if (timePicker1.isNotEmpty() && timePicker2.isNotEmpty()) {
                    checkAddDateTime(timePicker1, timePicker2)
                }
            }, hour, minute, true)

        mTimePickerEnd = TimePickerDialog(context,
            { _, hourOfDay, minute ->
                val minH = if (hourOfDay < 10) "0" + hourOfDay else hourOfDay
                val minM = if (minute < 10) "0" + minute else minute
//                Toast.makeText(getActivity(), setValue.toString(), Toast.LENGTH_SHORT).show()
                binding.etDateend.setText(String.format("%d/%d/%d %s : %s", year, month + 1, day, minH, minM))
                timePicker2 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + minH.toString() + ":" + minM.toString()
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
                    Toast.makeText(activity, "разница минут $minutes",
                        Toast.LENGTH_SHORT).show()
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
            val valueStudent = checkValidStudent()
            var checkField = false
            if (valueStudent.size <= 0) {
                Toast.makeText(activity, "Урок не может создан без учеников", Toast.LENGTH_SHORT).show()
                checkField = viewModel.validateInput(binding.etTitle.text.toString(), valueStudent, binding.etPrice.text.toString(),
                    binding.etDatestart.text.toString(), binding.etDateend.text.toString())
            } else {
                checkField = viewModel.validateInput(binding.etTitle.text.toString(), valueStudent, binding.etPrice.text.toString(),
                    binding.etDatestart.text.toString(), binding.etDateend.text.toString())
            }
                /*val checkField = viewModel.validateInput(binding.etTitle.text.toString(), valueStudent, binding.etPrice.text.toString(),
                binding.etDatestart.text.toString(), binding.etDateend.text.toString())*/
            if(checkField && valueStudent.size > 0) {
                viewModel.addLessonsItem(
                    binding.etTitle.text.toString(),
                    "",
                    valueStudent.toString(),
                    //binding.etStudent.text.toString(),
                    binding.etPrice.text.toString(),
                    binding.etDatestart.text.toString(),
                    binding.etDateend.text.toString()
                )
            } else {
                setHideError()
            }
        }
    }

    private fun checkValidStudent(): HashSet<Int?> {
        val studentIds: String = adapter.arrayList.toString()
        val groupStudentIds: String
        val allStudent: String
        if (dataGroupListString) {
            groupStudentIds = adapterGroup.arrayList.toString()
            allStudent = studentIds + groupStudentIds
        } else {
            allStudent = studentIds
        }

        val lstValues: ArrayList<Int> = ArrayList()

        allStudent.forEach {
            if (it.isDigit()) {
                val str = it.toString()
                lstValues.add(str.toInt())
            }
        }


        return HashSet(lstValues)

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

    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_item_container, fragment)
            .addToBackStack(null)
            .commit()
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