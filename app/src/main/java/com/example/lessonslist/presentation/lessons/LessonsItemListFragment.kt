package com.example.lessonslist.presentation.lessons


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentGroupItemListBinding
import com.example.lessonslist.databinding.FragmentLessonsItemListBinding


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(LessonsListViewModel::class.java)
        viewModel.lessonsList.observe(viewLifecycleOwner) {
            lessonsListAdapter.submitList(it)
        }

        binding.buttonAddLessonsItem.setOnClickListener {
            val fragmentTransaction = fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_item_container, LessonsItemFragment.newInstanceAddItem())
                ?.addToBackStack(null)
                ?.commit()
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
       // setupSwipeListener(binding.rvShopList)
    }


    private fun setupClickListener() {
        lessonsListAdapter.onLessonsItemClickListener = {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_item_container, LessonsItemFragment.newInstanceEditItem(it.id))
                ?.addToBackStack(null)
                ?.commit()
       }
    }

}
