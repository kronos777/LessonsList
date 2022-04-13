package com.example.lessonslist.presentation.group

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.data.student.StudentListRepositoryImpl
import com.example.lessonslist.databinding.FragmentGroupItemBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.student.GetStudentItemListUseCase
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.MainViewModel
import com.example.lessonslist.presentation.lessons.LessonsListViewModel
import com.example.lessonslist.presentation.student.StudentItemFragment


class GroupItemFragment : Fragment() {

    private lateinit var viewModel: GroupItemViewModel
    private lateinit var viewModelStudent: MainViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentGroupItemBinding? = null
    private val binding: FragmentGroupItemBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")

    private var screenMode: String = MODE_UNKNOWN
    private var groupItemId: Int = GroupItem.UNDEFINED_ID


    private lateinit var adapter: GroupStudentListAdapter
   /* private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null
    private lateinit var listView: ListView

*/

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
        _binding = FragmentGroupItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[GroupItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //addTextChangeListeners()
        launchRightMode()
        observeViewModel()



        viewModelStudent = ViewModelProvider(this)[MainViewModel::class.java]
        viewModelStudent.studentList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        /*

        listView = binding.listView as ListView
        dataStudentGroupModel = ArrayList<DataStudentGroupModel>()


        viewModelStudent = ViewModelProvider(this)[MainViewModel::class.java]
        val dataStudentList = viewModelStudent.studentList.value
        if (dataStudentList != null) {
            for(student in dataStudentList){
                val name = student.name + " " + student.lastname
                val id = student.id
                Log.d("listname", name)
                Log.d("id", id.toString())
                dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
            }
        }*/

            /*.observe(viewLifecycleOwner) {
           // shopListAdapter.submitList(it)
            for(student in it){
                val name = student.name + " " + student.lastname
                val id = student.id
                Log.d("listname", name)
                Log.d("id", id.toString())
                dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
            }

        }
*/

        /*
        dataStudentGroupModel!!.add(DataStudentGroupModel("Apple Pie", 4,false))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Banana Bread", false))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Cupcake", false))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Donut", true))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Eclair", true))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Froyo", true))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Gingerbread", true))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Honeycomb", false))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Ice Cream Sandwich", false))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Jelly Bean", false))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Kitkat", false))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Lollipop", false))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Marshmallow", false))
        dataStudentGroupModel!!.add(DataStudentGroupModel("Nougat", false))*/
/*  adapter = ListStudentAdapter(dataStudentGroupModel!!, requireContext().applicationContext)
 listView.adapter = adapter
 listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
     val dataStudentGroupModel: DataStudentGroupModel =
         dataStudentGroupModel!![position] as DataStudentGroupModel
     dataStudentGroupModel.checked = !dataStudentGroupModel.checked
     adapter.notifyDataSetChanged()
 }
 */
}

private fun addTextChangeListeners() {
 TODO("Nyet implemented")
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
     viewModel.editGroupItem(
         binding.etTitle.text.toString(),
         binding.etDescription.text.toString(),
         binding.etStudent.text.toString()
     )
 }
}

private fun launchAddMode() {
 binding.saveButton.setOnClickListener{
     viewModel.addGroupItem(
         binding.etTitle.text.toString(),
         binding.etDescription.text.toString(),
         binding.etStudent.text.toString()
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

 private const val SCREEN_MODE = "extra_mode"
 private const val GROUP_ITEM_ID = "extra_group_item_id"
 private const val MODE_EDIT = "mode_edit"
 private const val MODE_ADD = "mode_add"
 private const val MODE_UNKNOWN = ""

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

