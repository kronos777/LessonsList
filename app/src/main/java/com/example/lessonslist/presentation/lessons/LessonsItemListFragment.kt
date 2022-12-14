package com.example.lessonslist.presentation.lessons


import android.content.DialogInterface
import android.os.Bundle
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
import com.example.lessonslist.databinding.FragmentLessonsItemListBinding
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LessonsItemListFragment: Fragment() {

    private var _binding: FragmentLessonsItemListBinding? = null
    private val binding: FragmentLessonsItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private lateinit var viewModel: LessonsListViewModel
    private lateinit var lessonsListAdapter: LessonsListAdapter
    private lateinit var viewModelPayment: PaymentListViewModel





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsItemListBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title  = "Список уроков"

        setupRecyclerView()
        val args = requireArguments()
        val dateFilter = args.getString(DATE_ID)

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem4).isChecked = true


        if(dateFilter != null) {
                val listArrayPayment: ArrayList<LessonsItem> = ArrayList()
                viewModel = ViewModelProvider(this).get(LessonsListViewModel::class.java)

                viewModel.lessonsList.observe(viewLifecycleOwner) {
                    for (lessons in it) {
                        val pay = lessons.dateEnd.split(" ")
                        val datePay = Date(pay[0])
                        val dateFormated = SimpleDateFormat("d/M/yyyy").format(datePay)
                        if(dateFormated == dateFilter){
                            listArrayPayment.add(lessons)
                        }
                    }
                    if(listArrayPayment.size > 0) {
                        lessonsListAdapter.submitList(listArrayPayment)
                    } else {
                        Toast.makeText(getActivity(),"На эту дату уроков не запланировано!",Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            setCustomDataLessons()
        }


        binding.buttonAddLessonsItem.setOnClickListener {
           /* fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_item_container, LessonsItemAddFragment.addInstance(""))
                //?.replace(R.id.fragment_item_container, LessonsItemFragment.newInstanceAddItem("10/5/2022"))
                ?.addToBackStack(null)
                ?.commit()*/
            navigateBtnAddLessons("")
        }

        goCalendarFragmentBackPressed()

    }

    private fun deletePaymentToLessons(lessonsId: Int) {
        viewModelPayment = ViewModelProvider(this).get(PaymentListViewModel::class.java)
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            for (payment in it) {
                if(payment.lessonsId == lessonsId) {
                    viewModelPayment.deletePaymentItem(payment)
                    //Toast.makeText(activity, payment.student, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setCustomDataLessons() {
        viewModel = ViewModelProvider(this).get(LessonsListViewModel::class.java)
        viewModel.lessonsList.observe(viewLifecycleOwner) {
            lessonsListAdapter.submitList(it)
        }
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

    private fun setupRecyclerView() {
        with(binding.rvLessonsList) {
            lessonsListAdapter = LessonsListAdapter()
            adapter = lessonsListAdapter
            recycledViewPool.setMaxRecycledViews(
                LessonsListAdapter.VIEW_TYPE_ENABLED,
                LessonsListAdapter.MAX_POOL_SIZE
            )

        }
        setupLongClickListener()
        setupClickListener()
       // setupSwipeListener(binding.rvLessonsList)
    }

    private fun setupLongClickListener() {
        lessonsListAdapter.onLessonsItemLongClickListener = {
            val item = lessonsListAdapter.currentList[it.id -1]
            // viewModel.deleteStudentItem(item)
            dialogWindow(item.id, item, item.title)
        }
    }


    private fun setupClickListener() {
        lessonsListAdapter.onLessonsItemClickListener = {
            navigateBtnEditLessons(it.id)
        }
    }

    private fun navigateBtnEditLessons(id: Int) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemEditFragment.SCREEN_MODE, LessonsItemEditFragment.MODE_EDIT)
            putInt(LessonsItemEditFragment.LESSONS_ITEM_ID, id)
        }
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.lessonsItemEditFragment, btnArgsLessons, animationOptions)
    }

    private fun navigateBtnAddLessons(dateId: String) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemAddFragment.SCREEN_MODE, LessonsItemAddFragment.MODE_ADD)
            putString(LessonsItemAddFragment.DATE_ADD, dateId)
        }
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.lessonsItemAddFragment, btnArgsLessons, animationOptions)
    }

    private fun setupSwipeListener(rvLessonsList: RecyclerView) {
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
                val item = lessonsListAdapter.currentList[viewHolder.adapterPosition]
              //viewModel.deleteLessonsItem(item)

                /*delete payment*/
                dialogWindow(item.id, item, item.title)
                /*delete payment*/

            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvLessonsList)
       // sleep(2000)

        //setCustomDataLessons()
    }

    private fun dialogWindow(lessonId: Int, lesson: LessonsItem, title: String) {

        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Удалить урок $title")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.HORIZONTAL

        val paymentsLabel = TextView(requireContext())
        paymentsLabel.setSingleLine()
        paymentsLabel.text = "Вы действительно хотите удалить выбранный урок ? Будьте внимательны с уроком удаляться и все его платежи."
        paymentsLabel.isSingleLine = false
        paymentsLabel.height = 150
        paymentsLabel.top = 15
        layout.addView(paymentsLabel)


        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("удалить урок и платежи", DialogInterface.OnClickListener {
                dialog, id ->
            //deleteLessonsPay
            deletePaymentToLessons(lessonId)
            viewModel.deleteLessonsItem(lesson)
        })

        alert.setNegativeButton("не удалять", DialogInterface.OnClickListener {
                dialog, id ->
            dialog.dismiss()
        })

        alert.setCancelable(false)
        alert.show()

    }

    companion object {

        const val SCREEN_MODE = "screen_mode"
        const val CUSTOM_LIST = "custom_list"
       // private const val STUDENT_ID_LIST = "student_id_list"
       // private const val LESSONS_ID_LIST = "lesson_id_list"
        const val DATE_ID_LIST = "date_id_list"


        const val DATE_ID = "date_id"
       // private const val STUDENT_ID = "student_id"
        //private const val LESSONS_ID = "lessons_id"

        fun newInstanceNoneParams(): LessonsItemListFragment {
            return LessonsItemListFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, CUSTOM_LIST)
                }
            }
        }

        /*fun newInstanceStudentId(studentId: Int): LessonsItemListFragment {
            return LessonsItemListFragment().apply {
                arguments = Bundle().apply {
                    putInt(STUDENT_ID, studentId)
                    putString(SCREEN_MODE, STUDENT_ID_LIST)
                }
            }
        }
        fun newInstanceLessonsId(lessonsId: Int): LessonsItemListFragment {
            return LessonsItemListFragment().apply {
                arguments = Bundle().apply {
                    putInt(LESSONS_ID, lessonsId)
                    putString(SCREEN_MODE, LESSONS_ID_LIST)
                }
            }
        }*/

        fun newInstanceDateId(dateId: String): LessonsItemListFragment {
            return LessonsItemListFragment().apply {
                arguments = Bundle().apply {
                    putString(DATE_ID, dateId)
                    putString(SCREEN_MODE, DATE_ID_LIST)
                }
            }
        }

    }


}
