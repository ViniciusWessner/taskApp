package com.example.taskapp.data.Model

import android.app.ActivityManager.TaskDescription
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//usamos a data class pra jogar dados em memoria de uma classe para outra etc

@Parcelize
data class Task(
    val id: String,
    val description: String,
    val status: Status = Status.TODO
):Parcelable
