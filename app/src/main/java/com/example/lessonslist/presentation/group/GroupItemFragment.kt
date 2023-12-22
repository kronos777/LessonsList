package com.example.lessonslist.presentation.group

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentGroupItemBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.presentation.helpers.NavigationOptions
import com.example.lessonslist.presentation.helpers.StringHelpers
import com.example.lessonslist.presentation.student.StudentListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class GroupItemFragment : Fragment() {

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener


    private var _binding: FragmentGroupItemBinding? = null
    private val binding: FragmentGroupItemBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")

    private var screenMode: String = MODE_UNKNOWN
    private var groupItemId: Int = GroupItem.UNDEFINED_ID


    private lateinit var adapter: ListStudentAdapter
    private lateinit var listView: ListView
    private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null

    private val viewModel by lazy {
      ViewModelProvider(this)[GroupItemViewModel::class.java]
    }

    private val dataStudentlList by lazy {
        ViewModelProvider(this)[StudentListViewModel::class.java]
    }

    private val navController by lazy {
        (activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment).navController
    }

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
        _binding = FragmentGroupItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Группа"


        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        launchRightMode()
        observeViewModel()

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem3).isChecked = true



        binding.tilStudent.visibility = View.GONE
        listView = binding.listView

        dataStudentGroupModel = ArrayList()

        dataStudentlList.studentList.observe(viewLifecycleOwner) {
            for(student in it){
                val name = student.name + " " + student.lastname
                val id = student.id
                if(viewModel.groupItem.value != null) {
                    viewModel.groupItem.observe(viewLifecycleOwner) { group ->
                        val lstValues: List<Int> = StringHelpers.getStudentIds(group.student)
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


        }

        addTextChangeListeners()
        goGroupListFragmentBackPressed()
    }

    private fun goGroupListFragmentBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController.popBackStack(R.id.groupItemListFragment, true)
            navController.navigate(R.id.groupItemListFragment, null, NavigationOptions().invoke())
        }
    }


    private fun setHideError() {
        if(viewModel.errorInputTitle.value == true) {
            binding.tilTitle.error = "Введите название группы"
        } else {
            binding.tilTitle.error = ""
        }
        if(viewModel.errorInputStudent.value == true) {
            Toast.makeText(activity, "Группа не может быть создана без учеников.", Toast.LENGTH_LONG).show()
        }
    }




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
        when (screenMode) {
          MODE_EDIT -> launchEditMode()
          MODE_ADD -> launchAddMode()
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
            setHideError()
             return@setOnClickListener
        } else {
            binding.textViewChangeStateCheckbox.setTextColor(ContextCompat.getColor(requireContext().applicationContext,R.color.custom_calendar_date_weekend_background))
            checkField = viewModel.validateInput(binding.etTitle.text.toString())

        }

        if(checkField && valueStudent.size > 0) {
            viewModel.checkExistsGroupItem(binding.etTitle.text.toString().trim())
            viewModel.checkExistsGroupItem.observe(viewLifecycleOwner) {
             if(it == null) {
                 viewModel.addGroupItem(
                     binding.etTitle.text.toString(),
                     binding.etDescription.text.toString(),
                     valueStudent.toString()
                 )
             }  else {
                 Toast.makeText(activity, "Группа с таким названием уже существует.", Toast.LENGTH_SHORT).show()
             }
            }

        } else {
            setHideError()
        }
       }
    }
    private fun checkValidStudent(): HashSet<Int?> {
        val lstValues: ArrayList<Int> = ArrayList()

        adapter.arrayList.forEach {
            lstValues.add(it)
        }


        return HashSet(lstValues)

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


    }

}

