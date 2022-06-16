package com.example.lessonslist.presentation

import android.accounts.AccountManager
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lessonslist.PaymentWork
import com.example.lessonslist.R
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.databinding.ActivityMainBinding
import com.example.lessonslist.presentation.calendar.CalendarItemFragment
import com.example.lessonslist.presentation.calendar.CalendarPaymentItemFragment
import com.example.lessonslist.presentation.group.GroupItemFragment
import com.example.lessonslist.presentation.group.GroupItemListFragment
import com.example.lessonslist.presentation.lessons.LessonsItemFragment
import com.example.lessonslist.presentation.lessons.LessonsItemListFragment
import com.example.lessonslist.presentation.payment.PaymentItemFragment
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.settings.SettingsItemFragment
import com.example.lessonslist.presentation.student.StudentItemEditFragment
import com.example.lessonslist.presentation.student.StudentItemFragment
import com.example.lessonslist.presentation.student.StudentItemListFragment
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import java.security.AccessController.getContext
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), StudentItemFragment.OnEditingFinishedListener, GroupItemFragment.OnEditingFinishedListener, LessonsItemFragment.OnEditingFinishedListener, PaymentItemFragment.OnEditingFinishedListener, CalendarItemFragment.OnEditingFinishedListener, CalendarPaymentItemFragment.OnEditingFinishedListener, SettingsItemFragment.OnEditingFinishedListener, StudentItemEditFragment.OnEditingFinishedListener {


    private lateinit var binding: ActivityMainBinding

    lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_item_container)

        if (currentFragment == null) {
            if (isOnePaneMode()) {
                launchFragmentTemp(CalendarItemFragment())
            } else {
                launchFragment(CalendarItemFragment())
            }
        }

        /* setupRecyclerView()

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
        }*/

        val drawerLayout: DrawerLayout? = binding.drawerLayoutId
        //val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView?.setNavigationItemSelectedListener {
            when (it.itemId) {
            //    R.id.muItem1 -> goGroupFragment()
                R.id.muItem2 -> goSettingsFragment()
                R.id.muItem3 -> launchFragment(CalendarItemFragment())
                R.id.muItem4 -> goPaymentFragment()
                R.id.muItem5 -> goGroupListFragment()
                R.id.muItem6 -> goLessonsListFragment()
                R.id.muItem7 -> goStudentListFragment()
                R.id.muItem8 -> goPaymentCalendarFragment()
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

        //val request = PeriodicWorkRequestBuilder<PaymentWork>(20, TimeUnit.MINUTES).build()
        val request = OneTimeWorkRequestBuilder<PaymentWork>().build()//change
        WorkManager.getInstance(this).enqueue(request)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
            .observe(this, Observer {

                val status: String = it.state.name
                Toast.makeText(this,status, Toast.LENGTH_SHORT).show()
            })
           /**/
 /*work manager */
        /*get account*/
        val accManager : AccountManager = AccountManager.get(getApplicationContext())
        val acc = accManager.getAccountsByType("com.google")
        Log.d("accountName", acc.size.toString())
        /*  val accCount = acc.size
          Log.d("accountName", acc.get(0).name)
          for (i in 0 until accCount) {
              //Do your task here...
              //Toast.makeText(applicationContext, acc[i].name, Toast.LENGTH_SHORT).show()
              Log.d("accountName", acc[i].name)
          }*/
        /*get account*/
        //RoomBackup.BACKUP_FILE_LOCATION_INTERNAL



   //     val database: String = AppDatabase.DB_NAME
     //   val path = applicationContext.filesDir.absolutePath
     //  Toast.makeText(this, "database path!" + RoomBackup.BACKUP_FILE_LOCATION_INTERNAL, Toast.LENGTH_SHORT).show();
     //   Toast.makeText(this, "path name!" + path.toString(), Toast.LENGTH_SHORT).show();



/*

  val backup = RoomBackup(this)
        backup
            .database(AppDatabase.getInstance(applicationContext as Application))
            .enableLogDebug(true)
            .backupIsEncrypted(true)
            .customEncryptPassword("YOUR_SECRET_PASSWORD")
            .backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_DIALOG)
            //.backupLocation(RoomBackup.BACKUP_FILE_LOCATION_INTERNAL)
            .maxFileCount(5)
            .apply {
                onCompleteListener { success, message, exitCode ->
                    //Log.d(TAG, "success: $success, message: $message, exitCode: $exitCode")
                //    Toast.makeText(this, "vse ok!", Toast.LENGTH_SHORT).show();

                    //  if (success) restartApp(Intent(this@MainActivity, MainActivity::class.java))
                }
            }
            .backup()
*/

        /*
  Восстановить
        val backup = RoomBackup(this)

        backup
            .database(AppDatabase.getInstance(applicationContext as Application))
            .enableLogDebug(true)
            .backupIsEncrypted(true)
            .customEncryptPassword("YOUR_SECRET_PASSWORD")
            .backupLocation(RoomBackup.BACKUP_FILE_LOCATION_INTERNAL)
            .apply {
                onCompleteListener { success, message, exitCode ->
                    Log.d(TAG, "success: $success, message: $message, exitCode: $exitCode")
                    if (success) restartApp(Intent(this@MainActivity, MainActivity::class.java))
                }
            }
            .restore()
        */




    }


    private fun goSettingsFragment() {
        if (!isOnePaneMode()) {
            launchFragment(SettingsItemFragment())
        } else {
            recyclerMainGone()
            launchFragmentTemp(SettingsItemFragment())
            Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun goPaymentCalendarFragment() {
        if (!isOnePaneMode()) {
            launchFragment(CalendarPaymentItemFragment())
        } else {
            recyclerMainGone()
            launchFragmentTemp(CalendarPaymentItemFragment())
            Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun goStudentListFragment() {
        if (!isOnePaneMode()) {
            launchFragment(StudentItemListFragment())
        } else {
            recyclerMainGone()
            launchFragmentTemp(StudentItemListFragment())
           // Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
        }
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
         launchFragment(LessonsItemListFragment.newInstanceNoneParams())
     } else {
         recyclerMainGone()
         launchFragmentTemp(LessonsItemListFragment.newInstanceNoneParams())
      //   Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
     }
    }


    fun goGroupListFragment() {
     if (!isOnePaneMode()) {
         launchFragment(GroupItemListFragment())
     } else {
         recyclerMainGone()
         launchFragmentTemp(GroupItemListFragment())
     //    Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
     }
    }

    fun goGroupFragment() {
     if (!isOnePaneMode()) {
         launchFragment(GroupItemFragment())
     } else {
         recyclerMainGone()
         launchFragmentTemp(GroupItemFragment())
       //  Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
     }
    }

    fun goLessonsFragment() {
     if (!isOnePaneMode()) {
         launchFragment(LessonsItemFragment())
     } else {
         recyclerMainGone()
         launchFragmentTemp(LessonsItemFragment())
    //     Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
     }
    }




    fun goPaymentFragment() {
     if (!isOnePaneMode()) {
         launchFragment(PaymentItemListFragment.newInstanceNoneParams())
     } else {
         recyclerMainGone()
         launchFragmentTemp(PaymentItemListFragment.newInstanceNoneParams())
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
         .replace(R.id.fragment_item_container, fragment)
         .addToBackStack(null)
         .commit()
    }

    fun launchFragmentTemp(fragment: Fragment) {
     supportFragmentManager.popBackStack()
     supportFragmentManager.beginTransaction()
         .replace(R.id.fragment_item_container, fragment)
         .addToBackStack(BACK_STACK_ROOT_TAG)
         .commit()
    }

/*
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
    }*/
    companion object {
             const val BACK_STACK_ROOT_TAG = "root_fragment"
    }
}
