package com.example.taskapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taskapp.R
import com.example.taskapp.data.Model.Status
import com.example.taskapp.databinding.FragmentFormTaskBinding
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.initToolbar
import com.example.taskapp.util.showButtomSheet

class FormTaskFragment : Fragment() {

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: com.example.taskapp.data.Model.Task
    private var status: Status = Status.TODO
    private var newTask: Boolean = true

    private val args: FormTaskFragmentArgs by navArgs()
    private val viewModel:TaskViewModel by activityViewModels()



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

        getArgs()
        initListeners()
    }

    private fun getArgs(){
        args.task.let {
            if (it != null){
                this.task = it
                configTask()
            }
        }
    }

    private fun initListeners(){
        binding.btnsave.setOnClickListener{
            validadeData()
            //findNavController().navigate(R.id.action_global_homeFragment3)
        }
        binding.rgStatus.setOnCheckedChangeListener{_, id ->
            status = when(id) {
                R.id.rbTodo -> Status.TODO
                R.id.rbDoing -> Status.DOING
                else -> Status.DONE
            }
        }
    }

    private fun configTask(){
        newTask = false
        status = task.status
        binding.textToolbar.setText("Editando tarefa")
        binding.editDescription.setText(task.description)
        setStatus()
    }

    private fun setStatus(){
        binding.rgStatus.check(when(task.status){
            Status.TODO -> R.id.rbTodo
            Status.DOING -> R.id.rbDoing
            else -> R.id.rbDone
        })
    }

    private fun validadeData(){
        val description = binding.editDescription.text.toString().trim()


        if (description.isNotEmpty()){
            binding.progressBar.isVisible = true

            if (newTask) {
                task = com.example.taskapp.data.Model.Task()

            }
            task.description = description
            task.status = status

            saveTask()
        }else{
            showButtomSheet(massage = getString(R.string.fragment_form_task_massage))
        }
    }

    private fun saveTask(){
        FirebaseHelper.getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser())
            .child(task.id)
            .setValue(task).addOnCompleteListener { result ->
                if(result.isSuccessful){
                    Toast.makeText(requireContext(), R.string.return_Success_Create_Task, Toast.LENGTH_SHORT).show()
                    if(newTask){
                        findNavController().popBackStack()
                    } else {
                        viewModel.setUpdateTask(task)
                        binding.progressBar.isVisible = false
                    }
                }else{
                    showButtomSheet(massage = getString(R.string.return_Error_Create_Task))
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}