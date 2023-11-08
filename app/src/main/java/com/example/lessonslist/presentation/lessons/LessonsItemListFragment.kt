package com.example.lessonslist.presentation.lessons



import android.annotation.SuppressLint
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
import android.widget.Toast
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
import com.example.lessonslist.databinding.FragmentLessonsItemListBinding
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class LessonsItemListFragment: Fragment(), MenuProvider {

    private var _binding: FragmentLessonsItemListBinding? = null
    private val binding: FragmentLessonsItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private lateinit var viewModel: LessonsListViewModel
    private lateinit var lessonsListAdapter: LessonsListAdapter
    private lateinit var viewModelPayment: PaymentListViewModel


    private var toolbar: MaterialToolbar? = null
    private var menuChoice: Menu? = null
    private var hideModifyAppBar = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsItemListBinding.inflate(inflater, container, false)
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title  = "Список уроков"

        setupRecyclerView()

        val args = requireArguments()
        val dateFilter = args.getString(DATE_ID)

        showDateOrCustomList(dateFilter)

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem4).isChecked = true

        binding.buttonAddLessonsItem.setOnClickListener {
            navigateBtnAddLessons("")
        }

        goCalendarFragmentBackPressed()

    }

    private fun compareDateTimeLessonsAndNow(lessonsTime: String): Boolean {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
        val formatted = current.format(formatter)
        val currentTime = LocalDateTime.parse(formatted, formatter)
        val lessTime = LocalDateTime.parse(lessonsTime, formatter)
        return currentTime > lessTime
    }

    /*new menu in bar */

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        //Toast.makeText(activity, "menu choice is active", Toast.LENGTH_SHORT).show()
        menuChoice = menu
        menuInflater.inflate(R.menu.menu_recycler_choice, menu)
        showDeleteMenu(false)
        //return super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.menu_delete) {
            delete()
        } else if(menuItem.itemId == R.id.menu_select_all) {
            selectAll()
        }
        return false
    }

    fun showDeleteMenu(show: Boolean) {
      toolbar = (activity as AppCompatActivity).findViewById(R.id.tool_bar)
      val bottomNavigation = (activity as AppCompatActivity?)!!. window.findViewById<BottomNavigationView>(R.id.nav_view_bottom)
      if(show) {
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
              lessonsListAdapter.pairList.clear()
              setCustomDataLessonsCheckAll(false)
          }
          hideModifyAppBar = true
      } else {
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

    private fun goCalendarFragment() {
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navController.navigate(R.id.calendarItemFragment, null, animationOptions)
    }

    /*
        @Deprecated("Deprecated in Java")
        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
                 menuChoice = menu
                 inflater.inflate(R.menu.menu_recycler_choice, menu)
                 Toast.makeText(activity, "menu choice is active", Toast.LENGTH_SHORT).show()
                 showDeleteMenu(false)
                 return super.onCreateOptionsMenu(menu, inflater)
        }

        @Deprecated("Deprecated in Java")
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
                 when(item.itemId) {
                     R.id.menu_delete -> delete()
                     R.id.menu_select_all -> selectAll()
                 }
            return super.onOptionsItemSelected(item)
        }
    */
    private fun delete() {
        val alert = AlertDialog.Builder(requireContext())
        val title: String
        val txtDescription: String
        if(lessonsListAdapter.pairList.size == 1) {
            title = "Удалить урок"
            txtDescription = "Вы действительно хотите удалить выбранный урок ? Будьте внимательны с уроком удаляться и все его платежи."
        } else {
            title = "Удалить уроки"
            txtDescription = "Вы действительно хотите удалить выбранные уроки ? Будьте внимательны с уроками удаляться и все их платежи."
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
            if (lessonsListAdapter.pairList.isNotEmpty()) {
                lessonsListAdapter.pairList.forEach {
                    deletePaymentToLessons(it.key)
                    viewModel.deleteLessonsItem(it.value)
                }
            }
        }

        alert.setNegativeButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(false)
        alert.show()
    }

    private fun selectAll() {
        val itemCount = lessonsListAdapter.itemCount
        if(lessonsListAdapter.pairList.isNotEmpty() && lessonsListAdapter.pairList.size < itemCount) {
            if(lessonsListAdapter.pairList.size > 3) {
                lessonsListAdapter.pairList.clear()
                setCustomDataLessonsCheckAll(false)
            } else {
                setCustomDataLessonsCheckAll(true)
            }

        } else if (lessonsListAdapter.pairList.size == itemCount){
            lessonsListAdapter.pairList.clear()
            setCustomDataLessonsCheckAll(false)
        } else if (lessonsListAdapter.pairList.isEmpty()) {
             setCustomDataLessonsCheckAll(true)
        }
    }

    private fun deletePaymentToLessons(lessonsId: Int) {
        viewModelPayment = ViewModelProvider(this).get(PaymentListViewModel::class.java)
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            for (payment in it) {
                if(payment.lessonsId == lessonsId) {
                    viewModelPayment.deletePaymentItem(payment)
                }
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun showDateOrCustomList(dateFilter: String?) {
        if(dateFilter != null) {
            val listArrayPayment: ArrayList<LessonsItem> = ArrayList()
            viewModel = ViewModelProvider(this).get(LessonsListViewModel::class.java)
            viewModel.lessonsList.observe(viewLifecycleOwner) {
                listArrayPayment.clear()
                /*for (lessons in it) {
                    val pay = lessons.dateEnd.split(" ")
                    val datePay = Date(pay[0])
                    val dateFormatted = SimpleDateFormat("d/M/yyyy").format(datePay)
                    if(dateFormatted == dateFilter){
                        if(compareDateTimeLessonsAndNow(lessons.dateEnd)) {
                            val nn = lessons.copy(notifications = "finished")
                            listArrayPayment.add(nn)
                        } else {
                            listArrayPayment.add(lessons)
                        }

                    }
                }*/
                for (lessons in it) {
                    val pay = lessons.dateEnd.split(" ")
                    val formatter = DateTimeFormatter.ofPattern("yyyy/M/d")
                    val formatter2 = DateTimeFormatter.ofPattern("d/M/yyyy")
                    val lessTime = LocalDate.parse(pay[0], formatter)
                    val compareDt = LocalDate.parse(dateFilter, formatter2)
                    if(lessTime == compareDt){
                        if(compareDateTimeLessonsAndNow(lessons.dateEnd)) {
                            val nn = lessons.copy(notifications = "finished")
                            listArrayPayment.add(nn)
                        } else {
                            listArrayPayment.add(lessons)
                        }

                    }
                }



                if(listArrayPayment.size > 0) {
                    val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
                    val sortLessons = listArrayPayment.sortedByDescending {lessItem->
                        LocalDate.parse(lessItem.dateStart, formatter)
                    }
                    lessonsListAdapter.submitList(sortLessons)
                } else {
                    Toast.makeText(activity,"На эту дату уроков не запланировано!",Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            setCustomDataLessons()
        }
    }

    private fun setCustomDataLessons() {
        viewModel = ViewModelProvider(this)[LessonsListViewModel::class.java]
        viewModel.lessonsList.observe(viewLifecycleOwner) {listLessItem->
            val listNew = ArrayList<LessonsItem>()
            val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
            listLessItem.forEach {lessItem->
                if(compareDateTimeLessonsAndNow(lessItem.dateEnd)) {
                    val nn = lessItem.copy(notifications = "finished")
                    listNew.add(nn)
                } else {
                    listNew.add(lessItem)
                }

            }
            val sortLessons = listNew.sortedByDescending {sortLessItem->
                LocalDate.parse(sortLessItem.dateStart, formatter)
            }
            lessonsListAdapter.submitList(sortLessons)
        }
    }


    private fun setCustomDataLessonsCheckAll(selectAll: Boolean) {
        viewModel = ViewModelProvider(this)[LessonsListViewModel::class.java]
        viewModel.lessonsList.observe(viewLifecycleOwner) {listLessItem->
            val listNew = ArrayList<LessonsItem>()
            if(selectAll) {
                listLessItem.forEach {
                    if(compareDateTimeLessonsAndNow(it.dateEnd)) {
                        val nn = it.copy(notifications = "finished", student = "500")
                        listNew.add(nn)
                    } else {
                        val nn = it.copy(student = "500")
                        listNew.add(nn)
                    }
                }
            } else {
                listLessItem.forEach {
                    if(compareDateTimeLessonsAndNow(it.dateEnd)) {
                        val nn = it.copy(notifications = "finished", student = "0")
                        listNew.add(nn)
                    } else {
                        val nn = it.copy(student = "0")
                        listNew.add(nn)
                    }
                }
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
            val sortLessons = listNew.sortedByDescending {
                LocalDate.parse(it.dateStart, formatter)
            }
            lessonsListAdapter.submitList(sortLessons)
        }
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
        with(binding.rvLessonsList) {
            lessonsListAdapter = LessonsListAdapter { show -> showDeleteMenu(show) }
            adapter = lessonsListAdapter
            recycledViewPool.setMaxRecycledViews(
                LessonsListAdapter.VIEW_TYPE_ENABLED,
                LessonsListAdapter.MAX_POOL_SIZE
            )

        }
        setupClickListener()
    }

    private fun setupClickListener() {
        lessonsListAdapter.onLessonsItemClickListener = {
            navigateBtnEditLessons(it.id)
        }
    }

    private fun navigateBtnEditLessons(id: Int) {
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val args = requireArguments()
        val mode = args.getString(DATE_ID)
        if (mode != null) {
           // Toast.makeText(activity, "ne raven null", Toast.LENGTH_SHORT).show()
            val btnArgsLessons = Bundle().apply {
                putString(LessonsItemEditFragment.SCREEN_MODE, LessonsItemEditFragment.MODE_EDIT)
                putString(LessonsItemEditFragment.DATE_ID_BACKSTACK, mode)
                putInt(LessonsItemEditFragment.LESSONS_ITEM_ID, id)
            }
            val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
                .setExitAnim(R.anim.slide_in_right)
                .setPopEnterAnim(R.anim.slide_out_left)
                .setPopExitAnim(R.anim.slide_out_right).build()
            navController.navigate(R.id.lessonsItemEditFragment, btnArgsLessons, animationOptions)
        } else {
            val btnArgsLessons = Bundle().apply {
                putString(LessonsItemEditFragment.SCREEN_MODE, LessonsItemEditFragment.MODE_EDIT)
                putInt(LessonsItemEditFragment.LESSONS_ITEM_ID, id)
            }
            val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
                .setExitAnim(R.anim.slide_in_right)
                .setPopEnterAnim(R.anim.slide_out_left)
                .setPopExitAnim(R.anim.slide_out_right).build()
            navController.navigate(R.id.lessonsItemEditFragment, btnArgsLessons, animationOptions)
        }


    }

    private fun navigateBtnAddLessons(dateId: String) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemAddFragment.SCREEN_MODE, LessonsItemAddFragment.MODE_ADD)
            putString(LessonsItemAddFragment.DATE_ADD, dateId)
        }
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.lessonsItemAddFragment, btnArgsLessons, animationOptions)
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

    companion object {
        const val SCREEN_MODE = "screen_mode"
        const val CUSTOM_LIST = "custom_list"
        const val DATE_ID_LIST = "date_id_list"
        const val DATE_ID = "date_id"
    }

}