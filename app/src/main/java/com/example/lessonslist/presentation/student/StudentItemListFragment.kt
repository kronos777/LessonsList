package com.example.lessonslist.presentation.student


import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentStudentItemListBinding
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.MainViewModel
import com.example.lessonslist.presentation.lessons.LessonsItemViewModel
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class StudentItemListFragment: Fragment() {

    private var _binding: FragmentStudentItemListBinding? = null
    private val binding: FragmentStudentItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private lateinit var viewModel: MainViewModel
    private lateinit var studentListAdapter: StudentListAdapter

    private lateinit var viewModelLessonsEdit: LessonsItemViewModel
    private lateinit var viewModelPayment: PaymentListViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //(activity as AppCompatActivity).supportActionBar?.title = "Список учеников"
        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Список учеников"

        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.studentList.observe(viewLifecycleOwner) {
            studentListAdapter.submitList(it)
        }
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem5).isChecked = true
        binding.buttonAddStudentItem.setOnClickListener {
            /*fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_item_container, StudentItemFragment.newInstanceAddItem())
                ?.addToBackStack("listStudent")
                ?.commit()*/
            navigateBtnAddStudent()
        }

        goCalendarFragmentBackPressed()
    }


    private fun goCalendarFragmentBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            navController.popBackStack(R.id.calendarItemFragment, true)
            val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
                .setExitAnim(R.anim.slide_in_right)
                .setPopEnterAnim(R.anim.slide_out_left)
                .setPopExitAnim(R.anim.slide_out_right).build()

            navController.navigate(R.id.calendarItemFragment, null, animationOptions)
        }
    }

    private fun navigateBtnAddStudent() {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(StudentItemFragment.SCREEN_MODE, StudentItemFragment.MODE_ADD)
        }
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.studentItemFragment, btnArgsLessons, animationOptions)
    }

    private fun navigateBtnEditStudent(id: Int) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(StudentItemEditFragment.SCREEN_MODE, StudentItemEditFragment.MODE_EDIT)
            putInt(StudentItemEditFragment.SHOP_ITEM_ID, id)
        }
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.studentItemEditFragment, btnArgsLessons, animationOptions)
    }



    private fun setupRecyclerView() {
        with(binding.rvStudentList) {
            studentListAdapter = StudentListAdapter()
            adapter = studentListAdapter
            recycledViewPool.setMaxRecycledViews(
                StudentListAdapter.VIEW_TYPE_ENABLED,
                StudentListAdapter.MAX_POOL_SIZE
            )

        }
        setupLongClickListener()
        setupClickListener()
        //setupSwipeListener(binding.rvStudentList)
    }

    private fun setupLongClickListener() {
        studentListAdapter.onStudentItemLongClickListener = {
            val item = studentListAdapter.currentList[it.id -1]
            // viewModel.deleteStudentItem(item)
            dialogWindow(item.id, item, item.name + " " + item.lastname)
        }
    }


    private fun setupClickListener() {
        studentListAdapter.onStudentItemClickListener = {
           /* fragmentManager?.beginTransaction()
             //   ?.replace(R.id.fragment_item_container, StudentItemFragment.newInstanceEditItem(it.id))
                ?.replace(R.id.fragment_item_container, StudentItemEditFragment.newInstanceEditItem(it.id))
                ?.addToBackStack("listStudent")
                ?.commit()*/
            navigateBtnEditStudent(it.id)
       }
    }

    private fun setupSwipeListener(rvStudentList: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = studentListAdapter.currentList[viewHolder.adapterPosition]
               // viewModel.deleteStudentItem(item)
                dialogWindow(item.id, item, item.name + " " + item.lastname)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvStudentList)
    }



    private fun dialogWindow(studentId: Int, student: StudentItem, title: String) {

        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Удалить студента $title")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val paymentsLabel = TextView(requireContext())
        paymentsLabel.setSingleLine()
        paymentsLabel.text = "Будьте внимательны вместе со студентом удаляются все данные о нем."
        paymentsLabel.isSingleLine = false
        paymentsLabel.height = 250
        paymentsLabel.top = 15
        layout.addView(paymentsLabel)


        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("удалить", DialogInterface.OnClickListener {
                dialog, id ->
            //deleteLessonsPay
            deletePaymentToStudent(studentId)
            viewModel.deleteStudentItem(student)
        })

        alert.setNegativeButton("не удалять", DialogInterface.OnClickListener {
                dialog, id ->
            dialog.dismiss()
        })

        alert.setCancelable(false)
        alert.show()

    }


    private fun editLessonsItem(idLessons: Int, studentId: Int) {
        viewModelLessonsEdit = ViewModelProvider(this).get(LessonsItemViewModel::class.java)
        viewModelLessonsEdit.getLessonsItem(idLessons)
       // val lessonsItem = viewModelLessonsEdit.lessonsItem
        viewModelLessonsEdit.lessonsItem.observe(viewLifecycleOwner) {
            //Log.d("valStudent", it.student)
            val newValueStudent = dropElementList(getStudentIds(it.student), studentId)
            //Log.d("delStudent", newValueStudent)
            viewModelLessonsEdit.editLessonsItem(
                it.title,
                it.description,
                newValueStudent,
                it.price.toString(),
                it.dateStart,
                it.dateEnd
            )
        }

        //Log.d("delStudent", newValueStudent)
        //val newValueStudent = dropElementList(getStudentIds(lessonsItem.value?.student.toString()), studentId)

       /* viewModelLessonsEdit.editLessonsItem(
            lessonsItem.value?.title.toString(),
            lessonsItem.value?.description.toString(),
            newValueStudent,
            lessonsItem.value?.price.toString(),
            lessonsItem.value?.dateStart.toString(),
            lessonsItem.value?.dateEnd.toString()
        )*/
    }

    private fun dropElementList(arrayList: List<Int>, el: Int): String {
        val elementList = mutableListOf<Int>()
        for(item in arrayList) {
            if(item != el){
                elementList.add(item)
            }

        }
        return elementList.toString()
    }

    private fun getStudentIds(dataString: String): List<Int> {
        var dataStr = dataString.replace("]", "")
        dataStr = dataStr.replace("[", "")
        return dataStr.split(",").map { it.trim().toInt() }
    }

    private fun deletePaymentToStudent(studentId: Int) {
        viewModelPayment = ViewModelProvider(this).get(PaymentListViewModel::class.java)
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            for (payment in it) {
                if(payment.studentId == studentId) {
                    Log.d("payment.lessonsId", payment.lessonsId.toString())
                    editLessonsItem(payment.lessonsId, studentId)
                    viewModelPayment.deletePaymentItem(payment)
                   // Toast.makeText(activity, payment.lessonsId.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
