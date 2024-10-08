package com.leslist.lessonslist.presentation.lessons



import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
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
import androidx.navigation.fragment.NavHostFragment
import com.leslist.lessonslist.R
import com.leslist.lessonslist.databinding.FragmentLessonsItemListBinding
import com.leslist.lessonslist.domain.lessons.LessonsItem
import com.leslist.lessonslist.presentation.helpers.NavigationOptions
import com.leslist.lessonslist.presentation.lessons.sale.SalesItemListViewModel
import com.leslist.lessonslist.presentation.payment.PaymentListViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class LessonsItemListFragment: Fragment(), MenuProvider {

    private var _binding: FragmentLessonsItemListBinding? = null
    private val binding: FragmentLessonsItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this)[LessonsListViewModel::class.java]
    }
    private lateinit var lessonsListAdapter: LessonsListAdapter
    private val viewModelPayment by lazy {
        ViewModelProvider(this)[PaymentListViewModel::class.java]
    }
    private val viewModelSalesList by lazy {
        ViewModelProvider(this)[SalesItemListViewModel::class.java]
    }

    private var toolbar: MaterialToolbar? = null
    private var menuChoice: Menu? = null
    private var hideModifyAppBar = false
    private var flagNightMode = false

    private val navController by lazy {
        (activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment).navController
    }

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
            navigateBtnAddLessons()
        }

        goCalendarFragmentBackPressed()

        stateNightMode()
    }

    private fun stateNightMode() {
        val uiModeManager = requireContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val mode = uiModeManager.nightMode
        if (mode == UiModeManager.MODE_NIGHT_YES) {
            flagNightMode = true
            // System is in Night mode
        } else if (mode == UiModeManager.MODE_NIGHT_NO) {
            // System is in Day mode
            flagNightMode = false
        }
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

    private fun showDeleteMenu(show: Boolean) {
      toolbar = (activity as AppCompatActivity).findViewById(R.id.tool_bar)
      val bottomNavigation = (activity as AppCompatActivity?)!!. window.findViewById<BottomNavigationView>(R.id.nav_view_bottom)
      if(show) {
          binding.lessonsListRecyclerLayout.background = ColorDrawable(Color.parseColor("#CFCACA"))
          bottomNavigation.itemBackgroundResource = R.color.active_select_items
          toolbar?.findViewById<View>(R.id.menu_delete)?.visibility = View.VISIBLE
          toolbar?.findViewById<View>(R.id.menu_select_all)?.visibility = View.VISIBLE
          if (flagNightMode) {
              (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#032B47")
              toolbar?.background = ColorDrawable(Color.parseColor("#032B47"))
          } else {
              (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#0e0f0f")
              toolbar?.background = ColorDrawable(Color.parseColor("#0e0f0f"))
          }
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
          if (flagNightMode) {
              binding.lessonsListRecyclerLayout.background = ColorDrawable(Color.parseColor("#000000"))
              toolbar?.background = ColorDrawable(Color.parseColor("#000000"))
              (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#000000")
          } else {
              binding.lessonsListRecyclerLayout.background = ColorDrawable(Color.parseColor("#FFFFFF"))
              toolbar?.background = ColorDrawable(Color.parseColor("#0061A5"))
              (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#0061A5")
          }
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
        navController.navigate(R.id.calendarItemFragment, null, NavigationOptions().invoke())
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
        var alert = AlertDialog.Builder(requireContext())
        if (flagNightMode) {
            alert = AlertDialog.Builder(requireContext(), R.style.AlertDialog)
        }
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
                    deleteAllSaleItem(it.key)
                    viewModel.deleteLessonsItem(it.value)
                }
                showDeleteMenu(false)
                setCustomDataLessonsCheckAll(false)
            }
        }

        alert.setNegativeButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(true)
        alert.show()
    }


    private fun deleteAllSaleItem(id: Int) {
        val listDeleteId = HashSet<Int>()
        viewModelSalesList.salesList.observe(viewLifecycleOwner) { sales ->
            for (item in sales) {
                if(item.idLessons == id) {
                    if (!listDeleteId.contains(item.id)) {
                        listDeleteId.add(item.id)
                        viewModelSalesList.deleteSaleItem(item.id)
                    }
                }
            }
        }
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
            viewModel.lessonsList.observe(viewLifecycleOwner) {
                listArrayPayment.clear()
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
                    hideImageNoneItem()
                    lessonsListAdapter.submitList(sortLessons)
                } else {
                    Toast.makeText(activity,"На эту дату уроков не запланировано!",Toast.LENGTH_SHORT).show()
                    showImageNoneItem()
                }
            }
        } else {
            setCustomDataLessons()
        }


    }

    private fun setCustomDataLessons() {
        viewModel.lessonsList.observe(viewLifecycleOwner) { listLessItem ->
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
            val sortLessons = listNew.sortedByDescending { sortLessItem ->
                LocalDate.parse(sortLessItem.dateStart, formatter)
            }
            hideImageNoneItem()
            lessonsListAdapter.submitList(sortLessons)
            if(listLessItem.isEmpty()) {
                showImageNoneItem()
            }

        }
    }


    private fun showImageNoneItem() {
        binding.noLessons.visibility = View.VISIBLE
    }

    private fun hideImageNoneItem() {
        binding.noLessons.visibility = View.GONE
    }


    private fun setCustomDataLessonsCheckAll(selectAll: Boolean) {
        viewModel.lessonsList.observe(viewLifecycleOwner) {listLessItem->
            val listNew = ArrayList<LessonsItem>()
            if(selectAll) {
                listLessItem.forEach {
                    if(compareDateTimeLessonsAndNow(it.dateEnd)) {
                        val nn = it.copy(notifications = "finished", student = "500")
                        lessonsListAdapter.pairList[it.id] = it
                        listNew.add(nn)
                    } else {
                        val nn = it.copy(student = "500")
                        lessonsListAdapter.pairList[it.id] = it
                        listNew.add(nn)
                    }
                }
            } else {
                listLessItem.forEach {
                    if(compareDateTimeLessonsAndNow(it.dateEnd)) {
                        val nn = it.copy(notifications = "finished", student = "0")
                        lessonsListAdapter.pairList.remove(it.id)
                        listNew.add(nn)
                    } else {
                        val nn = it.copy(student = "0")
                        lessonsListAdapter.pairList.remove(it.id)
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
            navController.popBackStack(R.id.calendarItemFragment, true)
            navController.navigate(R.id.calendarItemFragment, null, NavigationOptions().invoke())
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
        val args = requireArguments()
        val mode = args.getString(DATE_ID)
        if (mode != null) {
           // Toast.makeText(activity, "ne raven null", Toast.LENGTH_SHORT).show()
            val btnArgsLessons = Bundle().apply {
                putString(LessonsItemEditFragment.SCREEN_MODE, LessonsItemEditFragment.MODE_EDIT)
                putString(LessonsItemEditFragment.DATE_ID_BACKSTACK, mode)
                putInt(LessonsItemEditFragment.LESSONS_ITEM_ID, id)
            }

            navController.navigate(R.id.lessonsItemEditFragment, btnArgsLessons, NavigationOptions().invoke())
        } else {
            val btnArgsLessons = Bundle().apply {
                putString(LessonsItemEditFragment.SCREEN_MODE, LessonsItemEditFragment.MODE_EDIT)
                putInt(LessonsItemEditFragment.LESSONS_ITEM_ID, id)
            }

            navController.navigate(R.id.lessonsItemEditFragment, btnArgsLessons, NavigationOptions().invoke())
        }


    }

    private fun navigateBtnAddLessons() {
        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemAddFragment.SCREEN_MODE, LessonsItemAddFragment.MODE_ADD)
            putString(LessonsItemAddFragment.DATE_ADD, "")
        }

        navController.navigate(R.id.lessonsItemAddFragment, btnArgsLessons, NavigationOptions().invoke())
    }



    override fun onStop() {
        super.onStop()
        if(hideModifyAppBar) {
            hideModifyAppBar()
        }
    }

    private fun hideModifyAppBar() {
        if (flagNightMode) {
            // System is in Night mode
            toolbar?.background = ColorDrawable(Color.parseColor("#000000"))
            (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#000000")
        } else {
            // System is in Day mode
            toolbar?.background = ColorDrawable(Color.parseColor("#0061A5"))
            (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#0061A5")
        }
        val bottomNavigation = (activity as AppCompatActivity?)!!. window.findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigation.itemBackgroundResource = R.color.noactive_select_items
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