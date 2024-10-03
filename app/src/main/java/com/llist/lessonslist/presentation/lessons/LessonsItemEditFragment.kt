package com.llist.lessonslist.presentation.lessons

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.llist.lessonslist.R
import com.llist.lessonslist.databinding.FragmentLessonsItemEditBinding
import com.llist.lessonslist.domain.group.GroupItem
import com.llist.lessonslist.domain.lessons.LessonsItem
import com.llist.lessonslist.presentation.group.DataStudentGroupModel
import com.llist.lessonslist.presentation.group.ListStudentAdapter
import com.llist.lessonslist.presentation.helpers.NavigationOptions
import com.llist.lessonslist.presentation.helpers.PhoneTextFormatter
import com.llist.lessonslist.presentation.helpers.StringHelpers
import com.llist.lessonslist.presentation.lessons.sale.DataSalePaymentModel
import com.llist.lessonslist.presentation.lessons.sale.ListFlexibleSaleAdapter
import com.llist.lessonslist.presentation.lessons.sale.SaleItemViewModel
import com.llist.lessonslist.presentation.lessons.sale.SalesItemListViewModel
import com.llist.lessonslist.presentation.payment.PaymentListViewModel
import com.llist.lessonslist.presentation.student.StudentListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


@Suppress("NAME_SHADOWING", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNUSED_EXPRESSION")
class LessonsItemEditFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[LessonsItemViewModel::class.java]
    }
    private val viewModelSale by lazy {
        ViewModelProvider(this)[SaleItemViewModel::class.java]
    }
    private val viewModelSalesList by lazy {
        ViewModelProvider(this)[SalesItemListViewModel::class.java]
    }

    private val dataStudentlList by lazy {
        ViewModelProvider(this)[StudentListViewModel::class.java]
    }

    private val viewModelPayment by lazy {
        ViewModelProvider(this)[PaymentListViewModel::class.java]
    }


    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentLessonsItemEditBinding? = null
    private val binding: FragmentLessonsItemEditBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")



    private var lessonsItemId: Int = LessonsItem.UNDEFINED_ID

    private lateinit var listViewSale: ListView
    private var dataStudentSaleModel: ArrayList<DataSalePaymentModel>? = null
    private lateinit var adapterSaleReadyFlexible: ListFlexibleSaleAdapter
    private lateinit var salePaymentValueDate: HashSet<Int?>

    private lateinit var adapter: ListStudentAdapter
    private lateinit var listView: ListView
    private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null


    private var dataPaymentStudentModel: ArrayList<DataPaymentStudentLessonsModel>? = null
    private var notificationString: String = ""
    private var notificationBoolean: Boolean = true

    private val mCurrentTime: Calendar = Calendar.getInstance()
    private var year: Int = mCurrentTime.get(Calendar.YEAR)
    private var month: Int = mCurrentTime.get(Calendar.MONTH)
    private var day: Int = mCurrentTime.get(Calendar.DAY_OF_MONTH)

    private val navController by lazy {
        (activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsItemEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Урок"

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        launchEditMode()
        observeViewModel()

        binding.tilStudent.visibility = View.GONE
        binding.listSalePayment.visibility = View.GONE

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem4).isChecked = true

        setDataLessonsPayment()
        uiDatePickerElement()
        switchCardStudents()
        switchCardSaleStudent()

        notificationsLessons()
        changeDataLessons()
        initDate()

    }

    override fun onStart() {
        super.onStart()
        viewModel.lessonsItem.observe(this) { lessonItem ->
            setTitleInfo(lessonItem.title)
        }
    }

    private fun setTitleInfo(infoTitle: String) {
        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = infoTitle
    }
    private fun initDate() {
        viewModel.lessonsItem.observe(viewLifecycleOwner) {
            // Toast.makeText(activity, "curDate: " + it.dateStart, Toast.LENGTH_SHORT).show()
            val firstField = it.dateStart
            val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
            val fTime = LocalDateTime.parse(firstField, formatter)
            year = fTime.year
            month = fTime.month.value - 1
            day = fTime.dayOfMonth
        }

    }


    private fun changeDataLessons() {
        binding.textSwitchData.setOnClickListener {
            setNewDayLessons()
        }
    }

    private fun setDataLessonsPayment() {
        listView = binding.listView
        dataStudentGroupModel = ArrayList()
        var studentName: Array<String> = emptyArray()
        dataPaymentStudentModel = ArrayList()
        viewModelPayment.paymentList.observe(viewLifecycleOwner) { paymentItemList ->
            if(paymentItemList.isNotEmpty()) {
                for (payment in paymentItemList) {
                    if(payment.lessonsId == lessonsItemId) {
                        if (payment.enabled) {
                            dataPaymentStudentModel!!.add(DataPaymentStudentLessonsModel(payment.student ," Оплачен: ", payment.price.toString()))
                        } else {
                            dataPaymentStudentModel!!.add(DataPaymentStudentLessonsModel(payment.student, " Долг: ", "-" + payment.price.toString()))
                        }

                    }
                }
            }


            if(dataPaymentStudentModel!!.size > 0) {
                val adapter =  ListPaymentLessonsAdapter(dataPaymentStudentModel!!, requireContext().applicationContext)
                listView.adapter = adapter
                setFocusableEditText()
            } else {
                binding.textViewPriceInfo.visibility = View.GONE
                dataStudentlList.studentList.observe(viewLifecycleOwner) {
                    if(it.isNotEmpty()) {
                        for(student in it){
                            val name = student.name + " " + student.lastname
                            val id = student.id
                            studentName += name
                            if(viewModel.lessonsItem.value != null) {
                                viewModel.lessonsItem.observe(viewLifecycleOwner) {lesson->

                                    val lstValues: List<Int> = StringHelpers.getStudentIds(lesson.student)
                                    if(lstValues.contains(id)) {
                                        dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,true))
                                    } else {
                                        dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                                    }
                                }
                            } else {
                                dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,false))
                            }
                        }
                        adapter = ListStudentAdapter(dataStudentGroupModel!!, requireContext().applicationContext)
                        listView.adapter = adapter
                    } else {
                        studentName += "в учениках пока нет значений"
                    }
                }
            }
            goLessonsListFragmentBackPressed()
        }
    }

    private fun switchCardStudents() {
        binding.cardStudents.setOnClickListener {
            if (binding.listView.visibility == View.VISIBLE){
                showUiLessonsElement()
                binding.listView.visibility = View.GONE
                binding.listSalePayment.visibility = View.GONE
            } else if(binding.listSalePayment.visibility == View.VISIBLE) {
                hideUiLessonsElement()
                binding.listView.visibility = View.VISIBLE
                binding.listSalePayment.visibility = View.GONE
            } else {
                hideUiLessonsElement()
                binding.listView.visibility = View.VISIBLE
                binding.listSalePayment.visibility = View.GONE
            }
        }
    }
    private fun switchCardSaleStudent() {
        binding.cardSaleStudent.setOnClickListener {
            if(binding.listSalePayment.visibility == View.VISIBLE) {
                showUiLessonsElement()
                binding.listView.visibility = View.GONE
                binding.listSalePayment.visibility = View.GONE
            } else if (binding.listView.visibility == View.VISIBLE){
                hideUiLessonsElement()
                binding.listView.visibility = View.GONE
                binding.listSalePayment.visibility = View.VISIBLE
            } else {
                hideUiLessonsElement()
                binding.listView.visibility = View.GONE
                binding.listSalePayment.visibility = View.VISIBLE
            }
            salePaymentValueDate = checkValidStudent()
            if(salePaymentValueDate.size == 0) {
                for (index in dataStudentGroupModel!!.indices) {
                    if(dataStudentGroupModel!![index].checked) {
                        salePaymentValueDate.add(dataStudentGroupModel!![index].id!!.toInt())
                    }

                }
                setListViewSaleFlexible(salePaymentValueDate)
                //saveEditAddDeleteSale()
            } else {
                setListViewSaleFlexible(salePaymentValueDate)
                // saveEditAddDeleteSale()
            }

        }
    }

    private fun hideUiLessonsElement() {
        binding.tilTitle.visibility = View.GONE
        binding.tilPrice.visibility = View.GONE
        binding.tilDatestart.visibility = View.GONE
        binding.tilDateend.visibility = View.GONE
        binding.tilNotifications.visibility = View.GONE
    }

    private fun showUiLessonsElement() {
        binding.tilTitle.visibility = View.VISIBLE
        binding.tilPrice.visibility = View.VISIBLE
        binding.tilDatestart.visibility = View.VISIBLE
        binding.tilDateend.visibility = View.VISIBLE
        binding.tilNotifications.visibility = View.VISIBLE
    }

    private fun uiDatePickerElement() {

        val mTimePicker: TimePickerDialog
        val mTimePickerEnd: TimePickerDialog

        val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mCurrentTime.get(Calendar.MINUTE)

        var timePicker1 = ""
        var timePicker2 = ""

        mTimePicker = TimePickerDialog(context,
            { _, hourOfDay, minute ->
                val minH = StringHelpers.timeForLessons(hourOfDay)
                val minM = StringHelpers.timeForLessons(minute)
                binding.etDatestart.setText(String.format("%d/%d/%d %s:%s", year, month + 1, day, minH, minM))
                timePicker1 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + minH + ":" + minM
                if (timePicker1.isNotEmpty() && timePicker2.isNotEmpty()) {
                    checkAddDateTime(timePicker1, timePicker2)
                }
            }, hour, minute, true)

        mTimePickerEnd = TimePickerDialog(context,
            { _, hourOfDay, minute ->
                val minH = StringHelpers.timeForLessons(hourOfDay)
                val minM = StringHelpers.timeForLessons(minute)
                binding.etDateend.setText(String.format("%d/%d/%d %s:%s", year, month + 1, day, minH, minM))
                timePicker2 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + minH + ":" + minM
                if (timePicker1.isNotEmpty() && timePicker2.isNotEmpty()) {
                    checkAddDateTime(timePicker1, timePicker2)
                }
            }, hour, minute, true)

        binding.etDatestart.setOnClickListener{
            mTimePicker.show()
        }

        binding.etDateend.setOnClickListener{
            mTimePickerEnd.show()

        }
    }

    private fun setNewDayLessons() {

        val firstField = binding.etDatestart.text
        val twoField = binding.etDateend.text
        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
        val fTime = LocalDateTime.parse(firstField, formatter)
        val tTime = LocalDateTime.parse(twoField, formatter)

       // Toast.makeText(activity, "current Time minute"+fTime.minute, Toast.LENGTH_SHORT).show()
       // if (minute < 10) "0$minute" else if(minute == 0) "00" else minute

        val dpd =
            activity?.let {
                DatePickerDialog(requireContext(), { _, yearcur, monthOfYear, dayOfMonth ->
                    mCurrentTime.set(yearcur, monthOfYear, dayOfMonth)
                    year = mCurrentTime[Calendar.YEAR]
                    month = mCurrentTime[Calendar.MONTH]
                    day = mCurrentTime[Calendar.DAY_OF_MONTH]
                    binding.etDatestart.setText(String.format("%d/%d/%d %s:%s", year, month + 1, day, fTime.hour, if (fTime.minute < 10) "0${fTime.minute}" else if(fTime.minute==0){ "00" } else { fTime.minute }))
                    binding.etDateend.setText(String.format("%d/%d/%d %s:%s", year, month + 1, day, tTime.hour, if (tTime.minute < 10) "0${tTime.minute}" else if(tTime.minute==0){ "00" } else { tTime.minute }))
                }, year, month, day)
            }
        dpd!!.show()
    }
    /*
    private fun listenSwitchSalePayment() {
        val switchChoose = binding.paymentSale
        switchChoose.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                binding.listView.visibility = View.GONE
                binding.listSalePayment.visibility = View.VISIBLE
                binding.textViewChangeStateCheckbox.text = "Скидки"
                salePaymentValueDate = checkValidStudent()
                setListViewSalePayment(salePaymentValueDate, viewModel.lessonsItem.value!!.price)
                getValueAdapterSale()

            } else {
                hideSaleUIElement()
                binding.listView.visibility = View.VISIBLE
                binding.listSalePayment.visibility = View.GONE
                binding.textViewChangeStateCheckbox.text = "Список учеников."
                //  Toast.makeText(activity, "unchecked", Toast.LENGTH_SHORT).show()
            }

        }
    }*/

    private fun setListViewSaleFlexible(salePaymentValueDate: HashSet<Int?>) {
        listViewSale = binding.listSalePayment
        dataStudentSaleModel = ArrayList()
        var studentName: Array<String> = emptyArray()
        var hideChoose = true
        val existsStudentId = ArrayList<Int>()
        viewModelSalesList.salesList.observe(viewLifecycleOwner) { sales ->
            //var lstValues: MutableList<Int> = transformStringToListInt(viewModel.lessonsItem.value!!.student)
            val studentsLessonsAdapter: MutableList<Int> = transformListToMutableListInt(salePaymentValueDate)
            dataStudentSaleModel!!.clear()
            for (saleItem in sales.indices) {
                if(sales[saleItem].idLessons == lessonsItemId) {
                    hideChoose = false
                    //  hideSaleUIElement()
                    dataStudentlList.studentList.observe(viewLifecycleOwner) { stList ->
                        if(stList.isNotEmpty()) {
                            for(student in stList){
                                val studentId = student.id
                                val name = student.name + " " + student.lastname
                                if(sales[saleItem].idStudent == studentId) {
                                    existsStudentId.add(sales[saleItem].idStudent)
                                    studentName += name
                                    studentsLessonsAdapter.remove(studentId)
                                    viewModel.lessonsItem.observe(viewLifecycleOwner) {
                                        dataStudentSaleModel!!.add(DataSalePaymentModel(name, sales[saleItem].price, studentId, true, it.price))
                                    }
                                    //dataStudentSaleModel!!.add(DataSalePaymentModel(name, sales[saleItem].price, sales[saleItem].id, true))
                                }
                            }
                        }

                    }
                }
            }
            //end for
            dataStudentlList.studentList.observe(viewLifecycleOwner) { students ->
                if(students.isNotEmpty()) {
                    for(student in students){
                        val studentId = student.id
                        val name = student.name + " " + student.lastname
                        if(studentsLessonsAdapter.contains(studentId)) {
                            //dataStudentSaleModel!!.add(DataSalePaymentModel(name, 0, studentId, false, viewModel.lessonsItem.value!!.price))
                            viewModel.lessonsItem.observe(viewLifecycleOwner) {
                                dataStudentSaleModel!!.add(DataSalePaymentModel(name, 0, studentId, false, it.price))
                            }
                        }
                    }
                }
            }

            //add adapter
            adapterSaleReadyFlexible = ListFlexibleSaleAdapter(dataStudentSaleModel!!, requireContext().applicationContext)
            listViewSale.adapter =  adapterSaleReadyFlexible

            listViewSale.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val dataStudent: DataSalePaymentModel = dataStudentSaleModel!![position]
                dataStudent.checked = !dataStudent.checked
                //deleteSaleInListDialogWindow()
            }
        }
    }


    private fun transformListToMutableListInt(listInt: HashSet<Int?>): MutableList<Int> {
        val dtString = mutableListOf<Int>()
        listInt.forEach {
            dtString.add(it!!)
        }
        return dtString
    }


    private fun deleteAllSaleInCurrentLessons() {
        viewModelSalesList.salesList.observe(viewLifecycleOwner) { sales ->
            dataStudentSaleModel!!.clear()
            for (saleItem in sales.indices) {
                if(sales[saleItem].idLessons == lessonsItemId) {
                    val saleId = sales[saleItem].id
                    viewModelSalesList.deleteSaleItem(saleId)
                }
            }
        }
    }





    private fun saveEditAddDeleteSale() {
        if(this::adapterSaleReadyFlexible.isInitialized) {
            val mapDataSales = adapterSaleReadyFlexible.idValueMutableMap
            currentLessonHaveSaleOrNotHave(mapDataSales)
        }
    }

    private fun currentLessonHaveSaleOrNotHave(adapterValue: MutableMap<Int, Int>) {
        viewModelSalesList.currentLessonHaveSale(lessonsItemId).observe(viewLifecycleOwner) { sales ->
            if(sales.isNotEmpty()) {
                //видим что скидки уже есть и возможно требуется их изменение или добавление новых и все это делается в этом блоке кода
                /*
                * проверяем что есть в существующих скидках и нужно ли там что то менять
                * */
                if(adapterValue.isNotEmpty()) {
                    val salesMap: MutableMap<Int, Int> = mutableMapOf()
                    sales.forEach {
                        salesMap[it.idStudent] = it.price
                        if (adapterValue.containsKey(it.idStudent) && !adapterValue.containsValue(it.price)) {
                            //тут необходимо сделать редактирование скидки
                            // val countSaleForCheck =  calculatePercentages(adapterValue[it.idStudent]!!.toString(), viewModel.lessonsItem.value!!.price)
                            val countSaleForCheck = StringHelpers.calculateTheDiscountValue(adapterValue[it.idStudent]!!.toString(), viewModel.lessonsItem.value!!.price.toString())

                            if (countSaleForCheck != null) {
                                if(countSaleForCheck > 0 &&  countSaleForCheck < viewModel.lessonsItem.value!!.price) {
                                    viewModelSale.editSaleItem(it, adapterValue[it.idStudent]!!.toInt())
                                } else {
                                    Toast.makeText(activity, "сумма скидки не может превышать стоимость урока", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else if(!adapterValue.containsKey(it.idStudent)) {
                            //тут необходимо удалить
                            viewModelSale.deleteSaleItem(it.id)
                         }
                    }
                    adapterValue.forEach {
                        if (!salesMap.containsKey(it.key)) {
                            //val countSaleForCheck =  calculatePercentages(it.value.toString(), viewModel.lessonsItem.value!!.price)
                            val countSaleForCheck =  StringHelpers.calculateTheDiscountValue(it.value.toString(), viewModel.lessonsItem.value!!.price.toString())
                            if (countSaleForCheck != null) {
                                if(countSaleForCheck > 0 &&  countSaleForCheck < viewModel.lessonsItem.value!!.price) {
                                    viewModelSale.addSaleItem(it.key, lessonsItemId, it.value)
                                } else {
                                    Toast.makeText(activity, "сумма скидки не может превышать стоимость урока", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    //all exists sale delete
                    deleteAllSaleInCurrentLessons()
                }
            } else {
                adapterValue.forEach {
                    //val countSaleForCheck =  calculatePercentages(it.value.toString(), viewModel.lessonsItem.value!!.price)
                    val countSaleForCheck =  StringHelpers.calculateTheDiscountValue(it.value.toString(), viewModel.lessonsItem.value!!.price.toString())
                    if (countSaleForCheck != null) {
                        if(countSaleForCheck > 0 &&  countSaleForCheck < viewModel.lessonsItem.value!!.price) {
                            viewModelSale.addSaleItem(it.key, lessonsItemId, countSaleForCheck.toInt())
                        } else {
                            Toast.makeText(activity, "сумма скидки не может превышать стоимость урока", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    private fun checkValidStudent(): HashSet<Int?> {
        val lstValues: ArrayList<Int> = ArrayList()
        adapter.arrayList.forEach {
            lstValues.add(it)
        }

        return HashSet(lstValues)
    }





    private fun setFocusableEditText() {
        binding.tilNotifications.visibility = View.GONE
        binding.tilPrice.visibility = View.GONE
        binding.saveButton.text = "Список уроков."
        binding.etTitle.setBackgroundResource(R.color.white_for_day_night)
        binding.textSwitchData.visibility = View.GONE
        binding.etTitle.isFocusable = false
        binding.etDatestart.setBackgroundResource(R.color.white_for_day_night)
        binding.etDateend.setBackgroundResource(R.color.white_for_day_night)
        binding.cardStudents.visibility = View.GONE
        binding.cardSaleStudent.visibility = View.GONE
        binding.listView.visibility = View.VISIBLE
        binding.etDatestart.setOnClickListener{
            false
        }
        binding.etDateend.setOnClickListener{
            false
        }
        binding.saveButton.setOnClickListener {
            launchLessonsListFragment()
        }

    }



    private fun launchLessonsListFragment() {
        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.CUSTOM_LIST)
        }

        navController.navigate(R.id.lessonsItemListFragment, btnArgsLessons, NavigationOptions().invoke())

    }



    private fun checkAddDateTime(valueCheck1: String, valueCheck2: String): Boolean {

        if (valueCheck1.isNotEmpty() && valueCheck2.isNotEmpty()) {

            val formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm")
            val dt: LocalDateTime = LocalDateTime.parse(valueCheck1, formatter)
            val dt2: LocalDateTime = LocalDateTime.parse(valueCheck2, formatter)


            if(dt == dt2) {
                Toast.makeText(activity, "Время начала и конца урока не могут совпадать.", Toast.LENGTH_SHORT).show()
                return false
            } else if (dt > dt2) {
                Toast.makeText(activity, "Время начала урока не может превышать время конца урока.", Toast.LENGTH_SHORT).show()
                return false
            } else if (dt < dt2) {
                val diff: Duration = Duration.between(dt, dt2)
                val minutes = diff.toMinutes()
                return if(minutes < 30) {
                    Toast.makeText(activity, "урок не может быть менее 30 минут", Toast.LENGTH_SHORT).show()
                    false
                } else {
                    Toast.makeText(activity, "разница минут $minutes", Toast.LENGTH_SHORT).show()
                    true
                }

            }
        } else {
            Toast.makeText(activity, "Не все поля с датами были заполнены.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }




      private fun launchEditMode() {
        viewModel.getLessonsItem(lessonsItemId)
        binding.saveButton.setOnClickListener{
            saveEditAddDeleteSale()
            validValueNotifications()
            var studentIds: String = adapter.arrayList.toString()
            val arrayListLocal: ArrayList<Int> = ArrayList()
            if(adapter.arrayList.size == 0) {

                for (index in dataStudentGroupModel!!.indices) {
                    if (dataStudentGroupModel!![index].checked) {
                       arrayListLocal.add(dataStudentGroupModel!![index].id!!)
                    }

                }
                studentIds = arrayListLocal.toString()
            }

            if(adapter.arrayList.size == 0 && arrayListLocal.size == 0) {
                Toast.makeText(activity, "В уроке нет учеников", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.editLessonsItem(
                    binding.etTitle.text.toString(),
                    notificationString,
                    studentIds,
                    binding.etPrice.text.toString(),
                    binding.etDatestart.text.toString(),
                    binding.etDateend.text.toString()
                )
            }
        }
    }


    private fun observeViewModel() {
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
    }

    interface OnEditingFinishedListener {
        fun onEditingFinished()
    }

    private fun parseParams() {
        val args = requireArguments()
        lessonsItemId = args.getInt(LESSONS_ITEM_ID, GroupItem.UNDEFINED_ID)
    }

    private fun goLessonsListFragmentBackPressed() {
        val argss = requireArguments()
        val mode = argss.getString(DATE_ID_BACKSTACK)

        if (mode != null) {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                val arguments = Bundle().apply {
                    putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.DATE_ID_LIST)
                    putString(LessonsItemListFragment.DATE_ID, mode)
                }
                navController.popBackStack(R.id.lessonsItemListFragment, true)
                navController.navigate(R.id.lessonsItemListFragment, arguments, NavigationOptions().invoke())
            }
        } else {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                val arguments = Bundle().apply {
                    putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.CUSTOM_LIST)
                }
                navController.popBackStack(R.id.lessonsItemListFragment, true)
                navController.navigate(R.id.lessonsItemListFragment, arguments, NavigationOptions().invoke())
            }
        }

    }

    private fun notificationsLessons() {
        val switchChoose = binding.etNotifications
        binding.etTimeNotifications.addTextChangedListener(PhoneTextFormatter(binding.etTimeNotifications, "##:##"))
        switchChoose.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                binding.cardNotifications.visibility = View.VISIBLE
                binding.etTimeNotifications.setOnClickListener {
                    setNotifications()
                }
            } else {
                Toast.makeText(activity, "Вы убрали уведомление.", Toast.LENGTH_SHORT).show()
                binding.cardNotifications.visibility = View.GONE
                notificationString = ""
                binding.tilTimeNotifications.error = ""
                binding.etTimeNotifications.setText("")
                notificationBoolean = true
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun setNotifications() {
        val c = Calendar.getInstance()
        val mHour = c[Calendar.HOUR_OF_DAY]
        val mMinute = c[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(activity,
            { _, hourOfDay, minute ->
                val minH = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay
                val minM = if (minute < 10) "0$minute" else minute
                binding.etTimeNotifications.setText("$minH:$minM")
            },
            mHour,
            mMinute,
            true
        )
        timePickerDialog.show()
    }

    private fun validValueNotifications() {
        val notify = binding.etTimeNotifications.text.toString()
        if (notify.isNotBlank() && binding.etDatestart.text.toString().isNotBlank()) {
            val dateStart = binding.etDatestart.text.toString().split(" ")
            val timeDateStart = dateStart[1]
            /*time*/
            val hstart = timeDateStart.split(":")
            val hhstart = hstart[0].toInt()
            val mmstart = hstart[1].toInt()

            val hnotify = notify.split(":")
            val hhnotify = hnotify[0].toInt()
            val mmnotify = hnotify[1].toInt()
            /*time*/

            if (notify.isNotBlank() && timeDateStart.isNotBlank()) {
                val formattedDatetimeDateNotifications = LocalTime.of(hhnotify, mmnotify)
                val formattedDatetimeDateStart = LocalTime.of(hhstart, mmstart)
                if(formattedDatetimeDateNotifications >= formattedDatetimeDateStart.minusMinutes(20)) {
                    Toast.makeText(activity, "Значение даты напоминания не может быть больше или равному дате начала урока за 20 минут.", Toast.LENGTH_SHORT).show()
                    binding.tilTimeNotifications.error = "Значение даты напоминания не может быть больше или равному дате начала урока за 20 минут."
                    notificationBoolean = false
                } else {
                    notificationString = formattedDatetimeDateNotifications.toString()
                    binding.tilTimeNotifications.error = ""
                    notificationBoolean = true

                }

            } else if(notify.isBlank() && timeDateStart.isNotBlank()) {
                notificationString = ""
                binding.tilTimeNotifications.error = ""
                notificationBoolean = true
            }
        }
    }


    companion object {
        const val SCREEN_MODE = "extra_mode"
        const val LESSONS_ITEM_ID = "extra_lessons_item_id"
        const val MODE_EDIT = "mode_edit"
        const val DATE_ID_BACKSTACK = ""

    }
}