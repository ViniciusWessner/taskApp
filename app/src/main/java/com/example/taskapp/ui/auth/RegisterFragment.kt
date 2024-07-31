package com.example.taskapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.taskapp.R
import com.example.taskapp.databinding.FragmentRegisterBinding
import com.example.taskapp.ui.BaseFragment
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.initToolbar
import com.example.taskapp.util.showButtomSheet

class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        initListeners()
    }

    private fun initListeners(){
        binding.btnRegister.setOnClickListener{
            validadeData()
            //findNavController().navigate(R.id.action_global_homeFragment3)
        }
    }

    private fun validadeData(){
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if (email.isNotEmpty()){
            if (password.isNotEmpty()){
                hideKeyboard()
                binding.progressBar.isVisible = true
                registerUser(email, password)
            }else{
                showButtomSheet(massage = getString(R.string.password_empty))
            }
        }else{
            showButtomSheet(massage = getString(R.string.email_empty))
        }
    }

    private fun registerUser(email: String, password: String){
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_global_homeFragment3)
                } else {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}