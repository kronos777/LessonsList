package com.example.lessonslist.presentation.student

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.InputType.TYPE_CLASS_NUMBER
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentStudentItemEditBinding
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.helpers.BottomFragment
import com.example.lessonslist.presentation.helpers.PhoneTextFormatter
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentItemViewModel
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.example.lessonslist.presentation.student.notes.DataNotesStudentModel
import com.example.lessonslist.presentation.student.parentContact.DataParentContactStudentModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import java.io.*
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class StudentItemEditFragment : Fragment() {

    private lateinit var viewModel: StudentItemViewModel
    private lateinit var viewModelPayment: PaymentListViewModel
    private lateinit var viewModelPaymentItem: PaymentItemViewModel
    private lateinit var viewModelNotesItem: NotesItemViewModel
    private lateinit var viewModelParentContact: ParentContactViewModel

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentStudentItemEditBinding? = null
    private val binding: FragmentStudentItemEditBinding
        get() = _binding ?: throw RuntimeException("FragmentStudentItemEditBinding == null")


    private lateinit var listView: ListView
    private lateinit var listViewNotes: ListView
    private lateinit var listViewParentContact: ListView
    private var screenMode: String = MODE_UNKNOWN
    private var studentItemId: Int = StudentItem.UNDEFINED_ID
    private var dataPaymentStudentModel: ArrayList<DataPaymentStudentModel>? = null
    private var dataNotesStudentModel: ArrayList<DataNotesStudentModel>? = null
    private var dataParentContactStudentModel: ArrayList<DataParentContactStudentModel>? = null

    private lateinit var chosenImageUri: Uri


    private lateinit var mImageView: ImageView

    val myExecutor = Executors.newSingleThreadExecutor()
    val myHandler = Handler(Looper.getMainLooper())

    private var pathImageSrc: String = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
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
      //  observeViewModel()
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem5).isChecked = true
        //viewModel.studentItem.

        viewModel.studentItem.observe(viewLifecycleOwner) { stItem ->
            val payBalance = stItem.paymentBalance
            if (stItem.paymentBalance <= 0) {
                //Toast.makeText(getActivity(), "it.paymentBalance count^" + it.paymentBalance, Toast.LENGTH_LONG).show()
                totalDebt()
            }
         /*   binding.cardPaymentOff.setOnClickListener {
                if(payBalance <= 0) {
                    Toast.makeText(getActivity(), "Пополните баланс и сможете списать долг.", Toast.LENGTH_LONG).show()
                } else {
                    payOffDebtsAll(studentItemId, payBalance)
                }
            }*/

            if(stItem.image.isNotBlank()) {
                myHandler.post {
                    val file = File(stItem.image)
                    //    Log.d("imageTag", it.image)
                    Picasso.get()
                        .load(file)
                        .resize(200, 200)
                        .rotate(90f)
                        .into(mImageView)
                    pathImageSrc = file.toString()
                    /*Picasso.get()
                        .load(it.image)
                        .resize(400, 300)
                        // .transform(CropCircleTransformation())
                        .rotate(90f)
                        .into(mImageView)*/
                }
            }

            val textTelephone = stItem.telephone.toString()
               // binding.textViewTelephone.text.toString()
            binding.textViewTelephone.setOnClickListener {
                //Toast.makeText(getActivity(),"phone number"+stItem.telephone.toString(),Toast.LENGTH_SHORT).show();
                if(textTelephone.count() > 0) {
                       askEditNumber()
                   } else {
                       addPhoneNumber()
                   }

                binding.textViewTelephone.text = stItem.telephone
            }

        }
/*           binding.paymentStudent.setOnClickListener {
                launchFragment(PaymentItemListFragment.newInstanceStudentId(studentItemId))
            }
 */





/* */

      /*  listViewParentContact.setOnClickListener {
            Toast.makeText(getActivity(), "item click^" + it.toString(), Toast.LENGTH_LONG).show()
        }*/



        mImageView = binding.imageView

        mImageView.setOnClickListener {
            actionChangeImage()
        }

        binding.cardAddBalance.setOnClickListener {
            actionAddMoney()
        }

        binding.cardAddNotes.setOnClickListener {
            getActivity()?.let { BottomFragment.newInstanceNotesStudent(studentItemId).show(it.supportFragmentManager, "tag") }
        }

       binding.cardPaymentStudent.setOnClickListener {
                getActivity()?.let { BottomFragment.newInstancePaymentBalance(studentItemId).show(it.supportFragmentManager, "tag") }
       }

        binding.cardGroupStudent.setOnClickListener {
            getActivity()?.let { BottomFragment.newInstanceGroupStudent(studentItemId).show(it.supportFragmentManager, "tag") }
        }

       binding.cardParentContact.setOnClickListener {
           getActivity()?.let { BottomFragment.newInstanceParentsContacts(studentItemId).show(it.supportFragmentManager, "tag") }
       }

    }


    override fun onPause() {
        super.onPause()
        Toast.makeText(activity, "fragment in pause", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(activity, "fragment resume", Toast.LENGTH_SHORT).show()
    }

    private fun askEditNumber() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Телефон студента содержит значение, желаете его изменить ?")
        //alert.setMessage("Enter phone details and amount to buy airtime.")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("Добавить") { _, _ ->
            addPhoneNumber()
        }

        alert.setNegativeButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(false)
        alert.show()
    }

   /*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.student_menu_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id) {
            R.id.action_add_money -> actionAddMoney()
            R.id.action_change_image -> actionChangeImage()
            R.id.action_payment_student -> launchFragment(PaymentItemListFragment.newInstanceStudentId(studentItemId))
            R.id.action_get_lessons -> Toast.makeText(getActivity(), "action_get_lessons.", Toast.LENGTH_LONG).show()
            R.id.action_get_group -> Toast.makeText(getActivity(), "action_get_group.", Toast.LENGTH_LONG).show()
            R.id.action_add_contact_parent -> addParentContactStudent()
            R.id.action_add_notes_student -> addNotesStudent()
        }

        return super.onOptionsItemSelected(item)

    }
*/

    private fun actionAddMoney() {
        var newBalance: Int
        val inputEditTextField = EditText(requireActivity())
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Пополнить баланс студента.")
            //.setMessage("Message")
            .setView(inputEditTextField)
            .setPositiveButton("OK") { _, _ ->
                val editTextInput = inputEditTextField.text.toString()
                Log.d("editext value is:", editTextInput)

                if(isNumeric(editTextInput)){
                    Toast.makeText(getActivity(),"Строка число можно сохранять.",Toast.LENGTH_SHORT).show();
                    newBalance = editTextInput.toInt()
                    viewModel.studentItem.observe(viewLifecycleOwner) {
                        //Log.d("new balance", it.paymentBalance.toString())
                        viewModel.editPaymentBalance(it.id, (it.paymentBalance + newBalance))
                        if(it.paymentBalance < 0) {
                            Toast.makeText(getActivity(),"payment balance"+(it.paymentBalance).toString(),Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getActivity(),"new balance!"+(it.paymentBalance + newBalance).toString(),Toast.LENGTH_SHORT).show();
                        binding.textViewPaymentBalance.setText((it.paymentBalance + newBalance).toString())
                        /*     if(it.paymentBalance > sumOffDebts()) {
                                 alertDialogSetMove(it.paymentBalance + newBalance)
                             }*/
                    }
                } else {
                    Toast.makeText(getActivity(),"Строка не является числом, сохранить невозможно.",Toast.LENGTH_SHORT).show();
                }


            }
            .setNegativeButton("Отмена", null)
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
            if(it.size > 0) {
                for (payment in it) {
                    if(payment.studentId == studentItemId) {
                        //dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,true))
                        if (payment.enabled == false) {
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
            Toast.makeText(activity, "Saved Sucessfully", Toast.LENGTH_LONG).show()
            viewModel.editPhoneNumber(studentItemId, number)
            sleep(100)
            viewModel.studentItem.observe(viewLifecycleOwner) {
                binding.textViewTelephone.text = it.telephone
            }
        }

        alert.setNegativeButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(false)
        alert.show()
    }

    fun updatePaymentBalance(money: Int) {
        binding.textViewPaymentBalance.text = money.toString()
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
    }
    // Function to convert string to URL
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }



    private fun mLoadLocal(string: String): Bitmap? {
    //private fun mLoadLocal(string: String): Bitmap? {
        try {
            val inputStream: InputStream? = activity?.applicationContext?.getContentResolver()?.openInputStream(string.toUri())
            Toast.makeText(getActivity(), "OK BitmapFactory object", Toast.LENGTH_LONG).show()
            return BitmapFactory.decodeStream(inputStream)
            //return BitmapFactory.decodeFile(string)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(getActivity(), "Error BitmapFactory", Toast.LENGTH_LONG).show()
        }
        return null
    }


    private fun mSaveMediaToStorage(bitmap: Bitmap?): File {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }*/
        val imagesDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + "lessonslist")
        imagesDir.apply {
            if (!this.exists()) this.mkdir()
        }
        //val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)

        fos.use {

            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(getActivity() , "Saved to Gallery", Toast.LENGTH_SHORT).show()

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
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_EDIT) {
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            studentItemId = args.getInt(SHOP_ITEM_ID, StudentItem.UNDEFINED_ID)

        }
    }

    interface OnEditingFinishedListener {

        fun onEditingFinished()

    }


    companion object {

        const val SCREEN_MODE = "extra_mode"
        const val SHOP_ITEM_ID = "extra_shop_item_id"
        const val MODE_EDIT = "mode_edit"
        const val MODE_UNKNOWN = ""

        fun newInstanceEditItem(shopItemId: Int): StudentItemEditFragment {
            return StudentItemEditFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }

    }
}

