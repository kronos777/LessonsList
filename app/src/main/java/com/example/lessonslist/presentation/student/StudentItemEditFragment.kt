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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentStudentItemEditBinding
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.helpers.BottomFragment
import com.example.lessonslist.presentation.helpers.PhoneTextFormatter
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

    private lateinit var viewModel: StudentItemViewModel
    private lateinit var viewModelLessonsEdit: LessonsItemViewModel
    private lateinit var viewModelPayment: PaymentListViewModel

    private lateinit var viewModelSale: SaleItemViewModel
    private lateinit var viewModelSalesList: SalesItemListViewModel

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentStudentItemEditBinding? = null
    private val binding: FragmentStudentItemEditBinding
        get() = _binding ?: throw RuntimeException("FragmentStudentItemEditBinding == null")


    private var screenMode: String = MODE_UNKNOWN
    private var studentItemId: Int = StudentItem.UNDEFINED_ID

    private lateinit var mImageView: ImageView

    private val myExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())

    private var pathImageSrc: String = ""

    private val TAKEPHOTO = 1

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

        viewModel = ViewModelProvider(this)[StudentItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        launchRightMode()
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem5).isChecked = true

        viewModel.studentItem.observe(viewLifecycleOwner) { stItem ->
            if (stItem.paymentBalance <= 0) {
                totalDebt()
            }

            if(stItem.image.isNotBlank()) {
                myHandler.post {
                    val file = File(stItem.image)
                    Log.d("imageTag", stItem.image)
                    Picasso.get()
                        .load(file)
                        .resize(200, 200)
                       // .rotate(90f)
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
            viewModel.deleteStudentItem(studentItemId)
        }


    }


    private fun deleteAllSaleItem() {
        viewModelSalesList = ViewModelProvider(this)[SalesItemListViewModel::class.java]
        viewModelSale = ViewModelProvider(this)[SaleItemViewModel::class.java]
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
            navigateBtnAddStudent("all_payment")
        }

        alert.setNegativeButton("Долги") { _, _ ->
            navigateBtnAddStudent("all_false_payment")

        }

        alert.setNeutralButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }


        alert.setCancelable(false)
        alert.show()
    }

    private fun callStudent(number: String?) {
        // val dialIntent = Intent(Intent.ACTION_SEND)
        val dialIntent = Intent(Intent.ACTION_VIEW)
        dialIntent.data = Uri.parse("tel:" + number)
        startActivity(dialIntent)
    }

    private fun navigateBtnAddStudent(params: String) {

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragment_item_container) as NavHostFragment
        val navController = navHostFragment.navController
        val animationOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_right)
            .setPopEnterAnim(R.anim.slide_out_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

        if(params == "all_payment") {
            val btnArgsStudentNoParams = Bundle().apply {
                putInt(PaymentItemListFragment.STUDENT_ID, studentItemId)
                putString(PaymentItemListFragment.SCREEN_MODE, PaymentItemListFragment.STUDENT_ID_LIST)
            }
            navController.navigate(R.id.paymentItemListFragment, btnArgsStudentNoParams, animationOptions)
        } else if(params == "all_false_payment") {
            val btnArgsStudentFalsePayment = Bundle().apply {
                putInt(PaymentItemListFragment.STUDENT_ID, studentItemId)
                putString(
                    PaymentItemListFragment.SCREEN_MODE,
                    PaymentItemListFragment.STUDENT_NO_PAY_LIST
                )
            }
            navController.navigate(R.id.paymentItemListFragment, btnArgsStudentFalsePayment, animationOptions)
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

        alert.setCancelable(false)
        alert.show()
    }



    @SuppressLint("SetTextI18n")
    private fun actionAddMoney() {
        var newBalance: Int
        val inputEditTextField = EditText(requireActivity())
        //inputEditTextField.setInputType(InputType.TYPE_CLASS_NUMBER)
        inputEditTextField.inputType = 2
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Пополнить баланс студента.")
            //.setMessage("Message")
            .setView(inputEditTextField)
            .setPositiveButton("OK") { _, _ ->
                val editTextInput = inputEditTextField.text.toString()

                if(isNumeric(editTextInput)){
                    newBalance = editTextInput.toInt()
                    viewModel.studentItem.observe(viewLifecycleOwner) {
                        viewModel.editPaymentBalance(it.id, (it.paymentBalance + newBalance))
                        binding.textViewPaymentBalance.text = (it.paymentBalance + newBalance).toString()
                    }
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


    fun totalDebt() {
        var summDept = 0
        viewModelPayment = ViewModelProvider(this)[PaymentListViewModel::class.java]
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                for (payment in it) {
                    if(payment.studentId == studentItemId) {
                        //dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,true))
                        if (!payment.enabled) {
                            summDept += payment.price
                             //dataPaymentStudentModel!!.add(DataPaymentStudentModel(payment.id,"Долг: " + payment.title, "-" + payment.price.toString()))
                        }

                    }
                }
                //Log.d("summDept", summDept.toString())
                binding.textViewPaymentBalance.text = summDept.toString()
                binding.textViewPaymentBalance.setTextColor(R.color.custom_calendar_weekend_days_bar_text_color.dec())
                //binding.textViewPaymentBalance.setTextColor(-0x000000ff)
            }

        }
    }


/*
    private fun addParentContactStudent() {

        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Добавить телефон родителей.")
        //alert.setMessage("Enter phone details and amount to buy airtime.")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL


        val mobileNoET = EditText(requireContext())
        mobileNoET.setSingleLine()
        mobileNoET.hint = "Имя"
        layout.addView(mobileNoET)

        val amountET = EditText(requireContext())
        amountET.setSingleLine()
        amountET.hint = "Телефон"
        amountET.inputType = TYPE_CLASS_NUMBER
        amountET.addTextChangedListener(PhoneTextFormatter(amountET, "+7 (###) ###-####"))
        layout.addView(amountET)

        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("Добавить") { _, _ ->
            val name = mobileNoET.text.toString()
            val number = amountET.text.toString()
            Toast.makeText(activity, "Saved Sucessfully", Toast.LENGTH_LONG).show()
            viewModelParentContact.addParentContact(name, number, studentItemId)
        }

        alert.setNegativeButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(false)
        alert.show()
    }*/

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
           // Toast.makeText(activity, "Saved Sucessfully" + number, Toast.LENGTH_LONG).show()
            viewModel.editPhoneNumber(studentItemId, number)
          //  sleep(100)
           /* viewModel.studentItem.observe(viewLifecycleOwner) {
                binding.textViewTelephone.text = it.telephone
             //   Toast.makeText(activity, "new phone" + it.telephone, Toast.LENGTH_LONG).show()
            }*/
        }

        alert.setNegativeButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(false)
        alert.show()
    }

    fun getImageLocal() {
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 1)
    }

/*
    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == RESULT_OK) {
                    val chosenImageUri: Uri? = data.data
                }
            }
        }
    }*/
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        chosenImageUri = data.data!!

                        var mImage: Bitmap?
                        //Toast.makeText(getActivity(), "File path" + chosenImageUri, Toast.LENGTH_LONG).show()
                        mImage = mLoadLocal(chosenImageUri.toString())

                    //    binding.imagepath.setText(chosenImageUri.toString())
                        myHandler.post {
                                                  //mImageView.setImageBitmap(mImage)
                                                  Picasso.get()
                                                      .load(chosenImageUri.toString())
                                                      .resize(400, 300)
                                                      // .transform(CropCircleTransformation())
                                                     .rotate(90f)
                                                      .into(mImageView)


                        if(mImage!=null){

                                 pathImageSrc = mSaveMediaToStorage(mImage).toString()

                              }
                        }

                    }
                }
            }
        }
    }*/

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TAKEPHOTO && resultCode == RESULT_OK) {
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

        val imagesDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + "lessonslist")
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
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
        }
    }

   private fun isLessthanZero(number: Int): Int {
       if(number < 0) {
           return 0
       } else {
           return number
       }
   }


    private fun launchEditMode() {
        viewModel.getStudentItem(studentItemId)
        binding.cardSaveData.setOnClickListener {
            viewModel.editStudentItem(
                viewModel.studentItem.value?.name,
                viewModel.studentItem.value?.lastname,
                isLessthanZero(binding.textViewPaymentBalance.text.toString().toInt()).toString(),
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
        viewModelPayment = ViewModelProvider(this).get(PaymentListViewModel::class.java)
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
        viewModelLessonsEdit = ViewModelProvider(this).get(LessonsItemViewModel::class.java)
        viewModelLessonsEdit.getLessonsItem(idLessons)
        // val lessonsItem = viewModelLessonsEdit.lessonsItem
        viewModelLessonsEdit.lessonsItem.observe(viewLifecycleOwner) {
            //Log.d("valStudent", it.student)
            val newValueStudent = dropElementList(getStudentIds(it.student), studentId)
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

    private fun dropElementList(arrayList: List<Int>, el: Int): String {
        val elementList = mutableListOf<Int>()
        for(item in arrayList) {
            if(item != el){
                elementList.add(item)
            }

        }
        return elementList.toString()
    }

    private fun getStudentIds(dataString: String): List<Int> {
        var dataStr = dataString.replace("]", "")
        dataStr = dataStr.replace("[", "")
        return dataStr.split(",").map { it.trim().toInt() }
    }


    interface OnEditingFinishedListener {

        fun onEditingFinished()

    }


    companion object {

        const val SCREEN_MODE = "extra_mode"
        const val SHOP_ITEM_ID = "extra_shop_item_id"
        const val MODE_EDIT = "mode_edit"
        const val MODE_UNKNOWN = ""

    }
}

