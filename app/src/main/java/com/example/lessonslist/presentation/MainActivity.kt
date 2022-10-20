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
import com.example.lessonslist.domain.user.UserItem
import com.example.lessonslist.presentation.authuser.SignInFragment
import com.example.lessonslist.presentation.calendar.CalendarItemFragment
import com.example.lessonslist.presentation.calendar.CalendarPaymentItemFragment
import com.example.lessonslist.presentation.group.GroupItemFragment
import com.example.lessonslist.presentation.group.GroupItemListFragment
import com.example.lessonslist.presentation.group.GroupListViewModel
import com.example.lessonslist.presentation.info.AboutFragment
import com.example.lessonslist.presentation.info.InstructionFragment
import com.example.lessonslist.presentation.lessons.*
import com.example.lessonslist.presentation.payment.PaymentItemFragment
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentItemViewModel
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.example.lessonslist.presentation.settings.SettingsItemFragment
import com.example.lessonslist.presentation.student.StudentItemEditFragment
import com.example.lessonslist.presentation.student.StudentItemFragment
import com.example.lessonslist.presentation.student.StudentItemListFragment
import com.example.lessonslist.presentation.student.StudentItemViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import org.w3c.dom.Text
import ru.cleverpumpkin.calendar.CalendarDate
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), StudentItemFragment.OnEditingFinishedListener, GroupItemFragment.OnEditingFinishedListener, LessonsItemFragment.OnEditingFinishedListener, PaymentItemFragment.OnEditingFinishedListener, CalendarItemFragment.OnEditingFinishedListener, CalendarPaymentItemFragment.OnEditingFinishedListener, SettingsItemFragment.OnEditingFinishedListener, StudentItemEditFragment.OnEditingFinishedListener, LessonsItemAddFragment.OnEditingFinishedListener, LessonsItemEditFragment.OnEditingFinishedListener {


    private lateinit var binding: ActivityMainBinding

    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var backup: RoomBackup

    private var doubleBackToExitPressedOnce = false

    private var alertCount = 0
    private var redCircle: FrameLayout? = null
    private var countTextView: TextView? = null

    private lateinit var viewModelPayment: PaymentListViewModel
    private lateinit var viewModelLessons: LessonsListViewModel
    private lateinit var viewModelGroup: GroupListViewModel
    private lateinit var viewModelStudent: MainViewModel
    // create Firebase authentication object
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialising auth object

        auth = Firebase.auth
        /*launchMainFragment(SignInFragment(), "registration")*/
        if(auth.uid == null) {
            launchMainFragment(SignInFragment(), "registration")
        } else {
            launchMainFragment(CalendarItemFragment(), "calendar")
            getUserFireStore()
        }

        parseParamsExtra()

        backup = RoomBackup(this)

        initDrawerNavigation()
        initBottomNavigation()
        initWorkManager()
        initMaterialToolBar()
        getDeptPayment()
        initNavHeader()

    }


    private fun initMaterialToolBar() {

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


    private fun parseParamsExtra() {
        if (intent.getStringExtra("extra") != null) {
            Toast.makeText(this, "extra params" + intent.getStringExtra("extra"), Toast.LENGTH_SHORT).show()
            val lessonIdForFragment = intent.getStringExtra("extra")
            if (lessonIdForFragment != null) {
                launchFragment(LessonsItemEditFragment.newInstanceEditItem(lessonIdForFragment.toInt()))
            }
        }
    }

    private fun getDeptPayment() {
        val listArrayPayment: ArrayList<PaymentItem> = ArrayList()
        viewModelPayment = ViewModelProvider(this).get(PaymentListViewModel::class.java)
        viewModelPayment.paymentList.observe(this) {
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

    private fun initDrawerNavigation() {

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
             //   R.id.muItem2 -> getDialogBackup()
                R.id.muItem3 -> launchFragment(InstructionFragment())
                R.id.muItem4 -> launchFragment(AboutFragment())
               /* R.id.muItem5 -> goGroupListFragment()
                R.id.muItem6 -> goLessonsListFragment()
                R.id.muItem7 -> goStudentListFragment()
                R.id.muItem8 -> goTestAddLessons()*/

            }
            true
        }




    }


    private fun initBottomNavigation() {
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
    }

    private fun initWorkManager() {
        /*work manager */
        //PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 30, TimeUnit.MINUTES, 25, TimeUnit.MINUTES).build();
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
    }

    private fun testGetAccount() {
        val accManager : AccountManager = AccountManager.get(getApplicationContext())
        val acc = accManager.getAccountsByType("com.google")
        Log.d("accountName", acc.size.toString())
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

    fun initNavHeader() {

        val navigationView : NavigationView = binding.navView
        val headerView : View = navigationView.getHeaderView(0)
        val navSheduledCount : TextView = headerView.findViewById(R.id.nav_scheduled_count_lessons)
        val navConductedCount : TextView = headerView.findViewById(R.id.nav_conducted_count_lessons)
        val navStudentCount : TextView = headerView.findViewById(R.id.nav_value_count_student)
        val navPaidLessons : TextView = headerView.findViewById(R.id.nav_yes_paymenet_count_pay)
        val navNoPaidLessons : TextView = headerView.findViewById(R.id.nav_no_paymenet_count_pay)
        val navGroupCount : TextView = headerView.findViewById(R.id.nav_value_count_group)

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
            for (index in it.indices) {
                val dateTimeLessons = parseStringDate(it[index].dateEnd)
             //   Log.d("datelessons", it[index].dateEnd)

                calendar.set(dateTimeLessons[0], dateTimeLessons[1]  - 1, dateTimeLessons[2], dateTimeLessons[3], dateTimeLessons[4])
                val lessonsDate = calendar.time

                if(initialDate > lessonsDate) {
                    provedLessons++
                    Log.d("datelessons", lessonsDate.toString())
                } else {
                    zaplanLessons++
                    Log.d("datelessons1", lessonsDate.toString())
                }

            }
            //Toast.makeText(this, it.count().toString(), Toast.LENGTH_SHORT).show()
            navSheduledCount.text = "Запланировано: " + zaplanLessons.toString()
            navConductedCount.text = "Проведено: " + provedLessons.toString()

        }
        viewModelStudent.studentList.observe(this) {
            //Toast.makeText(this, it.size.toString(), Toast.LENGTH_SHORT).show()
            navStudentCount.text = it.size.toString()
        }

        viewModelPayment.paymentList.observe(this) {
            var paidLessons = 0
            var noPaidLessons = 0

            for (index in it.indices) {
                if(it[index].enabled) {
                    paidLessons++
                } else {
                    noPaidLessons++
                }

            }

            navPaidLessons.text = "Оплаченных: " + paidLessons
            navNoPaidLessons.text = "Неоплаченные: " + noPaidLessons

        }

        viewModelGroup.groupList.observe(this) {
            navGroupCount.text = it.size.toString()
        }

    //countLessons.setText(initialDate.toString())
    }

    private fun getUserFireStore() {

        val db = Firebase.firestore

        auth.uid?.let {
            val docRef = db.collection("Users").document(it)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                     //   val users = document.data.toObject(UserItem::class.java)
                        //val users = document.toObject<UserItem>()
                        //Log.d(TAG, "Данные DocumentSnapshot: ${document.data}")
                        //Log.d(TAG, "Данные DocumentSnapshot: ${document.data?.get("name")}")
                        val users = UserItem(document.data?.get("name").toString(), document.data?.get("sername").toString(),
                            document.data?.get("phone").toString(), document.data?.get("email").toString(),
                            document.data?.get("password").toString(), document.data?.get("id").toString())
                        (this as AppCompatActivity).findViewById<TextView>(R.id.nav_head_username).text = users.name + users.sername + "\n" + users.email
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }



    }


    fun testAuthEmailValidation() {
        TODO()
    }


   fun parseStringDate(string: String): ArrayList<Int> {

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
