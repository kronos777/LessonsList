package com.example.lessonslist.presentation

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.work.*
import com.example.lessonslist.R
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.data.service.PaymentWork
import com.example.lessonslist.databinding.ActivityMainBinding
import com.example.lessonslist.domain.payment.PaymentItem
import com.example.lessonslist.presentation.calendar.CalendarItemFragment
import com.example.lessonslist.presentation.group.GroupItemFragment
import com.example.lessonslist.presentation.group.GroupListViewModel
import com.example.lessonslist.presentation.lessons.*
import com.example.lessonslist.presentation.payment.PaymentItemFragment
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.example.lessonslist.presentation.settings.SettingsItemFragment
import com.example.lessonslist.presentation.student.StudentItemEditFragment
import com.example.lessonslist.presentation.student.StudentItemFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), StudentItemFragment.OnEditingFinishedListener, GroupItemFragment.OnEditingFinishedListener, LessonsItemFragment.OnEditingFinishedListener, PaymentItemFragment.OnEditingFinishedListener, CalendarItemFragment.OnEditingFinishedListener, SettingsItemFragment.OnEditingFinishedListener, StudentItemEditFragment.OnEditingFinishedListener, LessonsItemAddFragment.OnEditingFinishedListener, LessonsItemEditFragment.OnEditingFinishedListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var backup: RoomBackup
    //private var doubleBackToExitPressedOnce = false
    private var alertCount = 0
    private var redCircle: FrameLayout? = null
    private var countTextView: TextView? = null

    private lateinit var viewModelPayment: PaymentListViewModel
    private lateinit var viewModelLessons: LessonsListViewModel
    private lateinit var viewModelGroup: GroupListViewModel
    private lateinit var viewModelStudent: MainViewModel
    private lateinit var viewModelLesson: LessonsItemViewModel
    // create Firebase authentication object



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialising auth object
       /*launchMainFragment(SignInFragment(), "registration")*/
        parseParamsExtra()
      //  launchMainFragment(CalendarItemFragment(), "calendar")
        backup = RoomBackup(this)

        initDrawerNavigation()
        //initBottomNavigation()
        initWorkManager()
        initMaterialToolBar()
        getDeptPayment()
        initNavHeader()
        initBottomNavigationJetpack()
       /* val navController: NavController =
            findNavController(this, R.id.fragment_item_container)
        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        setupWithNavController(bottomNavigationView, navController)

*/


    }



    private fun initBottomNavigationJetpack() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        setupWithNavController(bottomNavigationView, navController)
        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.CUSTOM_LIST)
        }
        val btnArgsPayment = Bundle().apply {
            putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.CUSTOM_LIST)
        }
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.bottomItem1 -> {
                    // Respond to navigation item 1 click
                    //launchMainFragment(CalendarItemFragment(), "calendar")
                    navController.navigate(R.id.calendarItemFragment)
                    true
                }
                R.id.bottomItem2 -> {
                    // Respond to navigation item 2 click
                    //  Log.d("menuitem", "ite2")
                    //Toast.makeText(this, "item2", Toast.LENGTH_SHORT).show()
                    //goPaymentFragment()
                    navController.navigate(R.id.paymentItemListFragment, btnArgsPayment)
                    true
                }
                R.id.bottomItem3 -> {
                    // Respond to navigation item 2 click
                    navController.navigate(R.id.groupItemListFragment)
                    true
                }
                R.id.bottomItem4 -> {
                    // Respond to navigation item 2 click
                    //goLessonsListFragment()
                    navController.navigate(R.id.lessonsItemListFragment, btnArgsLessons)
                    true
                }
                R.id.bottomItem5 -> {
                    // Respond to navigation item 2 click
                    navController.navigate(R.id.studentItemListFragment)
                    //  goStudentListFragment()
                    true
                }
                else -> false
            }
            true
        }

    }

    private fun initMaterialToolBar() {

        redCircle = findViewById(R.id.view_alert_red_circle)
        countTextView = findViewById(R.id.view_alert_count_textview)

        val materialToolbar: MaterialToolbar = binding.toolBar
        val paymentBtnAppBarTop = findViewById<View>(R.id.payment)

        paymentBtnAppBarTop.setOnClickListener {
            if(alertCount > 0) {
               // launchFragment(PaymentItemListFragment.newInstanceEnabledPayment())
                launchPaymentListEnabledFragment()
            } else {
                //goPaymentFragment()
                launchPaymentListNoParamsFragment()
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


    }


    private fun launchPaymentListEnabledFragment() {
         val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.PAYMENT_ENABLED)
        }

        navController.navigate(R.id.paymentItemListFragment, btnArgsLessons)
    }

    private fun launchPaymentListNoParamsFragment() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.CUSTOM_LIST)
        }

        navController.navigate(R.id.paymentItemListFragment, btnArgsLessons)
    }


    override fun onBackPressed() {
       super.onBackPressed()
        /*val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController
        */
        //Toast.makeText(this, "содержимое бекстека." + navController.toString(), Toast.LENGTH_SHORT).show()
      //  navController.popBackStack()
      //  navController.popBackStack(R.id.calendarItemFragment, true)
      //  navController.navigate(R.id.calendarItemFragment)
        //    supportFragmentManager.popBackStack("calendar", 0)
       //val myFragment: Fragment = supportFragmentManager.findFragmentByTag("MainCalendarFragment") as Fragment
      /* val myFragment: Fragment = supportFragmentManager.findFragmentById(R.id.calendarItemFragment) as Fragment
        if (doubleBackToExitPressedOnce) {
            // super.onBackPressed()
            //return
            if (myFragment.isVisible) {
                this.finishAffinity()
              //  Toast.makeText(this, "Текущий форагмент календарь, можно выходить.", Toast.LENGTH_SHORT).show()
            }
        }
        this.doubleBackToExitPressedOnce = true
       // Toast.makeText(this, "Нажмите еще раз назад для выхода.", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 1000)*/
        /*supportFragmentManager.popBackStack("listStudent", 0)*/
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
            .setPositiveButton("Создать резервную копию.") { _, _ ->
                backup()
            }
            .setNegativeButton("Восстановить из резервной копии.") { _, _ ->
                //    log(date.toString())
                restore()
            }
            .setNeutralButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }



    private fun parseParamsExtra() {
        if (intent.getStringExtra("extra") != null) {
            Toast.makeText(this, "extra params" + intent.getStringExtra("extra"), Toast.LENGTH_SHORT).show()
            val lessonIdForFragment = intent.getStringExtra("extra")
            if (lessonIdForFragment != null) {
                //launchFragment(LessonsItemEditFragment.newInstanceEditItem(lessonIdForFragment.toInt()))
                launchLessonsItemEditFragmentId(lessonIdForFragment)
            }
        }
    }

    private fun launchLessonsItemEditFragmentId(lessonIdForFragment: String) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemEditFragment.SCREEN_MODE, LessonsItemEditFragment.MODE_EDIT)
            putInt(LessonsItemEditFragment.LESSONS_ITEM_ID, lessonIdForFragment.toInt())
        }

        navController.navigate(R.id.lessonsItemEditFragment, btnArgsLessons)
    }


    private fun getDeptPayment() {
        val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
        viewModelPayment = ViewModelProvider(this)[PaymentListViewModel::class.java]
        viewModelPayment.paymentList.observe(this) {
            listArrayPayment.clear()
            for (payment in it) {
                if(!payment.enabled){
                    listArrayPayment.add(payment)
                }
            }
            if(listArrayPayment.size > 0) {
                alertCount = 0
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


    private fun initDrawerNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController
        toggle = getActionBarDrawerToggle(binding.drawerLayoutId, binding.toolBar).apply {

            setToolbarNavigationClickListener {
                // Back to home fragment for any hit to the back button
                navController.navigate(R.id.main_navigation)

            }
            // Intialize the icon at the app start
            enableHomeBackIcon(false)
        }

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                //    R.id.muItem1 -> goGroupFragment()
                //      R.id.muItem2 -> launchFragment(SettingsItemFragment())
             //   R.id.muItem2 -> getDialogBackup()
                //R.id.muItem3 -> launchFragment(InstructionFragment())
                R.id.muItem3 ->  navController.navigate(R.id.instructionFragment)
                //R.id.muItem4 -> launchFragment(AboutFragment())
                R.id.muItem4 -> navController.navigate(R.id.aboutFragment)
                //R.id.muItem5 -> exitApplication()
                R.id.muItem5 -> exitApplication()
               /* R.id.muItem6 -> goLessonsListFragment()
                R.id.muItem7 -> goStudentListFragment()
                R.id.muItem8 -> goTestAddLessons()*/

            }
            true
        }

    }

    private fun exitApplication() {
        this.finishAffinity()
    }



    private fun initWorkManager() {
        /*work manager */
        //PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 30, TimeUnit.MINUTES, 25, TimeUnit.MINUTES).build();
         val request = PeriodicWorkRequestBuilder<PaymentWork>(20, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
           /*.setConstraints(
                 Constraints.Builder()
                 .setRequiresCharging(true)
                 .build()
             )*/
             .build()
        //val request = OneTimeWorkRequestBuilder<PaymentWork>().build()//change
       // WorkManager.getInstance(this).enqueue(request)
      /**  WorkManager.getInstance(this).enqueue(request)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
            .observe(this) {
                it.state.name

            }*/
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "paymentWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
            .observe(this) {
                it.state.name
                Log.d("worker_info", it.state.name)
            }

        /*work manager */
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

    /*
    private fun isOnePaneMode(): Boolean {
     return binding.shopItemContainer == null
    }
    */


    private fun initNavHeader() {

        val navigationView : NavigationView = binding.navView
        val headerView : View = navigationView.getHeaderView(0)
        val navSheduledCount : TextView = headerView.findViewById(R.id.nav_scheduled_count_lessons)
        val navConductedCount : TextView = headerView.findViewById(R.id.nav_conducted_count_lessons)
        val navStudentCount : TextView = headerView.findViewById(R.id.nav_value_count_student)
        val navPaidLessons : TextView = headerView.findViewById(R.id.nav_yes_paymenet_count_pay)
        val navNoPaidLessons : TextView = headerView.findViewById(R.id.nav_no_paymenet_count_pay)
        val navGroupCount : TextView = headerView.findViewById(R.id.nav_value_count_group)
        val navZaplanMoneyCount : TextView = headerView.findViewById(R.id.nav_title_income_count_expected)
        val navActualMoneyCount : TextView = headerView.findViewById(R.id.nav_title_income_count_actual)
        val navDeptMoneyCount : TextView = headerView.findViewById(R.id.nav_title_count_debts)

        val calendar = Calendar.getInstance()
        val calendarTimeZone: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val currentYear = calendarTimeZone[Calendar.YEAR]
        val currentMonth = calendarTimeZone[Calendar.MONTH]
        val currentDay = calendarTimeZone[Calendar.DAY_OF_MONTH]
        val currentHour = calendarTimeZone[Calendar.HOUR_OF_DAY]
        val currentMinute = calendarTimeZone[Calendar.MINUTE]

        calendar.set(currentYear, currentMonth, currentDay, currentHour, currentMinute)
        val initialDate = calendar.time

        viewModelLessons = ViewModelProvider(this)[LessonsListViewModel::class.java]
        viewModelStudent = ViewModelProvider(this)[MainViewModel::class.java]
        viewModelPayment = ViewModelProvider(this)[PaymentListViewModel::class.java]
        viewModelGroup = ViewModelProvider(this)[GroupListViewModel::class.java]
        viewModelLesson = ViewModelProvider(this)[LessonsItemViewModel::class.java]
      //  val countLessonsP = findViewById<TextView>(R.id.nav_conducted_count_lessons)
        //val countLessonsZ = findViewById<TextView>(R.id.nav_scheduled_count_lessons)
        //Toast.makeText(this, initialDate.toString(), Toast.LENGTH_SHORT).show()
        //Log.d("datelessons", initialDate.toString())
        //Log.d("currentMonth", currentMonth.toString())
        //val dataTime = parseStringDate("2022/9/15 20 : 50")
        //Log.d("datelessons", dataTime[4].toString())
        //countLessonsZ.setText("asgadfgdafgfadg")
        viewModelLessons.lessonsList.observe(this) {
            var provedLessons = 0
            var zaplanLessons = 0
            var zaplanMoney = 0

            for (index in it.indices) {
                val dateTimeLessons = parseStringDate(it[index].dateEnd)
             //   Log.d("datelessons", it[index].dateEnd)

                calendar.set(dateTimeLessons[0], dateTimeLessons[1]  - 1, dateTimeLessons[2], dateTimeLessons[3], dateTimeLessons[4])
                val lessonsDate = calendar.time
                val countStudent = it[index].student.split(",").toTypedArray().size
                //val countStudent1 = countStudent.size
                zaplanMoney += it[index].price * countStudent

                if(initialDate > lessonsDate) {
                    provedLessons++
                    Log.d("datelessons", lessonsDate.toString())
                } else {
                    zaplanLessons++
                    Log.d("datelessons1", lessonsDate.toString())
                }

            }

            navZaplanMoneyCount.text = "ожидаемый доход $zaplanMoney"

            navSheduledCount.text = "Запланировано: $zaplanLessons"
            navConductedCount.text = "Проведено: $provedLessons"

        }
        viewModelStudent.studentList.observe(this) {
            //Toast.makeText(this, it.size.toString(), Toast.LENGTH_SHORT).show()
            navStudentCount.text = it.size.toString()
        }

        viewModelPayment.paymentList.observe(this) {

            var paidLessons = 0
            var noPaidLessons = 0
            var deptMoney = 0
            var actualMoney = 0
            for (index in it.indices) {

                if(it[index].enabled) {
                    actualMoney += it[index].price
                    paidLessons++
                } else {
                    if(it[index].price != it[index].allprice) {
                        actualMoney += (it[index].allprice + it[index].price)
                    }
                    deptMoney += it[index].price
                    noPaidLessons++
                }

            }

            navDeptMoneyCount.text = R.string.nav_header_dept.toString() + " $deptMoney"
            navActualMoneyCount.text = "фактический доход $actualMoney"
            navPaidLessons.text = "Оплаченных: $paidLessons"
            navNoPaidLessons.text = "Неоплаченные: " + noPaidLessons

        }

        viewModelGroup.groupList.observe(this) {
            navGroupCount.text = it.size.toString()
        }

    //countLessons.setText(initialDate.toString())
    }


   private fun parseStringDate(string: String): ArrayList<Int> {

       val valueReturn: ArrayList<Int> = ArrayList()
       val allData = string.split(" ")
       val dataDate = allData[0].split("/")
       val year = dataDate[0]
       val month = dataDate[1]
       val day = dataDate[2]

      // var dataTimeMinute = allData[1].split(":")
       val hour = allData[1]
       val minute = allData[3]

       valueReturn.add(year.toInt())
       valueReturn.add(month.toInt())
       valueReturn.add(day.toInt())
       valueReturn.add(hour.toInt())
       valueReturn.add(minute.toInt())

       return valueReturn

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

    private fun enableHomeBackIcon(enabled: Boolean) {
        // Enable/Disable opening the drawer from the start side
        toggle.isDrawerIndicatorEnabled = !enabled
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
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        return toggle
    }

    private fun updateAlertIcon() {
        // if alert count extends into two digits, just show the red circle
      //  Toast.makeText(this, alertCount.toString(), Toast.LENGTH_SHORT).show()
        countTextView?.text = alertCount.toString()
        /*if (alertCount in 1..100) {
            countTextView?.text = java.lang.String.valueOf(alertCount)
        } else {
            countTextView?.text = ""
        }*/
        redCircle?.visibility = if (alertCount > 0) View.VISIBLE else View.GONE
    }

    private fun clearAlertIcon() {
        // if alert count extends into two digits, just show the red circle
        alertCount = 0
        redCircle?.visibility = View.GONE
    }

}