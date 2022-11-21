package com.example.lessonslist.presentation.group


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentGroupItemListBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class GroupItemListFragment: Fragment() {

    private var _binding: FragmentGroupItemListBinding? = null
    private val binding: FragmentGroupItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private lateinit var viewModel: GroupListViewModel
    private lateinit var groupListAdapter: GroupListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Список групп"

        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(GroupListViewModel::class.java)
        viewModel.groupList.observe(viewLifecycleOwner) {
            groupListAdapter.submitList(it)
        }

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem3).isChecked = true


        binding.buttonAddGroupItem.setOnClickListener {
            /*fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_item_container, GroupItemFragment.newInstanceAddItem())
                ?.addToBackStack(null)
                ?.commit()*/
            navigateBtnAddGroup()
        }

        goCalendarFragmentBackPressed()
    }

    private fun goCalendarFragmentBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
            val navController = navHostFragment.navController
            navController.popBackStack(R.id.calendarItemFragment, true)
            navController.navigate(R.id.calendarItemFragment)
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
        setupClickListener()
        setupSwipeListener(binding.rvGroupList)
    }


    private fun setupClickListener() {
        groupListAdapter.onGroupItemClickListener = {
           /* fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_item_container, GroupItemFragment.newInstanceEditItem(it.id))
                ?.addToBackStack(null)
                ?.commit()*/
            navigateBtnEditGroup(it.id)
       }
    }

    private fun navigateBtnAddGroup() {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsGroup = Bundle().apply {
            putString(GroupItemFragment.SCREEN_MODE, GroupItemFragment.MODE_ADD)
        }

        navController.navigate(R.id.groupItemFragment, btnArgsGroup)
    }

    private fun navigateBtnEditGroup(id: Int) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsGroup = Bundle().apply {
            putString(GroupItemFragment.SCREEN_MODE, GroupItemFragment.MODE_EDIT)
            putInt(GroupItemFragment.GROUP_ITEM_ID, id)
        }

        navController.navigate(R.id.groupItemFragment, btnArgsGroup)
    }

    private fun setupSwipeListener(rvGroupList: RecyclerView) {
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
                val item = groupListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteGroupItem(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvGroupList)
    }

}
