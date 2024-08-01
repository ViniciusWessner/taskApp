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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskapp.R
import com.example.taskapp.data.Model.Status
import com.example.taskapp.data.Model.Task
import com.example.taskapp.databinding.FragmentTodoBinding
import com.example.taskapp.ui.adapter.TaskAdapter
import com.example.taskapp.util.showButtomSheet

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
        observeViewModel()

        viewModel.getTasks(Status.TODO)

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

        viewModel.taskList.observe(viewLifecycleOwner){taskList ->
            binding.progressBarList.isVisible = false
            listEmpty(taskList)

            taskAdapter.submitList(taskList)
        }

        viewModel.taskInsert.observe(viewLifecycleOwner){ task ->
            if (task.status == Status.TODO){
                val oldList = taskAdapter.currentList //lista atual

                val newList = oldList.toMutableList().apply{
                    add(0, task)
                }

                taskAdapter.submitList(newList)
            }
        }

        viewModel.taskUpdate.observe(viewLifecycleOwner){updateTask ->
            val oldList = taskAdapter.currentList //lista atual

            val newList = oldList.toMutableList().apply{
                if (updateTask.status == Status.TODO) {
                    find { it.id == updateTask.id }?.description = updateTask.description
                } else {
                    remove(updateTask)
                }
            }

            val position = newList.indexOfFirst { it.id == updateTask.id }

            taskAdapter.submitList(newList)

            taskAdapter.notifyItemChanged(position)
        }

        viewModel.taskDelete.observe(viewLifecycleOwner){task ->
            Toast.makeText(requireContext(), R.string.return_task_delete, Toast.LENGTH_SHORT).show()

            val oldList = taskAdapter.currentList
            val newList = oldList.toMutableList().apply {
                remove(task)
            }
            taskAdapter.submitList(newList)
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
                        viewModel.deleteTask(task)
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
                task.status = Status.DOING
                viewModel.updateTask(task)
                Toast.makeText(requireContext(), "Avancando tarefa: ${task.description}", Toast.LENGTH_SHORT).show()

            }

        }
    }



    private fun setPositionRecyclerView(){
        taskAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            //observa se tem alguma tarefa adicionada e muda para a nova posicao deixando ela ser a primeira
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.rvTask.scrollToPosition(0)
            }
        })
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