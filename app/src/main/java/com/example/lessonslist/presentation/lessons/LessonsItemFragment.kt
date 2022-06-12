package com.example.lessonslist.presentation.lessons

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.databinding.FragmentLessonsItemBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.presentation.MainViewModel
import com.example.lessonslist.presentation.group.DataStudentGroupModel
import com.example.lessonslist.presentation.group.GroupListViewModel
import com.example.lessonslist.presentation.group.ListStudentAdapter
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import java.util.*


class LessonsItemFragment : Fragment() {

    private lateinit var viewModel: LessonsItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentLessonsItemBinding? = null
    private val binding: FragmentLessonsItemBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")

    private var screenMode: String = MODE_UNKNOWN
    private var lessonsItemId: Int = LessonsItem.UNDEFINED_ID



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
     //   return inflater.inflate(R.layout.fragment_group_item, container, false)
        _binding = FragmentLessonsItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Урок"

        viewModel = ViewModelProvider(this)[LessonsItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //addTextChangeListeners()
        launchRightMode()
        observeViewModel()



        listView = binding.listView

        dataStudentlList = ViewModelProvider(this)[MainViewModel::class.java]
        dataStudentGroupModel = ArrayList<DataStudentGroupModel>()

        dataStudentlList.studentList.observe(viewLifecycleOwner) {
            if(it.size > 0) {
                for(student in it){
                    val name = student.name + " " + student.lastname
                    val id = student.id
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

                listView.adapter = adapter
            } else {
                log("в учениках пока нет значений")
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

      /*  val year = mcurrentTime.get(Calendar.YEAR)
        val month = mcurrentTime.get(Calendar.MONTH)
        val day = mcurrentTime.get(Calendar.DAY_OF_MONTH)
*/
        val year: Int
        val month: Int
        val day: Int

        val args = requireArguments()
        val dateAdd = args.getString(DATE_ADD)


        val mode = args.getString(SCREEN_MODE)
        if (mode == MODE_ADD) {
            binding.paymentLesson?.setVisibility (View.GONE)
           if (dateAdd == "") {
                    log("string date add is null")
                    year = mcurrentTime.get(Calendar.YEAR)
                    month = mcurrentTime.get(Calendar.MONTH)
                    day = mcurrentTime.get(Calendar.DAY_OF_MONTH)

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
            binding.paymentLesson.setOnClickListener {
                launchFragment(PaymentItemListFragment.newInstanceLessonsId(lessonsItemId))
            }
           }


        mTimePicker = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.etDatestart.setText(String.format("%d/%d/%d %d : %d", year, month + 1, day, hourOfDay, minute))
            }
        }, hour, minute, false)

        mTimePickerEnd = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.etDateend.setText(String.format("%d/%d/%d %d : %d", year, month + 1, day, hourOfDay, minute))
            }
        }, hour, minute, false)

         binding.etDatestart.setOnClickListener{
             mTimePicker.show()
         }

        binding.etDateend.setOnClickListener{
            mTimePickerEnd.show()
        }


    }

    private fun getStudentsOfString(student: String) : List<Int> {
        //val ss = student.joinToString()
        val ss = student
        ss.replace("]", "")
        ss.replace("[", "")
        val lstValues: List<Int> = ss.split(",").map { it -> it.trim().toInt() }
        return lstValues.distinct()
    }



    private fun addTextChangeListeners() {
        TODO("Not yet implemented")
    }


    private fun launchRightMode() {
        Log.d("screenMode", screenMode)
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
           // else -> launchEditMode()
        }
    }


    private fun launchEditMode() {
      //  binding.etStudent.setVisibility(View.GONE)
        binding.etPriceAdd?.setVisibility(View.GONE)
        binding.listViewGroup.setVisibility(View.GONE)
        viewModel.getLessonsItem(lessonsItemId)
      //  binding.etPrice.text = viewModel.lessonsItem.price
            binding.saveButton.setOnClickListener{
            var studentIds: String = adapter.arrayList.toString()
            viewModel.editLessonsItem(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                //binding.etStudent.text.toString(),
                studentIds,
                binding.etPrice.text.toString(),
                binding.etDatestart.text.toString(),
                binding.etDateend.text.toString()
            )
        }
    }



    private fun launchAddMode() {
        binding.etPrice?.setVisibility(View.GONE)
        binding.etStudent.setVisibility(View.GONE)
        binding.saveButton.setOnClickListener{
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
            /*   Log.d("allStudent", noD.toString())
             var allStudent: ArrayList<String> = ArrayList()
              allStudent.add(studentIds)
              allStudent.add(groupStudentIds)*/
          //  var resultStudent = getStudentsOfString(studentIds)

            viewModel.addLessonsItem(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                noD.toString(),
                //binding.etStudent.text.toString(),
                binding.etPriceAdd?.text.toString(),
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
        if (mode != MODE_EDIT && mode != MODE_ADD) {
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

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "DateCalendar: $message")
    }

    companion object {

        private const val SCREEN_MODE = "extra_mode"
        private const val LESSONS_ITEM_ID = "extra_lessons_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""
        private const val DATE_ADD = "date_add"

        fun newInstanceAddItem(date: String): LessonsItemFragment {
            return LessonsItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                    putString(DATE_ADD, date)
                }
            }
        }

        fun newInstanceEditItem(lessonsItemId: Int): LessonsItemFragment {
            return LessonsItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(LESSONS_ITEM_ID, lessonsItemId)
                }
            }
        }
    }
}

