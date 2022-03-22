package com.example.lessonslist.presentation

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ActivityMainBinding
import com.example.lessonslist.presentation.student.StudentItemActivity
import com.example.lessonslist.presentation.student.StudentItemFragment
import com.example.lessonslist.presentation.student.StudentListAdapter
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationBarItemView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), StudentItemFragment.OnEditingFinishedListener {


    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: StudentListAdapter
    private lateinit var binding: ActivityMainBinding

    lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.studentList.observe(this) {
            shopListAdapter.submitList(it)
        }
        binding.buttonAddShopItem.setOnClickListener {
            if (isOnePaneMode()) {
                val intent = StudentItemActivity.newIntentAddItem(this)
                startActivity(intent)
            } else {
                launchFragment(StudentItemFragment.newInstanceAddItem())
            }
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.navView)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.muItem1 -> Toast.makeText(this,"Text1!",Toast.LENGTH_SHORT).show()
                R.id.muItem2 -> Toast.makeText(this,"Text2!",Toast.LENGTH_SHORT).show()
                R.id.muItem3 -> Toast.makeText(this,"Text3!",Toast.LENGTH_SHORT).show()
                R.id.muItem4 -> Toast.makeText(this,"Text4 !",Toast.LENGTH_SHORT).show()
            }
            true
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onEditingFinished() {
        Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
        supportFragmentManager.popBackStack()
    }

    private fun isOnePaneMode(): Boolean {
        return binding.shopItemContainer == null
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecyclerView() {
        with(binding.rvShopList) {
            shopListAdapter = StudentListAdapter()
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(
                StudentListAdapter.VIEW_TYPE_ENABLED,
                StudentListAdapter.MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                StudentListAdapter.VIEW_TYPE_DISABLED,
                StudentListAdapter.MAX_POOL_SIZE
            )
        }
        setupLongClickListener()
        setupClickListener()
        setupSwipeListener(binding.rvShopList)
    }

    private fun setupSwipeListener(rvShopList: RecyclerView) {
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
                val item = shopListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteStudentItem(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }

    private fun setupClickListener() {
        shopListAdapter.onStudentItemClickListener = {
            if (isOnePaneMode()) {
                val intent = StudentItemActivity.newIntentEditItem(this, it.id)
                startActivity(intent)
            } else {
                launchFragment(StudentItemFragment.newInstanceEditItem(it.id))
            }
        }
    }

    private fun setupLongClickListener() {
        shopListAdapter.onStudentItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
    }
}
