package com.example.lessonslist.presentation.lessons

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentLessonsItemEditBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.presentation.MainViewModel
import com.example.lessonslist.presentation.group.DataStudentGroupModel
import com.example.lessonslist.presentation.group.ListStudentAdapter
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class LessonsItemEditFragment : Fragment() {

    private lateinit var viewModel: LessonsItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentLessonsItemEditBinding? = null
    private val binding: FragmentLessonsItemEditBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")


    private var screenMode: String = MODE_UNKNOWN
    private var lessonsItemId: Int = LessonsItem.UNDEFINED_ID



    private lateinit var adapter: ListStudentAdapter
    private lateinit var listView: ListView
    private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null
    private lateinit var dataStudentlList: MainViewModel

    private lateinit var viewModelPayment: PaymentListViewModel

    private var dataPaymentStudentModel: ArrayList<DataPaymentStudentLessonsModel>? = null


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
        _binding = FragmentLessonsItemEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Урок"

        viewModel = ViewModelProvider(this)[LessonsItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        launchRightMode()
        observeViewModel()


        binding.tilStudent.setVisibility (View.GONE)


        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem4).isChecked = true


        listView = binding.listView

        dataStudentlList = ViewModelProvider(this)[MainViewModel::class.java]
        dataStudentGroupModel = ArrayList<DataStudentGroupModel>()
        var studentName: Array<String> = emptyArray()

       // binding.layoutInfo.setVisibility (View.GONE)

        dataPaymentStudentModel = ArrayList<DataPaymentStudentLessonsModel>()
        viewModelPayment = ViewModelProvider(this)[PaymentListViewModel::class.java]
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            if(it.size > 0) {
                for (payment in it) {
                    if(payment.lessonsId == lessonsItemId) {
                        if (payment.enabled == true) {
                            dataPaymentStudentModel!!.add(DataPaymentStudentLessonsModel(payment.student ," Оплачен: ", payment.price.toString()))
                        } else {
                            dataPaymentStudentModel!!.add(DataPaymentStudentLessonsModel(payment.student, " Долг: ", "-" + payment.price.toString()))
                        }

                    }
                }
            }


            if(dataPaymentStudentModel!!.size > 0) {
                val adapter =  ListPaymentLessonsAdapter(dataPaymentStudentModel!!, requireContext().applicationContext)
                listView.adapter = adapter
                setFocusableEditText()
            } else {
                binding.textViewPriceInfo.setVisibility (View.GONE)
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
                                    } else {
                                        dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                                    }
                                }
                            } else {
                                dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                            }



                        }


                        adapter = ListStudentAdapter(dataStudentGroupModel!!, requireContext().applicationContext)

                        listView.adapter = adapter

                    } else {

                        studentName += "в учениках пока нет значений"
                    }
                }
            }

            goLessonsListFragmentBackPressed()

        }





        val mTimePicker: TimePickerDialog
        val mTimePickerEnd: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)


        var year: Int = mcurrentTime.get(Calendar.YEAR)
        var month: Int = mcurrentTime.get(Calendar.MONTH)
        var day: Int = mcurrentTime.get(Calendar.DAY_OF_MONTH)

        var timePicker1 = ""
        var timePicker2 = ""


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




    fun setFocusableEditText() {
        //binding.saveButton.setVisibility (View.GONE)
        binding.tilPrice.setVisibility (View.GONE)
        binding.saveButton.text = "Список уроков."
        binding.etTitle.setBackgroundResource(R.color.white)
        binding.etTitle.isFocusable = false
        binding.etDatestart.setBackgroundResource(R.color.white)
        binding.etDateend.setBackgroundResource(R.color.white)
        binding.textViewChangeStateCheckbox.text = "Список платежей:"
        binding.etDatestart.setOnClickListener{
             false
        }
        binding.etDateend.setOnClickListener{
             false
        }
        binding.saveButton.setOnClickListener {
            //launchFragment(LessonsItemListFragment.newInstanceNoneParams())
            launchLessonsListFragment()
        }

//        binding.saveButton

     /*   binding.saveButton.setVisibility (View.GONE)
        binding.layoutInfo.setVisibility (View.VISIBLE)
        //binding.textViewChangeStateCheckbox.setVisibility (View.GONE)

        binding.textViewChangeStateCheckbox.isFocusable = false*/
    }



    private fun launchLessonsListFragment() {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.CUSTOM_LIST)
        }

        navController.navigate(R.id.lessonsItemListFragment, btnArgsLessons)
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

            val formatter = DateTimeFormatter.ofPattern("yyyy-M-dd HH:m")
            val dt: LocalDateTime = LocalDateTime.parse(valueCheck1, formatter)
            val dt2: LocalDateTime = LocalDateTime.parse(valueCheck2, formatter)


            if(dt == dt2) {
                Toast.makeText(activity, "Время начала и конца урока не могут совпадать.", Toast.LENGTH_SHORT).show()
                return false
            } else if (dt > dt2) {
                Toast.makeText(activity, "Время начала урока не может превышать время конца урока.", Toast.LENGTH_SHORT).show()
                return false
            } else if (dt < dt2) {
                val diff: Duration = Duration.between(dt, dt2)
                val minutes = diff.toMinutes()
                if(minutes < 30) {
                    Toast.makeText(activity, "урок не может быть менее 30 минут", Toast.LENGTH_SHORT).show()
                    return false
                } else {
                    Toast.makeText(activity, "разница минут" + minutes.toString(), Toast.LENGTH_SHORT).show()
                    return true
                }

            }
        } else {
            Toast.makeText(activity, "Не все поля с датами были заполнены.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }




    private fun launchRightMode() {
        Log.d("screenMode", screenMode)
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
        }
    }


    private fun launchEditMode() {
        viewModel.getLessonsItem(lessonsItemId)
            binding.saveButton.setOnClickListener{
            var studentIds: String = adapter.arrayList.toString()
            viewModel.editLessonsItem(
                binding.etTitle.text.toString(),
                "",
                studentIds,
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
        if (mode != MODE_EDIT) {
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(LESSONS_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            lessonsItemId = args.getInt(LESSONS_ITEM_ID, GroupItem.UNDEFINED_ID)

        }

    }

    private fun goLessonsListFragmentBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            val arguments = Bundle().apply {
                putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.CUSTOM_LIST)
            }
            navController.popBackStack(R.id.lessonsItemListFragment, true)
            navController.navigate(R.id.lessonsItemListFragment, arguments)
        }
    }


    companion object {

        const val SCREEN_MODE = "extra_mode"
        const val LESSONS_ITEM_ID = "extra_lessons_item_id"
        const val MODE_EDIT = "mode_edit"
        const val MODE_UNKNOWN = ""


        fun newInstanceEditItem(lessonsItemId: Int): LessonsItemEditFragment {
            return LessonsItemEditFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(LESSONS_ITEM_ID, lessonsItemId)
                }
            }
        }
    }
}

