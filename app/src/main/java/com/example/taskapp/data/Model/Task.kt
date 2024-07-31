package com.example.taskapp.data.Model

import android.os.Parcelable
import com.example.taskapp.util.FirebaseHelper
import kotlinx.parcelize.Parcelize

//usamos a data class pra jogar dados em memoria de uma classe para outra etc

@Parcelize
data class Task(
    var id: String = "",
    var description: String = "",
    var status: Status = Status.TODO
):Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
