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
import com.example.lessonslist.presentation.helpers.PhoneTextFormatter
import com.example.lessonslist.presentation.payment.PaymentItemListFragment
import com.example.lessonslist.presentation.payment.PaymentItemViewModel
import com.example.lessonslist.presentation.payment.PaymentListViewModel
import com.example.lessonslist.presentation.student.notes.DataNotesStudentModel
import com.example.lessonslist.presentation.student.notes.ListNotesAdapter
import com.example.lessonslist.presentation.student.parentContact.DataParentContactStudentModel
import com.example.lessonslist.presentation.student.parentContact.ListParentContactAdapter
import com.squareup.picasso.Picasso
import java.io.*
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
    private lateinit var mButton: Button

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

        //viewModel.studentItem.
        dataPaymentStudentModel = ArrayList<DataPaymentStudentModel>()
        listView = binding.listView
        val args = requireArguments()
        val mode = args.getString(SCREEN_MODE)
            viewModelPayment = ViewModelProvider(this)[PaymentListViewModel::class.java]
            viewModelPayment.paymentList.observe(viewLifecycleOwner) {
                if(it.size > 0) {
                    for (payment in it) {
                        if(payment.studentId == studentItemId) {
                            //dataStudentGroupModel!!.add(DataStudentGroupModel(name, id,true))
                            if (payment.enabled == true) {
                                dataPaymentStudentModel!!.add(DataPaymentStudentModel("Оплачен: " + payment.title, payment.price.toString()))
                            } else {
                                dataPaymentStudentModel!!.add(DataPaymentStudentModel("Долг: " + payment.title, "-" + payment.price.toString()))
                            }

                            Log.d("nowmodeadd", "aaa" + payment.title + " " + payment.price)
                        }
                    }
                }
/*                adapter = ListStudentAdapter(dataStudentGroupModel!!, requireContext().applicationContext)

                listView.adapter = adapter*/
                val adapter =  ListPaymentAdapter(dataPaymentStudentModel!!, requireContext().applicationContext)
                listView.adapter = adapter

            }

/*           binding.paymentStudent.setOnClickListener {
                launchFragment(PaymentItemListFragment.newInstanceStudentId(studentItemId))
            }
 */

        listViewNotes = binding.listViewNotes
        dataNotesStudentModel = ArrayList<DataNotesStudentModel>()
        viewModelNotesItem = ViewModelProvider(this)[NotesItemViewModel::class.java]
        viewModelNotesItem.notesList.getNotesList().observe(viewLifecycleOwner) {

            for (item in it) {
                if(item.student == studentItemId) {
                    dataNotesStudentModel!!.add(DataNotesStudentModel(item.text, item.date))
                    Log.d("notes current student", item.text + item.date)
                 //   Toast.makeText(activity, "notes current student" + item.text + item.date, Toast.LENGTH_LONG).show()
                }
            }

            val adapterNotes = ListNotesAdapter(dataNotesStudentModel!!, requireContext().applicationContext)

            listViewNotes.adapter = adapterNotes

        }

        listViewParentContact = binding.listViewParentContact
        viewModelParentContact = ViewModelProvider(this)[ParentContactViewModel::class.java]
        dataParentContactStudentModel = ArrayList<DataParentContactStudentModel>()
        viewModelParentContact.parentContactList.getParentList().observe(viewLifecycleOwner) {
            for (item in it) {

                if (item.student == studentItemId){
                    dataParentContactStudentModel!!.add(DataParentContactStudentModel(item.name, item.number))
                }

            }

            val adapterParentContact = ListParentContactAdapter(dataParentContactStudentModel!!, requireContext().applicationContext)
            listViewParentContact.adapter = adapterParentContact


        }

       listViewParentContact.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position)
           call(dataParentContactStudentModel?.get(position)?.phone)
           Toast.makeText(getActivity(), "item click^" + dataParentContactStudentModel?.get(position)?.phone.toString(), Toast.LENGTH_LONG).show()
        }



/* */

      /*  listViewParentContact.setOnClickListener {
            Toast.makeText(getActivity(), "item click^" + it.toString(), Toast.LENGTH_LONG).show()
        }*/

        mImageView = binding.imageView

        mImageView.setOnClickListener {
            actionChangeImage()
        }


        viewModel.studentItem.observe(viewLifecycleOwner) {
                if(it.image != "") {
                    myHandler.post {
                        val file = File(it.image)
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
            }




    }

    fun call(number: String?) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + number)
        startActivity(dialIntent)
    }



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
                        viewModel.editPaymentBalance(it.id, (it.paymentBalance + newBalance).toFloat())
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
    private fun actionPaymentStudent() {
        TODO()
    }
    private fun actionGetLessons() {
        TODO()
    }
    private fun actionGetGroup() {
        TODO()
    }
    private fun actionAddContactparent() {
        TODO()
    }

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
    }


    private fun addNotesStudent() {

        val calendarTimeZone: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val currentYear = calendarTimeZone[Calendar.YEAR]
        val currentMonth = calendarTimeZone[Calendar.MONTH]
        val currentDay = calendarTimeZone[Calendar.DAY_OF_MONTH]
        val currentHour = calendarTimeZone[Calendar.HOUR]
        val currentMinute = calendarTimeZone[Calendar.MINUTE]




        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Добавить заметку")
        //alert.setMessage("Enter phone details and amount to buy airtime.")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL


        val mobileNoET = EditText(requireContext())
        mobileNoET.setSingleLine()
        mobileNoET.hint = "Текст заметки"
        layout.addView(mobileNoET)

        val amountET = EditText(requireContext())
        amountET.setSingleLine()
        amountET.hint = "дата" + currentDate
        layout.addView(amountET)

        layout.setPadding(50, 40, 50, 10)

        alert.setView(layout)

        alert.setPositiveButton("Добавить") { _, _ ->
            val mobileNo = mobileNoET.text.toString()
            val amount = amountET.text.toString()

            Log.i("xxx",mobileNo )
            Log.i("xxx",amount )

            Toast.makeText(activity, "Saved Sucessfully", Toast.LENGTH_LONG).show()
            viewModelNotesItem.addNotesItem(mobileNo, amount, studentItemId)
        }

        alert.setNegativeButton("отмена") { dialog, _ ->
            dialog.dismiss()
        }

        alert.setCancelable(false)
        alert.show()
    }

    fun expopupshow() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Buy Airtime")
        alert.setMessage("Enter phone details and amount to buy airtime.")
    
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
    
        val mobileNoET = EditText(requireContext())
        mobileNoET.setSingleLine()
        mobileNoET.hint = "Mobile Number"
        layout.addView(mobileNoET)
    
        val amountET = EditText(requireContext())
        amountET.setSingleLine()
        amountET.hint = "Amount"
        layout.addView(amountET)
    
        val networkET = EditText(requireContext())
        networkET.setSingleLine()
        networkET.hint = "Network"
        layout.addView(networkET)
    
        layout.setPadding(50, 40, 50, 10)
    
        alert.setView(layout)
    
        alert.setPositiveButton("Proceed") { _, _ ->
            val mobileNo = mobileNoET.text.toString()
            val amount = amountET.text.toString()
            val network = networkET.text.toString()
    
            Log.i("xxx",mobileNo )
            Log.i("xxx",amount )
            Log.i("xxx",network )
    
            Toast.makeText(activity, "Saved Sucessfully", Toast.LENGTH_LONG).show()
        }
    
        alert.setNegativeButton("Cancel") { dialog, _ ->
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
    // Function to establish connection and load image
    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show()
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

    private fun payOffDebtsAll(studentId: Int, studentBalance: Int): Int {
        var summPaymentDolg: ArrayList<Int> = ArrayList()
        /// if(dataPaymentStudentModel != null) {
        viewModel = ViewModelProvider(this)[StudentItemViewModel::class.java]
        //viewModelPayment = ViewModelProvider(this)[PaymentListViewModel::class.java]
        viewModelPaymentItem = ViewModelProvider(this)[PaymentItemViewModel::class.java]
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            if(it.size > 0) {
                for (payment in it) {
                    if(!payment.enabled && studentId == payment.studentId) {
                       // Log.d("tagView:", studentItemId.toString())
                        if(studentBalance >= payment.price) {
                            viewModelPaymentItem.getPaymentItem(payment.id)
                            viewModelPaymentItem.editPaymentItem(payment.title, payment.description, payment.lessonsId.toString(),
                                payment.studentId.toString(), payment.datePayment, payment.student, (payment.price + payment.price).toString(), true)

                            //(inputTitle: String, inputDescription: String, inputLessonsId: String, inputStudentId: String,
                            // inputDatePayment: String, inputStudent: String, inputPrice: String, enabledPayment: Boolean)

                            val balance = studentBalance + payment.price
                            Toast.makeText(getActivity(), "new balance!" + balance.toString(), Toast.LENGTH_SHORT).show();
                            viewModel.editPaymentBalance(payment.studentId, balance.toFloat())
                            Toast.makeText(getActivity(), "paymentBalance!" + (payment.studentId + payment.price.toFloat()).toString(), Toast.LENGTH_SHORT).show();
//idPaymnet: Int, inputTitle: String, inputDescription: String, inputLessonsId: String, inputStudentId: String, inputDatePayment: String, inputStudent: String, inputPrice: String, enabledPayment: Boolean
                            summPaymentDolg.add(payment.price)
                            //dolgPay.add(payment.id.toString() + " " + payment.title + ' ' + payment.price + ' ' + payment.enabled)
                        } else {
                            Toast.makeText(getActivity(), "На оплату оставшихся долгов не хватает средств!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }



        }

        return summPaymentDolg.sum()
    }

    private fun alertDialogSetMove(studentBalance: Int) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Сумма баланса позволяет списать все долги студента.")
            .setPositiveButton("Списать долги") { _, _ ->
                //Toast.makeText(getActivity(), "Долги в количестве " + payOffDebtsAll().toString() + " были списаны и неолаченных платежей нет.", Toast.LENGTH_SHORT).show();
              // payOffDebtsAll(studentBalance)
            }
            .setNegativeButton("Отмена", null)
            .create()
        dialog.show()
    }

    private fun sumOffDebts(): Int {
        var summPaymentDolg: ArrayList<Int> = ArrayList()
        /// if(dataPaymentStudentModel != null) {
        viewModelPayment.paymentList.observe(viewLifecycleOwner) {
            if(it.size > 0) {
                for (payment in it) {
                    if(!payment.enabled && studentItemId == payment.studentId) {
                        summPaymentDolg.add(payment.price)
                    }
                }
            }


            Toast.makeText(getActivity(), "obsii summa dolga!" + summPaymentDolg.sum().toString(), Toast.LENGTH_SHORT).show();

        }
        return summPaymentDolg?.sum()
    }






    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(com.example.lessonslist.R.id.fragment_item_container, fragment)
            .addToBackStack(null)
            .commit()
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


    private fun launchEditMode() {
        viewModel.getStudentItem(studentItemId)
        /**/binding.saveButton.setOnClickListener {
            viewModel.editStudentItem(
                binding.etName.text?.toString(),
                binding.etLastname.text?.toString(),
                binding.textViewPaymentBalance.text.toString(),
                " ",
                " ",
                pathImageSrc,
                " "
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

        private const val SCREEN_MODE = "extra_mode"
        private const val SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""



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

