package com.janoff.hackathonproject

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.janoff.hackathonproject.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var idToken: String = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.txtGoToRegister.setOnClickListener {
            val action =
                LoginFragmentDirections.actionLoginFragmentToMapsActivity()
            navigateFragment(action)
        }


        binding.btnLoginWithPhone.setOnClickListener {
            val action =
                LoginFragmentDirections.actionLoginFragmentToMapsActivity()
            navigateFragment(action)
        }

        binding.login.setOnClickListener {

            Log.d("LoginButton", "onCreate: ")
            when {
                TextUtils.isEmpty(binding.loginEmail.text.toString().trim() { it <= ' ' }) -> {
                    Snackbar.make(
                        requireActivity().findViewById<ConstraintLayout>(R.id.main_cLayout), "Please enter email.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(binding.loginPassword.text.toString().trim() { it <= ' ' }) -> {
                    Snackbar.make(
                        requireActivity().findViewById<ConstraintLayout>(R.id.main_cLayout), "Please enter password.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    val email: String = binding.loginEmail.text.toString().trim() { it <= ' ' }
                    val password: String =
                        binding.loginPassword.text.toString().trim() { it <= ' ' }

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->

                                if (task.isSuccessful) {

                                    Snackbar.make(
                                        binding.loginLayout, "You were registered successfully",
                                        Snackbar.LENGTH_SHORT
                                    ).show()

                                    val firebaseUser = task.result?.user

                                    navigateFragmentWithToken(getToken(firebaseUser!!))

                                } else {
                                    Snackbar.make(
                                        binding.loginLayout,
                                        task.exception!!.message.toString(),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                }

            }


        }


        return binding.root
    }



    fun navigateFragment(action: NavDirections){
        findNavController().popBackStack(R.id.loginFragment, true)
        findNavController().navigate(action)
    }

    fun navigateFragmentWithToken(idToken: String){

        val bundle = Bundle()
        bundle.putString("idToken", idToken)

        findNavController().popBackStack(R.id.loginFragment, true)
    }

    private fun getToken(user: FirebaseUser): String{
        user.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    idToken = task.result?.token.toString()
                } else {
                    // Handle error -> task.getException();
                }
            }
        return idToken
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}