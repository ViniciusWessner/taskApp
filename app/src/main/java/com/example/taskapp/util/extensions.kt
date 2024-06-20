package com.example.taskapp.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.taskapp.R
import com.example.taskapp.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

//extensao de um fragment que recebe uma toolbar, somente poderá ser usada em fragmentos
fun Fragment.initToolbar(toolbar: Toolbar) {
     (activity as AppCompatActivity).setSupportActionBar(toolbar)
     (activity as AppCompatActivity).title = ""
     (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
}

//componente do modal que avisa de erros, recebe e transmite mensagens
fun Fragment.showButtomSheet(
    titleDialog: Int? = null,
    titleButton: Int? = null,
    massage: Int,
    onClick: ()-> Unit = {}
){
    val botomSheetDialog = BottomSheetDialog(requireContext(), )
    val binding: BottomSheetBinding =
        BottomSheetBinding.inflate(layoutInflater, null, false)
    binding.txtTitle.text = getText(titleDialog ?: R.string.text_title_warning)
    binding.txtMessage.text = getText(massage)
    binding.btnsave.text = getText(titleDialog ?: R.string.text_button_warning)
    binding.btnsave.setOnClickListener{
        onClick()
        botomSheetDialog.dismiss()
    }
    botomSheetDialog.setContentView(binding.root)
    botomSheetDialog.show()

}