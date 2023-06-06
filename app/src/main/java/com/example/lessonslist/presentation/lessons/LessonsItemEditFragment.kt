package com.example.lessonslist.presentation.lessons

import android.app.AlertDialog
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
import android.widget.TimePicker
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
import com.example.lessonslist.presentation.MainViewModel
import com.example.lessonslist.presentation.group.DataStudentGroupModel
import com.example.lessonslist.presentation.group.ListStudentAdapter
import com.example.lessonslist.presentation.lessons.sale.*
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.example.lessonslist.presentation.student.StudentItemViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


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
    private lateinit var salePaymentValueDate: HashSet<Int?>

    private lateinit var adapter: ListStudentAdapter
    private lateinit var listView: ListView
    private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null
    private lateinit var dataStudentlList: MainViewModel

    private lateinit var viewModelPayment: PaymentListViewModel

    private var dataPaymentStudentModel: ArrayList<DataPaymentStudentLessonsModel>? = null


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
    ): View? {
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
        hideSaleUIElement()

        binding.tilStudent.visibility = View.GONE
        binding.listSalePayment.visibility = View.GONE

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem4).isChecked = true


        listView = binding.listView

        dataStudentlList = ViewModelProvider(this)[MainViewModel::class.java]
        dataStudentGroupModel = ArrayList<DataStudentGroupModel>()
        var studentName: Array<String> = emptyArray()

       // binding.layoutInfo.setVisibility (View.GONE)

        dataPaymentStudentModel = ArrayList<DataPaymentStudentLessonsModel>()

        viewModelPayment = ViewModelProvider(this)[PaymentListViewModel::class.java]
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                for (payment in it) {
                    if(payment.lessonsId == lessonsItemId) {
                        if (payment.enabled == true) {
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
                    if(it.size > 0) {
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



        uiDatePickerElement()



       // listenSwitchSalePayment()
        binding.cardStudents.setOnClickListener {
         /*   Log.d("listViewState", binding.listView.visibility.toString())
            if (uiLessonsElement) {
                uiLessonsElement = false
                hideUiLessonsElement()
            } else {
                uiLessonsElement = true
                showUiLessonsElement()
            }
            hideSaleUIElement()
            binding.listView.visibility = View.VISIBLE
            binding.listSalePayment.visibility = View.GONE*/
            if (binding.listView.visibility == View.VISIBLE){
               // Log.d("listViewState", "vissible")
                showUiLessonsElement()
                binding.listView.visibility = View.GONE
                binding.listSalePayment.visibility = View.GONE
                hideSaleUIElement()
            } else if(binding.listSalePayment.visibility == View.VISIBLE) {
                hideUiLessonsElement()
                binding.listView.visibility = View.VISIBLE
                binding.listSalePayment.visibility = View.GONE
                hideSaleUIElement()
            } else {
                hideUiLessonsElement()
                binding.listView.visibility = View.VISIBLE
                binding.listSalePayment.visibility = View.GONE
                hideSaleUIElement()
                //Log.d("listViewState", "GONE")
            }
        }

        binding.cardSaleStudent.setOnClickListener {
            if(binding.listSalePayment.visibility == View.VISIBLE) {
                showUiLessonsElement()
                binding.listView.visibility = View.GONE
                binding.listSalePayment.visibility = View.GONE
               // Log.d("listViewStateSale", "vissible")
                hideSaleUIElement()
            } else if (binding.listView.visibility == View.VISIBLE){
                hideUiLessonsElement()
                binding.listView.visibility = View.GONE
                binding.listSalePayment.visibility = View.VISIBLE
                showSaleUIElement()
                //Log.d("listViewStateSale", "gone")
                //Log.d("listViewState", "GONE")
            } else {
               // Log.d("listViewState", "ostalnie sluchai")
                hideUiLessonsElement()
                binding.listView.visibility = View.GONE
                binding.listSalePayment.visibility = View.VISIBLE
                showSaleUIElement()
            }
           /* if (uiLessonsElement) {
                uiLessonsElement = false
                hideUiLessonsElement()
            } else {
                uiLessonsElement = true
                showUiLessonsElement()
            }
            binding.listView.visibility = View.GONE
            binding.listSalePayment.visibility = View.VISIBLE
            */


            salePaymentValueDate = checkValidStudent()

            if(salePaymentValueDate.size == 0) {

                for (index in dataStudentGroupModel!!.indices) {
                    if(dataStudentGroupModel!![index].checked) {
                        salePaymentValueDate.add(dataStudentGroupModel!![index].id!!.toInt())
                    }

                }

                setListViewSalePayment(salePaymentValueDate, viewModel.lessonsItem.value!!.price)
                getValueAdapterSale()
            } else {
                setListViewSalePayment(salePaymentValueDate, viewModel.lessonsItem.value!!.price)
                getValueAdapterSale()

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
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)


        val year: Int = mcurrentTime.get(Calendar.YEAR)
        val month: Int = mcurrentTime.get(Calendar.MONTH)
        val day: Int = mcurrentTime.get(Calendar.DAY_OF_MONTH)

        var timePicker1 = ""
        var timePicker2 = ""


        mTimePicker = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.etDatestart.setText(String.format("%d/%d/%d %d : %d", year, month + 1, day, hourOfDay, minute))
                timePicker1 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + hourOfDay.toString() + ":" + minute.toString()
                if (timePicker1.length > 0 && timePicker2.length > 0) {
                    checkAddDateTime(timePicker1, timePicker2)
                }
            }
        }, hour, minute, true)

        mTimePickerEnd = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                binding.etDateend.setText(String.format("%d/%d/%d %d : %d", year, month + 1, day, hourOfDay, minute))
                timePicker2 = year.toString() + "-" + (month + 1).toString() + "-" + day.toString() + " " + hourOfDay.toString() + ":" + minute.toString()
                if (timePicker1.length > 0 && timePicker2.length > 0) {
                    checkAddDateTime(timePicker1, timePicker2)
                }
            }
        }, hour, minute, true)

        binding.etDatestart.setOnClickListener{
            mTimePicker.show()
        }

        binding.etDateend.setOnClickListener{
            mTimePickerEnd.show()
        }
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



    private fun setListViewSalePayment(salePaymentValueDate: HashSet<Int?>, price: Int) {

        listViewSale = binding.listSalePayment
        dataStudentSaleModel = ArrayList<DataSalePaymentModel>()
        dataStudentlList = ViewModelProvider(this)[MainViewModel::class.java]
        viewModelSalesList = ViewModelProvider(this)[SalesItemListViewModel::class.java]
        viewModelStudent = ViewModelProvider(this)[StudentItemViewModel::class.java]
        var studentName: Array<String> = emptyArray()
        var hideChoose: Boolean = true


      viewModelSalesList.salesList.observe(viewLifecycleOwner) { sales ->
          dataStudentSaleModel!!.clear()
           for (saleItem in sales.indices) {
                if(sales[saleItem].idLessons == lessonsItemId) {
                    hideChoose = false
                    hideSaleUIElement()
                    dataStudentlList.studentList.observe(viewLifecycleOwner) { it ->
                         if(it.isNotEmpty()) {
                             for(student in it){
                                 if(sales[saleItem].idStudent == student.id) {

                                     val name = student.name + " " + student.lastname
                                     studentName += name
                                     dataStudentSaleModel!!.add(DataSalePaymentModel(name, sales[saleItem].price, sales[saleItem].id, true))
                                 }
                             }
                           //  Toast.makeText(activity, "есть скидки на урок", Toast.LENGTH_SHORT).show()

                             adapterSaleReady = ListSaleReadyAdapter(dataStudentSaleModel!!, requireContext().applicationContext)
                             //openDialog(dataStudentGroupModel)
                             listViewSale.adapter =  adapterSaleReady

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


    private fun deleteSaleInListDialogWindow() {

        val alert = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        alert.setTitle("Удаление скидок")
        alert.setMessage("При нажатии кнопки удалить, все скидки урока будут удалены. После удаления появится возможность добавить скидки к уроку.")


        alert.setPositiveButton("Удалить", DialogInterface.OnClickListener {
                dialog, id ->
            for (index in dataStudentSaleModel!!.indices) {
                val idSale = dataStudentSaleModel!![index].id
                if (idSale != null) {
                    viewModelSale.deleteSaleItem(idSale)
                }
                //deleteSaleInList(dataStudentSaleModel.name.toString(), dataStudent.id!!.toInt())
            }

            showUiLessonsElement()
            hideSaleUIElement()
            binding.listView.visibility = View.GONE
            binding.listSalePayment.visibility = View.GONE
        })

        alert.setNeutralButton("Закрыть", DialogInterface.OnClickListener {
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
        binding.tilPrice.setVisibility (View.GONE)
        binding.saveButton.text = "Список уроков."
        binding.etTitle.setBackgroundResource(R.color.white)
        binding.etTitle.isFocusable = false
        binding.etDatestart.setBackgroundResource(R.color.white)
        binding.etDateend.setBackgroundResource(R.color.white)
        binding.cardStudents.setVisibility (View.GONE)
        binding.cardSaleStudent.setVisibility (View.GONE)
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
        val builder = AlertDialog.Builder(getContext())
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

            val formatter = DateTimeFormatter.ofPattern("yyyy-M-dd HH:m")
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
            var studentIds: String = adapter.arrayList.toString()
            if(adapter.arrayList.size == 0) {
                Toast.makeText(activity, "В уроке нет учеников", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.editLessonsItem(
                    binding.etTitle.text.toString(),
                    "",
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



    companion object {

        const val SCREEN_MODE = "extra_mode"
        const val LESSONS_ITEM_ID = "extra_lessons_item_id"
        const val MODE_EDIT = "mode_edit"
        const val MODE_UNKNOWN = ""
        const val DATE_ID_BACKSTACK = ""

        fun newInstanceEditItem(lessonsItemId: Int): LessonsItemEditFragment {
            return LessonsItemEditFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(LESSONS_ITEM_ID, lessonsItemId)
                }
            }
        }
    }
}

