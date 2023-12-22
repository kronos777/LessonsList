package com.example.lessonslist.presentation.student

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.InputType.TYPE_CLASS_NUMBER
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentStudentItemEditBinding
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.helpers.BottomFragment
import com.example.lessonslist.presentation.helpers.NavigationOptions
import com.example.lessonslist.presentation.helpers.PhoneTextFormatter
import com.example.lessonslist.presentation.helpers.StringHelpers
import com.example.lessonslist.presentation.lessons.LessonsItemViewModel
import com.example.lessonslist.presentation.lessons.sale.SaleItemViewModel
import com.example.lessonslist.presentation.lessons.sale.SalesItemListViewModel
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class StudentItemEditFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[StudentItemViewModel::class.java]
    }
    private val viewModelLessonsEdit by lazy {
        ViewModelProvider(this)[LessonsItemViewModel::class.java]
    }
    private val viewModelPayment by lazy {
        ViewModelProvider(this)[PaymentListViewModel::class.java]
    }
    private val viewModelParentContact by lazy {
        ViewModelProvider(this)[ParentContactViewModel::class.java]
    }
    private val viewModelNotesItem by lazy {
        ViewModelProvider(this)[NotesItemViewModel::class.java]
    }
    private val viewModelSale by lazy {
        ViewModelProvider(this)[SaleItemViewModel::class.java]
    }
    private val viewModelSalesList by lazy {
        ViewModelProvider(this)[SalesItemListViewModel::class.java]
    }

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentStudentItemEditBinding? = null
    private val binding: FragmentStudentItemEditBinding
        get() = _binding ?: throw RuntimeException("FragmentStudentItemEditBinding == null")


    private var studentItemId: Int = StudentItem.UNDEFINED_ID

    private lateinit var mImageView: ImageView

    private val myExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())

    private var pathImageSrc: String = ""

    private val takeFoto = 1

    private val navController by lazy {
        (activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment).navController
    }


    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uriContent)
            pathImageSrc = mSaveMediaToStorage(bitmap).toString()
            binding.imageView.setImageBitmap(bitmap)
        } else {
            // An error occurred.
            result.error
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentStudentItemEditBinding.inflate(inflater, container, false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.getStudentItem(studentItemId)
        launchRightMode()
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem5).isChecked = true


        viewModel.studentItem.observe(viewLifecycleOwner) { stItem ->
            setNameStudent(stItem.name + " " + stItem.lastname)
            if (stItem.paymentBalance <= 0) {
                totalDebt()
            }

            if(stItem.image.isNotBlank()) {
                myHandler.post {
                    val file = File(stItem.image)
                    Picasso.get()
                        .load(file)
                        .resize(200, 200)
                        .into(mImageView)
                    pathImageSrc = file.toString()
                }
            }

           binding.cardTelephoneStudent.setOnClickListener {
                   askEditNumber(stItem.telephone)
            }

        }

        mImageView = binding.imageView

        mImageView.setOnClickListener {
            actionChangeImage()
        }

        binding.cardAddBalance.setOnClickListener {
            actionAddMoney()
        }

        binding.cardAddNotes.setOnClickListener {
            activity?.let { BottomFragment.newInstanceNotesStudent(studentItemId).show(it.supportFragmentManager, "tag") }
        }

       binding.cardPaymentStudent.setOnClickListener {
                getDialogPaymentStudent()
       }

        binding.cardGroupStudent.setOnClickListener {
            activity?.let { BottomFragment.newInstanceGroupStudent(studentItemId).show(it.supportFragmentManager, "tag") }
        }

       binding.cardParentContact.setOnClickListener {
           activity?.let { BottomFragment.newInstanceParentsContacts(studentItemId).show(it.supportFragmentManager, "tag") }
       }

        binding.cardDeleteData.setOnClickListener {
            deleteAllSaleItem()
            deletePaymentToStudent(studentItemId)
            deleteAllContactStudent()
            deleteAllNotesStudent()
            viewModel.deleteStudentItem(studentItemId)
            observeViewModel()
        }

    }

    private fun setNameStudent(nameStudent: String) {
        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = nameStudent
    }

    private fun deleteAllNotesStudent() {
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

    private fun deleteAllContactStudent() {
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


    private fun deleteAllSaleItem() {
        viewModelSalesList.salesList.observe(viewLifecycleOwner) { sales ->
            for (saleItem in sales.indices) {
                if(studentItemId == sales[saleItem].idStudent) {
                    viewModelSale.deleteSaleItem(sales[saleItem].id)
                }
            }


        }
    }

    private fun getDialogPaymentStudent() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Платежи студента")
        //alert.setMessage("Enter phone details and amount to buy airtime.")
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        val paymentsLabel = TextView(requireContext())
        paymentsLabel.setSingleLine()
        paymentsLabel.text = "Отсюда Вы можете посмотреть все платежи или все долги студента."
        paymentsLabel.isSingleLine = false
        paymentsLabel.height = 250
        paymentsLabel.top = 15
        layout.addView(paymentsLabel)
        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("Платежи") { _, _ ->
            showWindowPayment("all_payment")
        }

        alert.setNegativeButton("Долги") { _, _ ->
            showWindowPayment("all_false_payment")

        }

        alert.setNeutralButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }


        alert.setCancelable(true)
        alert.show()
    }

    private fun callStudent(number: String?) {
        // val dialIntent = Intent(Intent.ACTION_SEND)
        val dialIntent = Intent(Intent.ACTION_VIEW)
        dialIntent.data = Uri.parse("tel:$number")
        startActivity(dialIntent)
    }

    private fun showWindowPayment(params: String) {

        if(params == "all_payment") {
            val btnArgsStudentNoParams = Bundle().apply {
                putInt(PaymentItemListFragment.STUDENT_ID, studentItemId)
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.STUDENT_ID_LIST)
            }
            navController.navigate(R.id.paymentItemListFragment, btnArgsStudentNoParams, NavigationOptions().invoke())
        } else if(params == "all_false_payment") {
            val btnArgsStudentFalsePayment = Bundle().apply {
                putInt(PaymentItemListFragment.STUDENT_ID, studentItemId)
                putString(
                    PaymentItemListFragment.SCREEN_MODE,
                    PaymentItemListFragment.STUDENT_NO_PAY_LIST
                )
            }
            navController.navigate(R.id.paymentItemListFragment, btnArgsStudentFalsePayment, NavigationOptions().invoke())
        }

    }


    private fun askEditNumber(telephone: String?) {
        val alert = AlertDialog.Builder(requireContext())
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(50, 40, 50, 10)
        alert.setView(layout)

        if(!telephone.isNullOrBlank()) {
            alert.setTitle("Телефон студента $telephone ?")
            alert.setPositiveButton("Изменить") { _, _ ->
                addPhoneNumber()
            }
            alert.setNeutralButton("Позвонить") { _, _ ->
                callStudent(telephone)
            }
        } else {
            alert.setTitle("Добавьте телефон студента.")
            alert.setPositiveButton("Добавить") { _, _ ->
                addPhoneNumber()
            }
        }


        alert.setNegativeButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(true)
        alert.show()
    }



    @SuppressLint("SetTextI18n")
    private fun actionAddMoney() {
        var newBalance: Int

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val inputEditTextField = EditText(requireActivity())
        inputEditTextField.inputType = 2
        layout.setPadding(50, 40, 50, 10)
        layout.addView(inputEditTextField)


        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Пополнить баланс студента.")
            //.setMessage("Message")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                val editTextInput = inputEditTextField.text.toString()

                if(isNumeric(editTextInput)){
                    newBalance = editTextInput.toInt()
                    viewModel.editPaymentBalance(studentItemId, (binding.textViewPaymentBalance.text.toString().toInt() + newBalance))
                    viewModel.getStudentItem(studentItemId)
                    viewModel.studentItem.observe(viewLifecycleOwner) {
                        binding.textViewPaymentBalance.text = it.paymentBalance.toString()
                    }
                   /* viewModel.studentItem.observe(viewLifecycleOwner) {
                        Log.d("currentBalance before", it.paymentBalance.toString())
                        viewModel.editPaymentBalance(it.id, (it.paymentBalance + newBalance))
                    }
                    viewModel.getStudentItem(studentItemId)
                    viewModel.studentItem.observe(viewLifecycleOwner) {
                        binding.textViewPaymentBalance.text = (it.paymentBalance + newBalance).toString()
                        Log.d("currentBalance after", it.paymentBalance.toString())
                    }*/
                } else {
                    Toast.makeText(activity,"Строка не является числом, сохранить невозможно.",Toast.LENGTH_SHORT).show()
                }


            }
            .setNegativeButton("отмена", null)
            .create()
        dialog.show()
        //Toast.makeText(getActivity(),"inputdata!"+inputEditTextField.text.toString(),Toast.LENGTH_SHORT).show();
        //Log.d("new balance", inputEditTextField.text.toString())
    }


    private fun actionChangeImage() {
        myExecutor.execute {
            getImageLocal()
        }
    }


    private fun totalDebt() {
        var sumDept = 0
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                for (payment in it) {
                    if(payment.studentId == studentItemId) {
                        if (!payment.enabled) {
                            sumDept += payment.price
                        }

                    }
                }
                binding.textViewPaymentBalance.setTextColor(R.color.custom_calendar_weekend_days_bar_text_color.dec())
                binding.textViewPaymentBalance.text = sumDept.toString()
            }

        }
    }



    private fun addPhoneNumber() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Добавить телефон студента.")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL


        val amountET = EditText(requireContext())
        amountET.setSingleLine()
        amountET.hint = "Телефон"
        amountET.inputType = TYPE_CLASS_NUMBER
        amountET.addTextChangedListener(PhoneTextFormatter(amountET, "+7 (###) ###-####"))
        layout.addView(amountET)
        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)
        alert.setPositiveButton("Добавить") { _, _ ->
            val number = amountET.text.toString()
            viewModel.editPhoneNumber(studentItemId, number)
        }

        alert.setNegativeButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(true)
        alert.show()
    }

    private fun getImageLocal() {
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 1)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == takeFoto && resultCode == RESULT_OK) {
            // Фотка сделана, извлекаем миниатюру картинки
            if(data?.extras?.get("data") != null) {
                val imageUri: Uri? = activity?.let { getImageUri(it.applicationContext,
                    data.extras?.get("data") as Bitmap
                ) }
                if (imageUri != null) {
                    startCrop(imageUri)
                }
            } else {
                val imageUri: Uri? = data?.data
                if (imageUri != null) {
                    startCrop(imageUri)
                }
            }

        }


    }


    private fun startCrop(uriFilePath: Uri) {
        // Start picker to get image for cropping and then use the image in cropping activity.
        cropImage.launch(
            options(uri = uriFilePath) {
                setGuidelines(CropImageView.Guidelines.ON)
                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
            }
        )

        // Start picker to get image for cropping from only gallery and then use the image in cropping activity.
        /*  cropImage.launch(
               options {
                   setImagePickerContractOptions(
                       PickImageContractOptions(includeGallery = true, includeCamera = false)
                   )
               }
           )
           // Start cropping activity for pre-acquired image saved on the device and customize settings.
           cropImage.launch(
               options(uri = uriFilePath) {
                   setGuidelines(CropImageView.Guidelines.ON)
                   setOutputCompressFormat(Bitmap.CompressFormat.PNG)
               }
           )*/
    }



    private fun mSaveMediaToStorage(bitmap: Bitmap?): File {
        val filename = "${System.currentTimeMillis()}.jpg"
        val fos: OutputStream?

        val imagesDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + "lessonsList")

        imagesDir.apply {
            if (!this.exists()) this.mkdir()
        }
        //val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)


        fos.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return image
    }

    private fun isNumeric(toCheck: String): Boolean {
        val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
        return toCheck.matches(regex)
    }




    private fun observeViewModel() {
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
    }

    private fun launchRightMode() {
        /*when (screenMode) {
            MODE_EDIT -> launchEditMode()
        }*/
        launchEditMode()
    }

   private fun isLessThanZero(number: Int): Int {
       return if(number < 0) {
           0
       } else {
           number
       }
   }


    private fun launchEditMode() {
        viewModel.getStudentItem(studentItemId)
        binding.cardSaveData.setOnClickListener {
            viewModel.editStudentItem(
                viewModel.studentItem.value?.name,
                viewModel.studentItem.value?.lastname,
                isLessThanZero(binding.textViewPaymentBalance.text.toString().toInt()).toString(),
                " ",
                " ",
                pathImageSrc,
                viewModel.studentItem.value?.telephone.toString()
                //binding.etNotes.text.toString(),
                //binding.etGroup.text.toString()
            )

        }
        observeViewModel()
    }



    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        studentItemId = args.getInt(SHOP_ITEM_ID, StudentItem.UNDEFINED_ID)
    }

    private fun deletePaymentToStudent(studentId: Int) {
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            for (payment in it) {
                if(payment.studentId == studentId) {
                    Log.d("payment.lessonsId", payment.lessonsId.toString())
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

    private fun dropElementList(arrayList: List<Int>, el: Int): String {
        val elementList = mutableListOf<Int>()
        for(item in arrayList) {
            if(item != el){
                elementList.add(item)
            }

        }
        return elementList.toString()
    }

    interface OnEditingFinishedListener {

        fun onEditingFinished()

    }


    companion object {

        const val SCREEN_MODE = "extra_mode"
        const val SHOP_ITEM_ID = "extra_shop_item_id"
        const val MODE_EDIT = "mode_edit"

    }
}

