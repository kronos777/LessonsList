package com.example.lessonslist.presentation.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.databinding.FragmentGroupItemListBinding
import com.example.lessonslist.presentation.student.StudentListAdapter
import java.lang.RuntimeException

class GroupItemListFragment: Fragment() {

    private var _binding: FragmentGroupItemListBinding? = null
    private val binding: FragmentGroupItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private lateinit var viewModel: GroupListViewModel
    private lateinit var groupListAdapter: GroupListAdapter
    //private lateinit var binding: FragmentGroupItemListBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(GroupListViewModel::class.java)
        viewModel.groupList.observe(viewLifecycleOwner) {
            groupListAdapter.submitList(it)
        }

    }


    private fun setupRecyclerView() {
        with(binding.rvGroupList) {
            groupListAdapter = GroupListAdapter()
            adapter = groupListAdapter
            recycledViewPool.setMaxRecycledViews(
                GroupListAdapter.VIEW_TYPE_ENABLED,
                GroupListAdapter.MAX_POOL_SIZE
            )

        }
       // setupLongClickListener()
       // setupClickListener()
       // setupSwipeListener(binding.rvShopList)
    }

}