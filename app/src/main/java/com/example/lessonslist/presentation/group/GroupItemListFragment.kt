package com.example.lessonslist.presentation.group


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentGroupItemListBinding
import com.example.lessonslist.domain.group.GroupItem
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


class GroupItemListFragment: Fragment(), MenuProvider {

    private var _binding: FragmentGroupItemListBinding? = null
    private val binding: FragmentGroupItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private lateinit var viewModel: GroupListViewModel
    private lateinit var groupListAdapter: GroupListAdapter

    private var toolbar: MaterialToolbar? = null
    private var menuChoice: Menu? = null
    private var hideModifyAppBar = false

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
        setCustomDataGroups()

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem3).isChecked = true


        binding.buttonAddGroupItem.setOnClickListener {
            navigateBtnAddGroup()
        }

        goCalendarFragmentBackPressed()
        //setupLongClickListener()
    }

    private fun goCalendarFragment() {
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navController.navigate(R.id.calendarItemFragment, null, animationOptions)
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
        with(binding.rvGroupList) {
            groupListAdapter = GroupListAdapter{ show -> showDeleteMenu(show)}
            adapter = groupListAdapter
            recycledViewPool.setMaxRecycledViews(
                GroupListAdapter.VIEW_TYPE_ENABLED,
                GroupListAdapter.MAX_POOL_SIZE
            )

        }
       // setupLongClickListener()
        setupClickListener()
    // setupSwipeListener(binding.rvGroupList)
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
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.groupItemFragment, btnArgsGroup, animationOptions)
    }

    private fun navigateBtnEditGroup(id: Int) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsGroup = Bundle().apply {
            putString(GroupItemFragment.SCREEN_MODE, GroupItemFragment.MODE_EDIT)
            putInt(GroupItemFragment.GROUP_ITEM_ID, id)
        }
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.groupItemFragment, btnArgsGroup, animationOptions)
    }

    private fun showDeleteMenu(show: Boolean) {
        toolbar = (activity as AppCompatActivity).findViewById(R.id.tool_bar)
        val bottomNavigation = (activity as AppCompatActivity?)!!.window.findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        if(show) {
            binding.groupListRecyclerLayout.background = ColorDrawable(Color.parseColor("#CFCACA"))
            bottomNavigation.itemBackgroundResource = R.color.active_select_items
            toolbar?.findViewById<View>(R.id.menu_delete)?.visibility = View.VISIBLE
            toolbar?.findViewById<View>(R.id.menu_select_all)?.visibility = View.VISIBLE
            (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#0e0f0f")
            toolbar?.background = ColorDrawable(Color.parseColor("#0e0f0f"))
            toolbar?.setOnMenuItemClickListener {
                onMenuItemSelected(it)
            }
            toolbar?.setNavigationIcon(R.drawable.baseline_close_24)
            toolbar?.setNavigationOnClickListener {
                showDeleteMenu(false)
                groupListAdapter.pairList.clear()
                setCustomDataGroupsCheckAll(false)
            }
            hideModifyAppBar = true
        } else {
            binding.groupListRecyclerLayout.background = ColorDrawable(Color.parseColor("#FFFFFF"))
            bottomNavigation.itemBackgroundResource = R.color.noactive_select_items
            toolbar?.background = ColorDrawable(Color.parseColor("#0061A5"))
            (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#0061A5")
            toolbar?.findViewById<View>(R.id.menu_delete)?.visibility = View.GONE
            toolbar?.findViewById<View>(R.id.menu_select_all)?.visibility = View.GONE
            toolbar?.setNavigationIcon(R.drawable.ic_baseline_navigate_before_24)
            toolbar?.setNavigationOnClickListener {
                goCalendarFragment()
            }
            hideModifyAppBar = false
        }
        menuChoice?.findItem(R.id.menu_delete)?.isVisible = show
        menuChoice?.findItem(R.id.menu_select_all)?.isVisible = show
    }

    private fun setCustomDataGroups() {
        viewModel = ViewModelProvider(this)[GroupListViewModel::class.java]
        viewModel.groupList.observe(viewLifecycleOwner) { listGroup ->
            groupListAdapter.submitList(listGroup)
            if (listGroup.isEmpty()) {
                binding.noGroup.visibility = View.VISIBLE
            }
        }
    }
    private fun setCustomDataGroupsCheckAll(selectAll: Boolean) {
        viewModel = ViewModelProvider(this)[GroupListViewModel::class.java]
        viewModel.groupList.observe(viewLifecycleOwner) { listGroup ->
            if(listGroup.isNotEmpty()){
                val listNew = ArrayList<GroupItem>()
                if(selectAll) {
                    listGroup.forEach {
                        val nn = it.copy(description = "500")
                        listNew.add(nn)
                    }
                } else {
                    listGroup.forEach {
                        val nn = it.copy(description = "0")
                        listNew.add(nn)
                    }
                }
                groupListAdapter.submitList(listNew)
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuChoice = menu
        menuInflater.inflate(R.menu.menu_recycler_choice, menu)
        showDeleteMenu(false)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.menu_delete) {
            delete()
        } else if(menuItem.itemId == R.id.menu_select_all) {
            selectAll()
        }
        return false
    }

    private fun selectAll() {
        val itemCount = groupListAdapter.itemCount
        if(groupListAdapter.pairList.isNotEmpty() && groupListAdapter.pairList.size < itemCount) {//notEmpty
            if(groupListAdapter.pairList.size > 3) {
                groupListAdapter.pairList.clear()
                setCustomDataGroupsCheckAll(false)
            } else {
                setCustomDataGroupsCheckAll(true)
            }

        } else if (groupListAdapter.pairList.size == itemCount){
            groupListAdapter.pairList.clear()
            setCustomDataGroupsCheckAll(false)
        } else if (groupListAdapter.pairList.isEmpty()) {
            setCustomDataGroupsCheckAll(true)
        }
    }

    private fun delete() {
        val alert = AlertDialog.Builder(requireContext())
        val title: String
        val txtDescription: String
        if(groupListAdapter.pairList.size == 1) {
            title = "Удалить группу"
            txtDescription = "Вы действительно хотите удалить выбранную группу ?"
        } else {
            title = "Удалить группы"
            txtDescription = "Вы действительно хотите удалить выбранные группы?"
        }

        alert.setTitle(title)


        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.HORIZONTAL

        val paymentsLabel = TextView(requireContext())
        paymentsLabel.setSingleLine()
        paymentsLabel.text = txtDescription
        paymentsLabel.isSingleLine = false
        paymentsLabel.height = 150
        paymentsLabel.top = 15
        layout.addView(paymentsLabel)


        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("удалить") { _, _ ->
            if (groupListAdapter.pairList.isNotEmpty()) {
                groupListAdapter.pairList.forEach {
                    viewModel.deleteGroupItemId(it.key)
                }
                setCustomDataGroupsCheckAll(false)
                showDeleteMenu(false)
            }
        }

        alert.setNegativeButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(false)
        alert.show()
    }


    override fun onStop() {
        super.onStop()
        if(hideModifyAppBar) {
            hideModifyAppBar()
        }
    }

    private fun hideModifyAppBar() {
        val bottomNavigation = (activity as AppCompatActivity?)!!. window.findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigation.itemBackgroundResource = R.color.noactive_select_items
        toolbar?.background = ColorDrawable(Color.parseColor("#0061A5"))
        (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#0061A5")
        toolbar?.findViewById<View>(R.id.menu_delete)?.visibility = View.GONE
        toolbar?.findViewById<View>(R.id.menu_select_all)?.visibility = View.GONE
    }

}
