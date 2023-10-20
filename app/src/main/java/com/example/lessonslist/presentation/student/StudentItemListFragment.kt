package com.example.lessonslist.presentation.student


import android.content.DialogInterface
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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentStudentItemListBinding
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.helpers.StringHelpers
import com.example.lessonslist.presentation.lessons.LessonsItemViewModel
import com.example.lessonslist.presentation.lessons.sale.SaleItemViewModel
import com.example.lessonslist.presentation.lessons.sale.SalesItemListViewModel
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView


class StudentItemListFragment: Fragment(), MenuProvider {

    private var _binding: FragmentStudentItemListBinding? = null
    private val binding: FragmentStudentItemListBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemListBinding == null")

    private lateinit var viewModel: StudentListViewModel
    private lateinit var viewModelLessonsEdit: LessonsItemViewModel
    private lateinit var viewModelPayment: PaymentListViewModel
    private lateinit var viewModelSale: SaleItemViewModel
    private lateinit var viewModelSalesList: SalesItemListViewModel

    private lateinit var studentListAdapter: StudentListAdapter

    private var toolbar: MaterialToolbar? = null
    private var menuChoice: Menu? = null


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

        //(activity as AppCompatActivity).supportActionBar?.title = "Список учеников"
        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Список учеников"

        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(StudentListViewModel::class.java)
        viewModel.studentList.observe(viewLifecycleOwner) {
            val studentSort = it.sortedBy { it.name }
            studentListAdapter.submitList(studentSort)
        }
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem5).isChecked = true
        binding.buttonAddStudentItem.setOnClickListener {
            /*fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_item_container, StudentItemFragment.newInstanceAddItem())
                ?.addToBackStack("listStudent")
                ?.commit()*/
            navigateBtnAddStudent()
        }

        goCalendarFragmentBackPressed()
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

    private fun navigateBtnAddStudent() {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(StudentItemFragment.SCREEN_MODE, StudentItemFragment.MODE_ADD)
        }
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.studentItemFragment, btnArgsLessons, animationOptions)
    }

    private fun navigateBtnEditStudent(id: Int) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(StudentItemEditFragment.SCREEN_MODE, StudentItemEditFragment.MODE_EDIT)
            putInt(StudentItemEditFragment.SHOP_ITEM_ID, id)
        }
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.studentItemEditFragment, btnArgsLessons, animationOptions)
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
        val bottom_navigation = (activity as AppCompatActivity?)!!. window.findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        if(show) {
            bottom_navigation.itemBackgroundResource = R.color.active_select_items
            toolbar?.findViewById<View>(R.id.menu_delete)?.visibility = View.VISIBLE
            toolbar?.findViewById<View>(R.id.menu_select_all)?.visibility = View.VISIBLE
            (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#0e0f0f")
            toolbar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0e0f0f")))
            toolbar?.setOnMenuItemClickListener {
                onMenuItemSelected(it)
            }
            toolbar?.setNavigationIcon(R.drawable.baseline_close_24)
            toolbar?.setNavigationOnClickListener {
                showDeleteMenu(false)
                studentListAdapter.pairList.clear()
                setCustomDataStudentsCheckAll(false)
            }
        } else {
            bottom_navigation.itemBackgroundResource = R.color.noactive_select_items
            toolbar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0061A5")))
            (activity as AppCompatActivity?)!!.window.statusBarColor = Color.parseColor("#0061A5")
            toolbar?.findViewById<View>(R.id.menu_delete)?.visibility = View.GONE
            toolbar?.findViewById<View>(R.id.menu_select_all)?.visibility = View.GONE
            toolbar?.setNavigationIcon(R.drawable.ic_baseline_navigate_before_24)
            toolbar?.setNavigationOnClickListener {
                goCalendarFragment()
            }
        }
        menuChoice?.findItem(R.id.menu_delete)?.isVisible = show
        menuChoice?.findItem(R.id.menu_select_all)?.isVisible = show
    }

    private fun setCustomDataStudentsCheckAll(b: Boolean) {
        viewModel.studentList.observe(viewLifecycleOwner) {
            val listNew = ArrayList<StudentItem>()
            if(b) {
                it.forEach {
                    val ns = it.copy(group = "500")
                    listNew.add(ns)
                }
            } else {
                it.forEach {
                    val ns = it.copy(group = "0")
                    listNew.add(ns)
                }
            }
            studentListAdapter.submitList(listNew)
        }
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
            if(studentListAdapter.pairList.size >= 3) {
                studentListAdapter.pairList.clear()
                setCustomDataStudentsCheckAll(false)
            } else {
                setCustomDataStudentsCheckAll(true)
            }

        } else if (studentListAdapter.pairList.size == itemCount){
            studentListAdapter.pairList.clear()
            setCustomDataStudentsCheckAll(false)
        } else if (studentListAdapter.pairList.isEmpty()) {
            setCustomDataStudentsCheckAll(true)
        }
    }

    private fun delete() {
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

        alert.setPositiveButton("удалить", DialogInterface.OnClickListener {
                dialog, id ->
            if(studentListAdapter.pairList.isNotEmpty()) {
                //Log.d("pairListElementForDelete", "allData" + studentListAdapter.pairList.size.toString())
                studentListAdapter.pairList.forEach {
                    deletePaymentToStudent(it.key)
                    deleteAllSaleItem(it.key)
                    viewModel.deleteStudentItem(it.key)
                }
            }
        })

        alert.setNegativeButton("не удалять", DialogInterface.OnClickListener {
                dialog, id ->
            dialog.dismiss()
        })

        alert.setCancelable(false)
        alert.show()
    }

    private fun deletePaymentToStudent(studentId: Int) {
        viewModelPayment = ViewModelProvider(this).get(PaymentListViewModel::class.java)
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            for (payment in it) {
                if(payment.studentId == studentId) {
                    //Log.d("payment.lessonsId", payment.lessonsId.toString())
                    editLessonsItem(payment.lessonsId, studentId)
                    viewModelPayment.deletePaymentItem(payment)
                }
            }
        }
    }

    private fun editLessonsItem(idLessons: Int, studentId: Int) {
        viewModelLessonsEdit = ViewModelProvider(this).get(LessonsItemViewModel::class.java)
        viewModelLessonsEdit.getLessonsItem(idLessons)
        // val lessonsItem = viewModelLessonsEdit.lessonsItem
        viewModelLessonsEdit.lessonsItem.observe(viewLifecycleOwner) {
            //Log.d("valStudent", it.student)
            val newValueStudent = dropElementList(StringHelpers.getStudentIds(it.student), studentId)
            //Log.d("delStudent", newValueStudent)
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
        val studentItemId = id
        viewModelSalesList = ViewModelProvider(this)[SalesItemListViewModel::class.java]
        viewModelSale = ViewModelProvider(this)[SaleItemViewModel::class.java]
        viewModelSalesList.salesList.observe(viewLifecycleOwner) { sales ->
            for (saleItem in sales.indices) {
                if(studentItemId == sales[saleItem].idStudent) {
                    viewModelSale.deleteSaleItem(sales[saleItem].id)
                    //  Log.d("studentDataForDelete", sales[saleItem].toString())
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
           /* fragmentManager?.beginTransaction()
             //   ?.replace(R.id.fragment_item_container, StudentItemFragment.newInstanceEditItem(it.id))
                ?.replace(R.id.fragment_item_container, StudentItemEditFragment.newInstanceEditItem(it.id))
                ?.addToBackStack("listStudent")
                ?.commit()*/
            navigateBtnEditStudent(it.id)
       }
    }

    private fun setupSwipeListener(rvStudentList: RecyclerView) {
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
                val item = studentListAdapter.currentList[viewHolder.adapterPosition]
               // viewModel.deleteStudentItem(item)
                dialogWindow(item.id, item, item.name + " " + item.lastname)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvStudentList)
    }



    private fun dialogWindow(studentId: Int, student: StudentItem, title: String) {

        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Удалить студента $title")

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

        alert.setPositiveButton("удалить", DialogInterface.OnClickListener {
                dialog, id ->
            //deleteLessonsPay
          //  deletePaymentToStudent(studentId)
          //  viewModel.deleteStudentItem(student)
        })

        alert.setNegativeButton("не удалять", DialogInterface.OnClickListener {
                dialog, id ->
            dialog.dismiss()
        })

        alert.setCancelable(false)
        alert.show()

    }

    override fun onStop() {
        super.onStop()
        showDeleteMenu(false)
    }



}
