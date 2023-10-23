package com.example.lessonslist.presentation.student

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentStudentItemBinding
import com.example.lessonslist.domain.student.StudentItem
import com.example.lessonslist.presentation.helpers.PhoneTextFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import java.io.*
import java.lang.Thread.sleep
import java.util.concurrent.Executors


class StudentItemFragment : Fragment() {

    private lateinit var viewModel: StudentItemViewModel

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentStudentItemBinding? = null
    private val binding: FragmentStudentItemBinding
        get() = _binding ?: throw RuntimeException("FragmentStudentItemBinding == null")


    private var screenMode: String = MODE_UNKNOWN
    private var studentItemId: Int = StudentItem.UNDEFINED_ID


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
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
            _binding = FragmentStudentItemBinding.inflate(inflater, container, false)
            return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Добавить ученика"
        viewModel = ViewModelProvider(this)[StudentItemViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        addTextChangeListeners()
        launchRightMode()
        observeViewModel()



        mImageView = binding.imageView

        mImageView.setOnClickListener {
            actionChangeImage()
        }

        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNavigationView.menu.findItem(R.id.bottomItem5).isChecked = true
      //  binding.etPaymentBalance.addTextChangedListener(PhoneTextFormatter(binding.etPaymentBalance, "### ### ### ### ### ### ### ###"))
        binding.etTelephone.addTextChangedListener(PhoneTextFormatter(binding.etTelephone, "+7 (###) ###-####"))

     /*   binding.etTelephone.addTextChangedListener(object : TextWatcher {
            var length_before = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                length_before = s.length
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (length_before < s.length) {
                    if (s.length == 3 || s.length == 7) s.append("-")
                    if (s.length > 3) {
                        if (Character.isDigit(s[3])) s.insert(3, "-")
                    }
                    if (s.length > 7) {
                        if (Character.isDigit(s[7])) s.insert(7, "-")
                    }
                }
            }
        })*/


    }




    private fun actionChangeImage() {
        myExecutor.execute {
            getImageLocal()
        }
    }

    fun getImageLocal() {
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        chosenImageUri = data.data!!

                        var mImage: Bitmap?
                        mImage = mLoadLocal(chosenImageUri.toString())
                        myHandler.post {

                            Picasso.get()
                                .load(chosenImageUri.toString())
                                .resize(400, 300)
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

    private fun mLoadLocal(string: String): Bitmap? {
        try {
            val inputStream: InputStream? = activity?.applicationContext?.getContentResolver()?.openInputStream(string.toUri())
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    private fun mSaveMediaToStorage(bitmap: Bitmap?): File {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null

        val imagesDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + "lessonslist")
        imagesDir.apply {
            if (!this.exists()) this.mkdir()
        }

        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)

        fos.use {

            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)


        }
        return image
    }

    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_item_container, fragment)
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
            MODE_ADD  -> launchAddMode()
        }
    }

    private fun addTextChangeListeners() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.resetErrorInputName()
                    setHideErrorInput()
               }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.etLastname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputLastName()
                setHideErrorInput()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.etPaymentBalance.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputPaymentBalance()
                setHideErrorInput()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.etTelephone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputPhone()
                setHideErrorInput()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun launchEditMode() {
        viewModel.getStudentItem(studentItemId)
        binding.saveButton.setOnClickListener {
            viewModel.editStudentItem(
                binding.etName.text?.toString(),
                binding.etLastname.text?.toString(),
                binding.etPaymentBalance.text.toString(),
                "",
                "",
                pathImageSrc,
                binding.etTelephone.text.toString()

            )
        }
    }

    private fun launchAddMode() {
        binding.saveButton.setOnClickListener {
            if (viewModel.validateInput(binding.etName.text.toString(),  binding.etPaymentBalance.text.toString())) {
                checkExistsStudent(binding.etName.text?.toString()!!.trim(), binding.etLastname.text?.toString()!!.trim())
                viewModel.existsStudent.observe(viewLifecycleOwner) {
                    if (it == null) {
                        viewModel.addStudentItem(
                            binding.etName.text?.toString(),
                            binding.etLastname.text?.toString(),
                            binding.etPaymentBalance.text.toString(),
                            "",
                            "",
                            inputImage = pathImageSrc ?: " ",
                            binding.etTelephone.text.toString()
                        )
                    } else {
                        Toast.makeText(activity, "Ученик с таким именем и фамилией уже существует.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                setHideErrorInput()
            }
        }
    }

    private fun checkExistsStudent(name: String, lastName: String) {
        return viewModel.checkExistsStudent(name, lastName)
    }


    private fun setHideErrorInput() {
        if(viewModel.errorInputName.value == true) {
            binding.tilName.error = "Проверьте правиальность введенного имени"
        } else {
            binding.tilName.error = ""
        }
        if(viewModel.errorInputLastName.value == true) {
            binding.tilLastname.error = "Проверьте правиальность введенной фамилии"
        } else {
            binding.tilLastname.error = ""
        }
        if(viewModel.errorInputPaymentBalance.value == true) {
            binding.tilPaymentBalance.error = "Неправильно введен платежный баланс"
        } else {
            binding.tilPaymentBalance.error = ""
        }
        if(viewModel.errorInputPhone.value == true) {
            binding.tilTelephone.error = "Неправильно введен телефон"
        } else {
            binding.tilTelephone.error = ""
        }
    }

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
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
        const val MODE_ADD = "mode_add"
        const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): StudentItemFragment {
            return StudentItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): StudentItemFragment {
            return StudentItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }
}
