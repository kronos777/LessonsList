package com.example.lessonslist.presentation.lessons


import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentGroupItemListBinding
import com.example.lessonslist.databinding.FragmentLessonsItemListBinding
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.domain.payment.PaymentItem
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log


class LessonsItemListFragment: Fragment() {

    private var _binding: FragmentLessonsItemListBinding? = null
    private val binding: FragmentLessonsItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private lateinit var viewModel: LessonsListViewModel
    private lateinit var lessonsListAdapter: LessonsListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLessonsItemListBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun log(message: String) {
        Log.d("SERVICE_TAG", "LessonsListDate: $message")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Список уроков"

        setupRecyclerView()
        val args = requireArguments()
        val dateFilter = args.getString(DATE_ID)
        Toast.makeText(getActivity(),"Работает эта часть кода!" + dateFilter.toString(),Toast.LENGTH_SHORT).show();


        if(dateFilter != null) {
                val listArrayPayment: ArrayList<LessonsItem> = ArrayList()
                viewModel = ViewModelProvider(this).get(LessonsListViewModel::class.java)
                viewModel.lessonsList.observe(viewLifecycleOwner) {
                    for (lessons in it) {
                        var pay = lessons.dateEnd.split(" ")
                        val datePay = Date(pay[0])
                        val dateFormated = SimpleDateFormat("d/M/yyyy").format(datePay)
                        if(dateFormated == dateFilter){
                            listArrayPayment.add(lessons)
                        }
                    }
                    if(listArrayPayment.size > 0) {
                        lessonsListAdapter.submitList(listArrayPayment)
                    } else {
                        Toast.makeText(getActivity(),"На эту дату уроков не запланировано!",Toast.LENGTH_SHORT).show();
                    }
                }
        } else {
                viewModel = ViewModelProvider(this).get(LessonsListViewModel::class.java)
                viewModel.lessonsList.observe(viewLifecycleOwner) {
                    lessonsListAdapter.submitList(it)
                }
        }

      /*  if(getScreenOrientationLandscape() == false) {
            viewModel = ViewModelProvider(this).get(LessonsListViewModel::class.java)
            viewModel.lessonsList.observe(viewLifecycleOwner) {
                lessonsListAdapter.submitList(it)
            }
            Toast.makeText(getActivity(),"Работает эта часть кода!",Toast.LENGTH_SHORT).show();
        } else if (getScreenOrientationLandscape() == true) {
            log(dateFilter.toString())

            val listArrayPayment: ArrayList<LessonsItem> = ArrayList()
            viewModel = ViewModelProvider(this).get(LessonsListViewModel::class.java)
            viewModel.lessonsList.observe(viewLifecycleOwner) {
                for (lessons in it) {
                    var pay = lessons.dateEnd.split(" ")
                    val datePay = Date(pay[0])
                    val dateFormated = SimpleDateFormat("d/M/yyyy").format(datePay)
                    ///  val dateString = Date(dateId)
                    //Log.d("dateId", datePay.toString())
                    //Log.d("dateId", dateFormated.toString())
                    if(dateFormated == dateFilter){
                        listArrayPayment.add(lessons)
                    }
                }
                if(listArrayPayment.size > 0) {
                    lessonsListAdapter.submitList(listArrayPayment)
                } else {
                    Toast.makeText(getActivity(),"На эту дату уроков не запланировано!",Toast.LENGTH_SHORT).show();
                }

            }
        }*/

        if (getScreenOrientationLandscape() == true) {
            binding.buttonAddLessonsItem.setOnClickListener {
                val fragmentTransaction = fragmentManager?.beginTransaction()
                    ?.replace(R.id.shop_item_container, LessonsItemFragment.newInstanceAddItem(""))
                    //?.replace(R.id.fragment_item_container, LessonsItemFragment.newInstanceAddItem("10/5/2022"))
                    ?.addToBackStack(null)
                    ?.commit()
            }
        } else {
            binding.buttonAddLessonsItem.setOnClickListener {
                val fragmentTransaction = fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_item_container, LessonsItemFragment.newInstanceAddItem(""))
                    //?.replace(R.id.fragment_item_container, LessonsItemFragment.newInstanceAddItem("10/5/2022"))
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }

    }

    private fun getScreenOrientationLandscape(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> false
            Configuration.ORIENTATION_LANDSCAPE -> true
            else -> false
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
       // setupLongClickListener()
        setupClickListener()
        setupSwipeListener(binding.rvLessonsList)
    }


    private fun setupClickListener() {

        if(getScreenOrientationLandscape() == false) {
            lessonsListAdapter.onLessonsItemClickListener = {
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_item_container, LessonsItemFragment.newInstanceEditItem(it.id))
                    ?.addToBackStack(null)
                    ?.commit()
            }
        } else {
            lessonsListAdapter.onLessonsItemClickListener = {
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.shop_item_container, LessonsItemFragment.newInstanceEditItem(it.id))
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }




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
                viewModel.deleteLessonsItem(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvLessonsList)
    }


    companion object {

        private const val SCREEN_MODE = "screen_mode"
        private const val CUSTOM_LIST = "custom_list"
        private const val STUDENT_ID_LIST = "student_id_list"
        private const val LESSONS_ID_LIST = "lesson_id_list"
        private const val DATE_ID_LIST = "date_id_list"


        private const val DATE_ID = "date_id"
        private const val STUDENT_ID = "student_id"
        private const val LESSONS_ID = "lessons_id"

        fun newInstanceNoneParams(): LessonsItemListFragment {
            return LessonsItemListFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, CUSTOM_LIST)
                }
            }
        }

        fun newInstanceStudentId(studentId: Int): LessonsItemListFragment {
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
        }

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
