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
import com.example.taskapp.databinding.FragmentDoingBinding
import com.example.taskapp.ui.adapter.TaskAdapter
import com.example.taskapp.util.StateView
import com.example.taskapp.util.showButtomSheet

class DoingFragment : Fragment() {

    private var _binding: FragmentDoingBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskAdapter

    private val viewModel:TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initReciclerViewTasks()
        observeViewModel()

        viewModel.getTasks()
    }


    private fun observeViewModel(){

        viewModel.taskList.observe(viewLifecycleOwner){stateView ->
            when(stateView){
                is StateView.OnLoading -> {
                    binding.progressBarList.isVisible = true
                }
                is StateView.OnSuccess -> {

                    val taskList = stateView.data?.filter { it.status == Status.DOING }

                    binding.progressBarList.isVisible = false
                    listEmpty(taskList ?: emptyList())

                    taskAdapter.submitList(taskList)
                }
                is StateView.OnError -> {
                    binding.progressBarList.isVisible = false
                    Toast.makeText(requireContext(), stateView.massage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.taskInsert.observe(viewLifecycleOwner){ stateView ->

            when(stateView){
                is StateView.OnLoading -> {
                    binding.progressBarList.isVisible = true
                }
                is StateView.OnSuccess -> {
                    binding.progressBarList.isVisible = false
                    if (stateView.data?.status == Status.DOING){
                        val oldList = taskAdapter.currentList //lista atual

                        val newList = oldList.toMutableList().apply{
                            add(0, stateView.data)
                        }

                        taskAdapter.submitList(newList)
                        setPositionRecyclerView()
                    }
                }
                is StateView.OnError -> {
                    binding.progressBarList.isVisible = false
                    Toast.makeText(requireContext(), stateView.massage, Toast.LENGTH_SHORT).show()
                }
            }

        }

        viewModel.taskUpdate.observe(viewLifecycleOwner){stateView ->
            when(stateView){
                is StateView.OnLoading -> {
                    binding.progressBarList.isVisible = true
                }
                is StateView.OnSuccess -> {
                    binding.progressBarList.isVisible = false
                    val oldList = taskAdapter.currentList //lista atual

                    val newList = oldList.toMutableList().apply{

                        if (!oldList.contains(stateView.data) && stateView.data?.status == Status.DOING ){
                            add(0, stateView.data)
                            setPositionRecyclerView()
                        }

                        if (stateView.data?.status == Status.DOING) {
                            find { it.id == stateView.data.id }?.description = stateView.data.description
                        } else {
                            remove(stateView.data)
                        }
                    }

                    val position = newList.indexOfFirst { it.id == stateView.data?.id }

                    taskAdapter.submitList(newList)

                    taskAdapter.notifyItemChanged(position)
                }
                is StateView.OnError -> {
                    binding.progressBarList.isVisible = false
                    Toast.makeText(requireContext(), stateView.massage, Toast.LENGTH_SHORT).show()
                }
            }

        }

        viewModel.taskDelete.observe(viewLifecycleOwner){stateView ->
            when(stateView){
                is StateView.OnLoading -> {
                    binding.progressBarList.isVisible = true
                }
                is StateView.OnSuccess -> {
                    binding.progressBarList.isVisible = false
                    Toast.makeText(requireContext(), R.string.return_task_delete, Toast.LENGTH_SHORT).show()

                    val oldList = taskAdapter.currentList
                    val newList = oldList.toMutableList().apply {
                        remove(stateView.data)
                    }
                    taskAdapter.submitList(newList)

                }
                is StateView.OnError -> {
                    binding.progressBarList.isVisible = false
                    Toast.makeText(requireContext(), stateView.massage, Toast.LENGTH_SHORT).show()
                }
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
            TaskAdapter.SELECT_BACK -> {
                task.status = Status.TODO
                viewModel.updateTask(task)
                Toast.makeText(requireContext(), "voltando tarefa: ${task.description}", Toast.LENGTH_SHORT).show()
            }
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
                task.status = Status.DONE
                viewModel.updateTask(task)
                Toast.makeText(requireContext(), "Avancando tarefa: ${task.description}", Toast.LENGTH_SHORT).show()

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
    private fun setPositionRecyclerView(){
        taskAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            //observa se tem alguma tarefa adicionada e muda para a nova posicao deixando ela ser a primeira
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.rvTask.scrollToPosition(0)
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}