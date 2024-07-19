package com.example.taskapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskapp.R
import com.example.taskapp.data.Model.Status
import com.example.taskapp.data.Model.Task
import com.example.taskapp.databinding.FragmentHomeBinding
import com.example.taskapp.databinding.FragmentTodoBinding
import com.example.taskapp.ui.adapter.TaskAdapter
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

    private lateinit var reference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init firebase
        reference = Firebase.database.reference
        auth = Firebase.auth

        initListener()
        initReciclerViewTasks()
        getTasks()

    }

    private fun initListener(){
        binding.fabAdd.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_formTaskFragment)
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
                Toast.makeText(requireContext(), "Editando tarefa: ${task.description}", Toast.LENGTH_SHORT).show()

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
        reference
            .child("tasks")
            .child(auth.currentUser?.uid ?: "")
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
        reference
            .child("tasks")
            .child(auth.currentUser?.uid ?: "")
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