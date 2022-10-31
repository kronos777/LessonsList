package com.example.lessonslist.presentation.authuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.lessonslist.R
import com.example.lessonslist.databinding.FragmentLoginBinding
import com.example.lessonslist.presentation.calendar.CalendarItemFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment: Fragment() {

    private lateinit var onEditingFinishedListener: CalendarItemFragment.OnEditingFinishedListener

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding ?: throw RuntimeException("FragmentSigninBinding == null")
    private lateinit var auth: FirebaseAuth

    //private lateinit var user: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

       (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar).title = "Войти"
       (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.nav_view_bottom).visibility = View.GONE
       (activity as AppCompatActivity).findViewById<NavigationView>(R.id.navView).visibility = View.GONE

        binding.linkRegisterFragment.setOnClickListener {
            launchFragment(SignInFragment())
        }

        binding.enterButton.setOnClickListener {
            email = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()

            login(email, password)
           // Toast.makeText(getActivity(), "почта" + email +  "пароль" + password,  Toast.LENGTH_SHORT).show()

        }

    }



    private fun launchFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.popBackStack()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_item_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun login(email: String, password: String) {
        val mail = email
        val pass = password
        // calling signInWithEmailAndPassword(email, pass) "i.ziborov2018@yandex.ru" "123456"
        // function using Firebase auth object
        // On successful response Display a Toast
        activity?.let {
            auth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(it) {
                if (it.isSuccessful) {
                    val user = Firebase.auth.currentUser
                    Toast.makeText(activity, "Successfully LoggedIn your user id " + user?.uid.toString() + "user mail " + user?.email.toString(), Toast.LENGTH_SHORT).show()
                    //    binding.textView2.text = "Successfully login"
                    launchFragment(CalendarItemFragment())
                } else
                    Toast.makeText(activity, "Log In failed ", Toast.LENGTH_SHORT).show()
            }
        }
    }

}