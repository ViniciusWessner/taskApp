package com.example.taskapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.taskapp.R
import com.example.taskapp.databinding.FragmentRecoverAccountBinding
import com.example.taskapp.util.initToolbar
import com.example.taskapp.util.showButtomSheet


class RecoverAccountFragment : Fragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        initListeners()
    }

    private fun initListeners(){
        binding.btnRecover.setOnClickListener{
            validadeData()
            //findNavController().navigate(R.id.action_global_homeFragment3)
        }
    }

    private fun validadeData(){
        val email = binding.editEmail.text.toString().trim()

        if (email.isNotEmpty()){
            Toast.makeText(requireContext(), "Link de redefinicao encaminhado", Toast.LENGTH_SHORT).show()
        }else{
            showButtomSheet(massage = getString(R.string.fragment_account_massage_warning))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}