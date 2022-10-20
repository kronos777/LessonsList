package com.example.lessonslist.presentation.authuser


import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentRegisterBinding
import com.example.lessonslist.domain.user.UserItem
import com.example.lessonslist.presentation.calendar.CalendarItemFragment
import com.example.lessonslist.presentation.helpers.FirebaseUtils
import com.example.lessonslist.presentation.helpers.PhoneTextFormatter
import com.example.lessonslist.presentation.helpers.ValidPassword
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.delay
import java.util.regex.Pattern


class SignInFragment() : Fragment(), CalendarItemFragment.OnEditingFinishedListener {


    private lateinit var onEditingFinishedListener: CalendarItemFragment.OnEditingFinishedListener

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding
        get() = _binding ?: throw RuntimeException("FragmentRegisterBinding == null")
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
   // private lateinit var ImageStore: DatabaseReference
    private lateinit var database: DatabaseReference

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CalendarItemFragment.OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       //parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        auth = FirebaseAuth.getInstance()

        database = Firebase.database.reference

        Toast.makeText(getActivity(), "user id " + auth.uid.toString(), Toast.LENGTH_SHORT).show()

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Регистрация"

        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.nav_view_bottom).visibility = View.GONE
        (activity as AppCompatActivity).findViewById<NavigationView>(R.id.navView).visibility = View.GONE
        (activity as AppCompatActivity).findViewById<View>(R.id.payment).visibility = View.GONE
        (activity as AppCompatActivity).findViewById<View>(R.id.backup).visibility = View.GONE

        binding.etPhone.addTextChangedListener(PhoneTextFormatter(binding.etPhone, "+7 (###) ###-####"))

        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()

        binding.linkLoginFragment.setOnClickListener {
            launchFragment(LoginFragment())
        }

        binding.saveButton.setOnClickListener {
           // signInUserClient()
            if(signInUserClientFieldError()) {
                signInUserClient()
            }
        }


     //   getDatabaseData()

    }

    private fun signInUserLawyer() {
        val name = binding.etName.text.toString()
        val sername = binding.etSername.text.toString()
        val email = binding.etEmail.text.toString()
        val phone = binding.etPhone.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etRepeatPassword.text.toString()
    }


    private fun getDatabaseData() {
        database.child("users").get().addOnSuccessListener {
            Log.d("firebase", "Got value ${it.value}")

        }.addOnFailureListener{
            Log.d("firebase", "Error getting data", it)
        }
    }

    private fun signInUserClient() {

        val name = binding.etName.text.toString()
        val sername = binding.etSername.text.toString()
        val email = binding.etEmail.text.toString()
        val phone = binding.etPhone.text.toString()
        val password = binding.etPassword.text.toString()


        getActivity()?.let {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(it) {
                    if (it.isSuccessful) {
                        val userCurrent = Firebase.auth.currentUser
                        userCurrent!!.sendEmailVerification()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val profile = UserItem(name, sername, phone, email, password, userCurrent.uid)
                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("Users").document(profile.id)
                                        .set(profile, SetOptions.merge())
                                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                                    launchFragment(CalendarItemFragment())
                                }
                            }
                        //  getActivity()?.finish()
                    } else {
                        Toast.makeText(getActivity(), "Регистрация прервана, ошибка регистрации.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }



    private fun signInUserClientFieldError(): Boolean {

        var result = true
        val name = binding.etName.text.toString()
        val sername = binding.etSername.text.toString()
        val email = binding.etEmail.text.toString()
        val phone = binding.etPhone.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etRepeatPassword.text.toString()

        if (name.isBlank()) {
              showError("Заполните поле имя", binding.tilName)
              result = false
        } else {
            hideError("", binding.tilName)
            result = true
        }

        if (sername.isBlank()) {
            showError("Заполните поле фамилия", binding.tilSername)
            result = false
        } else {
            hideError("", binding.tilSername)
            result = true
        }

        if(validateEmail(email)) {
            result = true
            hideError("", binding.tilEmail)
        } else {
            result = false
            showError("Проверьте правильность написания адреса почты.", binding.tilEmail)
        }


        if (phone.isBlank()) {
            showError("Заполните поле телефон", binding.tilPhone)
            result = false
        } else {
            hideError("", binding.tilPhone)
            result = true
        }

        if(!password.isBlank()) {
            val validatePasswd = ValidPassword().checkPassword(password, confirmPassword)
            if(validatePasswd == "ok") {
                hideError("", binding.tilPassword)
                hideError("", binding.tilRepeatPassword)
                result = true
            } else {
                showError(validatePasswd, binding.tilPassword)
                showError(validatePasswd, binding.tilRepeatPassword)
                result = false
            }
        } else {
            showError("Пароль не может быть пустым.", binding.tilPassword)
            result = false
        }

        val checkBox = binding.checkboxRememberMe
        if (checkBox.isChecked) {
            Toast.makeText(getActivity(), "Check.", Toast.LENGTH_SHORT).show()
            result = true
        } else {
            checkBox.text = "Пожалуйста, примите это!"
            checkBox.setTextColor(Color.parseColor("#FF0000"))
            result = false
        }

        return result
    }


    private fun validateEmail(email: String): Boolean  {

        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    private fun showError(error: String, element: TextInputLayout) {
            element.setError(error)
    }

    private fun hideError(error: String, element: TextInputLayout) {
            element.setError(error)
    }

    private fun launchFragment(fragment: Fragment) {
        // this.supportFragmentManager.popBackStack()
        getActivity()?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_item_container, fragment)
            ?.addToBackStack("registration")
            ?.commit()
    }


    private fun signUpUser() {
        val email = "i.ziborov2018@yandex.ru"
        val pass = "123456"
        val confirmPassword = "123456"

        // check pass
        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(getActivity(), "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPassword) {
            Toast.makeText(getActivity(), "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }
        // If all credential are correct
        // We call createUserWithEmailAndPassword
        // using auth object and pass the
        // email and pass in it.
        getActivity()?.let {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(it) {
                if (it.isSuccessful) {
                    //Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                    //   binding.textView2.text = "Successfully Singed Up"
                    getActivity()?.finish()
                } else {
                    //Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun createUserLawyerData() {

        // create a dummy data
        val hashMap = hashMapOf<String, Any>(
            "name" to "Chmel",
            "lastname" to "Lisisi",
            "gender" to "man",
            "id" to "iddata"
        )

        // use the add() method to create a document inside users collection
        FirebaseUtils().fireStoreDatabase.collection("users")
            .add(hashMap)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Added document with ID ${it.id}")
                //   binding.textView2.text = "Added document with ID ${it.id}"
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error adding document $exception")
            }

    }




    override fun onEditingFinished() {
        TODO("Not yet implemented")
    }

}