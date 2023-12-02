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
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.BottomSheetLayoutBinding
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.group.GroupItemFragment
import com.example.lessonslist.presentation.group.GroupListViewModel
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
    private lateinit var viewModelNotesItem: NotesItemViewModel
    private lateinit var viewModelParentContact: ParentContactViewModel
    private lateinit var viewModelGroup: GroupListViewModel

    private var dataNotesStudentModel: ArrayList<DataNotesStudentModel>? = null
    private var dataParentContactStudentModel: ArrayList<DataParentContactStudentModel>? = null
    private var dataStudentGroupModel: ArrayList<DataStudentGroupModel>? = null

    private var countContactParent: Int? = null
    private var countNotes: Int? = null

    // Можно обойтись без биндинга и использовать findViewById
    lateinit var binding: BottomSheetLayoutBinding


    private lateinit var adapterNotes: ListNotesAdapter


    // Переопределим тему, чтобы использовать нашу с закруглёнными углами
    override fun getTheme() = R.style.AppBottomSheetDialogTheme


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetLayoutBinding.bind(inflater.inflate(R.layout.bottom_sheet_layout, container))
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(screenMode) {
            "mode_notes" -> showModeNotes()
            "contact_parent" -> showModeContactParent()
            "group_student" -> showModeGroupStudent()
        }



    }

    private fun showModeNotes() {
        binding.simpleTextTitle.text = "Заметки о студенте"
        binding.etNotes.hint = "Внести заметку о студенте"
        binding.tilName.visibility = View.GONE
        binding.etNotes.inputType = InputType.TYPE_CLASS_TEXT
        showNotesStudent()

        binding.imageAddNotes.setOnClickListener {
            addStudentNotes()
        }
    }

    private fun showModeContactParent() {
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

    private fun showModeGroupStudent() {
        binding.simpleTextTitle.text = "Группы студента"
        binding.tilName.visibility = View.GONE
        binding.tilNotes.visibility = View.GONE
        binding.imageAddNotes.visibility = View.GONE
        showGroupStudentList()
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
            setupGroupItemAdapterClick(adapterGroup)
        }
    }

    private fun setupGroupItemAdapterClick(adapterGroup: ListStudentGroupAdapter) {
        adapterGroup.onGroupItemClick = {
            navigateBtnEditGroup(it.id)
        }
    }
    private fun navigateBtnEditGroup(id: Int) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController

        val btnArgsGroup = Bundle().apply {
            putString(GroupItemFragment.SCREEN_MODE, GroupItemFragment.MODE_EDIT)
            putInt(GroupItemFragment.GROUP_ITEM_ID, id)
        }
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navController.navigate(R.id.groupItemFragment, btnArgsGroup, animationOptions)
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
            if(countContactParent!! < 4) {
                viewModelParentContact.addParentContact(textName, textNumber, studentItemId)
                binding.etName.text?.clear()
                binding.etNotes.text?.clear()
            } else {
                Toast.makeText(activity, "Больше 4 контактов добавить нельзя.", Toast.LENGTH_LONG).show()
            }
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
            if(countNotes!! < 40) {
                hideError(binding.tilNotes)
                viewModelNotesItem = ViewModelProvider(this)[NotesItemViewModel::class.java]
                val answerNotes = viewModelNotesItem.addNotesItem(textNotes, currentDate, studentItemId)
                if(answerNotes) {
                    binding.etNotes.text?.clear()
                    showNotesStudent()
                }
            } else {
                Toast.makeText(activity, "Больше 40 заметок добавить нельзя.", Toast.LENGTH_LONG).show()
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


    private fun showNotesStudent() {
        listView = binding.listView
        dataNotesStudentModel = ArrayList<DataNotesStudentModel>()
        viewModelNotesItem = ViewModelProvider(this)[NotesItemViewModel::class.java]
        //val map: HashMap<String, String> = HashMap()
        viewModelNotesItem.notesList.getNotesList().observe(viewLifecycleOwner) {
            dataNotesStudentModel!!.clear()
            for (item in it) {
                if(item.student == studentItemId) {
//                    map[item.date] = item.text
                    dataNotesStudentModel!!.add(DataNotesStudentModel(item.text, item.date, item.id))
                    //map[item.date] = item.text
                }
            }
            countNotes = dataNotesStudentModel?.size ?: 0
            adapterNotes = ListNotesAdapter(dataNotesStudentModel!!, requireContext().applicationContext)
            listView.adapter = adapterNotes
            setupClickListenerNotes()
        }

    }

    private fun setupClickListenerNotes() {
        adapterNotes.onNotesItemClickListener = {
            getDialogNotesItem(it.date.toString(), it.text.toString(), it.id)
        }
    }

    private fun getDialogNotesItem(title: String, textNotes: String, id: Int) {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle(title)
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        val notesLabel = TextView(requireContext())
        notesLabel.setSingleLine()
        notesLabel.text = textNotes
        notesLabel.textSize = 22.0F
        notesLabel.isSingleLine = false
        notesLabel.height = 250
        notesLabel.top = 15
        layout.addView(notesLabel)
        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("удалить") { _, _ ->
            viewModelNotesItem.deleteNotesItem(id)
        }

        alert.setNegativeButton("закрыть") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(false)
        alert.show()
    }



    private fun showParentContact() {
        listView = binding.listView
        viewModelParentContact = ViewModelProvider(this)[ParentContactViewModel::class.java]
        dataParentContactStudentModel = ArrayList<DataParentContactStudentModel>()
        viewModelParentContact.parentContactList.getParentList().observe(viewLifecycleOwner) {
            dataParentContactStudentModel!!.clear()
            for (item in it) {
                if(item.student == studentItemId) {
                    dataParentContactStudentModel!!.add(DataParentContactStudentModel(item.name, item.number, item.id))
                }
            }
            countContactParent = dataParentContactStudentModel?.size ?: 0
            val adapterParentContact = ListParentContactAdapter(dataParentContactStudentModel!!, requireContext().applicationContext)
            listView.adapter = adapterParentContact


        }

        listView.setOnItemClickListener { _, _, position, _ ->
            //selectAction(dataParentContactStudentModel?.get(position)?.phone)
            getDialogContactItem(dataParentContactStudentModel?.get(position)?.text, dataParentContactStudentModel?.get(position)?.phone, dataParentContactStudentModel?.get(position)!!.id)
        }

    }

    private fun selectAction(number: String?) {
        val dialIntent = Intent(Intent.ACTION_VIEW)
        dialIntent.data = Uri.parse("tel:$number")
        startActivity(dialIntent)
    }

    @SuppressLint("SetTextI18n")
    private fun getDialogContactItem(name: String?, phone: String?, id: Int) {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Выберите действие для контакта")
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        val parentLabel = TextView(requireContext())
        parentLabel.setSingleLine()
        parentLabel.text = name + "\n" + phone
        parentLabel.textSize = 22.0F
        parentLabel.isSingleLine = false
        parentLabel.height = 250
        parentLabel.top = 15
        layout.addView(parentLabel)
        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("позвонить") { _, _ ->
            selectAction(phone)
        }

        alert.setNegativeButton("удалить") { _, _ ->
            viewModelParentContact.deleteParentContact(id)
        }
        alert.setNeutralButton("закрыть") { dialog, _ ->
            dialog.dismiss()
        }
        alert.setCancelable(false)
        alert.show()
    }




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