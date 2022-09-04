package com.example.lessonslist.presentation

import android.accounts.AccountManager
import android.app.Application
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lessonslist.R
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.data.service.PaymentWork
import com.example.lessonslist.databinding.ActivityMainBinding
import com.example.lessonslist.domain.payment.PaymentItem
import com.example.lessonslist.presentation.calendar.CalendarItemFragment
import com.example.lessonslist.presentation.calendar.CalendarPaymentItemFragment
import com.example.lessonslist.presentation.group.GroupItemFragment
import com.example.lessonslist.presentation.group.GroupItemListFragment
import com.example.lessonslist.presentation.lessons.LessonsItemAddFragment
import com.example.lessonslist.presentation.lessons.LessonsItemEditFragment
import com.example.lessonslist.presentation.lessons.LessonsItemFragment
import com.example.lessonslist.presentation.lessons.LessonsItemListFragment
import com.example.lessonslist.presentation.payment.PaymentItemFragment
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.example.lessonslist.presentation.settings.SettingsItemFragment
import com.example.lessonslist.presentation.student.StudentItemEditFragment
import com.example.lessonslist.presentation.student.StudentItemFragment
import com.example.lessonslist.presentation.student.StudentItemListFragment
import com.google.android.material.appbar.MaterialToolbar
import de.raphaelebner.roomdatabasebackup.core.RoomBackup


class MainActivity : AppCompatActivity(), StudentItemFragment.OnEditingFinishedListener, GroupItemFragment.OnEditingFinishedListener, LessonsItemFragment.OnEditingFinishedListener, PaymentItemFragment.OnEditingFinishedListener, CalendarItemFragment.OnEditingFinishedListener, CalendarPaymentItemFragment.OnEditingFinishedListener, SettingsItemFragment.OnEditingFinishedListener, StudentItemEditFragment.OnEditingFinishedListener, LessonsItemAddFragment.OnEditingFinishedListener, LessonsItemEditFragment.OnEditingFinishedListener {


    private lateinit var binding: ActivityMainBinding

    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var backup: RoomBackup

    private var doubleBackToExitPressedOnce = false

    private var alertCount = 0
    private var redCircle: FrameLayout? = null
    private var countTextView: TextView? = null

    private lateinit var viewModel: PaymentListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        launchMainFragment(CalendarItemFragment(), "calendar")

        if (intent.getStringExtra("extra") != null) {
            Toast.makeText(this, "extra params" + intent.getStringExtra("extra"), Toast.LENGTH_SHORT).show()
            val lessonIdForFragment = intent.getStringExtra("extra")
            if (lessonIdForFragment != null) {
                launchFragment(LessonsItemEditFragment.newInstanceEditItem(lessonIdForFragment.toInt()))
            }
        /*
            * fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_item_container, LessonsItemEditFragment.newInstanceEditItem(intent.getStringExtra("extra")), "OtherFragment")
                ?.addToBackStack(name)
                ?.commit()
                *
                *
                *
                *      supportFragmentManager.beginTransaction()
         .replace(R.id.fragment_item_container, fragment, "OtherFragment")
         .addToBackStack(name)
         //.addToBackStack("CalendarItemFragment")
         .commit()
            * */
        }
    /*    if (currentFragment == null) {
            if (isOnePaneMode()) {
                launchFragmentTemp(CalendarItemFragment())
            } else {
                launchFragment(CalendarItemFragment())
            }
        }*/

        backup = RoomBackup(this)

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


        toggle = getActionBarDrawerToggle(binding.drawerLayoutId, binding.toolBar!!).apply {
            setToolbarNavigationClickListener {
                // Back to home fragment for any hit to the back button
                //   navController.navigate(R.id.app_bar_top)
            }
            // Intialize the icon at the app start
            enableHomeBackIcon(false)
        }

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                //    R.id.muItem1 -> goGroupFragment()
                //      R.id.muItem2 -> launchFragment(SettingsItemFragment())
                R.id.muItem2 -> getDialogBackup()
                R.id.muItem3 -> launchFragment(CalendarItemFragment())
                R.id.muItem4 -> goPaymentFragment()
                R.id.muItem5 -> goGroupListFragment()
                R.id.muItem6 -> goLessonsListFragment()
                R.id.muItem7 -> goStudentListFragment()
                R.id.muItem8 -> goTestAddLessons()

            }
            true
        }

       /* val drawerLayout: DrawerLayout? = binding.drawerLayoutId
        //val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




*/




        binding.navViewBottom?.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.bottomItem1 -> {
                    // Respond to navigation item 1 click
                    launchMainFragment(CalendarItemFragment(), "calendar")
                    true
                }
                R.id.bottomItem2 -> {
                    // Respond to navigation item 2 click
                  //  Log.d("menuitem", "ite2")
                    //Toast.makeText(this, "item2", Toast.LENGTH_SHORT).show()
                    goPaymentFragment()
                    true
                }
                R.id.bottomItem3 -> {
                    // Respond to navigation item 2 click
                    goGroupListFragment()
                    true
                }
                R.id.bottomItem4 -> {
                    // Respond to navigation item 2 click
                    goLessonsListFragment()
                    true
                }
                R.id.bottomItem5 -> {
                    // Respond to navigation item 2 click
                    goStudentListFragment()
                    true
                }
                else -> false
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
             //   Toast.makeText(this,status, Toast.LENGTH_SHORT).show()
            })
           /**/
 /*work manager */
        /*get account*/
        val accManager : AccountManager = AccountManager.get(getApplicationContext())
        val acc = accManager.getAccountsByType("com.google")
        Log.d("accountName", acc.size.toString())
        /*get account*/
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

        redCircle = findViewById(R.id.view_alert_red_circle)
        countTextView = findViewById(R.id.view_alert_count_textview)

        val materialToolbar: MaterialToolbar = binding.toolBar!!
        val paymentBtnAppBarTop = findViewById<View>(R.id.payment)
        paymentBtnAppBarTop.setOnClickListener {
            if(alertCount > 0) {
                launchFragment(PaymentItemListFragment.newInstanceEnabledPayment())
            } else {
                goPaymentFragment()
            }
        }

        materialToolbar.setOnMenuItemClickListener {
            // Toast.makeText(this, "Favorites Clsadsaicked"+it.itemId, Toast.LENGTH_SHORT).show()
            when (it.itemId) {
                R.id.backup -> {
                    getDialogBackup()
                    true
                }
                else -> false
            }
        }


        getDeptPayment()

    }

/*
*                     Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show()
                    alertCount = (alertCount + 1) % 11; // cycle through 0 - 10
                    updateAlertIcon()
* */
    override fun onBackPressed() {
        //super.onBackPressed()
        supportFragmentManager.popBackStack("calendar", 0)
        val myFragment: Fragment? = supportFragmentManager.findFragmentByTag("MainCalendarFragment") as Fragment
        if (doubleBackToExitPressedOnce) {
            // super.onBackPressed()
            //return
            if (myFragment != null && myFragment.isVisible()) {
                this.finishAffinity()
                Toast.makeText(this, "Текущий форагмент календарь, можно выходить.", Toast.LENGTH_SHORT).show()
            }


            if (myFragment == null) {
                    launchMainFragment(CalendarItemFragment(), "calendar")
                    Toast.makeText(this, "Вызов нужного блока.", Toast.LENGTH_SHORT).show()
            }

        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun backup() {
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
    }

    private fun restore() {
        backup
            .database(AppDatabase.getInstance(applicationContext as Application))
            .enableLogDebug(true)
            .backupIsEncrypted(true)
            .customEncryptPassword("YOUR_SECRET_PASSWORD")
            .backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_DIALOG)
            .apply {
                onCompleteListener { success, message, exitCode ->
                    Log.d(TAG, "success: $success, message: $message, exitCode: $exitCode")
                    if (success) restartApp(Intent(this@MainActivity, MainActivity::class.java))
                }
            }
            .restore()
    }

    private fun getDialogBackup() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Создать/Восстановить резервную копию.")
            .setCancelable(false)
            .setPositiveButton("Создать резервную копию.", DialogInterface.OnClickListener {
                    dialog, id ->
                    backup()
            })
            .setNegativeButton("Восстановить из резервной копии.", DialogInterface.OnClickListener {
                    dialog, id ->
                //    log(date.toString())
                    restore()
            })
            .setNeutralButton("Закрыть", DialogInterface.OnClickListener {
                    dialog, id ->
                dialog.dismiss()
            })

        val dialog = builder.create()
        dialog.show()
    }

    private fun getDeptPayment() {
        val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
        viewModel = ViewModelProvider(this).get(PaymentListViewModel::class.java)
        viewModel.paymentList.observe(this) {
            for (payment in it) {
                if(payment.enabled == false){
                    listArrayPayment.add(payment)
                }
            }
            if(listArrayPayment.size > 0) {
                //Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show()
                //alertCount = (alertCount + 1) % 11; // cycle through 0 - 10
                alertCount = listArrayPayment.size
                updateAlertIcon()
            } else {
                clearAlertIcon()
            }
            //Toast.makeText(this, listArrayPayment.toString(), Toast.LENGTH_SHORT).show()
        }
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

    private fun goTestAddLessons() {
          //  launchFragment(LessonsItemAddFragment().newInstanceAdd(""))
        launchFragmentTemp(LessonsItemAddFragment.addInstance(""))
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
         //   recyclerMainGone()
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
        // recyclerMainGone()
         launchFragmentTemp(LessonsItemListFragment.newInstanceNoneParams())
      //   Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
     }
    }

    fun getVisibleFragment(): Fragment? {
        val fragmentManager: FragmentManager = this@MainActivity.supportFragmentManager
        val fragments: List<Fragment> = fragmentManager.getFragments()
        if (fragments != null) {
            for (fragment in fragments) {
                if (fragment != null && fragment.isVisible) return fragment
            }
        }
        return null
    }

    fun goGroupListFragment() {
     if (!isOnePaneMode()) {
         launchFragment(GroupItemListFragment())
     } else {
      //   recyclerMainGone()
         launchFragmentTemp(GroupItemListFragment())
     //    Toast.makeText(this, "Иван!", Toast.LENGTH_SHORT).show()
     }
    }



    fun goPaymentFragment() {
     if (!isOnePaneMode()) {
        launchFragment(PaymentItemListFragment.newInstanceNoneParams())
     } else {
       //  recyclerMainGone()
         launchFragmentTemp(PaymentItemListFragment.newInstanceNoneParams())
         //llaunchFragment(PaymentItemListFragment.newInstanceNoneParams())
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
    // Toast.makeText(this@MainActivity, "Отработал финиш", Toast.LENGTH_SHORT).show()
     supportFragmentManager.popBackStack()
    }

    private fun isOnePaneMode(): Boolean {
     return binding.shopItemContainer == null
    }


    private fun launchMainFragment(fragment: Fragment, name: String) {
     //   supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_item_container, fragment, "MainCalendarFragment")
            .addToBackStack(name)
            .commit()
    }


    private fun launchFragment(fragment: Fragment, name: String? = "other") {
     //supportFragmentManager.popBackStack()
     supportFragmentManager.beginTransaction()
         .replace(R.id.fragment_item_container, fragment, "OtherFragment")
         .addToBackStack(name)
         //.addToBackStack("CalendarItemFragment")
         .commit()
    }

    fun launchFragmentTemp(fragment: Fragment, name: String? = "other") {
     //supportFragmentManager.popBackStack()
     supportFragmentManager.beginTransaction()
         //.add(R.id.fragment_item_container, fragment)
         .replace(R.id.fragment_item_container, fragment, "OtherFragment")
         .addToBackStack(name)
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

    fun enableHomeBackIcon(enabled: Boolean) {
        // Enable/Disable opening the drawer from the start side
        toggle?.isDrawerIndicatorEnabled = !enabled

        // Change the default burger icon
        supportActionBar?.setHomeAsUpIndicator(
            if (enabled) R.drawable.ic_baseline_navigate_next_24
            else R.drawable.ic_baseline_menu_24
        )
    }

    private fun getActionBarDrawerToggle(
        drawerLayout: DrawerLayout,
        toolbar: Toolbar
    ): ActionBarDrawerToggle {
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle!!)
        toggle?.syncState()
        return toggle as ActionBarDrawerToggle
    }

    private fun updateAlertIcon() {
        // if alert count extends into two digits, just show the red circle
        if (0 < alertCount && alertCount < 10) {
            countTextView?.setText(java.lang.String.valueOf(alertCount))
        } else {
            countTextView?.setText("")
        }
        redCircle?.setVisibility(if (alertCount > 0) View.VISIBLE else View.GONE)
    }

    private fun clearAlertIcon() {
        // if alert count extends into two digits, just show the red circle
        alertCount = 0

        redCircle?.setVisibility(View.GONE)
    }

    companion object {
             const val BACK_STACK_ROOT_TAG = "root_fragment"
    }
}
