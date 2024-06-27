package com.example.taskapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskapp.data.Model.Task
import com.example.taskapp.databinding.ItemTaskBinding

class TaskAdapter(
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
    }

    //referencias dos nossos atributos
    inner class MyViewHolder(val binding: ItemTaskBinding):RecyclerView.ViewHolder(binding.root){

    }
}