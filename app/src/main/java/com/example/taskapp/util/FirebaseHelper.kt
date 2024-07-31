package com.example.taskapp.util

import com.example.taskapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import java.lang.Error

class FirebaseHelper {

    companion object{
        fun getDatabase() = Firebase.database.reference

        fun getAuth() = FirebaseAuth.getInstance()

        fun getIdUser() = getAuth().currentUser?.uid ?: ""

        fun isAutenticated() = getAuth().currentUser != null

        fun validError(error: String): Int {
            return when {
                error.contains("There is no user record corresponding to this identifier. The user may have been deleted.") -> {
                    R.string.acount_not_registre
                }
                error.contains("The email address is badly formatted.") -> {
                    R.string.invalid_email_login
                }
                error.contains("The supplied auth credential is incorrect, malformed or has expired") -> {
                    R.string.invalid_password_login
                }
                error.contains("The email address is already in use by another account.") -> {
                    R.string.email_in_user
                }
                error.contains("Password should be at least 6 characters") -> {
                    R.string.password_strong
                }
                else -> {
                    R.string.error_generic
                }
            }
        }
    }

}