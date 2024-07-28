package com.example.taskapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskapp.R
import com.example.taskapp.data.Model.Status
import com.example.taskapp.data.Model.Task
import com.example.taskapp.databinding.FragmentHomeBinding
import com.example.taskapp.databinding.FragmentTodoBinding
import com.example.taskapp.ui.adapter.TaskAdapter
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.showButtomSheet
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class TodoFragment : Fragment() {

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskAdapter

    private val viewModel:TaskViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initReciclerViewTasks()
        getTasks()

    }


    private fun initListener(){
        binding.fabAdd.setOnClickListener{
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormTaskFragment(null) //tarefa nova == null
            findNavController().navigate(action)
        }
        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.taskUpdate.observe(viewLifecycleOwner){updateTask ->
            if (updateTask.status == Status.TODO){
                val oldList = taskAdapter.currentList //lista atual

                val newList = oldList.toMutableList().apply{
                    find { it.id == updateTask.id }?.description = updateTask.description
                }

                val position = newList.indexOfFirst { it.id == updateTask.id }

                taskAdapter.submitList(newList)

                taskAdapter.notifyItemChanged(position)

            }
        }
    }

    private fun initReciclerViewTasks(){
        //inicializando adapter
        taskAdapter = TaskAdapter(requireContext()) { option, task ->
            optionSelected(option, task)
        }

        with(binding.rvTask) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = taskAdapter
        }
    }

    private fun optionSelected(task: Task, option: Int){
        when(option) {
            TaskAdapter.SELECT_REMOVE -> {
                showButtomSheet(
                    titleDialog = R.string.dialog_titile_Button_delete,
                    titleButton = R.string.dialog_message_title_delete,
                    massage = getString(R.string.dialog_message_button_delete),
                    onClick = {
                        deleteTask(task)
                    }
                )
            }
            TaskAdapter.SELECT_EDIT -> {

                //action_homeFragment_to_formTaskFragment
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_DETAILS -> {
                Toast.makeText(requireContext(), "Acessando detalhes da terefa: ${task.description}", Toast.LENGTH_SHORT).show()

            }
            TaskAdapter.SELECT_NEXT -> {
                Toast.makeText(requireContext(), "Avancando tarefa: ${task.description}", Toast.LENGTH_SHORT).show()

            }

        }
    }

    private fun getTasks() {
        FirebaseHelper.getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //qualquer alteracao o snapshot fica observando

                    val taskList = mutableListOf<Task>()

                    for (ds in snapshot.children){
                        val task = ds.getValue(Task::class.java) as Task
                        if (task.status == Status.TODO){
                            taskList.add(task)
                        }
                    }
                    listEmpty(taskList)
                    binding.progressBarList.isVisible = false

                    taskList.reverse()
                    taskAdapter.submitList(taskList)
                }

                override fun onCancelled(error: DatabaseError) {
                    //meto que vai ser chamado quando abrirmos o aplicativo e ele comecar uma busca,
                    // comecou e fechamos o app ele vai ser executado pois cancelamos a busca
                    Toast.makeText(requireContext(), R.string.return_Error_Create_Task, Toast.LENGTH_SHORT).show()

                }

            })
    }
    private fun deleteTask(taskId: Task){
        FirebaseHelper.getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser())
            .child(taskId.id)
            .removeValue().addOnCompleteListener {result ->
                if (result.isSuccessful){
                    Toast.makeText(requireContext(), R.string.return_task_delete, Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(requireContext(), R.string.return_Error_Create_Task, Toast.LENGTH_SHORT).show()
                }
            }

    }
    private fun listEmpty(taskList: List<Task>){
        binding.textInfo.text = if (taskList.isEmpty()){
            getString(R.string.return_empty_tasks)
        } else {
            ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}