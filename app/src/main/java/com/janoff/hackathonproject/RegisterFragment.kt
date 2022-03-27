package com.janoff.hackathonproject
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.janoff.hackathonproject.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {



    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var idToken: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)


        binding.register.setOnClickListener {

            when{
                TextUtils.isEmpty(binding.registerEmail.text.toString().trim(){it<=' '}) -> {
                    Snackbar.make(binding.registerLayout, "Please enter email.",
                        Snackbar.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.registerPassword.text.toString().trim(){it<=' '}) -> {
                    Snackbar.make(binding.registerLayout, "Please enter password.",
                        Snackbar.LENGTH_SHORT).show()
                }
                else -> {

                    val email: String = binding.registerEmail.text.toString().trim(){ it <= ' '}
                    val password: String = binding.registerPassword.text.toString().trim(){ it <= ' '}

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->

                                if (task.isSuccessful){

                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    Snackbar.make(binding.registerLayout, "You were registered successfully",
                                        Snackbar.LENGTH_SHORT).show()


                                    navigateFragmentWithToken(getToken(firebaseUser!!))

                                } else{
                                    Snackbar.make(binding.registerLayout,
                                        task.exception!!.message.toString(),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                }

            }

        }

        binding.txtGoToLogin.setOnClickListener {
            val action =
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            navigateFragment(action)
        }

        binding.btnRegisterWithPhone.setOnClickListener {
            val action =
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            navigateFragment(action)
        }
        return binding.root
    }


    fun navigateFragment(action: NavDirections){
        findNavController().popBackStack(R.id.registerFragment, true)
        findNavController().navigate(action,    )
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