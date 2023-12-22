package com.example.lessonslist.presentation.student


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
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentStudentItemListBinding
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.helpers.NavigationOptions
import com.example.lessonslist.presentation.helpers.StringHelpers
import com.example.lessonslist.presentation.lessons.LessonsItemViewModel
import com.example.lessonslist.presentation.lessons.sale.SalesItemListViewModel
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


class StudentItemListFragment: Fragment(), MenuProvider {

    private var _binding: FragmentStudentItemListBinding? = null
    private val binding: FragmentStudentItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this)[StudentListViewModel::class.java]
    }
    private val viewModelLessonsEdit by lazy {
        ViewModelProvider(this)[LessonsItemViewModel::class.java]
    }
    private val viewModelPayment by lazy {
        ViewModelProvider(this)[PaymentListViewModel::class.java]
    }
    private val viewModelSalesList by lazy {
        ViewModelProvider(this)[SalesItemListViewModel::class.java]
    }
    private val viewModelParentContact by lazy {
        ViewModelProvider(this)[ParentContactViewModel::class.java]
    }
    private val viewModelNotesItem by lazy {
        ViewModelProvider(this)[NotesItemViewModel::class.java]
    }

    private lateinit var studentListAdapter: StudentListAdapter

    private var toolbar: MaterialToolbar? = null
    private var menuChoice: Menu? = null
    private var hideModifyAppBar = false

    private val navController by lazy {
        (activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment).navController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Список учеников"

        setupRecyclerView()
        setData()


        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem5).isChecked = true
        binding.buttonAddStudentItem.setOnClickListener {
            navigateBtnAddStudent()
        }

        goCalendarFragmentBackPressed()
    }

    private fun setData() {
        viewModel.studentList.observe(viewLifecycleOwner) { listStudent ->
            if(listStudent.isNotEmpty()) {
                if(binding.noStudent.visibility == View.VISIBLE) {
                    binding.noStudent.visibility = View.GONE
                }
                val studentSort = listStudent.sortedBy { it.name }
                studentListAdapter.submitList(studentSort)
            } else {
                binding.noStudent.visibility = View.VISIBLE
            }
        }
    }


    private fun goCalendarFragmentBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController.popBackStack(R.id.calendarItemFragment, true)
            navController.navigate(R.id.calendarItemFragment, null, NavigationOptions().invoke())
        }
    }

    private fun navigateBtnAddStudent() {

        val btnArgsLessons = Bundle().apply {
            putString(StudentItemFragment.SCREEN_MODE, StudentItemFragment.MODE_ADD)
        }


        navController.navigate(R.id.studentItemFragment, btnArgsLessons, NavigationOptions().invoke())
    }

    private fun navigateBtnEditStudent(id: Int) {

        val btnArgsLessons = Bundle().apply {
            putString(StudentItemEditFragment.SCREEN_MODE, StudentItemEditFragment.MODE_EDIT)
            putInt(StudentItemEditFragment.SHOP_ITEM_ID, id)
        }

        navController.navigate(R.id.studentItemEditFragment, btnArgsLessons, NavigationOptions().invoke())
    }



    private fun setupRecyclerView() {
        with(binding.rvStudentList) {
            studentListAdapter = StudentListAdapter { show -> showDeleteMenu(show)  }
            adapter = studentListAdapter
            recycledViewPool.setMaxRecycledViews(
                StudentListAdapter.VIEW_TYPE_ENABLED,
                StudentListAdapter.MAX_POOL_SIZE
            )

        }
        //setupLongClickListener()
        setupClickListener()
        //setupSwipeListener(binding.rvStudentList)
    }

    private fun showDeleteMenu(show: Boolean) {
        toolbar = (activity as AppCompatActivity).findViewById(R.id.tool_bar)
        val bottomNavigation = (activity as AppCompatActivity?)!!. window.findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        if(show) {
            binding.studentListRecyclerLayout.background = ColorDrawable(Color.parseColor("#CFCACA"))
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
                studentListAdapter.pairList.clear()
                setCustomDataStudentsSelectAll(false)
            }
            hideModifyAppBar = true
        } else {
            binding.studentListRecyclerLayout.background = ColorDrawable(Color.parseColor("#FFFFFF"))
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

    private fun setCustomDataStudentsSelectAll(b: Boolean) {
        viewModel.studentList.observe(viewLifecycleOwner) {
            val listNew = ArrayList<StudentItem>()
            if(b) {
                it.forEach {si->
                    val ns = si.copy(group = "500")
                    studentListAdapter.pairList[si.id] = si
                    listNew.add(ns)
                }
            } else {
                it.forEach {si->
                    val ns = si.copy(group = "0")
                    studentListAdapter.pairList.remove(si.id)
                    listNew.add(ns)
                }
            }
            studentListAdapter.submitList(listNew)
        }
    }

    private fun goCalendarFragment() {
        navController.navigate(R.id.calendarItemFragment, null, NavigationOptions().invoke())
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
        val itemCount = studentListAdapter.itemCount
        if(studentListAdapter.pairList.isNotEmpty() && studentListAdapter.pairList.size < itemCount) {//notEmpty
            if(studentListAdapter.pairList.size > 3) {
                studentListAdapter.pairList.clear()
                setCustomDataStudentsSelectAll(false)
            } else {
                setCustomDataStudentsSelectAll(true)
            }
        } else if (studentListAdapter.pairList.size == itemCount){
            studentListAdapter.pairList.clear()
            setCustomDataStudentsSelectAll(false)
        } else if (studentListAdapter.pairList.isEmpty()) {
            setCustomDataStudentsSelectAll(true)
        }
    }

    private fun delete() {
        if (studentListAdapter.pairList.isNotEmpty()) {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Удалить студента")

            val layout = LinearLayout(requireContext())
            layout.orientation = LinearLayout.VERTICAL

            val paymentsLabel = TextView(requireContext())
            paymentsLabel.setSingleLine()
            paymentsLabel.text = "Будьте внимательны вместе со студентом удаляются все данные о нем."
            paymentsLabel.isSingleLine = false
            paymentsLabel.height = 250
            paymentsLabel.top = 15
            layout.addView(paymentsLabel)


            layout.setPadding(50, 40, 50, 10)

            alert.setView(layout)

            alert.setPositiveButton("удалить") { _, _ ->
                if (studentListAdapter.pairList.isNotEmpty()) {
                    studentListAdapter.pairList.forEach {
                        deletePaymentToStudent(it.key)
                        deleteAllSaleItem(it.key)
                        deleteAllContactStudent(it.key)
                        deleteAllNotesStudent(it.key)
                        viewModel.deleteStudentItem(it.key)
                    }
                    setCustomDataStudentsSelectAll(false)
                    showDeleteMenu(false)
                }
            }

            alert.setNegativeButton("не удалять") { dialog, _ ->
                dialog.dismiss()
            }

            alert.setCancelable(true)
            alert.show()
        } else {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Не выбран не один студент")

            val layout = LinearLayout(requireContext())
            layout.orientation = LinearLayout.VERTICAL

            val paymentsLabel = TextView(requireContext())
            paymentsLabel.setSingleLine()
            paymentsLabel.text = "Выберите хотя бы одного студента для удаления."
            paymentsLabel.isSingleLine = false
            paymentsLabel.height = 250
            paymentsLabel.top = 15
            layout.addView(paymentsLabel)


            layout.setPadding(50, 40, 50, 10)

            alert.setView(layout)

            alert.setNegativeButton("отмена") { dialog, _ ->
                dialog.dismiss()
            }

            alert.setCancelable(true)
            alert.show()
        }
    }
    private fun deleteAllNotesStudent(studentItemId: Int) {
        val listDeleteId = HashSet<Int>()
        viewModelNotesItem.notesList.getNotesList().observe(viewLifecycleOwner) {
            for (item in it) {
                if(item.student == studentItemId) {
                    if (!listDeleteId.contains(item.id)) {
                        listDeleteId.add(item.id)
                        viewModelNotesItem.deleteNotesItem(item.id)
                    }
                }
            }
        }
    }

    private fun deleteAllContactStudent(studentItemId: Int) {
        val listDeleteId = HashSet<Int>()
        viewModelParentContact.parentContactList.getParentList().observe(viewLifecycleOwner) {
            for (item in it) {
                if (item.student == studentItemId) {
                    if (!listDeleteId.contains(item.id)) {
                        listDeleteId.add(item.id)
                        viewModelParentContact.deleteParentContact(item.id)
                    }
                }
            }
        }

    }
    private fun deletePaymentToStudent(studentId: Int) {
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            for (payment in it) {
                if(payment.studentId == studentId) {
                    editLessonsItem(payment.lessonsId, studentId)
                    viewModelPayment.deletePaymentItem(payment)
                }
            }
        }
    }

    private fun editLessonsItem(idLessons: Int, studentId: Int) {
        viewModelLessonsEdit.getLessonsItem(idLessons)
        viewModelLessonsEdit.lessonsItem.observe(viewLifecycleOwner) {
            val newValueStudent = dropElementList(StringHelpers.getStudentIds(it.student), studentId)
            viewModelLessonsEdit.editLessonsItem(
                it.title,
                it.notifications,
                newValueStudent,
                it.price.toString(),
                it.dateStart,
                it.dateEnd
            )
        }
    }

    private fun deleteAllSaleItem(id: Int) {
        val listDeleteId = HashSet<Int>()
        viewModelSalesList.salesList.observe(viewLifecycleOwner) { sales ->
            for (item in sales) {
                if(item.idStudent == id) {
                    if (!listDeleteId.contains(item.id)) {
                        listDeleteId.add(item.id)
                        viewModelSalesList.deleteSaleItem(item.id)
                    }
                }
            }
        }
    }

    private fun dropElementList(arrayList: List<Int>, el: Int): String {
        val elementList = mutableListOf<Int>()
        for(item in arrayList) {
            if(item != el){
                elementList.add(item)
            }

        }
        return elementList.toString()
    }

    /*private fun setupLongClickListener() {
        studentListAdapter.onStudentItemLongClickListener = {
            val item = studentListAdapter.currentList[it.id -1]
            // viewModel.deleteStudentItem(item)
            dialogWindow(item.id, item, item.name + " " + item.lastname)
        }
    }*/


    private fun setupClickListener() {
        studentListAdapter.onStudentItemClickListener = {
            navigateBtnEditStudent(it.id)
       }
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
