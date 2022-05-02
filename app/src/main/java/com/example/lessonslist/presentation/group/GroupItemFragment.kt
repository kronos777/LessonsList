package com.example.lessonslist.presentation.group

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.databinding.FragmentGroupItemBinding
import com.example.lessonslist.databinding.RowGroupStudentItemBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.MainViewModel
import com.example.lessonslist.presentation.student.StudentItemActivity
import com.example.lessonslist.presentation.student.StudentItemFragment


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
    ): View? {
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


        /*get data student*/

        //var dataString: String = String()


      // Log.d("dataString", dataString)
            //viewModel.groupItem.student
        //if (dataStudentCheckBox != null) {

      //  }
        /*get data student*/


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
                        val lstValues: List<Int> = dataString.split(",").map { it -> it.trim().toInt() }
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
                Log.d("checkedItemPosition", listView[position].toString())
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
    }




    private fun touchListener(view: View) {
        view.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                Toast.makeText(activity, "you just touch the screen :-)", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

       // Log.d("dataStudentList", dataStudentListArray.toString())
/*     Log.d("dataStudentList", dataStudentList.toString())

     if (dataStudentList != null) {
         for(student in dataStudentList){
             val name = student.name + " " + student.lastname
             val id = student.id
             Log.d("listname", name)
             Log.d("id", id.toString())
             //dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
         }
     }

*/
     /*

     .observe(viewLifecycleOwner) {
         adapter.submitList(it)
     }
     listView.adapter = adapter





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
            dataStudentGroupModel!!.add(DataStudentGroupModel("Banana Bread", 5,false))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Cupcake", 6,false))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Donut", 7,true))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Eclair", 8,true))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Froyo", 9,true))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Gingerbread", 11, true))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Honeycomb", 12,false))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Ice Cream Sandwich", 14, false))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Jelly Bean", 15,false))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Kitkat", 45,false))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Lollipop",23, false))
            dataStudentGroupModel!!.add(DataStudentGroupModel("Marshmallow", 33, false))*/



    override fun onStart() {
        super.onStart()

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
        var studentIds: String = adapter.arrayList.toString()
      viewModel.editGroupItem(
          binding.etTitle.text.toString(),
          binding.etDescription.text.toString(),
        //  binding.etStudent.text.toString()
          studentIds
      )
    }
    }

    private fun launchAddMode() {
    binding.saveButton.setOnClickListener{
      var studentIds: String = adapter.arrayList.toString()
      viewModel.addGroupItem(
          binding.etTitle.text.toString(),
          binding.etDescription.text.toString(),
      //    binding.etStudent.text.toString()
          studentIds
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

