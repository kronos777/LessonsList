package com.example.lessonslist.presentation.student


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ActivityMainBinding.inflate
import com.example.lessonslist.databinding.FragmentGroupItemListBinding
import com.example.lessonslist.databinding.FragmentLessonsItemListBinding
import com.example.lessonslist.databinding.FragmentStudentItemListBinding
import com.example.lessonslist.presentation.MainViewModel


class StudentItemListFragment: Fragment() {

    private var _binding: FragmentStudentItemListBinding? = null
    private val binding: FragmentStudentItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private lateinit var viewModel: MainViewModel
    private lateinit var studentListAdapter: StudentListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Список учеников"

        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.studentList.observe(viewLifecycleOwner) {
            studentListAdapter.submitList(it)
        }

        binding.buttonAddStudentItem.setOnClickListener {
            val fragmentTransaction = fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_item_container, StudentItemFragment.newInstanceAddItem())
                ?.addToBackStack(null)
                ?.commit()
        }
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
       // setupLongClickListener()
        setupClickListener()
        setupSwipeListener(binding.rvStudentList)
    }


    private fun setupClickListener() {
        studentListAdapter.onStudentItemClickListener = {
            fragmentManager?.beginTransaction()
             //   ?.replace(R.id.fragment_item_container, StudentItemFragment.newInstanceEditItem(it.id))
                ?.replace(R.id.fragment_item_container, StudentItemEditFragment.newInstanceEditItem(it.id))
                ?.addToBackStack(null)
                ?.commit()
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
                viewModel.deleteStudentItem(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvStudentList)
    }



}
