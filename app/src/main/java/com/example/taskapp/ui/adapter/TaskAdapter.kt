package com.example.taskapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.taskapp.R
import com.example.taskapp.data.Model.Status
import com.example.taskapp.data.Model.Task
import com.example.taskapp.databinding.ItemTaskBinding

class TaskAdapter(
    private val context: Context,
    private val taskList: List<Task>
): RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {

    //criar uma visualizacao para cada linha
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    //setamos o tamanho da lista pro recycler view
    override fun getItemCount(): Int = taskList.size

    //aqui vamos exibir as informacoes dinamicamente (ex: titulo)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.textDescription.text = task.description
        setIndicators(task, holder)
    }

    private fun setIndicators(task: Task, holder: MyViewHolder){
        when (task.status) {

            Status.TODO -> {
                holder.binding.btnBack.isVisible = false
            }
            Status.DOING -> {
                holder.binding.btnBack.setColorFilter(ContextCompat.getColor(context, R.color.color_btnBack))
                holder.binding.btnNext.setColorFilter(ContextCompat.getColor(context, R.color.color_btnNext))
            }

            Status.DONE -> {
                holder.binding.btnNext.isVisible = false
            }

            else -> {
                holder.binding.btnNext.isVisible = true
                holder.binding.btnBack.isVisible = true
            }
        }

    }

    //referencias dos nossos atributos
    inner class MyViewHolder(val binding: ItemTaskBinding):RecyclerView.ViewHolder(binding.root){

    }
}