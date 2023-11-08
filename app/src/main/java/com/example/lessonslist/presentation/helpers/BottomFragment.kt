package com.example.lessonslist.presentation.helpers

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.R
import com.example.lessonslist.databinding.BottomSheetLayoutBinding
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.group.GroupListViewModel
import com.example.lessonslist.presentation.lessons.LessonsItemViewModel
import com.example.lessonslist.presentation.payment.PaymentItemViewModel
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.example.lessonslist.presentation.student.*
import com.example.lessonslist.presentation.student.group.DataStudentGroupModel
import com.example.lessonslist.presentation.student.group.ListStudentGroupAdapter
import com.example.lessonslist.presentation.student.notes.DataNotesStudentModel
import com.example.lessonslist.presentation.student.notes.ListNotesAdapter
import com.example.lessonslist.presentation.student.parentContact.DataParentContactStudentModel
import com.example.lessonslist.presentation.student.parentContact.ListParentContactAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

private const val COLLAPSED_HEIGHT = 650

class BottomFragment : BottomSheetDialogFragment() {

    private var screenMode: String = MODE_UNKNOWN
    private var studentItemId: Int = StudentItem.UNDEFINED_ID

    //по листам
    private lateinit var listView: ListView
    private lateinit var listView2: ListView

    private lateinit var viewModelPayment: PaymentListViewModel
    private lateinit var viewModelPayments: PaymentItemViewModel
    private lateinit var viewModelNotesItem: NotesItemViewModel
    private lateinit var viewModelParentContact: ParentContactViewModel
    private lateinit var viewModelStudent: StudentItemViewModel
    private lateinit var viewModelLessons: LessonsItemViewModel
    private lateinit var viewModelGroup: GroupListViewModel

    private var dataPaymentStudentModel: ArrayList<DataPaymentStudentModel>? = null
    private var dataNotesStudentModel: ArrayList<DataNotesStudentModel>? = null
    private var dataParentContactStudentModel: ArrayList<DataParentContactStudentModel>? = null
    private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null
    // Можно обойтись без биндинга и использовать findViewById
    lateinit var binding: BottomSheetLayoutBinding

    // Переопределим тему, чтобы использовать нашу с закруглёнными углами
    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetLayoutBinding.bind(inflater.inflate(R.layout.bottom_sheet_layout, container))
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parseParams()

        if(screenMode == "mode_payment") {
            binding.simpleTextTitle.text = "Платежи студента"
            binding.tilName.visibility = View.GONE
            binding.tilNotes.visibility = View.GONE
            binding.imageAddNotes.visibility = View.GONE
            showPaymentsList()
        }

        if(screenMode == "mode_notes") {
            binding.simpleTextTitle.text = "Заметки о студенте"
            binding.etNotes.hint = "Внести заметку о студенте"
            binding.tilName.visibility = View.GONE
            binding.etNotes.inputType = InputType.TYPE_CLASS_TEXT
            showNotesStudent()

            binding.imageAddNotes.setOnClickListener {
                addStudentNotes()
            }

           // binding.layoutCollapsed.orientation = LinearLayout.HORIZONTAL
        }


        if(screenMode == "contact_parent") {
            binding.simpleTextTitle.text = "Контакты родителей"
            binding.etName.hint = "Введите имя"
            binding.etNotes.hint = "Введите номер"
            binding.etName.inputType = InputType.TYPE_CLASS_TEXT
            binding.etNotes.inputType = InputType.TYPE_CLASS_NUMBER
            showParentContact()

            binding.etNotes.addTextChangedListener(PhoneTextFormatter(binding.etNotes, "+7 (###) ###-####"))

            binding.imageAddNotes.setOnClickListener {
                addParentContact()
            }
        }


        /*group studentd*/
        if(screenMode == "group_student") {
            binding.simpleTextTitle.text = "Группы студента"
            binding.tilName.visibility = View.GONE
            binding.tilNotes.visibility = View.GONE
            binding.imageAddNotes.visibility = View.GONE
            showGroupStudentList()
        }
        /*group studentd*/


    }


    private fun showGroupStudentList() {
        listView = binding.listView
        dataStudentGroupModel = ArrayList<DataStudentGroupModel>()
        viewModelGroup = ViewModelProvider(this)[GroupListViewModel::class.java]
        viewModelGroup.groupList.observe(viewLifecycleOwner) { groups ->
            for (group in groups) {
                val studentIds = StringHelpers.getStudentIds(group.student)
                if(studentIds.isNotEmpty()) {
                   if(studentIds.contains(studentItemId)) {
                       dataStudentGroupModel!!.add(DataStudentGroupModel(group.id, group.title))

                   }

                }
            }
            val adapterGroup = ListStudentGroupAdapter(dataStudentGroupModel!!, requireContext().applicationContext)
            listView.adapter = adapterGroup
        }
    }

    private fun addParentContact() {
      //  binding.etNotes.setHint("")
        val textName = binding.etName.text.toString()
        val textNumber = binding.etNotes.text.toString()

        val checkTxtName = checkFiledBlank(textName)
        val checkTxtNumber = checkFiledBlank(textNumber)

        if (checkTxtName) {
            hideError(binding.tilName)
        } else {
            showError(binding.tilName)
        }

        if (checkTxtNumber) {
            hideError(binding.tilNotes)
        } else {
            showError(binding.tilNotes)
        }

        if (checkTxtName && checkTxtNumber) {
            viewModelParentContact.addParentContact(textName, textNumber, studentItemId)
            binding.etName.text?.clear()
            binding.etNotes.text?.clear()
        }

    }

    private fun checkFiledBlank(str: String): Boolean {
        return str.isNotBlank()
    }

    @SuppressLint("SimpleDateFormat")
    private fun addStudentNotes() {
        val textNotes = binding.etNotes.text.toString()

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        if (checkFiledBlank(textNotes)) {
            hideError(binding.tilNotes)
            viewModelNotesItem = ViewModelProvider(this)[NotesItemViewModel::class.java]
            val answerNotes = viewModelNotesItem.addNotesItem(textNotes, currentDate, studentItemId)
            if(answerNotes) {
                binding.etNotes.text?.clear()
                showNotesStudent()
            }
        } else {
            showError(binding.tilNotes)
        }



    }

    private fun showError(element: TextInputLayout) {
        element.error = "поле не может быть пустым"
    }

    private fun hideError(element: TextInputLayout) {
        element.error = ""
    }

    private fun selectAction(number: String?) {
        val dialIntent = Intent(Intent.ACTION_VIEW)
        dialIntent.data = Uri.parse("tel:$number")
        startActivity(dialIntent)
    }


    private fun showNotesStudent() {
        listView = binding.listView
        dataNotesStudentModel = ArrayList<DataNotesStudentModel>()
        viewModelNotesItem = ViewModelProvider(this)[NotesItemViewModel::class.java]
        val map: HashMap<String, String> = HashMap()
        viewModelNotesItem.notesList.getNotesList().observe(viewLifecycleOwner) {

            for (item in it) {
                if(item.student == studentItemId) {
                    map[item.date] = item.text
                }
            }

            dataNotesStudentModel!!.clear()
            for ((key, value) in map)  {
                dataNotesStudentModel!!.add(DataNotesStudentModel(value, key))
            }

            val adapterNotes = ListNotesAdapter(dataNotesStudentModel!!, requireContext().applicationContext)
            listView.adapter = adapterNotes

        }
    }

    private fun showParentContact() {
        listView = binding.listView
        viewModelParentContact = ViewModelProvider(this)[ParentContactViewModel::class.java]
        dataParentContactStudentModel = ArrayList<DataParentContactStudentModel>()
        val map: HashMap<String, String> = HashMap()
        viewModelParentContact.parentContactList.getParentList().observe(viewLifecycleOwner) {
            for (item in it) {

                if (item.student == studentItemId){
                    map[item.name] = item.number
                }

            }

            dataParentContactStudentModel!!.clear()
            for ((key, value) in map)  {
                dataParentContactStudentModel!!.add(DataParentContactStudentModel(key, value))
            }

            val adapterParentContact = ListParentContactAdapter(dataParentContactStudentModel!!, requireContext().applicationContext)
            listView.adapter = adapterParentContact


        }

        listView.setOnItemClickListener { _, _, position, _ ->
            selectAction(dataParentContactStudentModel?.get(position)?.phone)
            //call(dataParentContactStudentModel?.get(position)?.phone)
        }

    }

    private fun showPaymentsList() {
        dataPaymentStudentModel = ArrayList<DataPaymentStudentModel>()
        listView = binding.listView
        listView2 = binding.listView2
        viewModelPayment = ViewModelProvider(this)[PaymentListViewModel::class.java]

        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            dataPaymentStudentModel!!.clear()
            if(it.isNotEmpty()) {
                for (payment in it) {
                    if(payment.studentId == studentItemId) {
                        //dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,true))
                        if (payment.enabled) {
                            dataPaymentStudentModel!!.add(DataPaymentStudentModel(payment.id, "Оплачен: " + payment.title, payment.price.toString()))
                        } else {
                            dataPaymentStudentModel!!.add(DataPaymentStudentModel(payment.id,"Долг: " + payment.title, "-" + payment.price.toString()))
                        }

                    }
                }
            }
/*                adapter = ListStudentAdapter(dataStudentGroupModel!!, requireContext().applicationContext)
                listView.adapter = adapter*/
            val adapter =  ListPaymentAdapter(dataPaymentStudentModel!!, requireContext().applicationContext)
            listView.adapter = adapter
            listView2.adapter = adapter

            listView.setOnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position) as DataPaymentStudentModel
                deptOff(selectedItem.id)

               // navigateEditPayment(selectedItem.id)
            }

            listView2.setOnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position) as DataPaymentStudentModel
                deptOff(selectedItem.id)
                //navigateEditPayment(selectedItem.id)
            }

        }


    }

    private fun deptOff(idPayment: Int) {
        viewModelPayments = ViewModelProvider(this)[PaymentItemViewModel::class.java]
        viewModelStudent = ViewModelProvider(this)[StudentItemViewModel::class.java]
        viewModelLessons = ViewModelProvider(this)[LessonsItemViewModel::class.java]

        viewModelPayments.getPaymentItem(idPayment)
        viewModelPayments.paymentItem.observe(viewLifecycleOwner) {payItem->
            if(!payItem.enabled) {
                val payOff = payItem.price
                val itemPaymentId = payItem.id
                val idLessons = payItem.lessonsId
                viewModelStudent.getStudentItem(payItem.studentId)
                viewModelStudent.studentItem.observe(viewLifecycleOwner) {studentItem ->
                    if(studentItem.paymentBalance >= ( - payOff)) {
                        //производит замену прайса с учетом списания долга в записи студента
                        viewModelStudent.editPaymentBalance(studentItem.id, (studentItem.paymentBalance + payOff))
                        //выстаявляет значение платежа в соответствии со стоимостью урока
                        viewModelLessons.getLessonsItem(idLessons)
                        viewModelLessons.lessonsItem.observe(viewLifecycleOwner) {lessItem->
                            viewModelPayments.changeEnableState(lessItem.price, itemPaymentId)

                        }
                        Toast.makeText(activity,"Баланс: " + studentItem.paymentBalance + " Долг:  " + payOff,Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity,"Баланс студента не позволяет списать долг.",Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(activity,"Платеж не является долгом и списывать его не нужно.",Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Я выбрал этот метод ЖЦ, и считаю, что это удачное место
    // Вы можете попробовать производить эти действия не в этом методе ЖЦ, а например в onCreateDialog()
    override fun onStart() {
        super.onStart()

        // Плотность понадобится нам в дальнейшем
        val density = requireContext().resources.displayMetrics.density

        dialog?.let {
            // Находим сам bottomSheet и достаём из него Behaviour
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)

            // Выставляем высоту для состояния collapsed и выставляем состояние collapsed
            behavior.peekHeight = (COLLAPSED_HEIGHT * density).toInt()
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // Нам не нужны действия по этому колбеку
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    with(binding) {
                        // Нас интересует только положительный оффсет, тк при отрицательном нас устроит стандартное поведение - скрытие фрагмента
                        if (slideOffset > 0) {
                            // Делаем "свёрнутый" layout более прозрачным
                            layoutCollapsed.alpha = 1 - 2 * slideOffset
                            // И в то же время делаем "расширенный layout" менее прозрачным
                            layoutExpanded.alpha = slideOffset * slideOffset

                            // Когда оффсет превышает половину, мы скрываем collapsed layout и делаем видимым expanded
                            if (slideOffset > 0.5) {
                                layoutCollapsed.visibility = View.GONE
                                layoutExpanded.visibility = View.VISIBLE
                            }

                            // Если же оффсет меньше половины, а expanded layout всё ещё виден, то нужно скрывать его и показывать collapsed
                            if (slideOffset < 0.5 && binding.layoutExpanded.visibility == View.VISIBLE) {
                                layoutCollapsed.visibility = View.VISIBLE
                                layoutExpanded.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            })
        }
    }



    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = args.getString(SCREEN_MODE) ?: throw RuntimeException("Unknown screen mode")
        screenMode = mode
        if (screenMode == MODE_PAYMENT) {
            if (!args.containsKey(STUDENT_ITEM_ID)) {
                throw RuntimeException("Param id is absent")
            }
            studentItemId = args.getInt(STUDENT_ITEM_ID, StudentItem.UNDEFINED_ID)
        }

        if (screenMode == MODE_NOTES) {
            if (!args.containsKey(STUDENT_ITEM_ID)) {
                throw RuntimeException("Param id is absent")
            }
            studentItemId = args.getInt(STUDENT_ITEM_ID, StudentItem.UNDEFINED_ID)
        }

        if (screenMode == GROUP_STUDENT) {
            if (!args.containsKey(STUDENT_ITEM_ID)) {
                throw RuntimeException("Param id is absent")
            }
            studentItemId = args.getInt(STUDENT_ITEM_ID, StudentItem.UNDEFINED_ID)
        }

        if (screenMode == CONTACT_PARENT) {
            if (!args.containsKey(STUDENT_ITEM_ID)) {
                throw RuntimeException("Param id is absent")
            }
            studentItemId = args.getInt(STUDENT_ITEM_ID, StudentItem.UNDEFINED_ID)
        }


    }


    companion object {

        private const val SCREEN_MODE = "extra_mode"
        private const val STUDENT_ITEM_ID = "extra_shop_item_id"
        private const val MODE_PAYMENT = "mode_payment"
        private const val MODE_NOTES = "mode_notes"
        private const val CONTACT_PARENT = "contact_parent"
        private const val GROUP_STUDENT = "group_student"
        private const val MODE_UNKNOWN = ""


        fun newInstanceParentsContacts(studentItemId: Int): BottomFragment {
            return BottomFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, CONTACT_PARENT)
                    putInt(STUDENT_ITEM_ID, studentItemId)
                }
            }
        }

        fun newInstanceNotesStudent(studentItemId: Int): BottomFragment {
            return BottomFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_NOTES)
                    putInt(STUDENT_ITEM_ID, studentItemId)
                }
            }
        }

        fun newInstanceGroupStudent(studentItemId: Int): BottomFragment {
            return BottomFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, GROUP_STUDENT)
                    putInt(STUDENT_ITEM_ID, studentItemId)
                }
            }
        }

    }

}