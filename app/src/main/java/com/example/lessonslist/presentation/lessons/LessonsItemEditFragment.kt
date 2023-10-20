package com.example.lessonslist.presentation.lessons

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentLessonsItemEditBinding
import com.example.lessonslist.domain.group.GroupItem
import com.example.lessonslist.domain.lessons.LessonsItem
import com.example.lessonslist.presentation.student.StudentListViewModel
import com.example.lessonslist.presentation.group.DataStudentGroupModel
import com.example.lessonslist.presentation.group.ListStudentAdapter
import com.example.lessonslist.presentation.helpers.PhoneTextFormatter
import com.example.lessonslist.presentation.lessons.sale.*
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.example.lessonslist.presentation.student.StudentItemViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class LessonsItemEditFragment : Fragment() {

    private lateinit var viewModel: LessonsItemViewModel
    private lateinit var viewModelSale: SaleItemViewModel
    private lateinit var viewModelSalesList: SalesItemListViewModel
    private lateinit var viewModelStudent: StudentItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentLessonsItemEditBinding? = null
    private val binding: FragmentLessonsItemEditBinding
        get() = _binding ?: throw RuntimeException("FragmentGroupItemBinding == null")


    private var screenMode: String = MODE_UNKNOWN
    private var lessonsItemId: Int = LessonsItem.UNDEFINED_ID

    private lateinit var listViewSale: ListView
    private var dataStudentSaleModel: ArrayList<DataSalePaymentModel>? = null
    private lateinit var adapterSale: ListSaleAdapter
    private lateinit var adapterSaleReady: ListSaleReadyAdapter
    private lateinit var adapterSaleReadyFlexible: ListFlexibleSaleAdapter
    private lateinit var salePaymentValueDate: HashSet<Int?>

    private lateinit var adapter: ListStudentAdapter
    private lateinit var listView: ListView
    private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null
    private lateinit var dataStudentlList: StudentListViewModel

    private lateinit var viewModelPayment: PaymentListViewModel

    private var dataPaymentStudentModel: ArrayList<DataPaymentStudentLessonsModel>? = null
    private var notificationString: String = ""
    private var notificationBoolean: Boolean = true

    val mcurrentTime = Calendar.getInstance()
    var year: Int = mcurrentTime.get(Calendar.YEAR)
    var month: Int = mcurrentTime.get(Calendar.MONTH)
    var day: Int = mcurrentTime.get(Calendar.DAY_OF_MONTH)


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

        viewModel = ViewModelProvider(this)[LessonsItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        launchRightMode()
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

        /*val firstField = binding.etDatestart.text
        val twoField = binding.etDateend.text
        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm")
        val fTime = LocalDateTime.parse(firstField, formatter)
        year = fTime.year
        month = fTime.month.value
        day = fTime.dayOfMonth*/
    }


    private fun changeDataLessons() {
        binding.textSwithData.setOnClickListener {
            setNewDayLessons()
        }
    }

    private fun setDataLessonsPayment() {
        listView = binding.listView
        dataStudentlList = ViewModelProvider(this)[StudentListViewModel::class.java]
        dataStudentGroupModel = ArrayList<DataStudentGroupModel>()
        var studentName: Array<String> = emptyArray()
        dataPaymentStudentModel = ArrayList<DataPaymentStudentLessonsModel>()
        viewModelPayment = ViewModelProvider(this)[PaymentListViewModel::class.java]
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                for (payment in it) {
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
                                viewModel.lessonsItem.observe(viewLifecycleOwner) {
                                    var dataString = it.student
                                    dataString = dataString.replace("]", "")
                                    dataString = dataString.replace("[", "")


                                    val lstValues: List<Int> = dataString.split(",").map { str -> str.trim().toInt() }
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
    }

    private fun showUiLessonsElement() {
        binding.tilTitle.visibility = View.VISIBLE
        binding.tilPrice.visibility = View.VISIBLE
        binding.tilDatestart.visibility = View.VISIBLE
        binding.tilDateend.visibility = View.VISIBLE
    }

    private fun uiDatePickerElement() {

        val mTimePicker: TimePickerDialog
        val mTimePickerEnd: TimePickerDialog

        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)



        var timePicker1 = ""
        var timePicker2 = ""


        mTimePicker = TimePickerDialog(context,
            { view, hourOfDay, minute ->
                val minH = if (hourOfDay < 10) "0" + hourOfDay else if(hourOfDay == 0) "00" else hourOfDay
                val minM = if (minute < 10) "0" + minute else if(minute == 0) "00" else minute
                binding.etDatestart.setText(String.format("%d/%d/%d %s:%s", year, month + 1, day, minH, minM))
                timePicker1 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + minH.toString() + ":" + minM.toString()
                if (timePicker1.length > 0 && timePicker2.length > 0) {
                    checkAddDateTime(timePicker1, timePicker2)
                }
            }, hour, minute, true)

        mTimePickerEnd = TimePickerDialog(context,
            { view, hourOfDay, minute ->
                val minH = if (hourOfDay < 10) "0" + hourOfDay else if(hourOfDay == 0) "00" else hourOfDay
                val minM = if (minute < 10) "0" + minute else if(minute == 0) "00" else minute
                binding.etDateend.setText(String.format("%d/%d/%d %s:%s", year, month + 1, day, minH, minM))
                timePicker2 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + minH.toString() + ":" + minM.toString()
                if (timePicker1.length > 0 && timePicker2.length > 0) {
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

        val dpd =
            activity?.let {
                DatePickerDialog(requireContext(), { _, yearcur, monthOfYear, dayOfMonth ->
                    mcurrentTime.set(yearcur, monthOfYear, dayOfMonth)
                    year = mcurrentTime[Calendar.YEAR]
                    month = mcurrentTime[Calendar.MONTH]
                    day = mcurrentTime[Calendar.DAY_OF_MONTH]
                    binding.etDatestart.setText(String.format("%d/%d/%d %s:%s", year, month + 1, day, fTime.hour, fTime.minute))
                    binding.etDateend.setText(String.format("%d/%d/%d %s:%s", year, month + 1, day, tTime.hour, tTime.minute))
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
        dataStudentSaleModel = ArrayList<DataSalePaymentModel>()
        dataStudentlList = ViewModelProvider(this)[StudentListViewModel::class.java]
        viewModelSalesList = ViewModelProvider(this)[SalesItemListViewModel::class.java]
        viewModelStudent = ViewModelProvider(this)[StudentItemViewModel::class.java]
        var studentName: Array<String> = emptyArray()
        var hideChoose = true
        val existsStudentId = ArrayList<Int>()
        viewModelSalesList.salesList.observe(viewLifecycleOwner) { sales ->
            //var lstValues: MutableList<Int> = transformStringToListInt(viewModel.lessonsItem.value!!.student)
            var studentsLessonsAdapter: MutableList<Int> = transformListToMutableListInt(salePaymentValueDate)
            dataStudentSaleModel!!.clear()
            for (saleItem in sales.indices) {
                if(sales[saleItem].idLessons == lessonsItemId) {
                    hideChoose = false
                  //  hideSaleUIElement()
                    dataStudentlList.studentList.observe(viewLifecycleOwner) { it ->
                        if(it.isNotEmpty()) {
                            for(student in it){
                                val studentId = student.id
                                val name = student.name + " " + student.lastname
                                if(sales[saleItem].idStudent == studentId) {
                                    existsStudentId.add(sales[saleItem].idStudent)
                                    studentName += name
                                    studentsLessonsAdapter.remove(studentId)
                                    dataStudentSaleModel!!.add(DataSalePaymentModel(name, sales[saleItem].price, studentId, true))
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
                            dataStudentSaleModel!!.add(DataSalePaymentModel(name, 0, studentId, false))
                        }
                    }
                }
            }


            //Toast.makeText(activity, "all students sale." + lstValues.toString(), Toast.LENGTH_SHORT).show()
            //add adapter
            adapterSaleReadyFlexible = ListFlexibleSaleAdapter(dataStudentSaleModel!!, requireContext().applicationContext)
            listViewSale.adapter =  adapterSaleReadyFlexible
          /*  listView.setOnItemClickListener { adapterView, view, i, l ->
                false
                Toast.makeText(activity, "Удалите скидки для редактирования.", Toast.LENGTH_SHORT).show()
            }*/



            listViewSale.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val dataStudent: DataSalePaymentModel = dataStudentSaleModel!![position]
                dataStudent.checked = !dataStudent.checked
                //deleteSaleInListDialogWindow()
            }
            //add adapter


            /*if(dataStudentSaleModel!!.size == 0 && hideChoose) {

                dataStudentlList.studentList.observe(viewLifecycleOwner) { it ->
                    if(it.isNotEmpty()) {
                        for(student in it){
                            if(salePaymentValueDate.contains(student.id)) {
                                val name = student.name + " " + student.lastname
                                val id = student.id
                                // val price = student.paymentBalance
                                studentName += name

                                dataStudentSaleModel!!.add(DataSalePaymentModel(name, 0, id, false))
                            }
                        }


                        adapterSaleReadyFlexible = ListFlexibleSaleAdapter(dataStudentSaleModel!!, requireContext().applicationContext)
                        //openDialog(dataStudentGroupModel)
                        listViewSale.adapter = adapterSaleReadyFlexible

                        listViewSale.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                            val dataStudent: DataSalePaymentModel = dataStudentSaleModel!![position]
                            dataStudent.checked = !dataStudent.checked
                            adapterSale.notifyDataSetChanged()
                        }

                    }

                }
            }*/

        }
    }

    private fun transformStringToListInt(dataString: String): MutableList<Int> {
        var dtString = dataString.replace("]", "")
        dtString = dtString.replace("[", "")
        return dtString.split(",").map { str -> str.trim().toInt() } as MutableList<Int>
    }

    private fun transformListToMutableListInt(listInt: HashSet<Int?>): MutableList<Int> {
        var dtString = mutableListOf<Int>()
        listInt.forEach {
            dtString.add(it!!)
        }
        return dtString
    }


    private fun setListViewSalePayment(salePaymentValueDate: HashSet<Int?>, price: Int) {

        listViewSale = binding.listSalePayment
        dataStudentSaleModel = ArrayList<DataSalePaymentModel>()
        dataStudentlList = ViewModelProvider(this)[StudentListViewModel::class.java]
        viewModelSalesList = ViewModelProvider(this)[SalesItemListViewModel::class.java]
        viewModelStudent = ViewModelProvider(this)[StudentItemViewModel::class.java]
        var studentName: Array<String> = emptyArray()
        var hideChoose = true


      viewModelSalesList.salesList.observe(viewLifecycleOwner) { sales ->
          dataStudentSaleModel!!.clear()
           for (saleItem in sales.indices) {
                if(sales[saleItem].idLessons == lessonsItemId) {
                    hideChoose = false
                    dataStudentlList.studentList.observe(viewLifecycleOwner) { it ->
                         if(it.isNotEmpty()) {
                             for(student in it){
                                 if(sales[saleItem].idStudent == student.id) {

                                     val name = student.name + " " + student.lastname
                                     studentName += name
                                     dataStudentSaleModel!!.add(DataSalePaymentModel(name, sales[saleItem].price, sales[saleItem].id, true))
                                 }
                             }
                             //Toast.makeText(activity, "есть скидки на урок", Toast.LENGTH_SHORT).show()

                             adapterSaleReady = ListSaleReadyAdapter(dataStudentSaleModel!!, requireContext().applicationContext)
                             //openDialog(dataStudentGroupModel)
                             listViewSale.adapter =  adapterSaleReady
                             listView.setOnItemClickListener { adapterView, view, i, l ->
                                 false
                                 Toast.makeText(activity, "Удалите скидки для редактирования.", Toast.LENGTH_SHORT).show()
                             }



                             listViewSale.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                                 val dataStudent: DataSalePaymentModel = dataStudentSaleModel!![position]
                                 dataStudent.checked = !dataStudent.checked
                                 deleteSaleInListDialogWindow()
                                 //Toast.makeText(activity, dataStudent.id.toString() + dataStudent.name.toString() + dataStudent.id.toString(), Toast.LENGTH_SHORT).show()
                                 //deleteSaleInList(dataStudent.name.toString(), dataStudent.id!!.toInt())
                             }

                         }

                     }


                 }
             }

          //end for
         if(dataStudentSaleModel!!.size == 0 && hideChoose) {
            //  Toast.makeText(activity, "в данном случае равно 0", Toast.LENGTH_SHORT).show()
              //showSaleUIElement()
              dataStudentlList.studentList.observe(viewLifecycleOwner) { it ->
                  if(it.isNotEmpty()) {
                      for(student in it){
                          if(salePaymentValueDate.contains(student.id)) {
                              val name = student.name + " " + student.lastname
                              val id = student.id
                              // val price = student.paymentBalance
                              studentName += name

                              dataStudentSaleModel!!.add(DataSalePaymentModel(name, price, id, false))
                          }
                      }


                      adapterSale = ListSaleAdapter(dataStudentSaleModel!!, requireContext().applicationContext)
                      //openDialog(dataStudentGroupModel)
                      listViewSale.adapter = adapterSale

                      listViewSale.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                          val dataStudent: DataSalePaymentModel = dataStudentSaleModel!![position]
                          dataStudent.checked = !dataStudent.checked
                          adapterSale.notifyDataSetChanged()
                      }

                  }

              }
          }




        }
    }


    private fun deleteAllSaleInCurrentLessons() {
        viewModelSalesList = ViewModelProvider(this)[SalesItemListViewModel::class.java]
        viewModelSalesList.salesList.observe(viewLifecycleOwner) { sales ->
            //var lstValues: MutableList<Int> = transformStringToListInt(viewModel.lessonsItem.value!!.student)
            var studentsLessonsAdapter: MutableList<Int> = transformListToMutableListInt(salePaymentValueDate)
            dataStudentSaleModel!!.clear()
            for (saleItem in sales.indices) {
                if(sales[saleItem].idLessons == lessonsItemId) {
                    val saleId = sales[saleItem].id
                    viewModelSalesList.deleteSaleItem(saleId)
                }
            }
        }
    }



    private fun deleteSaleInListDialogWindow() {

        val alert = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        alert.setTitle("Удаление скидок")
        alert.setMessage("При нажатии кнопки удалить, все скидки урока будут удалены. После удаления появится возможность добавить скидки к уроку.")


        alert.setPositiveButton("Удалить") { dialog, id ->
            for (index in dataStudentSaleModel!!.indices) {
                val idSale = dataStudentSaleModel!![index].id
                if (idSale != null) {
                    viewModelSale.deleteSaleItem(idSale)
                }
                //deleteSaleInList(dataStudentSaleModel.name.toString(), dataStudent.id!!.toInt())
            }
            showUiLessonsElement()
            binding.listView.visibility = View.GONE
            binding.listSalePayment.visibility = View.GONE
        }

        alert.setNeutralButton("Закрыть", {
                dialog, id ->
            dialog.dismiss()
        })

        alert.setCancelable(false)
        alert.show()

    }

    private fun deleteSaleInList(nameStudent: String, idSale: Int) {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Удалить скидку.")
        alert.setMessage("Удалить скидку для ученика " + nameStudent)

       alert.setPositiveButton("Удалить скидку", DialogInterface.OnClickListener {
                dialog, id ->
            viewModelSale.deleteSaleItem(idSale)
         //  checkValidSaleData(dataStudentSaleModel!!)
        })
        alert.setNeutralButton("Отмена", DialogInterface.OnClickListener {
                dialog, id ->
            dialog.dismiss()
        })

        alert.setCancelable(false)
        alert.show()
    }

/*
    private fun hideSaleUIElement() {
        binding.tilSale.visibility = View.GONE
        binding.saveSaleButton.visibility = View.GONE
    }
    private fun showSaleUIElement() {
        binding.tilSale.visibility = View.VISIBLE
        binding.saveSaleButton.visibility = View.VISIBLE
    }

    private fun getValueAdapterSale() {
        viewModelSale = ViewModelProvider(this)[SaleItemViewModel::class.java]

        binding.saveSaleButton.setOnClickListener {
            //if(binding.etSale.text.toString().toInt() < viewModel.lessonsItem.value!!.price){
                //val countSaleForCheck = viewModel.lessonsItem.value!!.price - binding.etSale.text.toString().toInt()
                val countSaleForCheck =  calculatePercentages(binding.etSale.text.toString(), viewModel.lessonsItem.value!!.price)
                val studentIds = adapterSale.arrayList
                val hashSetStudent: HashSet<Int> = studentIds.toHashSet()
               // Toast.makeText(activity, hashSetStudent.toString(), Toast.LENGTH_SHORT).show()
                if(countSaleForCheck > 0 &&  countSaleForCheck < viewModel.lessonsItem.value!!.price){
//                    Toast.makeText(activity, countSaleForCheck, Toast.LENGTH_SHORT).show()
                    if(adapterSale.arrayList.size > 0) {
                        for(studentId in hashSetStudent) {
                                viewModelSale.addSaleItem(studentId, lessonsItemId, countSaleForCheck.toInt())
                           // Log.d("DataForSaleADd", (studentId.toString() + " " + lessonsItemId.toString() + " " + countSaleForCheck.toInt()).toString())
                        }
                        dataStudentSaleModel?.clear()

                    }
                } else {
                    Toast.makeText(activity, "сумма скидки не может превышать стоимость урока", Toast.LENGTH_SHORT).show()
                }

        }


    }*/


    private fun saveEditAddDeleteSale() {
        /*if(this::adapterSaleReadyFlexible.isInitialized) {
            val mapDataSales = adapterSaleReadyFlexible.idValueMutableMap
            currentLessonHaveSaleOrNotHave(mapDataSales)
        }*/
    }

    private fun currentLessonHaveSaleOrNotHave(adapterValue: MutableMap<Int, Int>) {
        viewModelSale = ViewModelProvider(this)[SaleItemViewModel::class.java]
        viewModelSalesList = ViewModelProvider(this)[SalesItemListViewModel::class.java]
        viewModelSalesList.currentLessonHaveSale(lessonsItemId).observe(viewLifecycleOwner) { sales ->
            if(sales.isNotEmpty()) {
                //видим что скидки уже есть и возможно требуется их изменение или добавление новых и все это делается в этом блоке кода
                /*
                * проверяем что есть в существующих скидках и нужно ли там что то менять
                * */
                if(adapterValue.size > 0) {
                    val salesMap: MutableMap<Int, Int> = mutableMapOf<Int, Int>()
                    sales.forEach {
                        salesMap.put(it.idStudent, it.price)
                        if (adapterValue.containsKey(it.idStudent) && !adapterValue.containsValue(it.price)) {
                            //тут необходимо сделать редактирование скидки
                            Log.d("existsSaleInCurrentLessons21", adapterValue[it.idStudent].toString())
                            viewModelSale.editSaleItem(it, adapterValue[it.idStudent]!!.toInt())
                        } else if(!adapterValue.containsKey(it.idStudent)) {
                            //тут необходимо удалить
                            viewModelSale.deleteSaleItem(it.id)
                            Log.d("existsSaleInCurrentLessons21", it.idStudent.toString() + "" + it.price.toString())
                        }
                    }
                    adapterValue.forEach {
                        if (!salesMap.containsKey(it.key)) {
                            Log.d("existsSaleInCurrentLessons333", it.toString())
                            viewModelSale.addSaleItem(it.key, lessonsItemId, it.value)
                        }
                    }
                } else {
                    //all exists sale delete
                    deleteAllSaleInCurrentLessons()
                }
            } else {
                Log.d("existsSaleInCurrentLessons2", adapterValue.toString())
                adapterValue.forEach {
                    val countSaleForCheck =  calculatePercentages(it.value.toString(), viewModel.lessonsItem.value!!.price)
                    if(countSaleForCheck > 0 &&  countSaleForCheck < viewModel.lessonsItem.value!!.price) {
                        viewModelSale.addSaleItem(it.key, lessonsItemId, countSaleForCheck.toInt())
                    } else {
                        Toast.makeText(activity, "сумма скидки не может превышать стоимость урока", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


    private fun calculatePercentages(valueSale: String, lessonsPrice: Int): Float {
        val price = valueSale.split("%")
        //println("lessonsPrice: " + lessonsPrice.toString())
        if(price[0] != "" && price.size > 1 && price.size < 3 && price[1] == "") {
            return (lessonsPrice).toFloat() / (100).toFloat() * price[0].toFloat()
        } /*else if(price[0] != "" && price.size > 1 && price[1].toInt() > 0){
            return lessonsPrice - price[1].toInt()
        } */else if (price.size == 1) {
           // println("old price value: " + (lessonsPrice - valueSale.toInt()).toString())
            //return (lessonsPrice - valueSale.toInt()).toFloat()
            return (valueSale.toInt()).toFloat()
        } else if(price.size >= 3) {
            return lessonsPrice.toFloat()
        } else {
            return lessonsPrice.toFloat()
        }

    }



    private fun checkValidSaleData(arrayList: ArrayList<DataSalePaymentModel>): HashSet<DataSalePaymentModel> {
        return HashSet(arrayList)
    }


    private fun checkValidStudent(): HashSet<Int?> {
        val studentIds: String = adapter.arrayList.toString()
        val allStudent: String
        allStudent = studentIds

        val lstValues: ArrayList<Int> = ArrayList()

        allStudent.forEach {
            if (it.isDigit()) {
                val str = it.toString()
                lstValues.add(str.toInt())
            }
        }


        return HashSet(lstValues)

    }





    fun setFocusableEditText() {
        //binding.saveButton.setVisibility (View.GONE)
        binding.tilPrice.visibility = View.GONE
        binding.saveButton.text = "Список уроков."
        binding.etTitle.setBackgroundResource(R.color.white)
        binding.textSwithData.visibility = View.GONE
        binding.etTitle.isFocusable = false
        binding.etDatestart.setBackgroundResource(R.color.white)
        binding.etDateend.setBackgroundResource(R.color.white)
        binding.cardStudents.visibility = View.GONE
        binding.cardSaleStudent.visibility = View.GONE
        binding.listView.visibility = View.VISIBLE
      //  binding.textViewChangeStateCheckbox.text = "Список платежей:"
        //binding.paymentSale.visibility = View.GONE
        binding.etDatestart.setOnClickListener{
             false
        }
        binding.etDateend.setOnClickListener{
             false
        }
        binding.saveButton.setOnClickListener {
            //launchFragment(LessonsItemListFragment.newInstanceNoneParams())
            launchLessonsListFragment()
        }

//        binding.saveButton

     /*   binding.saveButton.setVisibility (View.GONE)
        binding.layoutInfo.setVisibility (View.VISIBLE)
        //binding.textViewChangeStateCheckbox.setVisibility (View.GONE)

        binding.textViewChangeStateCheckbox.isFocusable = false*/
    }



    private fun launchLessonsListFragment() {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsLessons = Bundle().apply {
            putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.CUSTOM_LIST)
        }
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.lessonsItemListFragment, btnArgsLessons, animationOptions)

    }


    fun withMultiChoiceList(listData: Array<String>): ArrayList<String> {

        //val items = arrayOf("Microsoft", "Apple", "Amazon", "Google")
        val items = listData
        val selectedList = ArrayList<Int>()
        val builder = AlertDialog.Builder(context)
        val selectedStrings = ArrayList<String>()
        builder.setTitle("Выберите студентов")
        builder.setMultiChoiceItems(items, null
        ) { dialog, which, isChecked ->
            if (isChecked) {
                selectedList.add(which)
            } else if (selectedList.contains(which)) {
                selectedList.remove(Integer.valueOf(which))
            }
        }

        builder.setPositiveButton("ок") { dialogInterface, i ->


            for (j in selectedList.indices) {
                selectedStrings.add(items[selectedList[j]])
            }

           // Toast.makeText(getContext(), "Items selected are: " + Arrays.toString(selectedStrings.toTypedArray()), Toast.LENGTH_SHORT).show()


        }

        builder.show()

        return selectedStrings

    }

    private fun checkAddDateTime(valueCheck1: String, valueCheck2: String): Boolean {

        if (valueCheck1.length > 0 && valueCheck2.length > 0) {

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
                if(minutes < 30) {
                    Toast.makeText(activity, "урок не может быть менее 30 минут", Toast.LENGTH_SHORT).show()
                    return false
                } else {
                    Toast.makeText(activity, "разница минут" + minutes.toString(), Toast.LENGTH_SHORT).show()
                    return true
                }

            }
        } else {
            Toast.makeText(activity, "Не все поля с датами были заполнены.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }




    private fun launchRightMode() {
        Log.d("screenMode", screenMode)
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
        }
    }


    private fun launchEditMode() {
        viewModel.getLessonsItem(lessonsItemId)
            binding.saveButton.setOnClickListener{
            saveEditAddDeleteSale()
            validValueNotifications()
            var studentIds: String = adapter.arrayList.toString()
            var arrayListLocal: ArrayList<Int> = ArrayList()
            if(adapter.arrayList.size == 0) {

                for (index in dataStudentGroupModel!!.indices) {
                        if (dataStudentGroupModel!![index].checked) {
                           // Log.d("studentLessons", " index: " + dataStudentGroupModel!![index].id.toString() + " check: " + dataStudentGroupModel!![index].checked.toString())
                            arrayListLocal.add(dataStudentGroupModel!![index].id!!)
                        }

                }
                studentIds = arrayListLocal.toString()
            }
                //Toast.makeText(activity, "Ученики урока" + dataStudentGroupModel, Toast.LENGTH_SHORT).show()

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
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_EDIT) {
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(LESSONS_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            lessonsItemId = args.getInt(LESSONS_ITEM_ID, GroupItem.UNDEFINED_ID)

        }

    }

    private fun goLessonsListFragmentBackPressed() {
        val argss = requireArguments()
        val mode = argss.getString(LessonsItemEditFragment.DATE_ID_BACKSTACK)

        if (mode != null) {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
                val navController = navHostFragment.navController
                val arguments = Bundle().apply {
                    putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.DATE_ID_LIST)
                    putString(LessonsItemListFragment.DATE_ID, mode)
                }
                navController.popBackStack(R.id.lessonsItemListFragment, true)
                val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
                    .setExitAnim(R.anim.slide_in_right)
                    .setPopEnterAnim(R.anim.slide_out_left)
                    .setPopExitAnim(R.anim.slide_out_right).build()
                navController.navigate(R.id.lessonsItemListFragment, arguments, animationOptions)
            }
        } else {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
                val navController = navHostFragment.navController
                val arguments = Bundle().apply {
                    putString(LessonsItemListFragment.SCREEN_MODE, LessonsItemListFragment.CUSTOM_LIST)
                }
                navController.popBackStack(R.id.lessonsItemListFragment, true)
                val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
                    .setExitAnim(R.anim.slide_in_right)
                    .setPopEnterAnim(R.anim.slide_out_left)
                    .setPopExitAnim(R.anim.slide_out_right).build()
                navController.navigate(R.id.lessonsItemListFragment, arguments, animationOptions)
            }
        }

    }

    private fun notificationsLessons() {
        val switchChoose = binding.etNotifications
        binding.etTimeNotifications.addTextChangedListener(PhoneTextFormatter(binding.etTimeNotifications, "##:##"))
        //textChangedListener(PhoneTextFormatter(binding.etTimeNotifications, "##:##"))
        switchChoose.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                //   Toast.makeText(activity, "checked", Toast.LENGTH_SHORT).show()
                binding.cardNotifications.visibility = View.VISIBLE
                binding.etTimeNotifications.setOnClickListener {
                    setNotifications()
                }
                /*binding.etDateendRepeat.setOnClickListener {
                    chooseDateEndRepeat()
                }*/
            } else {
                Toast.makeText(activity, "Вы убрали уведомление.", Toast.LENGTH_SHORT).show()
                binding.cardNotifications.visibility = View.GONE
                notificationString = ""
                binding.tilTimeNotifications.error = ""
                binding.etTimeNotifications.setText("")
                notificationBoolean = true
                //Toast.makeText(activity, "unchecked", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setNotifications() {

        val c = Calendar.getInstance()
        val mHour = c[Calendar.HOUR_OF_DAY]
        val mMinute = c[Calendar.MINUTE]

        val timePickerDialog = TimePickerDialog(activity,

            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                val minH = if (hourOfDay < 10) "0" + hourOfDay else hourOfDay
                val minM = if (minute < 10) "0" + minute else minute

                binding.etTimeNotifications.setText("$minH:$minM")
                // binding.tilNotifications.setEndIconDrawable(R.drawable.ic_baseline_circle_24)
            },
            mHour,
            mMinute,
            true
        )
        timePickerDialog.show()
    }

    private fun validValueNotifications() {
        val notify = binding.etTimeNotifications.text.toString()
        if (!notify.isBlank() && !binding.etDatestart.text.toString().isBlank()) {
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

            if (!notify.isBlank() && !timeDateStart.isBlank()) {
                //val formatter = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                //val formattedDatetimeDateNotifications = formatter.parse(timeDateStart)
                //LocalTime time = LocalTime.of(23, 59);
                val formattedDatetimeDateNotifications = LocalTime.of(hhnotify, mmnotify)
                val formattedDatetimeDateStart = LocalTime.of(hhstart, mmstart)
                //val formattedDatetimeDateStart = formatter.parse(timeDateStart)
                if(formattedDatetimeDateNotifications >= formattedDatetimeDateStart.minusMinutes(20)) {
                    Toast.makeText(activity, "Значение даты напоминания не может быть больше или равному дате начала урока за 20 минут.", Toast.LENGTH_SHORT).show()
                    binding.tilTimeNotifications.error = "Значение даты напоминания не может быть больше или равному дате начала урока за 20 минут."
                    notificationBoolean = false
                } else {
                    notificationString = formattedDatetimeDateNotifications.toString()
                    binding.tilTimeNotifications.error = ""
                    notificationBoolean = true

                }
                //Toast.makeText(activity, "this value start time" + formattedDatetimeDateStart.minusMinutes(20), Toast.LENGTH_SHORT).show()

            } else if(notify.isBlank() && !timeDateStart.isBlank()) {
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
        const val MODE_UNKNOWN = ""
        const val DATE_ID_BACKSTACK = ""

    }
}

