package com.listlessons.lessonslist.presentation.helpers

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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.listlessons.lessonslist.R
import com.listlessons.lessonslist.databinding.BottomSheetLayoutBinding
import com.listlessons.lessonslist.domain.group.GroupItem
import com.listlessons.lessonslist.domain.notes.NotesItem
import com.listlessons.lessonslist.domain.parent.ParentContact
import com.listlessons.lessonslist.domain.student.StudentItem
import com.listlessons.lessonslist.presentation.group.GroupItemFragment
import com.listlessons.lessonslist.presentation.group.GroupListViewModel
import com.listlessons.lessonslist.presentation.student.*
import com.listlessons.lessonslist.presentation.student.group.GroupListAdapterBottomFragment
import com.listlessons.lessonslist.presentation.student.notes.NotesListAdapterBottomFragment
import com.listlessons.lessonslist.presentation.student.parentContact.ParentListAdapterBottomFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

private const val COLLAPSED_HEIGHT = 400

class BottomFragment : BottomSheetDialogFragment() {

    private var screenMode: String = MODE_UNKNOWN
    private var studentItemId: Int = StudentItem.UNDEFINED_ID

    //по листам
    private val viewModelNotesItem by lazy {
        ViewModelProvider(this)[NotesItemViewModel::class.java]
    }

    private val viewModelParentContact by lazy {
        ViewModelProvider(this)[ParentContactViewModel::class.java]
    }

    private val viewModelGroup by lazy {
        ViewModelProvider(this)[GroupListViewModel::class.java]
    }


    private lateinit var groupListAdapter: GroupListAdapterBottomFragment

    private lateinit var parentListAdapter: ParentListAdapterBottomFragment

    private lateinit var notesListAdapter: NotesListAdapterBottomFragment

    private var countContactParent: Int? = null
    private var countNotes: Int? = null

    // Можно обойтись без биндинга и использовать findViewById
    lateinit var binding: BottomSheetLayoutBinding


    private val allNotes = ArrayList<NotesItem>()
    private val allContact = ArrayList<ParentContact>()
    private val listGroup = ArrayList<GroupItem>()

    private val navController by lazy {
        (activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment).navController
    }


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
        //showGroupStudentList()
        //setupGroupStudentRecycler()
        showGroupStudent()
    }



    private fun setupNotesRecycler() {
        with(binding.rvList) {
            notesListAdapter = NotesListAdapterBottomFragment()
            adapter = notesListAdapter
            recycledViewPool.setMaxRecycledViews(
                NotesListAdapterBottomFragment.VIEW_TYPE_ENABLED,
                NotesListAdapterBottomFragment.MAX_POOL_SIZE
            )
        }

    }


    private fun setupParentRecycler() {
        with(binding.rvList) {
            parentListAdapter = ParentListAdapterBottomFragment()
            adapter = parentListAdapter
            recycledViewPool.setMaxRecycledViews(
                ParentListAdapterBottomFragment.VIEW_TYPE_ENABLED,
                ParentListAdapterBottomFragment.MAX_POOL_SIZE
            )
        }
    }


    private fun setupGroupStudentRecycler() {
        with(binding.rvList) {
            groupListAdapter = GroupListAdapterBottomFragment()
            adapter = groupListAdapter
            recycledViewPool.setMaxRecycledViews(
                GroupListAdapterBottomFragment.VIEW_TYPE_ENABLED,
                GroupListAdapterBottomFragment.MAX_POOL_SIZE
            )
        }
    }

    private fun setupExpandedRecycler() {
        when(screenMode) {
            "mode_notes" -> showModeExpandedNotes()
            "contact_parent" -> showModeExpandedContactParent()
            "group_student" -> showModeExpandedGroupStudent()
        }
    }

    private fun showModeExpandedGroupStudent() {
        with(binding.rvListExpanded) {
            adapter =  groupListAdapter
            recycledViewPool.setMaxRecycledViews(
                GroupListAdapterBottomFragment.VIEW_TYPE_ENABLED,
                GroupListAdapterBottomFragment.MAX_POOL_SIZE
            )
        }

        if(listGroup.isNotEmpty()) {
            groupListAdapter.submitList(listGroup)
            setupGroupItemAdapterClick()
        }
    }

    private fun showModeExpandedNotes() {
        with(binding.rvListExpanded) {
            adapter =  notesListAdapter
            recycledViewPool.setMaxRecycledViews(
                NotesListAdapterBottomFragment.VIEW_TYPE_ENABLED,
                NotesListAdapterBottomFragment.MAX_POOL_SIZE
            )
        }

        if(allNotes.isNotEmpty()){
            notesListAdapter.submitList(allNotes)
            setupClickListenerNotes()
        }
    }

    private fun showModeExpandedContactParent() {
        with(binding.rvListExpanded) {
            adapter =  parentListAdapter
            recycledViewPool.setMaxRecycledViews(
                ParentListAdapterBottomFragment.VIEW_TYPE_ENABLED,
                ParentListAdapterBottomFragment.MAX_POOL_SIZE
            )
        }

        if(allContact.isNotEmpty()) {
            parentListAdapter.submitList(allContact)
            setupParentClickListener()
        }

    }


    private fun showNotesStudent() {
        viewModelNotesItem.notesList.getNotesList().observe(viewLifecycleOwner) {
            allNotes.clear()
            for (item in it) {
                if(item.student == studentItemId) {
                    allNotes.add(item)
                }
            }
            countNotes = allNotes.size
            if(allNotes.isNotEmpty()){
                setupNotesRecycler()
                notesListAdapter.submitList(allNotes)
                setupClickListenerNotes()
            }
        }
    }




    private fun showParentContact() {
        viewModelParentContact.parentContactList.getParentList().observe(viewLifecycleOwner) { listContact ->
            allContact.clear()
            listContact.forEach {
                if(it.student == studentItemId) {
                    allContact.add(it)
                }
            }
            countContactParent = allContact.size
            if(allContact.isNotEmpty()) {
                setupParentRecycler()
                parentListAdapter.submitList(allContact)
                setupParentClickListener()
            }
        }
    }

    private fun setupParentClickListener() {
        parentListAdapter.onParentItemClickListener = {
            getDialogContactItem(it.name, it.number, it.id)
        }
    }


    private fun showGroupStudent() {
        viewModelGroup.groupList.observe(viewLifecycleOwner) { groups ->
            groups.forEach { 
                if(StringHelpers.getStudentIds(it.student).contains(studentItemId)) {
                    listGroup.add(it)
                }
            }
            if(listGroup.isNotEmpty()) {
                setupGroupStudentRecycler()
                groupListAdapter.submitList(listGroup)
                setupGroupItemAdapterClick()
            }

        }
    }




    private fun setupGroupItemAdapterClick() {
        groupListAdapter.onGroupItemClickListener = {
            navigateBtnEditGroup(it.id)
        }
    }
    private fun navigateBtnEditGroup(id: Int) {
        val btnArgsGroup = Bundle().apply {
            putString(GroupItemFragment.SCREEN_MODE, GroupItemFragment.MODE_EDIT)
            putInt(GroupItemFragment.GROUP_ITEM_ID, id)
        }
        navController.navigate(R.id.groupItemFragment, btnArgsGroup, NavigationOptions().invoke())
    }
    private fun addParentContact() {
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

    private fun setupClickListenerNotes() {
        notesListAdapter.onNotesItemClickListener = {
            getDialogNotesItem(it.date, it.text, it.id)
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

        alert.setCancelable(true)
        alert.show()
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
        alert.setCancelable(true)
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
                                setupExpandedRecycler()
                                layoutCollapsed.visibility = View.GONE
                                layoutExpanded.visibility = View.VISIBLE
                                layoutExpanded.minimumHeight = 700
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

