package com.example.lessonslist.presentation

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.impl.Schedulers
import com.example.lessonslist.R
import com.example.lessonslist.data.AppDatabase
import com.example.lessonslist.data.service.PaymentWork
import com.example.lessonslist.databinding.ActivityMainBinding
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.domain.payment.PaymentItem
import com.example.lessonslist.presentation.calendar.CalendarItemFragment
import com.example.lessonslist.presentation.calendar.CalendarPaymentItemFragment
import com.example.lessonslist.presentation.group.GroupItemFragment
import com.example.lessonslist.presentation.group.GroupListViewModel
import com.example.lessonslist.presentation.lessons.LessonsItemAddFragment
import com.example.lessonslist.presentation.lessons.LessonsItemEditFragment
import com.example.lessonslist.presentation.lessons.LessonsItemListFragment
import com.example.lessonslist.presentation.lessons.LessonsItemViewModel
import com.example.lessonslist.presentation.lessons.LessonsListViewModel
import com.example.lessonslist.presentation.payment.PaymentItemFragment
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.example.lessonslist.presentation.settings.SettingsNotificationsFragment
import com.example.lessonslist.presentation.student.StudentItemEditFragment
import com.example.lessonslist.presentation.student.StudentItemFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableOnSubscribe
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), StudentItemFragment.OnEditingFinishedListener, GroupItemFragment.OnEditingFinishedListener, PaymentItemFragment.OnEditingFinishedListener, CalendarItemFragment.OnEditingFinishedListener, SettingsNotificationsFragment.OnEditingFinishedListener, StudentItemEditFragment.OnEditingFinishedListener, LessonsItemAddFragment.OnEditingFinishedListener, LessonsItemEditFragment.OnEditingFinishedListener,
    CalendarPaymentItemFragment.OnEditingFinishedListener {

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
    private lateinit var navControllerTest: NavController


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
        startWorkManager()
        initWorkManager()
        initMaterialToolBar()
        getDeptPayment()
        initNavHeader()
        initBottomNavigationJetpack()
        //binding.fab.hide()
       /* val navController: NavController =
            findNavController(this, R.id.fragment_item_container)
        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        setupWithNavController(bottomNavigationView, navController)

*/
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        navControllerTest = navHostFragment.navController
        navControllerTest.addOnDestinationChangedListener { _, destination, _ ->
            onDestinationChanged(destination.id)
        }


    }




    private fun onDestinationChanged(currentDestination: Int) {
        val paymentBtnAppBarTop = findViewById<View>(R.id.payment)
        val backupBtnAppBarTop = findViewById<View>(R.id.backup)
        try {
            when(currentDestination) {
                R.id.calendarItemFragment -> {
                    enableHomeBackIcon(false)
                    paymentBtnAppBarTop.visibility = View.VISIBLE
                    backupBtnAppBarTop.visibility = View.VISIBLE
                } else -> {
                    enableHomeBackIcon(true)
                    toggle.setHomeAsUpIndicator(R.drawable.ic_baseline_navigate_before_24)
                    paymentBtnAppBarTop.visibility = View.GONE
                    backupBtnAppBarTop.visibility = View.GONE
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
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

        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()


        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.bottomItem1 -> {
                    navController.navigate(R.id.calendarItemFragment, null, animationOptions)
                    true
                }
                R.id.bottomItem2 -> {
                    navController.navigate(R.id.paymentItemListFragment, btnArgsPayment, animationOptions)
                    true
                }
                R.id.bottomItem3 -> {
                    navController.navigate(R.id.groupItemListFragment, null, animationOptions)
                    true
                }
                R.id.bottomItem4 -> {
                    navController.navigate(R.id.lessonsItemListFragment, btnArgsLessons, animationOptions)
                    true
                }
                R.id.bottomItem5 -> {
                    navController.navigate(R.id.studentItemListFragment, null, animationOptions)
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
                launchPaymentListEnabledFragment()
            } else {
                launchPaymentListNoParamsFragment()
            }
        }

        materialToolbar.setOnMenuItemClickListener {
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
    }

    private fun backup() {
        backup
            .database(AppDatabase.getInstance(applicationContext as Application))
            .enableLogDebug(true)
            .backupIsEncrypted(true)
            .customEncryptPassword("YOUR_SECRET_PASSWORD")
            .backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_DIALOG)
            .maxFileCount(5)
            .apply {
                onCompleteListener { _, _, _ ->

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
            val lessonIdForFragment = intent.getStringExtra("extra")
            if (lessonIdForFragment != null) {
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
                alertCount = listArrayPayment.size
                updateAlertIcon()
            } else {
                clearAlertIcon()
            }

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

        //toggle.syncState()


        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()


        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
               // R.id.muItem2 ->  navController.navigate(R.id.settingsNotificationsFragment, null, animationOptions)
                R.id.muItem3 ->  navController.navigate(R.id.instructionFragment, null, animationOptions)
                R.id.muItem4 -> navController.navigate(R.id.aboutFragment, null, animationOptions)
                R.id.muItem5 -> exitApplication()
            }
            true
        }



    }

    private fun exitApplication() {
        this.finishAffinity()
    }

    private fun startWorkManager() {
        val request = OneTimeWorkRequestBuilder<PaymentWork>().build()//change
        WorkManager.getInstance(this).enqueue(request)
    }

    private fun initWorkManager() {
        /*work manager */
            //PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 30, TimeUnit.MINUTES, 25, TimeUnit.MINUTES).build();
           // val request = OneTimeWorkRequestBuilder<PaymentWork>().build()
            /*val request = PeriodicWorkRequestBuilder<PaymentWork>(20, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
               .setConstraints(
                     Constraints.Builder()
                     .setRequiresCharging(true)
                     .build()
                 )
                 .build()*/
          //val request = OneTimeWorkRequestBuilder<PaymentWork>().build()//change
          // WorkManager.getInstance(this).enqueue(request)
          /*  WorkManager.getInstance(this).enqueue(request)
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
                .observe(this) {
                    it.state.name

                }*/
    /* this work copy*/
        val request = PeriodicWorkRequestBuilder<PaymentWork>(20, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "paymentWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
        /*work manager */
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         if(toggle.onOptionsItemSelected(item)){
             return true
         }
         return super.onOptionsItemSelected(item)
    }


    override fun onEditingFinished() {
        supportFragmentManager.popBackStack()
    }


    private fun initNavHeader() {

        val navigationView : NavigationView = binding.navView
        val headerView : View = navigationView.getHeaderView(0)
        val navScheduledCount : TextView = headerView.findViewById(R.id.nav_scheduled_count_lessons)
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

        viewModelLessons.lessonsList.observe(this) {
            var zaplanLessons = 0
            var zaplanMoney = 0

            for (index in it.indices) {
                val dateTimeLessons = parseStringDate(it[index].dateEnd)

                calendar.set(dateTimeLessons[0], dateTimeLessons[1]  - 1, dateTimeLessons[2], dateTimeLessons[3], dateTimeLessons[4])
                val lessonsDate = calendar.time
                val countStudent = it[index].student.split(",").toTypedArray().size
                //val countStudent1 = countStudent.size
                zaplanMoney += it[index].price * countStudent

                if(initialDate < lessonsDate) {
                    zaplanLessons++
                }

            }

            navZaplanMoneyCount.text = "Ожидаемый доход $zaplanMoney"
            navScheduledCount.text = "Запланировано: $zaplanLessons"

        }
        viewModelStudent.studentList.observe(this) {
            navStudentCount.text = it.size.toString()
        }

        viewModelPayment.paymentList.observe(this) {

            var paidLessons = 0
            var noPaidLessons = 0
            var deptMoney = 0
            var actualMoney = 0
            val idLessonsMap: HashSet<Int> = HashSet<Int>()

            for (index in it.indices) {
                idLessonsMap.add(it[index].lessonsId)
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

            val provedCountLess = idLessonsMap.size.toString()

            navDeptMoneyCount.text = "Долги: $deptMoney"
            navActualMoneyCount.text = "Фактический доход $actualMoney"
            navPaidLessons.text = "Оплаченных: $paidLessons"
            navNoPaidLessons.text = "Неоплаченные: $noPaidLessons"
            navConductedCount.text = "Проведено: $provedCountLess"
        }

        viewModelGroup.groupList.observe(this) {
            navGroupCount.text = it.size.toString()
        }

    }


   private fun parseStringDate(string: String): ArrayList<Int> {

       val valueReturn: ArrayList<Int> = ArrayList()
       val allData = string.split(" ")
       val dataDate = allData[0].split("/")
       val year = dataDate[0]
       val month = dataDate[1]
       val day = dataDate[2]

       var hour = ""
       var minute = ""
       if(allData.size > 2) {
           hour = allData[1]
           minute = allData[3]
       } else {
           val newDateTime = allData[1].split(":")
           hour = newDateTime[0]
           minute = newDateTime[1]
       }


       valueReturn.add(year.toInt())
       valueReturn.add(month.toInt())
       valueReturn.add(day.toInt())
       valueReturn.add(hour.toInt())
       valueReturn.add(minute.toInt())

       return valueReturn

   }

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
        countTextView?.text = alertCount.toString()
        redCircle?.visibility = if (alertCount > 0) View.VISIBLE else View.GONE
    }

    private fun clearAlertIcon() {
        alertCount = 0
        redCircle?.visibility = View.GONE
    }

}
