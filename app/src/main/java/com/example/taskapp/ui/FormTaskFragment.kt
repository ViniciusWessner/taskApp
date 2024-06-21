package com.example.taskapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.taskapp.R
import com.example.taskapp.databinding.FragmentFormTaskBinding
import com.example.taskapp.databinding.FragmentRecoverAccountBinding
import com.example.taskapp.util.initToolbar
import com.example.taskapp.util.showButtomSheet

class FormTaskFragment : Fragment() {

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        initListeners()
    }


    private fun initListeners(){
        binding.btnsave.setOnClickListener{
            validadeData()
            //findNavController().navigate(R.id.action_global_homeFragment3)
        }
    }

    private fun validadeData(){
        val description = binding.editDescription.text.toString().trim()


        if (description.isNotEmpty()){
            Toast.makeText(requireContext(), "tudo certo", Toast.LENGTH_SHORT).show()
        }else{
            showButtomSheet(massage = getString(R.string.fragment_form_task_massage))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}