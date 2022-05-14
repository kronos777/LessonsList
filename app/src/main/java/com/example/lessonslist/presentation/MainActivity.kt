package com.example.lessonslist.presentation

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lessonslist.MyForegroundService
import com.example.lessonslist.PaymentWork
import com.example.lessonslist.R
import com.example.lessonslist.databinding.ActivityMainBinding
import com.example.lessonslist.presentation.group.GroupItemFragment
import com.example.lessonslist.presentation.group.GroupItemListFragment
import com.example.lessonslist.presentation.lessons.LessonsItemFragment
import com.example.lessonslist.presentation.lessons.LessonsItemListFragment
import com.example.lessonslist.presentation.payment.PaymentItemFragment
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.student.StudentItemActivity
import com.example.lessonslist.presentation.student.StudentItemFragment
import com.example.lessonslist.presentation.student.StudentListAdapter
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), StudentItemFragment.OnEditingFinishedListener, GroupItemFragment.OnEditingFinishedListener, LessonsItemFragment.OnEditingFinishedListener {


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

        val drawerLayout: DrawerLayout? = binding.drawerLayoutId
        //val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView?.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.muItem1 -> goGroupFragment()
                R.id.muItem2 -> goLessonsFragment()
                R.id.muItem3 -> goMainView()
                R.id.muItem4 -> goPaymentFragment()
                R.id.muItem5 -> goGroupListFragment()
                R.id.muItem6 -> goLessonsListFragment()
            }
            true
        }

        /*foreggroundservice */
/* ContextCompat.startForegroundService(
     this,
     MyForegroundService.newIntent(this)
 )*/
 /*foreggroundseice */
 /*work manager */
        //        PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 30, TimeUnit.MINUTES, 25, TimeUnit.MINUTES).build();

 val request = PeriodicWorkRequestBuilder<PaymentWork>(20, TimeUnit.MINUTES).build()
     //val request = OneTimeWorkRequestBuilder<PaymentWork>().build()
        WorkManager.getInstance(this).enqueue(request)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
            .observe(this, Observer {

                val status: String = it.state.name
                Toast.makeText(this,status, Toast.LENGTH_SHORT).show()
            })
           /**/
 /*work manager */

}



private fun goMainView() {
 if (isOnePaneMode()) {
     binding.parentRecyclerLayout?.setVisibility(View.VISIBLE)
     binding.fragmentItemContainer?.setVisibility (View.GONE)
 }
}

private fun recyclerMainGone() {
 binding.parentRecyclerLayout?.setVisibility(View.GONE)
 binding.fragmentItemContainer?.setVisibility (View.VISIBLE)
}

fun goLessonsListFragment() {
 if (!isOnePaneMode()) {
     launchFragment(LessonsItemListFragment())
 } else {
     recyclerMainGone()
     launchFragmentTemp(LessonsItemListFragment())
     Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
 }
}


fun goGroupListFragment() {
 if (!isOnePaneMode()) {
     launchFragment(GroupItemListFragment())
 } else {
     recyclerMainGone()
     launchFragmentTemp(GroupItemListFragment())
     Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
 }
}

fun goGroupFragment() {
 if (!isOnePaneMode()) {
     launchFragment(GroupItemFragment())
 } else {
     recyclerMainGone()
     launchFragmentTemp(GroupItemFragment())
     Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
 }
}

fun goLessonsFragment() {
 if (!isOnePaneMode()) {
     launchFragment(LessonsItemFragment())
 } else {
     recyclerMainGone()
     launchFragmentTemp(LessonsItemFragment())
     Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
 }
}




fun goPaymentFragment() {
 if (!isOnePaneMode()) {
     launchFragment(PaymentItemListFragment())
 } else {
     recyclerMainGone()
     launchFragmentTemp(PaymentItemListFragment())
     Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
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

fun launchFragmentTemp(fragment: Fragment) {
 supportFragmentManager.popBackStack()
 supportFragmentManager.beginTransaction()
     .replace(R.id.fragment_item_container, fragment)
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
