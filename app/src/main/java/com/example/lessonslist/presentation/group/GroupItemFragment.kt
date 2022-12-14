package com.example.lessonslist.presentation.group

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentGroupItemBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.presentation.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class GroupItemFragment : Fragment() {

    private lateinit var viewModel: GroupItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

//    private var _bindingItem: RowGroupStudentItemBinding? = null
   // private lateinit var bindingItem: RowGroupStudentItemBinding
  //      get() = _bindingItem ?: throw RuntimeException("RowGroupItemBinding == null")


    private var _binding: FragmentGroupItemBinding? = null
    private val binding: FragmentGroupItemBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")

    private var screenMode: String = MODE_UNKNOWN
    private var groupItemId: Int = GroupItem.UNDEFINED_ID


    private lateinit var adapter: ListStudentAdapter
    private lateinit var listView: ListView
    private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null
    private lateinit var dataStudentlList: MainViewModel



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
     //   return inflater.inflate(R.layout.fragment_group_item, container, false)
        _binding = FragmentGroupItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Группа"


        viewModel = ViewModelProvider(this)[GroupItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //addTextChangeListeners()
        launchRightMode()
        observeViewModel()

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem3).isChecked = true



        binding.tilStudent.setVisibility (View.GONE)

        listView = binding.listView

        dataStudentlList = ViewModelProvider(this)[MainViewModel::class.java]
        dataStudentGroupModel = ArrayList<DataStudentGroupModel>()

        dataStudentlList.studentList.observe(viewLifecycleOwner) {

            for(student in it){
                val name = student.name + " " + student.lastname
                val id = student.id
                //Log.d("listname", name)
                //Log.d("viewModelgroupItem", viewModel.groupItem.value.toString())
                if(viewModel.groupItem.value != null) {
                    viewModel.groupItem.observe(viewLifecycleOwner) {
                        var dataString = it.student
                        dataString = dataString.replace("]", "")
                        dataString = dataString.replace("[", "")
                        val lstValues: List<Int> = dataString.split(",").map { it.trim().toInt() }
                        if(lstValues.contains(id)) {
                            dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,true))
                            // ListStudentAdapter.arrayList.add(id)
                        } else {
                            dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                        }
                        /*lstValues.forEach { it ->
                            if(it.toInt() == id) {
                                dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,true))
                            } else if (it.toInt() != id) {
                                dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                            }
                            //   Log.i("Values", "value=$it")
                            //Do Something
                        }*/

                        // Log.d("dataString", dataString)
                    }
                } else {
                    dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                }



                //dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
            }

            adapter = ListStudentAdapter(dataStudentGroupModel!!, requireContext().applicationContext)

            listView.adapter = adapter

            /*listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                Toast.makeText(activity, "Work :-)", Toast.LENGTH_SHORT).show()
                val dataStudentGroupModel: DataStudentGroupModel =
                    dataStudentGroupModel!![position] as DataStudentGroupModel

                dataStudentGroupModel.checked = !dataStudentGroupModel.checked
               // Log.d("checkedItemPosition", listView[position].toString())
                adapter.notifyDataSetChanged()

            }*/
        }

        //listView


        //listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->  } { _, _, position, _ ->
        //listView.onItemClickListener = AdapterView.OnItemClickListener { adapter, view, position, _ ->

       /* var checkBox: CheckBox = CheckBox(context)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(activity,isChecked.toString(),Toast.LENGTH_SHORT).show()
        }*/
       //    touchListener(listView)
       // onCheckboxClicked(listView.findViewWithTag("CheckBox"))
        addTextChangeListeners()
        goGroupListFragmentBackPressed()
    }

    private fun goGroupListFragmentBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            navController.popBackStack(R.id.groupItemListFragment, true)
            val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
                .setExitAnim(R.anim.slide_in_right)
                .setPopEnterAnim(R.anim.slide_out_left)
                .setPopExitAnim(R.anim.slide_out_right).build()
            navController.navigate(R.id.groupItemListFragment, null, animationOptions)
        }
    }


    private fun setHideError() {
        if(viewModel.errorInputTitle.value == true) {
            binding.tilTitle.error = "Введите название группы"
        } else {
            binding.tilTitle.error = ""
        }
        if(viewModel.errorInputStudent.value == true) {
            //binding.tilTitle.error = "Введите название группы"
            Toast.makeText(activity, "Группа не может быть создана без учеников.", Toast.LENGTH_LONG).show()
        } else {
            //binding.tilTitle.error = ""
        }
    }

    /*private fun touchListener(view: View) {
        view.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                Toast.makeText(activity, "you just touch the screen :-)", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
    */


    private fun addTextChangeListeners() {

        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputTitle()
                setHideError()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
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

        viewModel.getGroupItem(groupItemId)
        binding.saveButton.setOnClickListener{
            var studentIds: String = adapter.arrayList.toString()
            studentIds = studentIds.replace("[", "")
            studentIds = studentIds.replace("]", "")
            if(viewModel.validateInput(binding.etTitle.text.toString())) {
                  viewModel.editGroupItem(
                      binding.etTitle.text.toString(),
                      binding.etDescription.text.toString(),
                    //  binding.etStudent.text.toString()
                      studentIds
                  )
                } else {
                    setHideError()
                }
            }
    }

    private fun launchAddMode() {
    binding.saveButton.setOnClickListener{
        val valueStudent = checkValidStudent()
        val checkField: Boolean
        if(valueStudent.size <= 0) {
            Toast.makeText(activity, "Без учеников группа не может быть создана.", Toast.LENGTH_LONG).show()
            binding.textViewChangeStateCheckbox.setTextColor(ContextCompat.getColor(requireContext().applicationContext,R.color.custom_calendar_weekend_days_bar_text_color))
            checkField = viewModel.validateInput(binding.etTitle.text.toString())
            setHideError()
            //with(binding) { textViewChangeStateCheckbox.setTextColor(ContextCompat.getColor(requireContext().applicationContext,R.color.custom_calendar_weekend_days_bar_text_color)) }
             return@setOnClickListener
        } else {
            binding.textViewChangeStateCheckbox.setTextColor(ContextCompat.getColor(requireContext().applicationContext,R.color.custom_calendar_date_weekend_background))
            checkField = viewModel.validateInput(binding.etTitle.text.toString())

        }

        if(checkField == true && valueStudent.size > 0) {
            viewModel.addGroupItem(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                valueStudent.toString()
            )
        } else {
            setHideError()
        }
       }
    }
    private fun checkValidStudent(): HashSet<Int?> {
        val studentIds: String = adapter.arrayList.toString()
        val lstValues: ArrayList<Int> = ArrayList()

        studentIds.forEach {
            if (it.isDigit()) {
                val str = it.toString()
                lstValues.add(str.toInt())
            }
        }


        return HashSet(lstValues)

    }

    private fun getStudentIds(dataString: String): List<Int> {
        var dataStr = dataString.replace("]", "")
        dataStr = dataStr.replace("[", "")
        return dataStr.split(",").map { it.trim().toInt() }
    }

    private fun observeViewModel() {
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
          onEditingFinishedListener.onEditingFinished()
        }
    }

    interface OnEditingFinishedListener {
        fun onEditingFinished()
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
          if (!args.containsKey(GROUP_ITEM_ID)) {
              throw RuntimeException("Param shop item id is absent")
          }
          groupItemId = args.getInt(GROUP_ITEM_ID, GroupItem.UNDEFINED_ID)
        }
    }

    companion object {

        const val SCREEN_MODE = "extra_mode"
        const val GROUP_ITEM_ID = "extra_group_item_id"
        const val MODE_EDIT = "mode_edit"
        const val MODE_ADD = "mode_add"
        const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): GroupItemFragment {
          return GroupItemFragment().apply {
              arguments = Bundle().apply {
                  putString(SCREEN_MODE, MODE_ADD)
              }
          }
        }

        fun newInstanceEditItem(groupItemId: Int): GroupItemFragment {
          return GroupItemFragment().apply {
              arguments = Bundle().apply {
                  putString(SCREEN_MODE, MODE_EDIT)
                  putInt(GROUP_ITEM_ID, groupItemId)
              }
          }
        }


    }

}

