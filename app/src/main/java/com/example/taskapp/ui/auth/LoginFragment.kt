package com.example.taskapp.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.taskapp.R
import com.example.taskapp.databinding.FragmentLoginBinding
import com.example.taskapp.databinding.FragmentRegisterBinding
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.showButtomSheet
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        initListeners()
    }

    private fun initListeners(){
        binding.btnLogin.setOnClickListener{
            validadeData()
            //findNavController().navigate(R.id.action_global_homeFragment3)
        }

        binding.btnRegister.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnRecover.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_recoverAccountFragment)
        }
    }

    private fun validadeData(){
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if (email.isNotEmpty()){
            if (password.isNotEmpty()){
                login(email, password)
            }else{
                showButtomSheet(massage = getString(R.string.password_empty))
            }
        }else{
            showButtomSheet(massage = getString(R.string.email_empty))
        }
    }

    private fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        binding.progressBar.isVisible = true
                        findNavController().navigate(R.id.action_global_homeFragment3)
                    } else {
                        binding.progressBar.isVisible = false
                        Log.d("FIREBASE", "Erro de login: ${task.exception?.message}")
                        showButtomSheet(
                            massage = getString(FirebaseHelper.validError(task.exception?.message.toString()))
                        )
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}